package com.bank.models;
import java.math.BigDecimal;

public class FraudRule {
    private int        ruleId;
    private int        branchId;
    private BigDecimal largeTxMultiplier;
    private int        rapidTxLimit;
    private int        rapidTxWindowMins;
    private BigDecimal largeTxMinAmount;
    private BigDecimal DailyLimit;

    public FraudRule() {}

    public int        getRuleId()             { return ruleId; }
    public int        getBranchId()           { return branchId; }
    public BigDecimal getLargeTxMultiplier()  { return largeTxMultiplier; }
    public int        getRapidTxLimit()       { return rapidTxLimit; }
    public int        getRapidTxWindowMins()  { return rapidTxWindowMins; }
    public BigDecimal getLargeTxMinAmount()   { return largeTxMinAmount; }
    public BigDecimal getDailyLimit() { return DailyLimit; }

    public void setRuleId(int ruleId)                       { this.ruleId = ruleId; }
    public void setBranchId(int branchId)                   { this.branchId = branchId; }
    public void setLargeTxMultiplier(BigDecimal v)          { this.largeTxMultiplier = v; }
    public void setRapidTxLimit(int rapidTxLimit)           { this.rapidTxLimit = rapidTxLimit; }
    public void setRapidTxWindowMins(int rapidTxWindowMins) { this.rapidTxWindowMins = rapidTxWindowMins; }
    public void setLargeTxMinAmount(BigDecimal v)           { this.largeTxMinAmount = v; }
    public void setDailyLimit(BigDecimal v)         { this.DailyLimit = v; }
}
