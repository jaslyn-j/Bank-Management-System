package com.bank.services;

import com.bank.dao.BranchActivityDAO;
import com.bank.models.BranchActivity;

import java.util.List;

public class BranchActivityService {

    private BranchActivityDAO dao;

    public BranchActivityService() {
        this.dao = new BranchActivityDAO();
    }

    // Get the full activity summary for a specific branch
    // Used to populate the admin dashboard statistics panel
    public BranchActivity getBranchActivity(int branchId) {
        return dao.getBranchActivity(branchId);
    }

    // Get activity summary for all branches
    public List<BranchActivity> getAllBranchActivity() {
        return dao.getAllBranchActivity();
    }

    // Get branches that have items requiring attention
    public List<BranchActivity> getBranchesRequiringAttention() {
        return dao.getBranchesRequiringAttention();
    }

    // Get total pending approval count for a branch
    // Used to show a badge on the approvals tab
    public int getTotalPendingItems(int branchId) {
        return dao.getTotalPendingItems(branchId);
    }

    // Get open fraud alert count for a branch
    // Used to show a badge on the fraud alerts tab
    public int getOpenFraudAlertCount(int branchId) {
        return dao.getOpenFraudAlertCount(branchId);
    }

    // Check if a branch has any items requiring attention
    public boolean branchRequiresAttention(int branchId) {
        BranchActivity activity = dao.getBranchActivity(branchId);
        return activity != null && activity.requiresAttention();
    }
}