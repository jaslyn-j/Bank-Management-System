package com.bank.models;

import java.time.LocalDateTime;

public class Manager{
    private int managerId;
    private int branchId;
    private String fullName;
    private String username;
    private String passwordHash;
    private LocalDateTime createdAt;

    public Manager() {}

    public int           getManagerId()      { return managerId; }
    public int           getBranchId()     { return branchId; }
    public String        getFullName()     { return fullName; }
    public String        getUsername()     { return username; }
    public String        getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt()    { return createdAt; }

    public void setManagerId(int managerId)             { this.managerId = managerId; }
    public void setBranchId(int branchId)           { this.branchId = branchId; }
    public void setFullName(String fullName)        { this.fullName = fullName; }
    public void setUsername(String username)        { this.username = username; }
    public void setPasswordHash(String passwordHash){ this.passwordHash = passwordHash; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
}