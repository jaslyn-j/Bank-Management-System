package com.bank.models;

import java.math.BigDecimal;

public class BranchActivity {

    private int        branchId;
    private String     branchName;
    private String     branchCode;
    private int        totalCustomers;
    private int        totalAccounts;
    private int        totalCards;
    private BigDecimal totalBranchBalance;
    private int        pendingAccounts;
    private int        pendingCards;
    private int        openFraudAlerts;

    public BranchActivity() {}

    // Getters
    public int        getBranchId()           { return branchId; }
    public String     getBranchName()         { return branchName; }
    public String     getBranchCode()         { return branchCode; }
    public int        getTotalCustomers()     { return totalCustomers; }
    public int        getTotalAccounts()      { return totalAccounts; }
    public int        getTotalCards()         { return totalCards; }
    public BigDecimal getTotalBranchBalance() { return totalBranchBalance; }
    public int        getPendingAccounts()    { return pendingAccounts; }
    public int        getPendingCards()       { return pendingCards; }
    public int        getOpenFraudAlerts()    { return openFraudAlerts; }

    // Setters
    public void setBranchId(int branchId)                   { this.branchId = branchId; }
    public void setBranchName(String branchName)            { this.branchName = branchName; }
    public void setBranchCode(String branchCode)            { this.branchCode = branchCode; }
    public void setTotalCustomers(int totalCustomers)       { this.totalCustomers = totalCustomers; }
    public void setTotalAccounts(int totalAccounts)         { this.totalAccounts = totalAccounts; }
    public void setTotalCards(int totalCards)               { this.totalCards = totalCards; }
    public void setTotalBranchBalance(BigDecimal v)         { this.totalBranchBalance = v; }
    public void setPendingAccounts(int pendingAccounts)     { this.pendingAccounts = pendingAccounts; }
    public void setPendingCards(int pendingCards)           { this.pendingCards = pendingCards; }
    public void setOpenFraudAlerts(int openFraudAlerts)     { this.openFraudAlerts = openFraudAlerts; }

    // Returns total pending items combined
    public int getTotalPendingItems() {
        return pendingAccounts + pendingCards;
    }

    // Returns true if there are any items needing attention
    public boolean requiresAttention() {
        return pendingAccounts > 0
                || pendingCards > 0
                || openFraudAlerts > 0;
    }
}