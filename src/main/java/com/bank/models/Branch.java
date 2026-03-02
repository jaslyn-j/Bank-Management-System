package com.bank.models;

public class Branch{
    private int branchId;
    private String branchName;
    private String branchCode;
    private String address;
    private String phone;
    private String email;

    public Branch() {}

    public Branch(int branchId, String branchName, String branchCode,
                  String address, String phone, String email) {
        this.branchId   = branchId;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.address    = address;
        this.phone      = phone;
        this.email      = email;
    }

    public int    getBranchId()   { return branchId; }
    public String getBranchName() { return branchName; }
    public String getBranchCode() { return branchCode; }
    public String getAddress()    { return address; }
    public String getPhone()      { return phone; }
    public String getEmail()      { return email; }

    public void setBranchId(int branchId)       { this.branchId = branchId; }
    public void setBranchName(String branchName){ this.branchName = branchName; }
    public void setBranchCode(String branchCode){ this.branchCode = branchCode; }
    public void setAddress(String address)      { this.address = address; }
    public void setPhone(String phone)          { this.phone = phone; }
    public void setEmail(String email)          { this.email = email; }

    @Override
    public String toString() { return branchName; }
}
