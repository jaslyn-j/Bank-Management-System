package com.bank.models;

import java.math.BigDecimal;

public class CustomerFinancialSummary {
    private int        customerId;
    private String     firstName;
    private String     lastName;
    private int        totalAccounts;
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;
    private BigDecimal highestBalance;
    private BigDecimal lowestBalance;

    public CustomerFinancialSummary() {}

    public int        getCustomerId()     { return customerId; }
    public String     getFirstName()      { return firstName; }
    public String     getLastName()       { return lastName; }
    public String     getFullName()       { return firstName + " " + lastName; }
    public int        getTotalAccounts()  { return totalAccounts; }
    public BigDecimal getTotalBalance()   { return totalBalance; }
    public BigDecimal getAverageBalance() { return averageBalance; }
    public BigDecimal getHighestBalance() { return highestBalance; }
    public BigDecimal getLowestBalance()  { return lowestBalance; }

    public void setCustomerId(int customerId)           { this.customerId = customerId; }
    public void setFirstName(String firstName)          { this.firstName = firstName; }
    public void setLastName(String lastName)            { this.lastName = lastName; }
    public void setTotalAccounts(int totalAccounts)     { this.totalAccounts = totalAccounts; }
    public void setTotalBalance(BigDecimal v)           { this.totalBalance = v; }
    public void setAverageBalance(BigDecimal v)         { this.averageBalance = v; }
    public void setHighestBalance(BigDecimal v)         { this.highestBalance = v; }
    public void setLowestBalance(BigDecimal v)          { this.lowestBalance = v; }
}
