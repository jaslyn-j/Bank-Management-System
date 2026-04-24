package com.bank.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Customer {

    private int       customerId;
    private int       branchId;
    private String    firstName;
    private String    lastName;
    private String    email;
    private String    phone;
    private String    nationalId;
    private LocalDate dateOfBirth;

    // Replaced address with four fields
    private String    street;
    private String    city;
    private String    state;
    private String    pinCode;

    private String        passwordHash;
    private String        status;
    private LocalDateTime createdAt;

    public Customer() {}

    // Getters
    public int       getCustomerId()  { return customerId; }
    public int       getBranchId()    { return branchId; }
    public String    getFirstName()   { return firstName; }
    public String    getLastName()    { return lastName; }
    public String    getEmail()       { return email; }
    public String    getPhone()       { return phone; }
    public String    getNationalId()  { return nationalId; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String    getStreet()      { return street; }
    public String    getCity()        { return city; }
    public String    getState()       { return state; }
    public String    getPinCode()     { return pinCode; }
    public String    getPasswordHash(){ return passwordHash; }
    public String    getStatus()      { return status; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    // Full name helper
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Full address helper — combines all four
    // fields into one readable string
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street  != null
                && !street.isEmpty())
            sb.append(street);
        if (city    != null
                && !city.isEmpty())
            sb.append(", ").append(city);
        if (state   != null
                && !state.isEmpty())
            sb.append(", ").append(state);
        if (pinCode != null
                && !pinCode.isEmpty())
            sb.append(" - ").append(pinCode);
        return sb.toString();
    }

    // Setters
    public void setCustomerId(int customerId)
    { this.customerId = customerId; }
    public void setBranchId(int branchId)
    { this.branchId = branchId; }
    public void setFirstName(String firstName)
    { this.firstName = firstName; }
    public void setLastName(String lastName)
    { this.lastName = lastName; }
    public void setEmail(String email)
    { this.email = email; }
    public void setPhone(String phone)
    { this.phone = phone; }
    public void setNationalId(String nationalId)
    { this.nationalId = nationalId; }
    public void setDateOfBirth(
            LocalDate dateOfBirth)
    { this.dateOfBirth = dateOfBirth; }
    public void setStreet(String street)
    { this.street = street; }
    public void setCity(String city)
    { this.city = city; }
    public void setState(String state)
    { this.state = state; }
    public void setPinCode(String pinCode)
    { this.pinCode = pinCode; }
    public void setPasswordHash(
            String passwordHash)
    { this.passwordHash = passwordHash; }
    public void setStatus(String status)
    { this.status = status; }
    public void setCreatedAt(
            LocalDateTime createdAt)
    { this.createdAt = createdAt; }
}