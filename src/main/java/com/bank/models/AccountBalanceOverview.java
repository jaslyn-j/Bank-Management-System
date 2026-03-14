package com.bank.models;

import java.math.BigDecimal;

public class AccountBalanceOverview {

    private int        accountId;
    private String     accountNumber;
    private String     accountType;
    private BigDecimal balance;
    private String     status;
    private int        branchId;
    private String     balanceCategory;

    public AccountBalanceOverview() {}

    public int        getAccountId()       { return accountId; }
    public String     getAccountNumber()   { return accountNumber; }
    public String     getAccountType()     { return accountType; }
    public BigDecimal getBalance()         { return balance; }
    public String     getStatus()          { return status; }
    public int        getBranchId()        { return branchId; }
    public String     getBalanceCategory() { return balanceCategory; }

    public void setAccountId(int accountId)             { this.accountId = accountId; }
    public void setAccountNumber(String accountNumber)  { this.accountNumber = accountNumber; }
    public void setAccountType(String accountType)      { this.accountType = accountType; }
    public void setBalance(BigDecimal balance)          { this.balance = balance; }
    public void setStatus(String status)                { this.status = status; }
    public void setBranchId(int branchId)               { this.branchId = branchId; }
    public void setBalanceCategory(String balanceCategory) {
        this.balanceCategory = balanceCategory;
    }
}