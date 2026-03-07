package com.bank.models;
import java.time.LocalDateTime;

public class FraudAlert {
    private int          alertId;
    private int          accountId;
    private Integer      transactionId;
    private String       alertReason;
    private String       severity;
    private String       status;
    private LocalDateTime flaggedAt;
    private Integer      reviewedBy;
    private LocalDateTime reviewedAt;

    public FraudAlert() {}

    public int            getAlertId()       { return alertId; }
    public int            getAccountId()     { return accountId; }
    public Integer        getTransactionId() { return transactionId; }
    public String         getAlertReason()   { return alertReason; }
    public String         getSeverity()      { return severity; }
    public String         getStatus()        { return status; }
    public LocalDateTime  getFlaggedAt()     { return flaggedAt; }
    public Integer        getReviewedBy()    { return reviewedBy; }
    public LocalDateTime  getReviewedAt()    { return reviewedAt; }

    public void setAlertId(int alertId)                   { this.alertId = alertId; }
    public void setAccountId(int accountId)               { this.accountId = accountId; }
    public void setTransactionId(Integer transactionId)   { this.transactionId = transactionId; }
    public void setAlertReason(String alertReason)        { this.alertReason = alertReason; }
    public void setSeverity(String severity)              { this.severity = severity; }
    public void setStatus(String status)                  { this.status = status; }
    public void setFlaggedAt(LocalDateTime flaggedAt)     { this.flaggedAt = flaggedAt; }
    public void setReviewedBy(Integer reviewedBy)         { this.reviewedBy = reviewedBy; }
    public void setReviewedAt(LocalDateTime reviewedAt)   { this.reviewedAt = reviewedAt; }
}
