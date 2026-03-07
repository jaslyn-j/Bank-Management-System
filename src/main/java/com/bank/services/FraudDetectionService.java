package com.bank.services;

import com.bank.dao.FraudAlertDAO;
import com.bank.models.FraudAlert;
import com.bank.models.FraudRule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class FraudDetectionService {
    private FraudAlertDAO fraudAlertDAO;

    public FraudDetectionService() {
        this.fraudAlertDAO = new FraudAlertDAO();
    }

    // Result object returned to the transaction service
    public static class FraudCheckResult {
        public final boolean   blocked;
        public final List<String> triggeredRules;

        public FraudCheckResult(boolean blocked, List<String> triggeredRules) {
            this.blocked        = blocked;
            this.triggeredRules = triggeredRules;
        }

        public boolean isFlagged() {
            return !triggeredRules.isEmpty();
        }
    }

    public FraudCheckResult analyze(int accountId, int branchId,
                                    BigDecimal amount, String transactionType) {

        FraudRule rule = fraudAlertDAO.getFraudRuleByBranch(branchId);

        if (rule == null) {
            return new FraudCheckResult(false, new ArrayList<>());
        }

        List<String> triggered = new ArrayList<>();
        boolean blocked = false;

        // --- Rule 1: Unusually large transaction ---
        // Flag if amount exceeds the configured multiplier of the account's average
        BigDecimal average = fraudAlertDAO.getAverageTransactionAmount(accountId);
        if (average.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal threshold = average.multiply(rule.getLargeTxMultiplier());
            if (amount.compareTo(threshold) > 0) {
                triggered.add("LARGE_TRANSACTION: Amount of " + amount +
                        " exceeds " + rule.getLargeTxMultiplier() +
                        "x the account average of " + average);
            }
        }

        // --- Rule 2: Absolute large amount threshold ---
        // Flag regardless of average if amount exceeds a fixed threshold
        if (amount.compareTo(rule.getLargeTxMinAmount()) >= 0) {
            triggered.add("HIGH_VALUE_TRANSACTION: Amount of " + amount +
                    " exceeds the high-value threshold of " +
                    rule.getLargeTxMinAmount());
        }

        // --- Rule 3: Rapid successive transactions ---
        // Flag if too many transactions in a short time window
        int recentCount = fraudAlertDAO.countRecentTransactions(
                accountId, rule.getRapidTxWindowMins());

        if (recentCount >= rule.getRapidTxLimit()) {
            triggered.add("RAPID_TRANSACTIONS: " + recentCount +
                    " transactions detected within " +
                    rule.getRapidTxWindowMins() + " minutes");
            blocked = true;
        }

        // --- Rule 4: Daily velocity limit ---
        // Block if total outgoing transactions today exceed the daily cap
        if (transactionType.equals("withdrawal") ||
                transactionType.equals("transfer_out")) {

            BigDecimal dailyTotal = fraudAlertDAO.getDailyTransactionTotal(accountId);
            BigDecimal projectedTotal = dailyTotal.add(amount);

            if (projectedTotal.compareTo(rule.getDailyLimit()) > 0) {
                triggered.add("DAILY_LIMIT_EXCEEDED: Projected daily total of " +
                        projectedTotal + " exceeds the daily limit of " +
                        rule.getDailyLimit());
                blocked = true;
            }
        }

        // Log all triggered rules as fraud alerts
        for (String reason : triggered) {
            String severity = blocked ? "high" : "medium";
            logAlert(accountId, null, reason, severity);
        }

        return new FraudCheckResult(blocked, triggered);
    }

    // Log a fraud alert with a known transaction ID (called after transaction is saved)
    public void logAlertWithTransaction(int accountId, int transactionId,
                                        String reason, String severity) {
        logAlert(accountId, transactionId, reason, severity);
    }

    // Admin reviews and updates an alert
    public boolean reviewAlert(int alertId, String status, int managerId) {
        return fraudAlertDAO.updateAlertStatus(alertId, status, managerId);
    }

    // Retrieve all open alerts for admin dashboard
    public java.util.List<FraudAlert> getOpenAlertsForBranch(int branchId) {
        return fraudAlertDAO.getOpenAlertsByBranch(branchId);
    }

    public java.util.List<FraudAlert> getAllAlertsForBranch(int branchId) {
        return fraudAlertDAO.getAllAlertsByBranch(branchId);
    }

    public int getOpenAlertCount(int branchId) {
        return fraudAlertDAO.getOpenAlertCount(branchId);
    }

    private void logAlert(int accountId, Integer transactionId,
                          String reason, String severity) {
        FraudAlert alert = new FraudAlert();
        alert.setAccountId(accountId);
        alert.setTransactionId(transactionId);
        alert.setAlertReason(reason);
        alert.setSeverity(severity);
        alert.setStatus("open");
        fraudAlertDAO.addAlert(alert);
    }
}
