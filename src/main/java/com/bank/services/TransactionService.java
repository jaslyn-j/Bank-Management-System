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
        this.connection     = DBConnection.getInstance().getConnection();
        this.accountDAO     = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.transferDAO    = new TransferDAO();
    }

    // Deposit an amount into an account
    public boolean deposit(int accountId, BigDecimal amount, String description) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Deposit amount must be greater than zero.");
            return false;
        }

        Account account = accountDAO.getAccountById(accountId);

        if (account == null) {
            System.err.println("Account not found.");
            return false;
        }

        if (!account.getStatus().equals("active")) {
            System.err.println("Account is not active.");
            return false;
        }

        BigDecimal newBalance = account.getBalance().add(amount);

        // Update the balance
        boolean balanceUpdated = accountDAO.updateBalance(accountId, newBalance);
        if (!balanceUpdated) return false;

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType("deposit");
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(description != null ? description : "Deposit");

        return transactionDAO.addTransaction(transaction);
    }

    // Withdraw an amount from an account
    public boolean withdraw(int accountId, BigDecimal amount, String description) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Withdrawal amount must be greater than zero.");
            return false;
        }

        Account account = accountDAO.getAccountById(accountId);

        if (account == null) {
            System.err.println("Account not found.");
            return false;
        }

        if (!account.getStatus().equals("active")) {
            System.err.println("Account is not active.");
            return false;
        }

        if (account.getBalance().compareTo(amount) < 0) {
            System.err.println("Insufficient funds.");
            return false;
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);

        boolean balanceUpdated = accountDAO.updateBalance(accountId, newBalance);
        if (!balanceUpdated) return false;

        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType("withdrawal");
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(description != null ? description : "Withdrawal");

        return transactionDAO.addTransaction(transaction);
    }

    // Transfer funds between two accounts
    // Uses a SQL transaction so both sides either fully complete or fully roll back
    public boolean transfer(int fromAccountId, int toAccountId, BigDecimal amount) {

        if (fromAccountId == toAccountId) {
            System.err.println("Cannot transfer to the same account.");
            return false;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Transfer amount must be greater than zero.");
            return false;
        }

        Account fromAccount = accountDAO.getAccountById(fromAccountId);
        Account toAccount   = accountDAO.getAccountById(toAccountId);

        if (fromAccount == null || toAccount == null) {
            System.err.println("One or both accounts not found.");
            return false;
        }

        if (!fromAccount.getStatus().equals("active")) {
            System.err.println("Source account is not active.");
            return false;
        }

        if (!toAccount.getStatus().equals("active")) {
            System.err.println("Destination account is not active.");
            return false;
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            System.err.println("Insufficient funds.");
            return false;
        }

        // Begin SQL transaction — all or nothing
        try {
            connection.setAutoCommit(false);

            BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
            BigDecimal newToBalance   = toAccount.getBalance().add(amount);

            // Debit the sender
            accountDAO.updateBalance(fromAccountId, newFromBalance);

            // Credit the receiver
            accountDAO.updateBalance(toAccountId, newToBalance);

            // Record the outgoing transaction for the sender
            Transaction outgoing = new Transaction();
            outgoing.setAccountId(fromAccountId);
            outgoing.setTransactionType("transfer_out");
            outgoing.setAmount(amount);
            outgoing.setBalanceAfter(newFromBalance);
            transactionDAO.addTransaction(outgoing);

            // Record the incoming transaction for the receiver
            Transaction incoming = new Transaction();
            incoming.setAccountId(toAccountId);
            incoming.setTransactionType("transfer_in");
            incoming.setAmount(amount);
            incoming.setBalanceAfter(newToBalance);
            transactionDAO.addTransaction(incoming);

            // Record the transfer itself
            Transfer transfer = new Transfer();
            transfer.setFromAccountId(fromAccountId);
            transfer.setToAccountId(toAccountId);
            transfer.setAmount(amount);
            transfer.setStatus("completed");
            transferDAO.addTransfer(transfer);

            // Commit everything
            connection.commit();
            return true;

        } catch (Exception e) {
            // If anything fails, roll back all changes
            try {
                connection.rollback();
                System.err.println("Transfer failed and was rolled back: " + e.getMessage());
            } catch (Exception rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            return false;

        } finally {
            // Always restore auto-commit after a manual transaction
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
                System.err.println("Failed to restore auto-commit: " + e.getMessage());
            }
        }
    }

    // Retrieve transaction history for an account
    public List<Transaction> getTransactionHistory(int accountId) {
        return transactionDAO.getTransactionsByAccount(accountId);
    }

    public TransactionSummary getTransactionSummary(int accountId) {
        return transactionDAO.getTransactionSummary(accountId);
    }
}
