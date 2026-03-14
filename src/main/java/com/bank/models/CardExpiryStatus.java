package com.bank.models;

import java.time.LocalDate;

public class CardExpiryStatus {

    private int       cardId;
    private String    cardNumber;
    private String    cardType;
    private String    status;
    private LocalDate expiryDate;
    private int       accountId;
    private String    accountNumber;
    private int       branchId;
    private String    expiryStatus;

    public CardExpiryStatus() {}

    public int       getCardId()        { return cardId; }
    public String    getCardNumber()    { return cardNumber; }
    public String    getCardType()      { return cardType; }
    public String    getStatus()        { return status; }
    public LocalDate getExpiryDate()    { return expiryDate; }
    public int       getAccountId()     { return accountId; }
    public String    getAccountNumber() { return accountNumber; }
    public int       getBranchId()      { return branchId; }
    public String    getExpiryStatus()  { return expiryStatus; }

    public void setCardId(int cardId)               { this.cardId = cardId; }
    public void setCardNumber(String cardNumber)    { this.cardNumber = cardNumber; }
    public void setCardType(String cardType)        { this.cardType = cardType; }
    public void setStatus(String status)            { this.status = status; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setAccountId(int accountId)         { this.accountId = accountId; }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public void setBranchId(int branchId)           { this.branchId = branchId; }
    public void setExpiryStatus(String expiryStatus) {
        this.expiryStatus = expiryStatus;
    }
}