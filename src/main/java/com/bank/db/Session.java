package com.bank.db;

import com.bank.models.Manager;
import com.bank.models.Branch;
import com.bank.models.Customer;

public class Session {
    private static Session instance;

    private Branch   selectedBranch;
    private Customer loggedInCustomer;
    private Manager   loggedInManager;
    private String   userMode; // "CUSTOMER" or "ADMIN"

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public Branch   getSelectedBranch()     { return selectedBranch; }
    public Customer getLoggedInCustomer()   { return loggedInCustomer; }
    public Manager    getLoggedInManager()      { return loggedInManager; }
    public String   getUserMode()           { return userMode; }

    public void setSelectedBranch(Branch branch)        { this.selectedBranch = branch; }
    public void setLoggedInCustomer(Customer customer)  { this.loggedInCustomer = customer; }
    public void setLoggedInAdmin(Manager manager)           { this.loggedInManager = manager; }
    public void setUserMode(String userMode)            { this.userMode = userMode; }

    // Call this on logout to wipe session data
    public void clearSession() {
        this.loggedInCustomer = null;
        this.loggedInManager    = null;
        this.userMode         = null;
    }
}
