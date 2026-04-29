package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.AccountBalanceOverview;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountBalanceOverviewDAO {

    private Connection connection;

    public AccountBalanceOverviewDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public List<AccountBalanceOverview> getBalanceOverviewByBranch(int branchId) {
        List<AccountBalanceOverview> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_account_balance_overview " +
                "WHERE branch_id = ? " +
                "ORDER BY balance DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching balance overview: "
                    + e.getMessage());
        }

        return results;
    }

    public List<AccountBalanceOverview> getByBalanceCategory(
            int branchId, String category) {

        List<AccountBalanceOverview> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_account_balance_overview " +
                "WHERE branch_id = ? AND balance_category = ? ORDER BY balance DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching accounts by category: "
                    + e.getMessage());
        }

        return results;
    }

    public List<Object[]> getBalanceCategoryCounts(int branchId) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT balance_category, COUNT(*) AS total FROM vw_account_balance_overview WHERE branch_id = ? " +
                "GROUP BY balance_category ORDER BY total DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("balance_category"),
                        rs.getInt("total")
                });
            }

        } catch (SQLException e) {
            System.err.println("Error counting balance categories: "
                    + e.getMessage());
        }

        return results;
    }

    public AccountBalanceOverview getByAccountId(int accountId) {
        String sql = "SELECT * FROM vw_account_balance_overview " +
                "WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching balance overview by account: "
                    + e.getMessage());
        }

        return null;
    }

    private AccountBalanceOverview mapResultSet(ResultSet rs)
            throws SQLException {

        AccountBalanceOverview overview = new AccountBalanceOverview();
        overview.setAccountId(rs.getInt("account_id"));
        overview.setAccountNumber(rs.getString("account_number"));
        overview.setAccountType(rs.getString("account_type"));
        overview.setBalance(rs.getBigDecimal("balance"));
        overview.setStatus(rs.getString("status"));
        overview.setBranchId(rs.getInt("branch_id"));
        overview.setBalanceCategory(rs.getString("balance_category"));
        return overview;
    }

    public List<AccountBalanceOverview> getBalanceOverviewByCustomer(
            int customerId) {

        List<AccountBalanceOverview> results = new ArrayList<>();
        String sql = "SELECT v.* FROM vw_account_balance_overview v " +
                "INNER JOIN Account a ON v.account_id = a.account_id " +
                "WHERE a.customer_id = ? " +
                "ORDER BY v.balance DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching balance overview by customer: "
                    + e.getMessage());
        }

        return results;
    }
}