package com.bank.models;

import java.time.LocalDate;

public class Customer{
    private int customerId;
    private int branchId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String nationalId;
    private LocalDate dateOfBirth;
    private String address;
    private String passwordHash;
    private String status;

    public Customer() {}

    // Getters
    public int           getCustomerId()   { return customerId; }
    public int           getBranchId()     { return branchId; }
    public String        getFirstName()    { return firstName; }
    public String        getLastName()     { return lastName; }
    public String        getFullName()     { return firstName + " " + lastName; }
    public String        getEmail()        { return email; }
    public String        getPhone()        { return phone; }
    public String        getNationalId()   { return nationalId; }
    public LocalDate     getDateOfBirth()  { return dateOfBirth; }
    public String        getAddress()      { return address; }
    public String        getPasswordHash() { return passwordHash; }
    public String        getStatus()       { return status; }

    // Setters
    public void setCustomerId(int customerId)           { this.customerId = customerId; }
    public void setBranchId(int branchId)               { this.branchId = branchId; }
    public void setFirstName(String firstName)          { this.firstName = firstName; }
    public void setLastName(String lastName)            { this.lastName = lastName; }
    public void setEmail(String email)                  { this.email = email; }
    public void setPhone(String phone)                  { this.phone = phone; }
    public void setNationalId(String nationalId)        { this.nationalId = nationalId; }
    public void setDateOfBirth(LocalDate dateOfBirth)   { this.dateOfBirth = dateOfBirth; }
    public void setAddress(String address)              { this.address = address; }
    public void setPasswordHash(String passwordHash)    { this.passwordHash = passwordHash; }
    public void setStatus(String status)                { this.status = status; }
}
