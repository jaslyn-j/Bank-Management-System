package com.bank.services;

import com.bank.db.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcedureService {

    private static Connection connection;

    public ProcedureService() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // CURSOR 1 — Block all active cards for a customer
    public int blockCustomerCards(int customerId) {
        String sql = "{CALL sp_block_customer_cards(?)}";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, customerId);
            stmt.execute();
            return countBlockedCardsForCustomer(customerId);

        } catch (SQLException e) {
            System.err.println("Error blocking customer cards: "
                    + e.getMessage());
        }

        return 0;
    }

    // CURSOR 2 — Close all zero balance accounts at a branch
    public int closeZeroBalanceAccounts(int branchId) {
        String sql = "{CALL sp_close_zero_balance_accounts(?)}";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("accounts_closed");
            }

        } catch (SQLException e) {
            System.err.println("Error closing zero balance accounts: "
                    + e.getMessage());
        }

        return 0;
    }

    // CURSOR 3 — Dismiss all reviewed fraud alerts for a branch
    public static int dismissReviewedAlerts(int branchId) {
        String sql = "{CALL sp_dismiss_reviewed_alerts(?)}";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("alerts_dismissed");
            }

        } catch (SQLException e) {
            System.err.println("Error dismissing reviewed alerts: "
                    + e.getMessage());
        }

        return 0;
    }

    // CURSOR 4 — Apply monthly interest to savings accounts
    public int applyMonthlyInterest(int branchId) {
        String sql = "{CALL sp_apply_monthly_interest(?)}";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, branchId);
            stmt.execute();
            return countInterestTransactionsToday(branchId);

        } catch (SQLException e) {
            System.err.println("Error applying monthly interest: "
                    + e.getMessage());
        }

        return 0;
    }

    // Helper — count blocked cards for a customer
    private int countBlockedCardsForCustomer(int customerId) {
        String sql = "SELECT COUNT(*) AS total FROM Card cd " +
                "INNER JOIN Account a ON cd.account_id = a.account_id " +
                "WHERE a.customer_id = ? AND cd.status = 'blocked'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error counting blocked cards: "
                    + e.getMessage());
        }

        return 0;
    }

    // Helper — count interest transactions applied today
    private int countInterestTransactionsToday(int branchId) {
        String sql = "SELECT COUNT(*) AS total " +
                "FROM Transaction t " +
                "INNER JOIN Account a ON t.account_id = a.account_id " +
                "WHERE a.branch_id = ? " +
                "AND t.description = 'Monthly interest applied' " +
                "AND DATE(t.timestamp) = CURDATE()";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error counting interest transactions: "
                    + e.getMessage());
        }

        return 0;
    }
}