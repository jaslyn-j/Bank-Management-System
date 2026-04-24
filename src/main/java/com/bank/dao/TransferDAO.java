package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.Transfer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransferDAO {
    private final Connection connection;

    public TransferDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Retrieve all transfers where the account was the sender
    public List<Transfer> getTransfersSent(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM Transfer WHERE from_account_id = ? ORDER BY time_stamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transfers.add(mapResultSetToTransfer(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching sent transfers: " + e.getMessage());
        }

        return transfers;
    }

    // Retrieve all transfers where the account was the receiver
    public List<Transfer> getTransfersReceived(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM Transfer WHERE to_account_id = ? ORDER BY time_stamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transfers.add(mapResultSetToTransfer(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching received transfers: " + e.getMessage());
        }

        return transfers;
    }

    // Insert a transfer record — always called inside a transaction block from the service layer
    public boolean addTransfer(Transfer transfer) {
        String sql = "INSERT INTO Transfer (from_account_id, to_account_id, amount, status) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, transfer.getFromAccountId());
            stmt.setInt(2, transfer.getToAccountId());
            stmt.setBigDecimal(3, transfer.getAmount());
            stmt.setString(4, transfer.getStatus());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding transfer: " + e.getMessage());
        }
        return false;
    }

    // Maps a ResultSet row to a Transfer object
    private Transfer mapResultSetToTransfer(ResultSet rs) throws SQLException {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setFromAccountId(rs.getInt("from_account_id"));
        transfer.setToAccountId(rs.getInt("to_account_id"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setStatus(rs.getString("status"));

        Timestamp timestamp = rs.getTimestamp("time_stamp");
        if (timestamp != null) {
            transfer.setTimestamp(timestamp.toLocalDateTime());
        }

        return transfer;
    }
}
