package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.Branch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {
    private Connection connection;

    public BranchDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Retrieve all branches (used to populate the branch selection screen)
    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();
        String sql = "SELECT * FROM Branch";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Branch branch = new Branch();
                branch.setBranchId(rs.getInt("branch_id"));
                branch.setBranchName(rs.getString("branch_name"));
                branch.setBranchCode(rs.getString("branch_code"));
                branch.setAddress(rs.getString("address"));
                branch.setPhone(rs.getString("phone"));
                branch.setEmail(rs.getString("email"));
                branches.add(branch);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching branches: " + e.getMessage());
        }

        return branches;
    }

    // Retrieve a single branch by its ID
    public Branch getBranchById(int branchId) {
        String sql = "SELECT * FROM Branch WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Branch branch = new Branch();
                branch.setBranchId(rs.getInt("branch_id"));
                branch.setBranchName(rs.getString("branch_name"));
                branch.setBranchCode(rs.getString("branch_code"));
                branch.setAddress(rs.getString("address"));
                branch.setPhone(rs.getString("phone"));
                branch.setEmail(rs.getString("email"));
                return branch;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching branch by ID: " + e.getMessage());
        }

        return null;
    }

    // Insert a new branch
    public boolean addBranch(Branch branch) {
        String sql = "INSERT INTO Branch (branch_name, branch_code, address, phone, email) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, branch.getBranchName());
            stmt.setString(2, branch.getBranchCode());
            stmt.setString(3, branch.getAddress());
            stmt.setString(4, branch.getPhone());
            stmt.setString(5, branch.getEmail());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding branch: " + e.getMessage());
        }

        return false;
    }
}
