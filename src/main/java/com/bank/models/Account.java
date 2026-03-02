package com.bank.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private int accountId;
    private int customerId;
    private int branchId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
    private Integer approvedBy;
    private LocalDateTime approvedAt;

    public Account() {}

    public int           getAccountId()     { return accountId; }
    public int           getCustomerId()    { return customerId; }
    public int           getBranchId()      { return branchId; }
    public String        getAccountNumber() { return accountNumber; }
    public String        getAccountType()   { return accountType; }
    public BigDecimal    getBalance()       { return balance; }
    public String        getStatus()        { return status; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public Integer       getApprovedBy()    { return approvedBy; }
    public LocalDateTime getApprovedAt()    { return approvedAt; }

    public void setAccountId(int accountId)             { this.accountId = accountId; }
    public void setCustomerId(int customerId)           { this.customerId = customerId; }
    public void setBranchId(int branchId)               { this.branchId = branchId; }
    public void setAccountNumber(String accountNumber)  { this.accountNumber = accountNumber; }
    public void setAccountType(String accountType)      { this.accountType = accountType; }
    public void setBalance(BigDecimal balance)          { this.balance = balance; }
    public void setStatus(String status)                { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }
    public void setApprovedBy(Integer approvedBy)       { this.approvedBy = approvedBy; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
}