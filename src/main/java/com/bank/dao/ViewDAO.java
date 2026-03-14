package com.bank.dao;

import com.bank.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewDAO {

    private final Connection connection;

    public ViewDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    //Queries vw_account_balance_overview
    //Returns accounts categorised by balance tier
    public List<Map<String, Object>> getAccountBalanceOverview(int branchId) {
        return queryView(
                "SELECT * FROM vw_account_balance_overview WHERE branch_id = ?",
                branchId,
                "Error querying balance overview"
        );
    }

    // Queries vw_card_expiry_status
    // Returns only cards that are expired or expiring soon
    public List<Map<String, Object>> getExpiringCards(int branchId) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_card_expiry_status " +
                "WHERE branch_id = ? " +
                "AND expiry_status != 'Valid' " +
                "ORDER BY expiry_date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }

        } catch (SQLException e) {
            System.err.println("Error querying card expiry: "
                    + e.getMessage());
        }

        return results;
    }

    public Map<String, Object> getBranchActivityOverview(int branchId) {
        String sql = "SELECT * FROM vw_branch_activity WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            if (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                return row;
            }

        } catch (SQLException e) {
            System.err.println("Error querying branch activity: "
                    + e.getMessage());
        }

        return null;
    }

    // Internal helper to avoid repeating the same
    // ResultSet reading logic across every method
    private List<Map<String, Object>> queryView(String sql, int branchId, String errorMessage) {

        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }

        } catch (SQLException e) {
            System.err.println(errorMessage + ": " + e.getMessage());
        }

        return results;
    }
}