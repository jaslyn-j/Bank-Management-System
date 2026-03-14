package com.bank.services;

import com.bank.dao.AccountDAO;
import com.bank.dao.ApprovalDAO;
import com.bank.dao.CardDAO;
import com.bank.models.PendingApproval;

import java.util.List;

public class ApprovalService {

    private ApprovalDAO approvalDAO;
    private AccountDAO  accountDAO;
    private CardDAO     cardDAO;

    public ApprovalService() {
        this.approvalDAO = new ApprovalDAO();
        this.accountDAO  = new AccountDAO();
        this.cardDAO     = new CardDAO();
    }

    // Get all pending approvals (both accounts and cards)
    // for a branch in one unified list
    public List<PendingApproval> getAllPendingApprovalsForBranch(int branchId) {
        return approvalDAO.getPendingApprovalsForBranch(branchId);
    }

    // Get only pending account requests for a branch
    public List<PendingApproval> getPendingAccountRequests(int branchId) {
        return approvalDAO.getPendingAccountRequestsForBranch(branchId);
    }

    // Get only pending card requests for a branch
    public List<PendingApproval> getPendingCardRequests(int branchId) {
        return approvalDAO.getPendingCardRequestsForBranch(branchId);
    }

    // Approve a request based on its type
    // Delegates to the correct DAO based on whether
    // it is an account or card request
    public boolean approveRequest(PendingApproval approval, int adminId) {
        if (approval.getRequestType().equals("Account Request")) {
            return accountDAO.approveAccount(approval.getRequestId(), adminId);
        } else if (approval.getRequestType().equals("Card Request")) {
            return cardDAO.approveCard(approval.getRequestId(), adminId);
        }
        return false;
    }

    // Decline a request based on its type
    public boolean declineRequest(PendingApproval approval, int adminId) {
        if (approval.getRequestType().equals("Account Request")) {
            return accountDAO.declineAccount(approval.getRequestId(), adminId);
        } else if (approval.getRequestType().equals("Card Request")) {
            return cardDAO.declineCard(approval.getRequestId(), adminId);
        }
        return false;
    }

    // Get count of all pending approvals for a branch
    // Used to display a notification badge on the admin dashboard
    public int getPendingApprovalCount(int branchId) {
        return approvalDAO.getPendingApprovalsForBranch(branchId).size();
    }
}