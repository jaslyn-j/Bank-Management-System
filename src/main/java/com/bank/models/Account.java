package com.bank.models;

import java.math.BigDecimal;

public class Account {
    private int accountId;
    private int customerId;
    private int branchId;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private Integer approvedBy;

    public Account() {}

    public int           getAccountId()     { return accountId; }
    public int           getCustomerId()    { return customerId; }
    public int           getBranchId()      { return branchId; }
    public String        getAccountType()   { return accountType; }
    public BigDecimal    getBalance()       { return balance; }
    public String        getStatus()        { return status; }
    public Integer       getApprovedBy()    { return approvedBy; }

    public void setAccountId(int accountId)             { this.accountId = accountId; }
    public void setCustomerId(int customerId)           { this.customerId = customerId; }
    public void setBranchId(int branchId)               { this.branchId = branchId; }
    public void setAccountType(String accountType)      { this.accountType = accountType; }
    public void setBalance(BigDecimal balance)          { this.balance = balance; }
    public void setStatus(String status)                { this.status = status; }
    public void setApprovedBy(Integer approvedBy)       { this.approvedBy = approvedBy; }
}