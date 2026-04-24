package com.bank.services;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import com.bank.dao.TransferDAO;
import com.bank.db.DBConnection;
import com.bank.models.Account;
import com.bank.models.Transaction;
import com.bank.models.Transfer;
import com.bank.models.TransactionSummary;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class TransactionService {

    private AccountDAO     accountDAO;
    private TransactionDAO transactionDAO;
    private TransferDAO    transferDAO;
    private Connection     connection;

    public TransactionService() {
        this.connection     =
                DBConnection.getInstance()
                        .getConnection();
        this.accountDAO     =
                new AccountDAO();
        this.transactionDAO =
                new TransactionDAO();
        this.transferDAO    =
                new TransferDAO();
    }

    // -------------------------------------------------------
    // DEPOSIT
    // -------------------------------------------------------
    public boolean deposit(
            int accountId,
            BigDecimal amount,
            String description) {

        if (amount.compareTo(
                BigDecimal.ZERO) <= 0) {
            System.err.println(
                    "Deposit amount must be "
                            + "greater than zero.");
            return false;
        }

        Account account =
                accountDAO.getAccountById(
                        accountId);

        if (account == null) {
            System.err.println(
                    "Account not found.");
            return false;
        }

        if (!account.getStatus()
                .equals("active")) {
            System.err.println(
                    "Account is not active.");
            return false;
        }

        // --- FRAUD CHECK ---
        // analyzeOnly() checks all rules
        // but does NOT log any alerts
        // Alerts are logged once below
        // after the transaction is saved
        FraudDetectionService fraudService =
                new FraudDetectionService();
        FraudDetectionService.FraudCheckResult
                fraudResult =
                fraudService.analyzeOnly(
                        accountId,
                        account.getBranchId(),
                        amount,
                        "deposit");

        if (fraudResult.blocked) {
            // Blocked — no transaction saved
            // log alerts with NULL tx_id
            fraudService.logTriggeredAlerts(
                    accountId,
                    null,
                    fraudResult);
            System.err.println(
                    "Deposit blocked by "
                            + "fraud detection.");
            return false;
        }
        // -------------------

        BigDecimal newBalance =
                account.getBalance()
                        .add(amount);

        boolean balanceUpdated =
                accountDAO.updateBalance(
                        accountId, newBalance);
        if (!balanceUpdated) return false;

        Transaction transaction =
                new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType(
                "deposit");
        transaction.setAmount(amount);
        transaction.setBalanceAfter(
                newBalance);
        transaction.setDescription(
                description != null
                        ? description : "Deposit");

        int txId =transactionDAO.addTransaction(transaction);

        if (txId == -1) return false;

        // Log fraud alerts with the real transaction_id from the same insert
        if (fraudResult.isFlagged()) {
            fraudService.logTriggeredAlerts(
                    accountId,
                    txId,
                    fraudResult);
        }

        return true;
    }

    // -------------------------------------------------------
    // WITHDRAW
    // -------------------------------------------------------
    public boolean withdraw(
            int accountId,
            BigDecimal amount,
            String description) {

        if (amount.compareTo(
                BigDecimal.ZERO) <= 0) {
            System.err.println(
                    "Withdrawal amount must "
                            + "be greater than zero.");
            return false;
        }

        Account account =
                accountDAO.getAccountById(
                        accountId);

        if (account == null) {
            System.err.println(
                    "Account not found.");
            return false;
        }

        if (!account.getStatus()
                .equals("active")) {
            System.err.println(
                    "Account is not active.");
            return false;
        }

        if (account.getBalance()
                .compareTo(amount) < 0) {
            System.err.println(
                    "Insufficient funds.");
            return false;
        }

        // --- FRAUD CHECK ---
        // analyzeOnly() — no alerts logged
        FraudDetectionService fraudService =
                new FraudDetectionService();
        FraudDetectionService.FraudCheckResult
                fraudResult =
                fraudService.analyzeOnly(
                        accountId,
                        account.getBranchId(),
                        amount,
                        "withdrawal");

        if (fraudResult.blocked) {
            // Blocked — log with NULL tx_id
            fraudService.logTriggeredAlerts(
                    accountId,
                    null,
                    fraudResult);
            System.err.println(
                    "Withdrawal blocked by "
                            + "fraud detection.");
            return false;
        }
        // -------------------

        BigDecimal newBalance =
                account.getBalance()
                        .subtract(amount);

        boolean balanceUpdated =
                accountDAO.updateBalance(
                        accountId, newBalance);
        if (!balanceUpdated) return false;

        Transaction transaction =
                new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType(
                "withdrawal");
        transaction.setAmount(amount);
        transaction.setBalanceAfter(
                newBalance);
        transaction.setDescription(
                description != null
                        ? description
                        : "Withdrawal");

        int txId =transactionDAO.addTransaction(transaction);

        if (txId == -1) return false;

        if (fraudResult.isFlagged()) {
            fraudService.logTriggeredAlerts(
                    accountId,
                    txId,
                    fraudResult);
        }

        return true;
    }

    // -------------------------------------------------------
    // TRANSFER
    // -------------------------------------------------------
    public boolean transfer(
            int fromAccountId,
            int toAccountId,
            BigDecimal amount) {

        if (fromAccountId == toAccountId) {
            System.err.println(
                    "Cannot transfer to "
                            + "the same account.");
            return false;
        }

        if (amount.compareTo(
                BigDecimal.ZERO) <= 0) {
            System.err.println(
                    "Transfer amount must be "
                            + "greater than zero.");
            return false;
        }

        Account fromAccount =
                accountDAO.getAccountById(
                        fromAccountId);
        Account toAccount =
                accountDAO.getAccountById(
                        toAccountId);

        if (fromAccount == null
                || toAccount == null) {
            System.err.println(
                    "One or both accounts "
                            + "not found.");
            return false;
        }

        if (!fromAccount.getStatus()
                .equals("active")) {
            System.err.println(
                    "Source account is "
                            + "not active.");
            return false;
        }

        if (!toAccount.getStatus()
                .equals("active")) {
            System.err.println(
                    "Destination account is "
                            + "not active.");
            return false;
        }

        if (fromAccount.getBalance()
                .compareTo(amount) < 0) {
            System.err.println(
                    "Insufficient funds.");
            return false;
        }

        // --- FRAUD CHECK ---
        // analyzeOnly() — no alerts logged
        FraudDetectionService fraudService =
                new FraudDetectionService();
        FraudDetectionService.FraudCheckResult
                fraudResult =
                fraudService.analyzeOnly(
                        fromAccountId,
                        fromAccount.getBranchId(),
                        amount,
                        "transfer_out");

        if (fraudResult.blocked) {
            // Blocked — log with NULL tx_id
            fraudService.logTriggeredAlerts(
                    fromAccountId,
                    null,
                    fraudResult);
            System.err.println(
                    "Transfer blocked by "
                            + "fraud detection.");
            return false;
        }
        // -------------------

        try {
            connection.setAutoCommit(false);

            BigDecimal newFromBalance =
                    fromAccount.getBalance()
                            .subtract(amount);
            BigDecimal newToBalance =
                    toAccount.getBalance()
                            .add(amount);

            accountDAO.updateBalance(
                    fromAccountId,
                    newFromBalance);
            accountDAO.updateBalance(
                    toAccountId,
                    newToBalance);

            Transaction outgoing =
                    new Transaction();
            outgoing.setAccountId(
                    fromAccountId);
            outgoing.setTransactionType(
                    "transfer_out");
            outgoing.setAmount(amount);
            outgoing.setBalanceAfter(
                    newFromBalance);
            int outgoingTxId =
                    transactionDAO
                            .addTransaction(outgoing);

            // Log fraud alerts with the real
            // outgoing transaction_id
            if (fraudResult.isFlagged()
                    && outgoingTxId != -1) {
                fraudService.logTriggeredAlerts(
                        fromAccountId,
                        outgoingTxId,
                        fraudResult);
            }
            // -----------------------------

            Transaction incoming =
                    new Transaction();
            incoming.setAccountId(
                    toAccountId);
            incoming.setTransactionType(
                    "transfer_in");
            incoming.setAmount(amount);
            incoming.setBalanceAfter(
                    newToBalance);
            transactionDAO.addTransaction(
                    incoming);

            Transfer transfer = new Transfer();
            transfer.setFromAccountId(
                    fromAccountId);
            transfer.setToAccountId(
                    toAccountId);
            transfer.setAmount(amount);
            transfer.setStatus("completed");
            transferDAO.addTransfer(transfer);

            connection.commit();
            return true;

        } catch (Exception e) {
            try {
                connection.rollback();
                System.err.println(
                        "Transfer failed and was "
                                + "rolled back: "
                                + e.getMessage());
            } catch (Exception rollbackEx) {
                System.err.println(
                        "Rollback failed: "
                                + rollbackEx.getMessage());
            }
            return false;

        } finally {
            try {
                connection.setAutoCommit(
                        true);
            } catch (Exception e) {
                System.err.println(
                        "Failed to restore "
                                + "auto-commit: "
                                + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------
    // READ METHODS — unchanged
    // -------------------------------------------------------
    public List<Transaction>
    getTransactionHistory(
            int accountId) {
        return transactionDAO
                .getTransactionsByAccount(
                        accountId);
    }

    public TransactionSummary
    getTransactionSummary(
            int accountId) {
        return transactionDAO
                .getTransactionSummary(
                        accountId);
    }

    public List<Transaction>
    getAllTransferTransactions(
            int accountId) {
        return transactionDAO
                .getAllTransferTransactions(
                        accountId);
    }
}