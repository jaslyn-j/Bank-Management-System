package com.bank.services;

import com.bank.dao.AccountDAO;
import com.bank.models.Account;

import java.util.List;
import java.util.UUID;

public class AccountService {

    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    // Submit a new account creation request for a customer
    public boolean requestNewAccount(int customerId, int branchId, String accountType) {
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setBranchId(branchId);
        account.setAccountType(accountType);
        account.setAccountNumber(generateAccountNumber());
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
    public boolean approveAccount(int accountId, int adminId) {
        return accountDAO.approveAccount(accountId, adminId);
    }

    // Admin declines an account creation request
    public boolean declineAccount(int accountId, int adminId) {
        return accountDAO.declineAccount(accountId, adminId);
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

    // Look up an account by its account number (used in fund transfers)
    public Account findAccountByNumber(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }

    // Look up an account by its ID
    public Account findAccountById(int accountId) {
        return accountDAO.getAccountById(accountId);
    }

    // Generates a unique 12-digit account number
    private String generateAccountNumber() {
        long number = Math.abs(UUID.randomUUID().getMostSignificantBits() % 900000000000L)
                + 100000000000L;
        return String.valueOf(number);
    }
}
