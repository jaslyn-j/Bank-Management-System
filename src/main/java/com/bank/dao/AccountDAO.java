package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.Account;
import com.bank.models.CustomerFinancialSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AccountDAO {
    private final Connection connection;

    public AccountDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Retrieve all accounts for a specific customer
    public List<Account> getAccountsByCustomer(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Account WHERE customer_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching accounts by customer: " + e.getMessage());
        }

        return accounts;
    }

    // Retrieve all accounts for a specific branch (used by admin)
    public List<Account> getAccountsByBranch(int branchId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Account WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching accounts by branch: " + e.getMessage());
        }

        return accounts;
    }

    // Retrieve all pending account creation requests for a branch
    public List<Account> getPendingAccountsByBranch(int branchId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Account WHERE branch_id = ? AND status = 'pending'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching pending accounts: " + e.getMessage());
        }

        return accounts;
    }

    public Account getAccountByFormattedNumber(String formattedNumber) {
        try {
            // Strip the "ACC" prefix and parse the integer
            int accountId = Integer.parseInt(formattedNumber.replace("ACC", "").trim());
            return getAccountById(accountId);
        } catch (NumberFormatException e) {
            System.err.println("Invalid formatted account number: " + formattedNumber);
            return null;
        }
    }

    // Retrieve a single account by its ID
    public Account getAccountById(int accountId) {
        String sql = "SELECT * FROM Account WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching account by ID: " + e.getMessage());
        }

        return null;
    }

    // Insert a new account creation request (starts as pending)
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO Account (customer_id, branch_id, " +
                "account_type, balance, status) VALUES (?, ?, ?, 0.00, 'pending')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, account.getCustomerId());
            stmt.setInt(2, account.getBranchId());
            stmt.setString(3, account.getAccountType());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
        }

        return false;
    }

    // Approve an account — admin sets status to active
    public boolean approveAccount(int accountId, int managerId) {
        String sql = "UPDATE Account SET status = 'active', approved_by = ? WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            stmt.setInt(2, accountId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error approving account: " + e.getMessage());
        }

        return false;
    }

    // Decline an account request — admin sets status to closed
    public boolean declineAccount(int accountId, int managerId) {
        String sql = "UPDATE Account SET status = 'closed', approved_by = ? WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            stmt.setInt(2, accountId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error declining account: " + e.getMessage());
        }

        return false;
    }

    // Update account status — used for blocking and unblocking
    public boolean updateAccountStatus(int accountId, String status) {
        String sql = "UPDATE Account SET status = ? WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, accountId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating account status: " + e.getMessage());
        }

        return false;
    }

    // Update account balance — called by TransactionDAO during deposits and withdrawals
    public boolean updateBalance(int accountId, java.math.BigDecimal newBalance) {
        String sql = "UPDATE Account SET balance = ? WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
        }

        return false;
    }

    // Delete an account permanently
    public boolean deleteAccount(int accountId) {
        String sql = "DELETE FROM Account WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
        }

        return false;
    }

    // Maps a ResultSet row to an Account object
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setBranchId(rs.getInt("branch_id"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(rs.getString("status"));


        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            account.setApprovedBy(approvedBy);
        }


        return account;
    }

    // Retrieves financial summary for all customers at a branch
// Uses aggregate functions COUNT, SUM, AVG, MAX, MIN
// with a multi-table JOIN across Customer and Account
    public List<CustomerFinancialSummary> getCustomerFinancialSummary(int branchId) {
        List<CustomerFinancialSummary> summaries = new ArrayList<>();

        String sql = "SELECT c.customer_id, c.first_name, c.last_name, " +
                "COUNT(a.account_id)  AS total_accounts, SUM(a.balance) AS total_balance, AVG(a.balance) AS average_balance, " +
                "MAX(a.balance) AS highest_balance, MIN(a.balance) AS lowest_balance " +
                "FROM Customer c INNER JOIN Account a ON c.customer_id = a.customer_id WHERE c.branch_id = ? " +
                "AND a.status = 'active' " +
                "GROUP BY c.customer_id, c.first_name, c.last_name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerFinancialSummary summary = new CustomerFinancialSummary();
                summary.setCustomerId(rs.getInt("customer_id"));
                summary.setFirstName(rs.getString("first_name"));
                summary.setLastName(rs.getString("last_name"));
                summary.setTotalAccounts(rs.getInt("total_accounts"));
                summary.setTotalBalance(rs.getBigDecimal("total_balance"));
                summary.setAverageBalance(rs.getBigDecimal("average_balance"));
                summary.setHighestBalance(rs.getBigDecimal("highest_balance"));
                summary.setLowestBalance(rs.getBigDecimal("lowest_balance"));
                summaries.add(summary);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer financial summary: "
                    + e.getMessage());
        }

        return summaries;
    }
}
