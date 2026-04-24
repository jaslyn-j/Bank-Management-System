package com.bank.services;

import com.bank.dao.AccountDAO;
import com.bank.dao.ApprovalDAO;
import com.bank.models.Account;
import com.bank.models.CustomerFinancialSummary;
import java.util.List;
import com.bank.models.PendingApproval;

import java.util.List;
import java.util.UUID;

public class AccountService {

    private AccountDAO accountDAO;
    private ApprovalDAO approvalDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    // Submit a new account creation request for a customer
    public boolean requestNewAccount(int customerId, int branchId, String accountType) {
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setBranchId(branchId);
        account.setAccountType(accountType);
        return accountDAO.createAccount(account);
    }

    // Retrieve all active accounts for a customer
    public List<Account> getActiveAccountsForCustomer(int customerId) {
        List<Account> all = accountDAO.getAccountsByCustomer(customerId);
        all.removeIf(a -> !a.getStatus().equals("active"));
        return all;
    }

    // Retrieve all accounts for a customer regardless of status
    public List<Account> getAllAccountsForCustomer(int customerId) {
        return accountDAO.getAccountsByCustomer(customerId);
    }

    // Retrieve all accounts for a branch (admin view)
    public List<Account> getAllAccountsForBranch(int branchId) {
        return accountDAO.getAccountsByBranch(branchId);
    }

    // Retrieve all pending account requests for a branch (admin approval queue)
    public List<Account> getPendingAccountsForBranch(int branchId) {
        return accountDAO.getPendingAccountsByBranch(branchId);
    }

    // Admin approves an account creation request
    public boolean approveAccount(int accountId, int managerId) {
        return accountDAO.approveAccount(accountId, managerId);
    }

    // Admin declines an account creation request
    public boolean declineAccount(int accountId, int managerId) {
        return accountDAO.declineAccount(accountId, managerId);
    }

    // Admin blocks an account
    public boolean blockAccount(int accountId) {
        return accountDAO.updateAccountStatus(accountId, "blocked");
    }

    // Admin unblocks an account
    public boolean unblockAccount(int accountId) {
        return accountDAO.updateAccountStatus(accountId, "active");
    }

    // Admin deletes an account
    public boolean deleteAccount(int accountId) {
        return accountDAO.deleteAccount(accountId);
    }

    public String formatAccountNumber(int accountId) {
        return String.format("ACC%09d", accountId);
    }

    // Look up an account by its ID
    public Account findAccountById(int accountId) {
        return accountDAO.getAccountById(accountId);
    }

    // Get financial summary for all customers at a branch
    public List<CustomerFinancialSummary> getCustomerFinancialSummary(int branchId) {
        return accountDAO.getCustomerFinancialSummary(branchId);
    }

    // Add this new method — does not replace getPendingAccountsForBranch()
    // it is an additional method that returns richer data
    public List<PendingApproval> getPendingApprovalRequests(int branchId) {
        return approvalDAO.getPendingAccountRequestsForBranch(branchId);
    }

    public List<Account> getDormantAccounts(int branchId) {
        return accountDAO.getDormantAccounts(branchId);
    }

    // Accepts a formatted string like "ACC000000007"
// Parses it back to the raw account_id
// and returns the matching account
    public Account findAccountByFormattedNumber(
            String formattedNumber) {
        try {
            int id = Integer.parseInt(
                    formattedNumber
                            .replace("ACC", "")
                            .trim());
            return accountDAO.getAccountById(id);
        } catch (NumberFormatException e) {
            System.err.println(
                    "Invalid account number format: "
                            + formattedNumber);
            return null;
        }
    }

}

