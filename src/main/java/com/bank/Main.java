package com.bank;

import com.bank.dao.BranchDAO;
import com.bank.models.Branch;
import com.bank.models.Customer;
import com.bank.services.AuthService;
import com.bank.services.TransactionService;
import com.bank.services.AccountService;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Test 1 — Database connection and branch loading
        System.out.println("=== Test 1: Load Branches ===");
        BranchDAO branchDAO = new BranchDAO();
        List<Branch> branches = branchDAO.getAllBranches();
        branches.forEach(b -> System.out.println("Branch: " + b.getBranchName()));

        // Test 2 — Admin login
        System.out.println("\n=== Test 2: Admin Login ===");
        AuthService authService = new AuthService();
        boolean adminLogin = authService.loginAdmin("admin", "test123", 1);
        System.out.println("Admin login success: " + adminLogin);

        // Test 3 — Account creation request
        System.out.println("\n=== Test 3: Request New Account ===");
        AccountService accountService = new AccountService();
        boolean accountCreated = accountService.requestNewAccount(1, 1, "savings");
        System.out.println("Account request submitted: " + accountCreated);

        // Test 4 — Deposit
        System.out.println("\n=== Test 4: Deposit ===");
        TransactionService transactionService = new TransactionService();
        boolean deposited = transactionService.deposit(1, new BigDecimal("500.00"), "Initial deposit");
        System.out.println("Deposit success: " + deposited);
    }
}
