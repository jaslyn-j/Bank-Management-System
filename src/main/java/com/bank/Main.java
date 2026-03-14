package com.bank;

import com.bank.dao.BranchDAO;
import com.bank.db.Session;
import com.bank.models.*;
import com.bank.services.*;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    // -------------------------------------------------------
    // Services
    // -------------------------------------------------------
    static AuthService                   authService            = new AuthService();
    static AccountService                accountService         = new AccountService();
    static TransactionService            transactionService     = new TransactionService();
    static CardService                   cardService            = new CardService();
    static CustomerService               customerService        = new CustomerService();
    static FraudDetectionService         fraudService           = new FraudDetectionService();
    static ApprovalService               approvalService        = new ApprovalService();
    static ProcedureService              procedureService       = new ProcedureService();
    static BranchActivityService         branchActivityService  = new BranchActivityService();
    static AccountBalanceOverviewService balanceOverviewService = new AccountBalanceOverviewService();
    static CardExpiryService             cardExpiryService      = new CardExpiryService();

    // -------------------------------------------------------
    // DAOs
    // -------------------------------------------------------
    static BranchDAO branchDAO = new BranchDAO();

    // -------------------------------------------------------
    // Shared test state
    // -------------------------------------------------------
    static int accountId  = -1;
    static int cardId     = -1;
    static int customerId = -1;
    static int branchId   = 1;
    static int adminId    = -1;

    // -------------------------------------------------------
    // Test counters
    // -------------------------------------------------------
    static int passed = 0;
    static int failed = 0;
    static int skipped = 0;

    public static void main(String[] args) {

        printHeader("BANK MANAGEMENT SYSTEM — FULL TEST SUITE");

        // DATABASE AND BRANCH
        testLoadBranches();

        // AUTHENTICATION
        testAdminLogin();
        testRegisterCustomer();
        testCustomerLogin();
        testInvalidCustomerLogin();
        testBlockedCustomerLogin();

        // ACCOUNT MANAGEMENT
        testRequestNewAccount();
        testGetPendingAccounts();
        testApproveAccount();
        testDeclineAccount();
        testBlockAndUnblockAccount();

        // TRANSACTIONS
        testDeposit();
        testWithdrawal();
        testInsufficientFunds();
        testTransactionHistory();
        testTransactionSummary();

        // FUND TRANSFERS
        testFundTransfer();
        testSelfTransfer();

        // CARD MANAGEMENT
        testApplyForCard();
        testApproveCard();
        testDeclineCard();
        testBlockAndUnblockCard();

        // UNIFIED APPROVALS
        testUnifiedPendingApprovals();

        // CUSTOMER FINANCIAL SUMMARY
        testCustomerFinancialSummary();

        // FRAUD DETECTION
        testFraudDetection();
        testFraudAlertReview();

        // VIEWS
        testAccountBalanceOverview();
        testCardExpiryStatus();
        testBranchActivityOverview();

        // CURSORS AND STORED PROCEDURES
        testBlockCustomerCards();
        testCloseZeroBalanceAccounts();
        testDismissReviewedAlerts();

        // ADMIN MANAGEMENT
        testGetAllAccountsForBranch();
        testGetAllCustomersForBranch();

        // SETS
        testSetQueries();

        printFooter();
    }

    // =======================================================
    // DATABASE AND BRANCH TESTS
    // =======================================================

    static void testLoadBranches() {
        printSection("DATABASE AND BRANCH");
        System.out.println("--- Test: Load Branches ---");
        try {
            List<Branch> branches = branchDAO.getAllBranches();
            if (branches.isEmpty()) {
                fail("No branches found. Run seed data first.");
            } else {
                branches.forEach(b -> System.out.println(
                        "  Branch: " + b.getBranchName()
                                + " (" + b.getBranchCode() + ")"));
                pass("Branches loaded: " + branches.size());
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // AUTHENTICATION TESTS
    // =======================================================

    static void testAdminLogin() {
        printSection("AUTHENTICATION");
        System.out.println("--- Test: Admin Login ---");
        try {
            boolean result = authService.loginManager(
                    "admin", "test123", branchId);
            if (result) {
                int managerId = Session.getInstance()
                        .getLoggedInManager()
                        .getManagerId();
                pass("Admin login successful. Admin ID: " + managerId);
            } else {
                fail("Admin login failed.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testRegisterCustomer() {
        System.out.println("\n--- Test: Register Customer ---");
        try {
            Customer customer = new Customer();
            customer.setBranchId(branchId);
            customer.setFirstName("John");
            customer.setLastName("Doe");
            customer.setEmail("john.doe@email.com");
            customer.setPhone("555-1234");
            customer.setNationalId("NID123456");
            customer.setAddress("456 Oak Avenue");

            boolean result = authService.registerCustomer(
                    customer, "password123");
            if (result) {
                pass("Customer registered successfully.");
            } else {
                skip("Customer already exists. Continuing with existing.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testCustomerLogin() {
        System.out.println("\n--- Test: Customer Login ---");
        try {
            boolean result = authService.loginCustomer(
                    "john.doe@email.com", "password123", branchId);
            if (result) {
                customerId = Session.getInstance()
                        .getLoggedInCustomer()
                        .getCustomerId();
                pass("Customer login successful. Customer ID: "
                        + customerId);
            } else {
                fail("Customer login failed.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testInvalidCustomerLogin() {
        System.out.println("\n--- Test: Invalid Customer Login ---");
        try {
            boolean result = authService.loginCustomer(
                    "wrong@email.com", "wrongpassword", branchId);
            if (!result) {
                pass("Correctly rejected invalid credentials.");
            } else {
                fail("Should have rejected invalid credentials.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testBlockedCustomerLogin() {
        System.out.println("\n--- Test: Blocked Customer Login ---");
        if (customerId == -1) {
            skip("No customer available.");
            return;
        }
        try {
            customerService.blockCustomer(customerId);
            boolean result = authService.loginCustomer(
                    "john.doe@email.com", "password123", branchId);
            if (!result) {
                pass("Correctly rejected blocked customer login.");
            } else {
                fail("Blocked customer should not be able to login.");
            }
            customerService.unblockCustomer(customerId);
            System.out.println("  Customer unblocked for remaining tests.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // ACCOUNT MANAGEMENT TESTS
    // =======================================================

    static void testRequestNewAccount() {
        printSection("ACCOUNT MANAGEMENT");
        System.out.println("--- Test: Request New Account ---");
        if (customerId == -1) {
            skip("No customer available.");
            return;
        }
        try {
            boolean result = accountService.requestNewAccount(
                    customerId, branchId, "savings");
            if (result) {
                pass("Account request submitted successfully.");
            } else {
                fail("Account request failed.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testGetPendingAccounts() {
        System.out.println("\n--- Test: Get Pending Accounts ---");
        try {
            List<Account> pending =
                    accountService.getPendingAccountsForBranch(branchId);
            System.out.println("  Pending accounts found: "
                    + pending.size());
            pass("Pending accounts retrieved.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testApproveAccount() {
        System.out.println("\n--- Test: Approve Account ---");
        try {
            authService.loginManager("rthompson", "hashed_mgr_pass_1", branchId);
            adminId = Session.getInstance()
                    .getLoggedInManager()
                    .getManagerId();

            List<Account> pending =
                    accountService.getPendingAccountsForBranch(branchId);

            if (!pending.isEmpty()) {
                accountId = pending.get(0).getAccountId();
                boolean result = accountService.approveAccount(
                        accountId, adminId);
                if (result) {
                    pass("Account approved. Account: "
                            + accountService.formatAccountNumber(accountId));
                } else {
                    fail("Account approval failed.");
                }
            } else {
                // Use existing active account
                List<Account> existing =
                        accountService.getActiveAccountsForCustomer(
                                customerId);
                if (!existing.isEmpty()) {
                    accountId = existing.get(0).getAccountId();
                    skip("No pending accounts. Using existing: "
                            + accountService.formatAccountNumber(accountId));
                } else {
                    fail("No accounts available.");
                }
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testDeclineAccount() {
        System.out.println("\n--- Test: Decline Account ---");
        try {
            // Create a new account request to decline
            boolean requested = accountService.requestNewAccount(
                    customerId, branchId, "checking");

            List<Account> pending =
                    accountService.getPendingAccountsForBranch(branchId);

            if (!pending.isEmpty()) {
                int declineId = pending.get(0).getAccountId();
                boolean result = accountService.declineAccount(
                        declineId, adminId);
                if (result) {
                    pass("Account declined successfully.");
                } else {
                    fail("Account decline failed.");
                }
            } else {
                skip("No pending accounts to decline.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testBlockAndUnblockAccount() {
        System.out.println("\n--- Test: Block and Unblock Account ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            boolean blocked = accountService.blockAccount(accountId);
            boolean unblocked = accountService.unblockAccount(accountId);

            if (blocked && unblocked) {
                pass("Account blocked and unblocked successfully.");
            } else {
                fail("Block/unblock failed. Blocked: " + blocked
                        + " Unblocked: " + unblocked);
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // TRANSACTION TESTS
    // =======================================================

    static void testDeposit() {
        printSection("TRANSACTIONS");
        System.out.println("--- Test: Deposit ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            boolean result = transactionService.deposit(
                    accountId, new BigDecimal("1000.00"),
                    "Test deposit");
            if (result) {
                Account updated =
                        accountService.findAccountById(accountId);
                pass("Deposit successful. Balance: $"
                        + updated.getBalance());
            } else {
                fail("Deposit failed.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testWithdrawal() {
        System.out.println("\n--- Test: Withdrawal ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            boolean result = transactionService.withdraw(
                    accountId, new BigDecimal("200.00"),
                    "Test withdrawal");
            if (result) {
                Account updated =
                        accountService.findAccountById(accountId);
                pass("Withdrawal successful. Balance: $"
                        + updated.getBalance());
            } else {
                fail("Withdrawal failed.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testInsufficientFunds() {
        System.out.println("\n--- Test: Insufficient Funds ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            boolean result = transactionService.withdraw(
                    accountId, new BigDecimal("99999.00"),
                    "Large withdrawal");
            if (!result) {
                pass("Correctly rejected insufficient funds.");
            } else {
                fail("Should have rejected insufficient funds.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testTransactionHistory() {
        System.out.println("\n--- Test: Transaction History ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            List<Transaction> history =
                    transactionService.getTransactionHistory(accountId);
            if (history.isEmpty()) {
                fail("No transactions found.");
            } else {
                System.out.println("  Transactions found: "
                        + history.size());
                history.forEach(t -> System.out.println(
                        "    " + t.getTransactionType()
                                + " | $" + t.getAmount()
                                + " | Balance after: $" + t.getBalanceAfter()
                                + " | " + t.getTimestamp()));
                pass("Transaction history retrieved.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testTransactionSummary() {
        System.out.println("\n--- Test: Transaction Summary ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            TransactionSummary summary =
                    transactionService.getTransactionSummary(accountId);
            if (summary == null) {
                fail("No transaction summary found.");
            } else {
                System.out.println("  Total transactions:    "
                        + summary.getTotalTransactions());
                System.out.println("  Total deposited:       $"
                        + summary.getTotalDeposited());
                System.out.println("  Total withdrawn:       $"
                        + summary.getTotalWithdrawn());
                System.out.println("  Total transferred out: $"
                        + summary.getTotalTransferredOut());
                System.out.println("  Total transferred in:  $"
                        + summary.getTotalTransferredIn());
                pass("Transaction summary retrieved.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // FUND TRANSFER TESTS
    // =======================================================

    static void testFundTransfer() {
        printSection("FUND TRANSFERS");
        System.out.println("--- Test: Fund Transfer ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            // Create a second account to transfer to
            accountService.requestNewAccount(
                    customerId, branchId, "checking");
            List<Account> pending =
                    accountService.getPendingAccountsForBranch(branchId);

            if (!pending.isEmpty()) {
                int secondAccountId = pending.get(0).getAccountId();
                accountService.approveAccount(secondAccountId, adminId);

                boolean result = transactionService.transfer(
                        accountId, secondAccountId,
                        new BigDecimal("100.00"));

                if (result) {
                    Account from =
                            accountService.findAccountById(accountId);
                    Account to =
                            accountService.findAccountById(secondAccountId);
                    pass("Transfer successful."
                            + " From: $" + from.getBalance()
                            + " To: $" + to.getBalance());
                } else {
                    fail("Transfer failed.");
                }
            } else {
                skip("No second account available for transfer.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testSelfTransfer() {
        System.out.println("\n--- Test: Self Transfer ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            boolean result = transactionService.transfer(
                    accountId, accountId, new BigDecimal("100.00"));
            if (!result) {
                pass("Correctly rejected self transfer.");
            } else {
                fail("Should have rejected self transfer.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // CARD MANAGEMENT TESTS
    // =======================================================

    static void testApplyForCard() {
        printSection("CARD MANAGEMENT");
        System.out.println("--- Test: Apply For Card ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            boolean result = cardService.applyForCard(
                    accountId, "debit");
            if (result) {
                List<Card> cards =
                        cardService.getCardsForAccount(accountId);
                if (!cards.isEmpty()) {
                    cardId = cards.get(0).getCardId();
                }
                pass("Card application submitted. Card ID: " + cardId);
            } else {
                fail("Card application failed.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testApproveCard() {
        System.out.println("\n--- Test: Approve Card ---");
        if (cardId == -1) {
            skip("No card application found.");
            return;
        }
        try {
            boolean result = cardService.approveCard(cardId, adminId);
            if (result) {
                pass("Card approved successfully.");
            } else {
                fail("Card approval failed.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testDeclineCard() {
        System.out.println("\n--- Test: Decline Card ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            // Apply for a second card to decline
            cardService.applyForCard(accountId, "credit");
            List<Card> cards =
                    cardService.getCardsForAccount(accountId);

            // Find a pending card to decline
            Card pendingCard = cards.stream()
                    .filter(c -> c.getStatus().equals("pending"))
                    .findFirst()
                    .orElse(null);

            if (pendingCard != null) {
                boolean result = cardService.declineCard(
                        pendingCard.getCardId(), adminId);
                if (result) {
                    pass("Card declined successfully.");
                } else {
                    fail("Card decline failed.");
                }
            } else {
                skip("No pending card to decline.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testBlockAndUnblockCard() {
        System.out.println("\n--- Test: Block and Unblock Card ---");
        if (cardId == -1) {
            skip("No card available.");
            return;
        }
        try {
            boolean blocked   = cardService.blockCard(cardId);
            boolean unblocked = cardService.unblockCard(cardId);

            if (blocked && unblocked) {
                pass("Card blocked and unblocked successfully.");
            } else {
                fail("Block/unblock failed. Blocked: " + blocked
                        + " Unblocked: " + unblocked);
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // UNIFIED APPROVALS TEST
    // =======================================================

    static void testUnifiedPendingApprovals() {
        printSection("UNIFIED APPROVALS — UNION QUERY");
        System.out.println("--- Test: Unified Pending Approvals ---");
        try {
            List<PendingApproval> approvals =
                    approvalService.getAllPendingApprovalsForBranch(branchId);
            System.out.println("  Pending approvals found: "
                    + approvals.size());
            approvals.forEach(p -> System.out.println(
                    "    Type: "      + p.getRequestType()
                            + " | Customer: " + p.getFullName()
                            + " | Reference: "+ p.getReference()
                            + " | Detail: "   + p.getDetail()));
            pass("Unified pending approvals retrieved.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // CUSTOMER FINANCIAL SUMMARY TEST
    // =======================================================

    static void testCustomerFinancialSummary() {
        printSection("CUSTOMER FINANCIAL SUMMARY — AGGREGATE FUNCTIONS");
        System.out.println("--- Test: Customer Financial Summary ---");
        try {
            List<CustomerFinancialSummary> summaries =
                    accountService.getCustomerFinancialSummary(branchId);
            if (summaries.isEmpty()) {
                fail("No summary data found.");
            } else {
                summaries.forEach(s -> {
                    System.out.println("  Customer: " + s.getFullName());
                    System.out.println("    Total accounts:  "
                            + s.getTotalAccounts());
                    System.out.println("    Total balance:   $"
                            + s.getTotalBalance());
                    System.out.println("    Average balance: $"
                            + s.getAverageBalance());
                    System.out.println("    Highest balance: $"
                            + s.getHighestBalance());
                    System.out.println("    Lowest balance:  $"
                            + s.getLowestBalance());
                });
                pass("Customer financial summary retrieved.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // FRAUD DETECTION TESTS
    // =======================================================

    static void testFraudDetection() {
        printSection("FRAUD DETECTION");
        System.out.println("--- Test: Fraud Detection ---");
        if (accountId == -1) {
            skip("No account available.");
            return;
        }
        try {
            FraudDetectionService.FraudCheckResult result =
                    fraudService.analyze(accountId, branchId,
                            new BigDecimal("99999.00"), "withdrawal");

            System.out.println("  Flagged: " + result.isFlagged());
            System.out.println("  Blocked: " + result.blocked);

            if (result.isFlagged()) {
                result.triggeredRules.forEach(r ->
                        System.out.println("  Rule: " + r));
            }

            pass("Fraud detection check completed.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testFraudAlertReview() {
        System.out.println("\n--- Test: Fraud Alert Review ---");
        try {
            List<FraudAlert> alerts =
                    fraudService.getOpenAlertsForBranch(branchId);
            System.out.println("  Open alerts: " + alerts.size());

            if (!alerts.isEmpty()) {
                int alertId = alerts.get(0).getAlertId();
                boolean reviewed = fraudService.reviewAlert(
                        alertId, "reviewed", adminId);
                pass("Alert " + alertId + " reviewed: " + reviewed);
            } else {
                skip("No open alerts to review.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // VIEW TESTS
    // =======================================================

    static void testAccountBalanceOverview() {
        printSection("VIEWS");
        System.out.println("--- Test: Account Balance Overview View ---");
        try {
            List<AccountBalanceOverview> overview =
                    balanceOverviewService.getBalanceOverview(branchId);
            System.out.println("  Accounts found: " + overview.size());
            overview.forEach(a -> System.out.println(
                    "    " + a.getAccountNumber()
                            + " | $" + a.getBalance()
                            + " | " + a.getBalanceCategory()));

            System.out.println("  Balance category counts:");
            balanceOverviewService.getBalanceCategoryCounts(branchId)
                    .forEach(row -> System.out.println(
                            "    " + row[0] + ": " + row[1]));

            pass("Account balance overview retrieved.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testCardExpiryStatus() {
        System.out.println("\n--- Test: Card Expiry Status View ---");
        try {
            List<CardExpiryStatus> allCards =
                    cardExpiryService.getAllCardExpiryStatus(branchId);
            System.out.println("  Active cards found: " + allCards.size());
            allCards.forEach(c -> System.out.println(
                    "    " + c.getCardNumber()
                            + " | " + c.getCardType()
                            + " | Expires: " + c.getExpiryDate()
                            + " | " + c.getExpiryStatus()));

            System.out.println("  Expiry status counts:");
            cardExpiryService.getExpiryStatusCounts(branchId)
                    .forEach(row -> System.out.println(
                            "    " + row[0] + ": " + row[1]));

            if (cardId != -1) {
                System.out.println("  Test card expired: "
                        + cardExpiryService.isCardExpired(cardId));
                System.out.println("  Test card expiring soon: "
                        + cardExpiryService.isCardExpiringSoon(cardId));
            }

            pass("Card expiry status retrieved.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testBranchActivityOverview() {
        System.out.println("\n--- Test: Branch Activity Overview View ---");
        try {
            BranchActivity activity =
                    branchActivityService.getBranchActivity(branchId);
            if (activity == null) {
                fail("No branch activity data found.");
            } else {
                System.out.println("  Branch: "
                        + activity.getBranchName()
                        + " (" + activity.getBranchCode() + ")");
                System.out.println("    Total customers:     "
                        + activity.getTotalCustomers());
                System.out.println("    Total accounts:      "
                        + activity.getTotalAccounts());
                System.out.println("    Total cards:         "
                        + activity.getTotalCards());
                System.out.println("    Total balance held:  $"
                        + activity.getTotalBranchBalance());
                System.out.println("    Pending accounts:    "
                        + activity.getPendingAccounts());
                System.out.println("    Pending cards:       "
                        + activity.getPendingCards());
                System.out.println("    Open fraud alerts:   "
                        + activity.getOpenFraudAlerts());
                System.out.println("    Requires attention:  "
                        + activity.requiresAttention());
                pass("Branch activity overview retrieved.");
            }
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // CURSOR AND STORED PROCEDURE TESTS
    // =======================================================



    static void testBlockCustomerCards() {
        System.out.println("\n--- Test: Block Customer Cards Cursor ---");
        if (customerId == -1) {
            skip("No customer available.");
            return;
        }
        try {
            System.out.println("  Card statuses before block:");
            cardService.getCardsForAccount(accountId)
                    .forEach(c -> System.out.println(
                            "    Card " + c.getCardId()
                                    + " | " + c.getStatus()));

            customerService.blockCustomer(customerId);
            System.out.println("  Customer blocked.");

            System.out.println("  Card statuses after block:");
            cardService.getCardsForAccount(accountId)
                    .forEach(c -> System.out.println(
                            "    Card " + c.getCardId()
                                    + " | " + c.getStatus()));

            customerService.unblockCustomer(customerId);
            System.out.println("  Customer unblocked for remaining tests.");
            pass("Customer cards blocked via cursor procedure.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testCloseZeroBalanceAccounts() {
        System.out.println("\n--- Test: Close Zero Balance Accounts ---");
        try {
            long zeroBefore = accountService
                    .getAllAccountsForBranch(branchId).stream()
                    .filter(a -> a.getBalance() != null
                            && a.getBalance().compareTo(
                            BigDecimal.ZERO) == 0
                            && a.getStatus().equals("active"))
                    .count();

            System.out.println("  Zero balance active accounts before: "
                    + zeroBefore);

            int closed = procedureService.closeZeroBalanceAccounts(branchId);
            System.out.println("  Accounts closed: " + closed);
            pass("Zero balance accounts closed via cursor procedure.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testDismissReviewedAlerts() {
        System.out.println("\n--- Test: Dismiss Reviewed Alerts ---");
        try {
            // Mark any open alert as reviewed first
            List<FraudAlert> openAlerts =
                    fraudService.getOpenAlertsForBranch(branchId);

            if (!openAlerts.isEmpty()) {
                fraudService.reviewAlert(
                        openAlerts.get(0).getAlertId(),
                        "reviewed", adminId);
                System.out.println("  Marked one alert as reviewed.");
            }

            int dismissed =
                    fraudService.dismissAllReviewedAlerts(branchId);
            System.out.println("  Alerts dismissed: " + dismissed);
            pass("Reviewed alerts dismissed via cursor procedure.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // ADMIN MANAGEMENT TESTS
    // =======================================================

    static void testGetAllAccountsForBranch() {
        printSection("ADMIN MANAGEMENT");
        System.out.println("--- Test: Get All Accounts For Branch ---");
        try {
            List<Account> accounts =
                    accountService.getAllAccountsForBranch(branchId);
            System.out.println("  Total accounts: " + accounts.size());
            accounts.forEach(a -> System.out.println(
                    "    "
                            + accountService.formatAccountNumber(a.getAccountId())
                            + " | " + a.getAccountType()
                            + " | $" + a.getBalance()
                            + " | " + a.getStatus()));
            pass("All accounts retrieved for branch.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    static void testGetAllCustomersForBranch() {
        System.out.println("\n--- Test: Get All Customers For Branch ---");
        try {
            List<Customer> customers =
                    customerService.getCustomersForBranch(branchId);
            System.out.println("  Total customers: " + customers.size());
            customers.forEach(c -> System.out.println(
                    "    " + c.getFullName()
                            + " | " + c.getEmail()
                            + " | " + c.getStatus()));
            pass("All customers retrieved for branch.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // SET QUERY TESTS
    // =======================================================

    static void testSetQueries() {
        printSection("SET QUERIES");
        System.out.println("--- Test: Unified Pending Approvals UNION ---");
        try {
            List<PendingApproval> all =
                    approvalService.getAllPendingApprovalsForBranch(branchId);
            List<PendingApproval> accountsOnly =
                    approvalService.getPendingAccountRequests(branchId);
            List<PendingApproval> cardsOnly =
                    approvalService.getPendingCardRequests(branchId);

            System.out.println("  All pending (UNION ALL): "
                    + all.size());
            System.out.println("  Account requests only:   "
                    + accountsOnly.size());
            System.out.println("  Card requests only:      "
                    + cardsOnly.size());

            pass("Set queries executed successfully.");
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }
    }

    // =======================================================
    // HELPER METHODS
    // =======================================================

    static void pass(String message) {
        System.out.println("  PASS — " + message);
        passed++;
    }

    static void fail(String message) {
        System.out.println("  FAIL — " + message);
        failed++;
    }

    static void skip(String message) {
        System.out.println("  SKIP — " + message);
        skipped++;
    }

    static void printHeader(String title) {
        System.out.println(
                "╔══════════════════════════════════════════════════╗");
        System.out.printf(
                "║  %-48s║%n", title);
        System.out.println(
                "╚══════════════════════════════════════════════════╝");
    }

    static void printSection(String title) {
        System.out.println(
                "\n══════════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println(
                "══════════════════════════════════════════════════");
    }

    static void printFooter() {
        System.out.println(
                "\n╔══════════════════════════════════════════════════╗");
        System.out.println(
                "║                  TEST RESULTS                   ║");
        System.out.println(
                "╠══════════════════════════════════════════════════╣");
        System.out.printf(
                "║  %-10s %-37d║%n", "PASSED:",  passed);
        System.out.printf(
                "║  %-10s %-37d║%n", "FAILED:",  failed);
        System.out.printf(
                "║  %-10s %-37d║%n", "SKIPPED:", skipped);
        System.out.printf(
                "║  %-10s %-37d║%n", "TOTAL:",
                passed + failed + skipped);
        System.out.println(
                "╚══════════════════════════════════════════════════╝");
    }
}