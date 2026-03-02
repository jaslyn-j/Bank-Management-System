package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final Connection connection;

    public TransactionDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Retrieve all transactions for a specific account
    public List<Transaction> getTransactionsByAccount(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE account_id = ? ORDER BY timestamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
        }

        return transactions;
    }

    // Insert a single transaction record
    public boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO Transaction (account_id, transaction_type, amount, " +
                "balance_after, description) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, transaction.getAccountId());
            stmt.setString(2, transaction.getTransactionType());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setBigDecimal(4, transaction.getBalanceAfter());
            stmt.setString(5, transaction.getDescription());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
        }

        return false;
    }

    // Maps a ResultSet row to a Transaction object
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setBalanceAfter(rs.getBigDecimal("balance_after"));
        transaction.setDescription(rs.getString("description"));

        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            transaction.setTimestamp(timestamp.toLocalDateTime());
        }

        return transaction;
    }
}
