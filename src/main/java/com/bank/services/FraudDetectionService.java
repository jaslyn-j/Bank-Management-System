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

    // -------------------------------------------------------
    // RESULT OBJECT
    // Returned to TransactionService
    // after every fraud check
    // -------------------------------------------------------
    public static class FraudCheckResult {
        public final boolean      blocked;
        public final List<String> triggeredRules;

        public FraudCheckResult(
                boolean blocked,
                List<String> triggeredRules) {
            this.blocked        = blocked;
            this.triggeredRules = triggeredRules;
        }

        public boolean isFlagged() {
            return !triggeredRules.isEmpty();
        }
    }

    // -------------------------------------------------------
    // ANALYZE — original method kept unchanged
    // Checks all rules AND logs alerts immediately
    // with NULL transaction_id
    // Kept for backward compatibility with any
    // other code that still calls it
    // -------------------------------------------------------
    public FraudCheckResult analyze(
            int accountId,
            int branchId,
            BigDecimal amount,
            String transactionType) {

        FraudRule rule =
                fraudAlertDAO
                        .getFraudRuleByBranch(
                                branchId);

        if (rule == null) {
            return new FraudCheckResult(
                    false, new ArrayList<>());
        }

        List<String> triggered =
                new ArrayList<>();
        boolean blocked = false;

        // Rule 1: Unusually large transaction
        BigDecimal average =
                fraudAlertDAO
                        .getAverageTransactionAmount(
                                accountId);
        if (average.compareTo(
                BigDecimal.ZERO) > 0) {
            BigDecimal threshold =
                    average.multiply(
                            rule.getLargeTxMultiplier());
            if (amount.compareTo(
                    threshold) > 0) {
                triggered.add(
                        "LARGE_TRANSACTION: "
                                + "Amount of " + amount
                                + " exceeds "
                                + rule.getLargeTxMultiplier()
                                + "x the account average "
                                + "of " + average);
            }
        }

        // Rule 2: Absolute large amount
        if (amount.compareTo(
                rule.getLargeTxMinAmount())
                >= 0) {
            triggered.add(
                    "HIGH_VALUE_TRANSACTION: "
                            + "Amount of " + amount
                            + " exceeds the high-value "
                            + "threshold of "
                            + rule.getLargeTxMinAmount());
        }

        // Rule 3: Rapid successive transactions
        int recentCount =
                fraudAlertDAO
                        .countRecentTransactions(
                                accountId,
                                rule.getRapidTxWindowMins());
        if (recentCount >=
                rule.getRapidTxLimit()) {
            triggered.add(
                    "RAPID_TRANSACTIONS: "
                            + recentCount
                            + " transactions detected "
                            + "within "
                            + rule.getRapidTxWindowMins()
                            + " minutes");
            blocked = true;
        }

        // Rule 4: Daily velocity limit
        if (transactionType.equals(
                "withdrawal")
                || transactionType.equals(
                "transfer_out")) {
            BigDecimal dailyTotal =
                    fraudAlertDAO
                            .getDailyTransactionTotal(
                                    accountId);
            BigDecimal projectedTotal =
                    dailyTotal.add(amount);
            if (projectedTotal.compareTo(
                    rule.getDailyLimit()) > 0) {
                triggered.add(
                        "DAILY_LIMIT_EXCEEDED: "
                                + "Projected daily total "
                                + "of " + projectedTotal
                                + " exceeds the daily "
                                + "limit of "
                                + rule.getDailyLimit());
                blocked = true;
            }
        }

        // Log all triggered rules immediately
        // with NULL transaction_id
        for (String reason : triggered) {
            String severity = blocked
                    ? "high" : "medium";
            logAlert(accountId, null,
                    reason, severity);
        }

        return new FraudCheckResult(
                blocked, triggered);
    }

    // -------------------------------------------------------
    // ANALYZE ONLY — NEW METHOD
    // Checks all four rules exactly like analyze()
    // but does NOT log any alerts
    // Used by TransactionService so alerts are only
    // logged once after the transaction is saved
    // with the real transaction_id attached
    // -------------------------------------------------------
    public FraudCheckResult analyzeOnly(
            int accountId,
            int branchId,
            BigDecimal amount,
            String transactionType) {

        FraudRule rule =
                fraudAlertDAO
                        .getFraudRuleByBranch(
                                branchId);

        if (rule == null) {
            return new FraudCheckResult(
                    false, new ArrayList<>());
        }

        List<String> triggered =
                new ArrayList<>();
        boolean blocked = false;

        // Rule 1: Unusually large transaction
        BigDecimal average =
                fraudAlertDAO
                        .getAverageTransactionAmount(
                                accountId);
        if (average.compareTo(
                BigDecimal.ZERO) > 0) {
            BigDecimal threshold =
                    average.multiply(
                            rule.getLargeTxMultiplier());
            if (amount.compareTo(
                    threshold) > 0) {
                triggered.add(
                        "LARGE_TRANSACTION: "
                                + "Amount of " + amount
                                + " exceeds "
                                + rule.getLargeTxMultiplier()
                                + "x the account average "
                                + "of " + average);
            }
        }

        // Rule 2: Absolute large amount
        if (amount.compareTo(
                rule.getLargeTxMinAmount())
                >= 0) {
            triggered.add(
                    "HIGH_VALUE_TRANSACTION: "
                            + "Amount of " + amount
                            + " exceeds the high-value "
                            + "threshold of "
                            + rule.getLargeTxMinAmount());
        }

        // Rule 3: Rapid successive transactions
        int recentCount =
                fraudAlertDAO
                        .countRecentTransactions(
                                accountId,
                                rule.getRapidTxWindowMins());
        if (recentCount >=
                rule.getRapidTxLimit()) {
            triggered.add(
                    "RAPID_TRANSACTIONS: "
                            + recentCount
                            + " transactions detected "
                            + "within "
                            + rule.getRapidTxWindowMins()
                            + " minutes");
            blocked = true;
        }

        // Rule 4: Daily velocity limit
        if (transactionType.equals(
                "withdrawal")
                || transactionType.equals(
                "transfer_out")) {
            BigDecimal dailyTotal =
                    fraudAlertDAO
                            .getDailyTransactionTotal(
                                    accountId);
            BigDecimal projectedTotal =
                    dailyTotal.add(amount);
            if (projectedTotal.compareTo(
                    rule.getDailyLimit()) > 0) {
                triggered.add(
                        "DAILY_LIMIT_EXCEEDED: "
                                + "Projected daily total "
                                + "of " + projectedTotal
                                + " exceeds the daily "
                                + "limit of "
                                + rule.getDailyLimit());
                blocked = true;
            }
        }

        // NO logging here
        // alerts are returned to the caller
        // and logged separately after the
        // transaction is saved
        return new FraudCheckResult(
                blocked, triggered);
    }

    // -------------------------------------------------------
    // LOG TRIGGERED ALERTS — NEW METHOD
    // Called by TransactionService after a transaction
    // is saved so alerts are stored with the real
    // transaction_id instead of NULL
    // For blocked transactions transactionId is NULL
    // because no transaction was saved
    // -------------------------------------------------------
    public void logTriggeredAlerts(
            int accountId,
            Integer transactionId,
            FraudCheckResult fraudResult) {

        for (String reason :
                fraudResult.triggeredRules) {
            // Use high severity if the
            // transaction was blocked
            // medium if it was only flagged
            String severity =
                    fraudResult.blocked
                            ? "high" : "medium";
            logAlert(accountId,
                    transactionId,
                    reason, severity);
        }
    }

    // -------------------------------------------------------
    // LOG ALERT WITH TRANSACTION ID
    // Original method kept unchanged
    // -------------------------------------------------------
    public void logAlertWithTransaction(
            int accountId,
            int transactionId,
            String reason,
            String severity) {
        logAlert(accountId,
                transactionId,
                reason, severity);
    }

    // -------------------------------------------------------
    // REVIEW ALERT
    // -------------------------------------------------------
    public boolean reviewAlert(
            int alertId,
            String status,
            int managerId) {
        return fraudAlertDAO
                .updateAlertStatus(
                        alertId, status, managerId);
    }

    // -------------------------------------------------------
    // GET ALERTS
    // -------------------------------------------------------
    public List<FraudAlert>
    getOpenAlertsForBranch(
            int branchId) {
        return fraudAlertDAO
                .getOpenAlertsByBranch(
                        branchId);
    }

    public List<FraudAlert>
    getAllAlertsForBranch(
            int branchId) {
        return fraudAlertDAO
                .getAllAlertsByBranch(
                        branchId);
    }

    public int getOpenAlertCount(
            int branchId) {
        return fraudAlertDAO
                .getOpenAlertCount(branchId);
    }

    public int dismissAllReviewedAlerts(
            int branchId) {
        return ProcedureService
                .dismissReviewedAlerts(
                        branchId);
    }

    public List<Integer>
    getUnblockedAccountsWithAlerts(
            int branchId) {
        return fraudAlertDAO
                .getUnblockedAccountsWithAlerts(
                        branchId);
    }

    // -------------------------------------------------------
    // PRIVATE LOG HELPER
    // Used internally by analyze() and
    // logTriggeredAlerts()
    // -------------------------------------------------------
    private void logAlert(
            int accountId,
            Integer transactionId,
            String reason,
            String severity) {
        FraudAlert alert = new FraudAlert();
        alert.setAccountId(accountId);
        alert.setTransactionId(transactionId);
        alert.setAlertReason(reason);
        alert.setSeverity(severity);
        alert.setStatus("open");
        fraudAlertDAO.addAlert(alert);
    }
}