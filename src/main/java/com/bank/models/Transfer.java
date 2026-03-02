package com.bank.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transfer {
    private int transferId;
    private int fromAccountId;
    private int toAccountId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String status;

    public Transfer() {}

    public int           getTransferId()     { return transferId; }
    public int           getFromAccountId()  { return fromAccountId; }
    public int           getToAccountId()    { return toAccountId; }
    public BigDecimal    getAmount()         { return amount; }
    public LocalDateTime getTimestamp()      { return timestamp; }
    public String        getStatus()         { return status; }

    public void setTransferId(int transferId)           { this.transferId = transferId; }
    public void setFromAccountId(int fromAccountId)     { this.fromAccountId = fromAccountId; }
    public void setToAccountId(int toAccountId)         { this.toAccountId = toAccountId; }
    public void setAmount(BigDecimal amount)            { this.amount = amount; }
    public void setTimestamp(LocalDateTime timestamp)   { this.timestamp = timestamp; }
    public void setStatus(String status)                { this.status = status; }
}