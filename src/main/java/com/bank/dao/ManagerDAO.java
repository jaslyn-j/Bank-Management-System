package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.Manager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ManagerDAO {
    private final Connection connection;

    public ManagerDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Find an admin by username and branch (used during login)
    public Manager getManagerByUsername(String username, int branchId) {
        String sql = "SELECT * FROM Manager WHERE username = ? AND branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToManager(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching manager by username: " + e.getMessage());
        }

        return null;
    }

    // Retrieve all managers belonging to a specific branch
    public List<Manager> getManagersByBranch(int branchId) {
        List<Manager> managers = new ArrayList<>();
        String sql = "SELECT * FROM Manager WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                managers.add(mapResultSetToManager(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching managers by branch: " + e.getMessage());
        }

        return managers;
    }

    // Insert a new admin
    public boolean addManager(Manager manager) {
        String sql = "INSERT INTO Manager (branch_id, full_name, username, password_hash) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, manager.getBranchId());
            stmt.setString(2, manager.getFullName());
            stmt.setString(3, manager.getUsername());
            stmt.setString(4, manager.getPasswordHash());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding manager: " + e.getMessage());
        }

        return false;
    }

    // Maps a ResultSet row to a Manager object
    private Manager mapResultSetToManager(ResultSet rs) throws SQLException {
        Manager manager = new Manager();
        manager.setManagerId(rs.getInt("manager_id"));
        manager.setBranchId(rs.getInt("branch_id"));
        manager.setFullName(rs.getString("full_name"));
        manager.setUsername(rs.getString("username"));
        manager.setPasswordHash(rs.getString("password_hash"));


        return manager;
    }
}
