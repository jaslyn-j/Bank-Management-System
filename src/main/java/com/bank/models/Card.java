package com.bank.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Card {
    private int cardId;
    private int accountId;
    private String cardType;
    private String cardNumber;
    private String cvvHash;
    private LocalDate expiryDate;
    private String status;
    private Integer approvedBy;

    public Card() {}

    public int           getCardId()      { return cardId; }
    public int           getAccountId()   { return accountId; }
    public String        getCardType()    { return cardType; }
    public String        getCardNumber()  { return cardNumber; }
    public String        getCvvHash()     { return cvvHash; }
    public LocalDate     getExpiryDate()  { return expiryDate; }
    public String        getStatus()      { return status; }
    public Integer       getApprovedBy()  { return approvedBy; }

    public void setCardId(int cardId)               { this.cardId = cardId; }
    public void setAccountId(int accountId)         { this.accountId = accountId; }
    public void setCardType(String cardType)        { this.cardType = cardType; }
    public void setCardNumber(String cardNumber)    { this.cardNumber = cardNumber; }
    public void setCvvHash(String cvvHash)          { this.cvvHash = cvvHash; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setStatus(String status)            { this.status = status; }
    public void setApprovedBy(Integer approvedBy)       { this.approvedBy = approvedBy; }
}