package com.bank.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private int accountId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime timestamp;

    public Transaction() {}

    public int           getTransactionId()   { return transactionId; }
    public int           getAccountId()       { return accountId; }
    public String        getTransactionType() { return transactionType; }
    public BigDecimal    getAmount()          { return amount; }
    public BigDecimal    getBalanceAfter()    { return balanceAfter; }
    public String        getDescription()     { return description; }
    public LocalDateTime getTimestamp()       { return timestamp; }

    public void setTransactionId(int transactionId)         { this.transactionId = transactionId; }
    public void setAccountId(int accountId)                 { this.accountId = accountId; }
    public void setTransactionType(String transactionType)  { this.transactionType = transactionType; }
    public void setAmount(BigDecimal amount)                { this.amount = amount; }
    public void setBalanceAfter(BigDecimal balanceAfter)    { this.balanceAfter = balanceAfter; }
    public void setDescription(String description)          { this.description = description; }
    public void setTimestamp(LocalDateTime timestamp)       { this.timestamp = timestamp; }
}