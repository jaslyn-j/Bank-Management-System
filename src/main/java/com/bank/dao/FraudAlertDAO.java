package com.bank.dao;
import com.bank.db.DBConnection;
import com.bank.models.FraudAlert;
import com.bank.models.FraudRule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class FraudAlertDAO {
    private Connection connection;

    public FraudAlertDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public boolean addAlert(FraudAlert alert) {
        String sql = "INSERT INTO FraudAlert (account_id, transaction_id, alert_reason, severity, status, time_stamp) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, alert.getAccountId());

            if (alert.getTransactionId() != null) {
                stmt.setInt(2, alert.getTransactionId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, alert.getAlertReason());
            stmt.setString(4, alert.getSeverity());
            stmt.setString(5, alert.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding fraud alert: " + e.getMessage());
        }

        return false;
    }

    public List<FraudAlert> getOpenAlertsByBranch(int branchId) {
        List<FraudAlert> alerts = new ArrayList<>();
        String sql = "SELECT fa.* FROM FraudAlert fa JOIN Account a ON fa.account_id = a.account_id " +
                "WHERE a.branch_id = ? AND fa.status = 'open'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching open alerts: " + e.getMessage());
        }

        return alerts;
    }

    public List<FraudAlert> getAllAlertsByBranch(int branchId) {
        List<FraudAlert> alerts = new ArrayList<>();
        String sql = "SELECT fa.* FROM FraudAlert fa JOIN Account a ON fa.account_id = a.account_id WHERE a.branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all alerts: " + e.getMessage());
        }

        return alerts;
    }

    public List<FraudAlert> getAlertsByAccount(int accountId) {
        List<FraudAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM FraudAlert WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching alerts by account: " + e.getMessage());
        }

        return alerts;
    }

    public boolean updateAlertStatus(int alertId, String status, int managerId) {
        String sql = "UPDATE FraudAlert SET status = ?, reviewed_by = ?, reviewed_at = CURRENT_TIMESTAMP WHERE alert_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, managerId);
            stmt.setInt(3, alertId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating alert status: " + e.getMessage());
        }

        return false;
    }

    public int getOpenAlertCount(int branchId) {
        String sql = "SELECT COUNT(*) FROM FraudAlert fa JOIN Account a ON fa.account_id = a.account_id WHERE a.branch_id = ? AND fa.status = 'open'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting open alerts: " + e.getMessage());
        }

        return 0;
    }

    public FraudRule getFraudRuleByBranch(int branchId) {
        String sql = "SELECT * FROM FraudRule WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                FraudRule rule = new FraudRule();
                rule.setRuleId(rs.getInt("rule_id"));
                rule.setBranchId(rs.getInt("branch_id"));
                rule.setLargeTxMultiplier(rs.getBigDecimal("large_tx_multiplier"));
                rule.setRapidTxLimit(rs.getInt("rapid_tx_limit"));
                rule.setRapidTxWindowMins(rs.getInt("rapid_tx_window_mins"));
                rule.setLargeTxMinAmount(rs.getBigDecimal("large_tx_min_amount"));
                rule.setDailyLimit(rs.getBigDecimal("daily_limit"));
                return rule;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching fraud rules: " + e.getMessage());
        }

        return null;
    }

    public int countRecentTransactions(int accountId, int withinMinutes) {
        String sql = "SELECT COUNT(*) FROM Transaction " +
                "WHERE account_id = ? AND time_stamp >= NOW() - INTERVAL ? MINUTE";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setInt(2, withinMinutes);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting recent transactions: " + e.getMessage());
        }

        return 0;
    }

    public java.math.BigDecimal getAverageTransactionAmount(int accountId) {
        String sql = "SELECT AVG(amount) FROM Transaction WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                java.math.BigDecimal avg = rs.getBigDecimal(1);
                return avg != null ? avg : java.math.BigDecimal.ZERO;
            }

        } catch (SQLException e) {
            System.err.println("Error calculating average transaction: " + e.getMessage());
        }

        return java.math.BigDecimal.ZERO;
    }

    public java.math.BigDecimal getDailyTransactionTotal(int accountId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM Transaction " +
                "WHERE account_id = ? AND DATE(time_stamp) = CURDATE() AND transaction_type IN ('withdrawal', 'transfer_out')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            System.err.println("Error calculating daily total: " + e.getMessage());
        }

        return java.math.BigDecimal.ZERO;
    }

    public List<FraudAlert> getActiveAlerts(int branchId) {
        List<FraudAlert> results = new ArrayList<>();
        String sql =
                "SELECT fa.* FROM FraudAlert fa " +
                        "INNER JOIN Account a ON fa.account_id = a.account_id " +
                        "WHERE a.branch_id = ? " +
                        "AND fa.status = 'open' " +

                        "UNION " +

                        "SELECT fa.* FROM FraudAlert fa " +
                        "INNER JOIN Account a ON fa.account_id = a.account_id " +
                        "WHERE a.branch_id = ? " +
                        "AND fa.status = 'reviewed' ";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.setInt(2, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToAlert(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching active alerts: "
                    + e.getMessage());
        }

        return results;
    }

    private FraudAlert mapResultSetToAlert(ResultSet rs) throws SQLException {
        FraudAlert alert = new FraudAlert();
        alert.setAlertId(rs.getInt("alert_id"));
        alert.setAccountId(rs.getInt("account_id"));
        alert.setAlertReason(rs.getString("alert_reason"));
        alert.setSeverity(rs.getString("severity"));
        alert.setStatus(rs.getString("status"));
        java.sql.Timestamp flaggedAt =
                rs.getTimestamp("time_stamp");
        if (flaggedAt != null) {
            alert.setFlaggedAt(
                    flaggedAt.toLocalDateTime());
        }
        int transactionId = rs.getInt("transaction_id");
        if (!rs.wasNull()) alert.setTransactionId(transactionId);

        int reviewedBy = rs.getInt("reviewed_by");
        if (!rs.wasNull()) alert.setReviewedBy(reviewedBy);

        Timestamp reviewedAt = rs.getTimestamp("reviewed_at");
        if (reviewedAt != null) alert.setReviewedAt(reviewedAt.toLocalDateTime());

        return alert;
    }
    public List<Integer>
    getUnblockedAccountsWithAlerts(
            int branchId) {
        List<Integer> accountIds =
                new ArrayList<>();

        String sql =
                "SELECT DISTINCT fa.account_id FROM FraudAlert fa "
                 + "INNER JOIN Account a ON fa.account_id = a.account_id "
                 + "WHERE a.branch_id = ? AND fa.status = 'open' AND a.status != 'blocked'";

        try (PreparedStatement stmt =connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accountIds.add(rs.getInt("account_id"));
            }
        } catch (SQLException e) {
            System.err.println( "Error getting unblocked accounts with alerts: " + e.getMessage());
        }
        return accountIds;
    }
}
