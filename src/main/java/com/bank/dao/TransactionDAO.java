package com.bank.dao;

import com.bank.models.TransactionSummary;
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

    public List<Transaction> getTransactionsByAccount(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE account_id = ? ORDER BY time_stamp DESC";

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

    public int addTransaction(
            Transaction transaction) {
        String sql =
                "INSERT INTO Transaction (account_id, transaction_type, amount, balance_after, description,time_stamp) VALUES ("
                        + "?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement stmt =connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1,transaction.getAccountId());
            stmt.setString(2,transaction.getTransactionType());
            stmt.setBigDecimal(3,transaction.getAmount());
            stmt.setBigDecimal(4,transaction.getBalanceAfter());
            stmt.setString(5, transaction.getDescription());

            int rowsAffected =stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys =stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId =generatedKeys.getInt(1);
                    transaction.setTransactionId(generatedId);
                    return generatedId;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding transaction: "+ e.getMessage());
        }
        return -1;
    }

    public int getLastInsertedId() {
        String sql = "SELECT LAST_INSERT_ID()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error fetching last insert ID: " + e.getMessage());
        }
        return -1;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setBalanceAfter(rs.getBigDecimal("balance_after"));
        transaction.setDescription(rs.getString("description"));

        Timestamp timestamp = rs.getTimestamp("time_stamp");
        if (timestamp != null) {
            transaction.setTimestamp(timestamp.toLocalDateTime());
        }

        return transaction;
    }
    public List<Transaction> getAllTransferTransactions(int accountId) {
        List<Transaction> results = new ArrayList<>();
        String sql =
                "SELECT * FROM Transaction WHERE account_id = ? AND transaction_type = 'transfer_out' " +
                 "UNION SELECT * FROM Transaction WHERE account_id = ? AND transaction_type = 'transfer_in' " +
                        "ORDER BY time_stamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setInt(2, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToTransaction(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching transfer transactions: "
                    + e.getMessage());
        }

        return results;
    }

    public TransactionSummary getTransactionSummary(int accountId) {

        String sql = "SELECT account_id, COUNT(*) AS total_transactions, " +
                "SUM(CASE WHEN transaction_type = 'deposit' THEN amount ELSE 0 END) AS total_deposited, " +
                "SUM(CASE WHEN transaction_type = 'withdrawal' THEN amount ELSE 0 END) AS total_withdrawn, " +
                "SUM(CASE WHEN transaction_type = 'transfer_out' THEN amount ELSE 0 END) AS total_transferred_out, " +
                "SUM(CASE WHEN transaction_type = 'transfer_in' THEN amount ELSE 0 END) AS total_transferred_in " +
                "FROM Transaction WHERE account_id = ? GROUP BY account_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                TransactionSummary summary = new TransactionSummary();
                summary.setAccountId(rs.getInt("account_id"));
                summary.setTotalTransactions(rs.getInt("total_transactions"));
                summary.setTotalDeposited(rs.getBigDecimal("total_deposited"));
                summary.setTotalWithdrawn(rs.getBigDecimal("total_withdrawn"));
                summary.setTotalTransferredOut(rs.getBigDecimal("total_transferred_out"));
                summary.setTotalTransferredIn(rs.getBigDecimal("total_transferred_in"));
                return summary;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching transaction summary: "
                    + e.getMessage());
        }

        return null;
    }
}
