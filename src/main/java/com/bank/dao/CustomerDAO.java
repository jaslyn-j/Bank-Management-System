package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CustomerDAO {
    private final Connection connection;

    public CustomerDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public Customer getCustomerByEmail(String email, int branchId) {
        String sql = "SELECT * FROM Customer WHERE email = ? AND branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, branchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer by email: " + e.getMessage());
        }

        return null;
    }

    public Customer getCustomerById(
            int customerId,
            int branchId) {

        String sql =
                "SELECT * FROM Customer WHERE customer_id = ? AND branch_id = ?";

        try (PreparedStatement stmt =
                     connection.prepareStatement(
                             sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, branchId);

            ResultSet rs =
                    stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCustomer(
                        rs);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error fetching customer "
                            + "by ID: "
                            + e.getMessage());
        }
        return null;
    }

    public List<Customer> getCustomersByBranch(int branchId) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customers by branch: " + e.getMessage());
        }

        return customers;
    }

    public int registerCustomer(
            Customer customer) {
        String sql =
                "INSERT INTO Customer (branch_id, first_name, last_name, email, phone, national_id, street, city, state, pin_code, "
                        + "password_hash, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'active')";

        try (PreparedStatement stmt = connection.prepareStatement( sql,PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1,customer.getBranchId());
            stmt.setString(2,  customer.getFirstName());
            stmt.setString(3,customer.getLastName());
            stmt.setString(4,customer.getEmail());
            stmt.setString(5,customer.getPhone());
            stmt.setString(6,customer.getNationalId());
            stmt.setString(7, customer.getStreet());
            stmt.setString(8,customer.getCity());
            stmt.setString(9,customer.getState());
            stmt.setString(10,customer.getPinCode());
            stmt.setString(11,customer.getPasswordHash());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet generatedKeys =
                        stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys
                            .getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error registering customer: "+ e.getMessage());
        }
        return -1;
    }

    public boolean updateCustomerStatus(int customerId, String status) {
        String sql = "UPDATE Customer SET status = ? WHERE customer_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, customerId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating customer status: " + e.getMessage());
        }

        return false;
    }

    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM Customer WHERE customer_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
        }

        return false;
    }

    private Customer mapResultSetToCustomer(
            ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setBranchId( rs.getInt("branch_id"));
        customer.setFirstName( rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail( rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setNationalId(rs.getString("national_id"));
        customer.setPasswordHash( rs.getString("password_hash"));
        customer.setStatus( rs.getString("status"));

        customer.setStreet(rs.getString("street"));
        customer.setCity(rs.getString("city"));
        customer.setState( rs.getString("state"));
        customer.setPinCode(rs.getString("pin_code"));

        java.sql.Date dob =
                rs.getDate("date_of_birth");
        if (dob != null) {
            customer.setDateOfBirth(
                    dob.toLocalDate());
        }

        return customer;
    }
}
