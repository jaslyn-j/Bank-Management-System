package com.bank.services;
import com.bank.dao.ManagerDAO;
import com.bank.dao.CustomerDAO;
import com.bank.db.Session;
import com.bank.models.Manager;
import com.bank.models.Customer;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private CustomerDAO customerDAO;
    private ManagerDAO managerDAO;

    public AuthService() {
        this.customerDAO = new CustomerDAO();
        this.managerDAO    = new ManagerDAO();
    }

    public boolean loginCustomer(
            int customerId,
            String plainPassword,
            int branchId) {

        Customer customer =
                customerDAO.getCustomerById(
                        customerId, branchId);

        if (customer == null) {
            System.err.println(
                    "Customer not found.");
            return false;
        }

        if (customer.getStatus()
                .equals("blocked")) {
            System.err.println(
                    "Customer account blocked.");
            return false;
        }

        // Verify password against stored hash
        if (!BCrypt.checkpw(
                plainPassword,
                customer.getPasswordHash())) {
            System.err.println(
                    "Invalid password.");
            return false;
        }

        Session.getInstance()
                .setLoggedInCustomer(customer);
        Session.getInstance()
                .setUserMode("CUSTOMER");
        return true;
    }

    // Authenticate an admin login attempt
    public boolean loginManager(String username, String plainPassword, int branchId) {

        Manager manager = managerDAO.getManagerByUsername(username, branchId);

        if (manager== null) {
            return false;
        }

        // For admins, support both BCrypt hashed and plain text passwords
        // Plain text is only used for initial seed data — should be hashed in production
        boolean passwordMatches;
        String storedHash = manager.getPasswordHash();

        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$")) {
            passwordMatches = BCrypt.checkpw(plainPassword, storedHash);
        } else {
            passwordMatches = storedHash.equals(plainPassword);
        }

        if (!passwordMatches) {
            return false;
        }

        // Store the logged in admin in the session
        Session.getInstance().setLoggedInManager(manager);
        Session.getInstance().setUserMode("MANAGER");
        return true;
    }

    // Register a new customer — hashes password before storing
    public int registerCustomer(
            Customer customer,
            String plainPassword) {
        String hashedPassword =
                BCrypt.hashpw(
                        plainPassword,
                        BCrypt.gensalt());
        customer.setPasswordHash(
                hashedPassword);
        return customerDAO
                .registerCustomer(customer);
    }

    // Hash a plain text password
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Verify a plain text password against a stored hash
    public boolean checkPassword(String plainPassword, String storedHash) {
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    // Log out the current user and clear the session
    public void logout() {
        Session.getInstance().clearSession();
    }
}
