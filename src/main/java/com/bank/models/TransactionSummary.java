package com.bank.models;

import java.math.BigDecimal;

public class TransactionSummary {

    private int        accountId;
    private int        totalTransactions;
    private BigDecimal totalDeposited;
    private BigDecimal totalWithdrawn;
    private BigDecimal totalTransferredOut;
    private BigDecimal totalTransferredIn;

    public TransactionSummary() {}

    public int        getAccountId()           { return accountId; }
    public int        getTotalTransactions()   { return totalTransactions; }
    public BigDecimal getTotalDeposited()      { return totalDeposited; }
    public BigDecimal getTotalWithdrawn()      { return totalWithdrawn; }
    public BigDecimal getTotalTransferredOut() { return totalTransferredOut; }
    public BigDecimal getTotalTransferredIn()  { return totalTransferredIn; }

    public void setAccountId(int accountId)                   { this.accountId = accountId; }
    public void setTotalTransactions(int totalTransactions)   { this.totalTransactions = totalTransactions; }
    public void setTotalDeposited(BigDecimal v)               { this.totalDeposited = v; }
    public void setTotalWithdrawn(BigDecimal v)               { this.totalWithdrawn = v; }
    public void setTotalTransferredOut(BigDecimal v)          { this.totalTransferredOut = v; }
    public void setTotalTransferredIn(BigDecimal v)           { this.totalTransferredIn = v; }
}