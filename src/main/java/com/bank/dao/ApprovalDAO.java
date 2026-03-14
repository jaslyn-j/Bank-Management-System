package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.PendingApproval;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApprovalDAO {

    private Connection connection;

    public ApprovalDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Retrieves all pending account and card requests
    // for a branch in one unified list using UNION ALL.
    // The first SELECT gets pending account requests,
    // the second SELECT gets pending card requests,
    // UNION ALL combines both result sets together.
    public List<PendingApproval> getPendingApprovalsForBranch(int branchId) {
        List<PendingApproval> approvals = new ArrayList<>();

        String sql =
                "SELECT " +
                        "'Account Request' AS request_type, " +
                        "a.account_id AS request_id, " +
                        "CONCAT('ACC', LPAD(a.account_id, 9, '0')) AS reference, " +
                        "c.first_name, " +
                        "c.last_name, " +
                        "a.account_type AS detail, " +
                        "a.branch_id " +
                        "FROM Account a " +
                        "INNER JOIN Customer c ON a.customer_id = c.customer_id " +
                        "WHERE a.status = 'pending' " +
                        "AND a.branch_id = ? " +

                        "UNION ALL " +

                        "SELECT " +
                        "'Card Request' AS request_type, " +
                        "cd.card_id AS request_id, " +
                        "cd.card_number AS reference, " +
                        "c.first_name, " +
                        "c.last_name, " +
                        "cd.card_type AS detail, " +
                        "a.branch_id " +
                        "FROM Card cd " +
                        "INNER JOIN Account  a ON cd.account_id = a.account_id " +
                        "INNER JOIN Customer c ON a.customer_id = c.customer_id " +
                        "WHERE cd.status = 'pending' " +
                        "AND a.branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Two ? placeholders, one for each SELECT in the UNION
            stmt.setInt(1, branchId);
            stmt.setInt(2, branchId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PendingApproval approval = new PendingApproval();
                approval.setRequestType(rs.getString("request_type"));
                approval.setRequestId(rs.getInt("request_id"));
                approval.setReference(rs.getString("reference"));
                approval.setFirstName(rs.getString("first_name"));
                approval.setLastName(rs.getString("last_name"));
                approval.setDetail(rs.getString("detail"));
                approval.setBranchId(rs.getInt("branch_id"));
                approvals.add(approval);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching pending approvals: "
                    + e.getMessage());
        }

        return approvals;
    }

    // Retrieves only pending account requests for a branch
    // using just the first half of the UNION query
    public List<PendingApproval> getPendingAccountRequestsForBranch(int branchId) {
        List<PendingApproval> approvals = new ArrayList<>();

        String sql =
                "SELECT " +
                        "'Account Request' AS request_type, " +
                        "a.account_id AS request_id, " +
                        "CONCAT('ACC', LPAD(a.account_id, 9, '0')) AS reference, " +
                        "c.first_name, " +
                        "c.last_name, " +
                        "a.account_type AS detail, " +
                        "a.branch_id " +
                        "FROM Account a " +
                        "INNER JOIN Customer c ON a.customer_id = c.customer_id " +
                        "WHERE a.status = 'pending' " +
                        "AND a.branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PendingApproval approval = new PendingApproval();
                approval.setRequestType(rs.getString("request_type"));
                approval.setRequestId(rs.getInt("request_id"));
                approval.setReference(rs.getString("reference"));
                approval.setFirstName(rs.getString("first_name"));
                approval.setLastName(rs.getString("last_name"));
                approval.setDetail(rs.getString("detail"));
                approval.setBranchId(rs.getInt("branch_id"));
                approvals.add(approval);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching pending account requests: "
                    + e.getMessage());
        }

        return approvals;
    }

    // Retrieves only pending card requests for a branch
    // using just the second half of the UNION query
    public List<PendingApproval> getPendingCardRequestsForBranch(int branchId) {
        List<PendingApproval> approvals = new ArrayList<>();

        String sql =
                "SELECT " +
                        "'Card Request' AS request_type, " +
                        "cd.card_id AS request_id, " +
                        "cd.card_number AS reference, " +
                        "c.first_name, " +
                        "c.last_name, " +
                        "cd.card_type AS detail, " +
                        "a.branch_id " +
                        "FROM Card cd " +
                        "INNER JOIN Account  a ON cd.account_id = a.account_id " +
                        "INNER JOIN Customer c ON a.customer_id = c.customer_id " +
                        "WHERE cd.status = 'pending' " +
                        "AND a.branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PendingApproval approval = new PendingApproval();
                approval.setRequestType(rs.getString("request_type"));
                approval.setRequestId(rs.getInt("request_id"));
                approval.setReference(rs.getString("reference"));
                approval.setFirstName(rs.getString("first_name"));
                approval.setLastName(rs.getString("last_name"));
                approval.setDetail(rs.getString("detail"));
                approval.setBranchId(rs.getInt("branch_id"));
                approvals.add(approval);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching pending card requests: "
                    + e.getMessage());
        }

        return approvals;
    }
}