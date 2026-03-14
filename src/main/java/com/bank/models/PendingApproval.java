package com.bank.models;

public class PendingApproval {

    private String requestType;  // "Account Request" or "Card Request"
    private int    requestId;    // account_id or card_id
    private String reference;    // formatted account number or card number
    private String firstName;
    private String lastName;
    private String detail;       // account_type or card_type
    private int    branchId;

    public PendingApproval() {}

    public String getRequestType() { return requestType; }
    public int    getRequestId()   { return requestId; }
    public String getReference()   { return reference; }
    public String getFirstName()   { return firstName; }
    public String getLastName()    { return lastName; }
    public String getFullName()    { return firstName + " " + lastName; }
    public String getDetail()      { return detail; }
    public int    getBranchId()    { return branchId; }

    public void setRequestType(String requestType) { this.requestType = requestType; }
    public void setRequestId(int requestId)        { this.requestId = requestId; }
    public void setReference(String reference)     { this.reference = reference; }
    public void setFirstName(String firstName)     { this.firstName = firstName; }
    public void setLastName(String lastName)       { this.lastName = lastName; }
    public void setDetail(String detail)           { this.detail = detail; }
    public void setBranchId(int branchId)          { this.branchId = branchId; }
}