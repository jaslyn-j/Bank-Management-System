package com.bank.services;

import com.bank.dao.CustomerDAO;
import com.bank.models.Customer;

import java.util.List;

public class CustomerService {

    private CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    // Retrieve all customers registered at a branch (admin view)
    public List<Customer> getCustomersForBranch(int branchId) {
        return customerDAO.getCustomersByBranch(branchId);
    }

    // Retrieve a single customer by their ID
    public Customer getCustomerById(int customerId, int branchId) {
        return customerDAO.getCustomerById(customerId, branchId);
    }

    // Admin blocks a customer account
    public boolean blockCustomer(int customerId) {
        return customerDAO.updateCustomerStatus(customerId, "blocked");
    }

    // Admin unblocks a customer account
    public boolean unblockCustomer(int customerId) {
        return customerDAO.updateCustomerStatus(customerId, "active");
    }

    // Admin deletes a customer record permanently
    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }
}