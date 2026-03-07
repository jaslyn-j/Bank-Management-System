package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.Account;

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

    // Retrieve a single account by account number
    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM Account WHERE account_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching account by number: " + e.getMessage());
        }

        return null;
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
        String sql = "INSERT INTO Account (customer_id, branch_id, account_number, " +
                "account_type, balance, status) VALUES (?, ?, ?, ?, 0.00, 'pending')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, account.getCustomerId());
            stmt.setInt(2, account.getBranchId());
            stmt.setString(3, account.getAccountNumber());
            stmt.setString(4, account.getAccountType());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
        }

        return false;
    }

    // Approve an account — admin sets status to active
    public boolean approveAccount(int accountId, int adminId) {
        String sql = "UPDATE Account SET status = 'active', approved_by = ?, " +
                "approved_at = CURRENT_TIMESTAMP WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            stmt.setInt(2, accountId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error approving account: " + e.getMessage());
        }

        return false;
    }

    // Decline an account request — admin sets status to closed
    public boolean declineAccount(int accountId, int adminId) {
        String sql = "UPDATE Account SET status = 'closed', approved_by = ?, " +
                "approved_at = CURRENT_TIMESTAMP WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
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
        account.setAccountNumber(rs.getString("account_number"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(rs.getString("status"));


        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            account.setApprovedBy(approvedBy);
        }


        return account;
    }
}
