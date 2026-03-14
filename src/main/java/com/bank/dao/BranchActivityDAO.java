package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.BranchActivity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchActivityDAO {

    private Connection connection;

    public BranchActivityDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Retrieve activity summary for a specific branch
    // Returns a single BranchActivity object with all statistics
    public BranchActivity getBranchActivity(int branchId) {
        String sql = "SELECT * FROM vw_branch_activity " +
                "WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching branch activity: "
                    + e.getMessage());
        }

        return null;
    }

    // Retrieve activity summary for all branches
    // Useful if a super admin view is ever added
    public List<BranchActivity> getAllBranchActivity() {
        List<BranchActivity> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_branch_activity " +
                "ORDER BY branch_name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all branch activity: "
                    + e.getMessage());
        }

        return results;
    }

    // Retrieve only branches that have pending items
    // or open fraud alerts requiring attention
    public List<BranchActivity> getBranchesRequiringAttention() {
        List<BranchActivity> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_branch_activity " +
                "WHERE pending_accounts > 0 " +
                "OR pending_cards > 0 " +
                "OR open_fraud_alerts > 0 " +
                "ORDER BY open_fraud_alerts DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching branches requiring attention: "
                    + e.getMessage());
        }

        return results;
    }

    // Get only the pending counts for a branch
    // Lightweight query used for notification badges
    public int getTotalPendingItems(int branchId) {
        String sql = "SELECT pending_accounts + pending_cards " +
                "AS total_pending " +
                "FROM vw_branch_activity " +
                "WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_pending");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching total pending items: "
                    + e.getMessage());
        }

        return 0;
    }

    // Get only the open fraud alert count for a branch
    // Lightweight query used for fraud alert badge
    public int getOpenFraudAlertCount(int branchId) {
        String sql = "SELECT open_fraud_alerts " +
                "FROM vw_branch_activity " +
                "WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("open_fraud_alerts");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching fraud alert count: "
                    + e.getMessage());
        }

        return 0;
    }

    // Maps a ResultSet row to a BranchActivity object
    private BranchActivity mapResultSet(ResultSet rs)
            throws SQLException {

        BranchActivity activity = new BranchActivity();
        activity.setBranchId(rs.getInt("branch_id"));
        activity.setBranchName(rs.getString("branch_name"));
        activity.setBranchCode(rs.getString("branch_code"));
        activity.setTotalCustomers(rs.getInt("total_customers"));
        activity.setTotalAccounts(rs.getInt("total_accounts"));
        activity.setTotalCards(rs.getInt("total_cards"));
        activity.setTotalBranchBalance(rs.getBigDecimal("total_branch_balance"));
        activity.setPendingAccounts(rs.getInt("pending_accounts"));
        activity.setPendingCards(rs.getInt("pending_cards"));
        activity.setOpenFraudAlerts(rs.getInt("open_fraud_alerts"));
        return activity;
    }
}