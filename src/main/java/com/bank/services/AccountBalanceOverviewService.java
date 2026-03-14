package com.bank.services;

import com.bank.dao.AccountBalanceOverviewDAO;
import com.bank.models.AccountBalanceOverview;

import java.util.List;

public class AccountBalanceOverviewService {

    private AccountBalanceOverviewDAO dao;

    public AccountBalanceOverviewService() {
        this.dao = new AccountBalanceOverviewDAO();
    }

    // Get full balance overview for all active accounts at a branch
    public List<AccountBalanceOverview> getBalanceOverview(int branchId) {
        return dao.getBalanceOverviewByBranch(branchId);
    }

    // Get accounts in a specific balance category
    // Valid categories: Zero Balance, Low Balance,
    // Normal Balance, Good Balance, High Balance
    public List<AccountBalanceOverview> getAccountsByCategory(
            int branchId, String category) {
        return dao.getByBalanceCategory(branchId, category);
    }

    // Get a count summary of how many accounts
    // fall into each balance category
    public List<Object[]> getBalanceCategoryCounts(int branchId) {
        return dao.getBalanceCategoryCounts(branchId);
    }

    // Get the balance overview for one specific account
    public AccountBalanceOverview getAccountBalanceOverview(int accountId) {
        return dao.getByAccountId(accountId);
    }

    // Get only zero balance accounts at a branch
    // Useful for admin to identify dormant accounts
    public List<AccountBalanceOverview> getZeroBalanceAccounts(int branchId) {
        return dao.getByBalanceCategory(branchId, "Zero Balance");
    }

    // Get only low balance accounts at a branch
    // Useful for admin to flag accounts that may need attention
    public List<AccountBalanceOverview> getLowBalanceAccounts(int branchId) {
        return dao.getByBalanceCategory(branchId, "Low Balance");
    }

    // Get only high balance accounts at a branch
    // Useful for admin to identify premium customers
    public List<AccountBalanceOverview> getHighBalanceAccounts(int branchId) {
        return dao.getByBalanceCategory(branchId, "High Balance");
    }

    public List<AccountBalanceOverview> getBalanceOverviewForCustomer(
            int customerId) {
        return dao.getBalanceOverviewByCustomer(customerId);
    }
}