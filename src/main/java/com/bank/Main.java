package com.bank;

import com.bank.dao.BranchDAO;
import com.bank.models.Branch;
import com.bank.models.Customer;
import com.bank.models.Account;
import com.bank.models.Card;
import com.bank.models.FraudAlert;
import com.bank.services.AuthService;
import com.bank.services.AccountService;
import com.bank.services.TransactionService;
import com.bank.services.CardService;
import com.bank.services.FraudDetectionService;
import com.bank.db.Session;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    static AuthService authService       = new AuthService();
    static AccountService accountService = new AccountService();
    static TransactionService transactionService = new TransactionService();
    static CardService cardService       = new CardService();
    static FraudDetectionService fraudService = new FraudDetectionService();
    static BranchDAO branchDAO           = new BranchDAO();

    static int accountId = -1;
    static int cardId    = -1;

    public static void main(String[] args) {

        // -------------------------------------------------------
        // TEST 1 — Load branches
        // -------------------------------------------------------
        System.out.println("=== Test 1: Load Branches ===");
        List<Branch> branches = branchDAO.getAllBranches();
        if (branches.isEmpty()) {
            System.out.println("FAIL — No branches found. Run seed data first.");
            return;
        }
        branches.forEach(b -> System.out.println("Branch found: "
                + b.getBranchName() + " (" + b.getBranchCode() + ")"));

        // -------------------------------------------------------
        // TEST 2 — Admin login
        // -------------------------------------------------------
        System.out.println("\n=== Test 2: Manager Login ===");
        boolean managerLogin = authService.loginManager("rthompson", "hashed_mgr_pass_1", 1);
        System.out.println("Manager login success: " + managerLogin);
        if (managerLogin) {
            System.out.println("Logged in as: "
                    + Session.getInstance().getLoggedInManager().getFullName());
        }

        // -------------------------------------------------------
        // TEST 3 — Register a new customer
        // -------------------------------------------------------
        System.out.println("\n=== Test 3: Register Customer ===");
        Customer customer = new Customer();
        customer.setBranchId(1);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@email.com");
        customer.setPhone("555-1234");
        customer.setNationalId("NID123456");
        customer.setAddress("456 Oak Avenue");

        boolean registered = authService.registerCustomer(customer, "password123");
        System.out.println(registered
                ? "Customer registered successfully."
                : "Customer may already exist. Skipping.");

        // -------------------------------------------------------
        // TEST 4 — Customer login
        // -------------------------------------------------------
        System.out.println("\n=== Test 4: Customer Login ===");
        boolean customerLogin = authService.loginCustomer(
                "john.doe@email.com", "password123", 1);
        System.out.println("Customer login success: " + customerLogin);
        if (customerLogin) {
            System.out.println("Logged in as: "
                    + Session.getInstance().getLoggedInCustomer().getFullName());
        }

        // -------------------------------------------------------
        // TEST 5 — Request a new account
        // -------------------------------------------------------
        System.out.println("\n=== Test 5: Request New Account ===");
        if (Session.getInstance().getLoggedInCustomer() == null) {
            System.out.println("SKIP — No customer logged in.");
        } else {
            int customerId = Session.getInstance().getLoggedInCustomer().getCustomerId();
            boolean requested = accountService.requestNewAccount(customerId, 1, "savings");
            System.out.println("Account request submitted: " + requested);
        }

        // -------------------------------------------------------
        // TEST 6 — Admin approves the account
        // -------------------------------------------------------
        System.out.println("\n=== Test 6: Manager Approves Account ===");
        authService.loginManager("manager", "test123", 1);
        int managerId = Session.getInstance().getLoggedInManager().getManagerId();
        List<Account> pending = accountService.getPendingAccountsForBranch(1);

        if (pending.isEmpty()) {
            System.out.println("No pending accounts found.");
        } else {
            accountId = pending.getFirst().getAccountId();
            boolean approved = accountService.approveAccount(accountId, managerId);
            System.out.println("Account approved: " + approved);
            System.out.println("Account number: "
                    + accountService.formatAccountNumber(accountId));
        }

        // -------------------------------------------------------
        // TEST 7 — Deposit
        // -------------------------------------------------------
        System.out.println("\n=== Test 7: Deposit ===");
        if (accountId == -1) {
            System.out.println("SKIP — No account available.");
        } else {
            boolean deposited = transactionService.deposit(
                    accountId, new BigDecimal("1000.00"), "Initial deposit");
            System.out.println("Deposit success: " + deposited);
            if (deposited) {
                Account updated = accountService.findAccountById(accountId);
                System.out.println("New balance: $" + updated.getBalance());
            }
        }

        // -------------------------------------------------------
        // TEST 8 — Withdrawal
        // -------------------------------------------------------
        System.out.println("\n=== Test 8: Withdrawal ===");
        if (accountId == -1) {
            System.out.println("SKIP — No account available.");
        } else {
            boolean withdrawn = transactionService.withdraw(
                    accountId, new BigDecimal("200.00"), "ATM withdrawal");
            System.out.println("Withdrawal success: " + withdrawn);
            if (withdrawn) {
                Account updated = accountService.findAccountById(accountId);
                System.out.println("New balance: $" + updated.getBalance());
            }
        }

        // -------------------------------------------------------
        // TEST 9 — Insufficient funds
        // -------------------------------------------------------
        System.out.println("\n=== Test 9: Insufficient Funds ===");
        if (accountId == -1) {
            System.out.println("SKIP — No account available.");
        } else {
            boolean withdrawn = transactionService.withdraw(
                    accountId, new BigDecimal("99999.00"), "Large withdrawal");
            System.out.println(withdrawn
                    ? "FAIL — Should have been rejected."
                    : "PASS — Correctly rejected insufficient funds.");
        }

        // -------------------------------------------------------
        // TEST 10 — Transaction history
        // -------------------------------------------------------
        System.out.println("\n=== Test 10: Transaction History ===");
        if (accountId == -1) {
            System.out.println("SKIP — No account available.");
        } else {
            var history = transactionService.getTransactionHistory(accountId);
            System.out.println("Transactions found: " + history.size());
            history.forEach(t -> System.out.println("  "
                    + t.getTransactionType()
                    + " | $" + t.getAmount()
                    + " | Balance after: $" + t.getBalanceAfter()
                    + " | " + t.getTimestamp()));
        }

        // -------------------------------------------------------
        // TEST 11 — Apply for a card
        // -------------------------------------------------------
        System.out.println("\n=== Test 11: Apply For Card ===");
        if (accountId == -1) {
            System.out.println("SKIP — No account available.");
        } else {
            boolean applied = cardService.applyForCard(accountId, "debit");
            System.out.println("Card application submitted: " + applied);
            if (applied) {
                List<Card> cards = cardService.getCardsForAccount(accountId);
                if (!cards.isEmpty()) {
                    cardId = cards.getFirst().getCardId();
                }
            }
        }

        // -------------------------------------------------------
        // TEST 12 — Admin approves the card
        // -------------------------------------------------------
        System.out.println("\n=== Test 12: Manager Approves Card ===");
        if (cardId == -1) {
            System.out.println("SKIP — No card application found.");
        } else {
            authService.loginManager("manager", "test123", 1);
            int managerIdForCard = Session.getInstance().getLoggedInManager().getManagerId();
            boolean cardApproved = cardService.approveCard(cardId, managerIdForCard);
            System.out.println("Card approved: " + cardApproved);
        }

        // -------------------------------------------------------
        // TEST 13 — Block and unblock account
        // -------------------------------------------------------
        System.out.println("\n=== Test 13: Block and Unblock Account ===");
        if (accountId == -1) {
            System.out.println("SKIP — No account available.");
        } else {
            boolean blocked = accountService.blockAccount(accountId);
            System.out.println("Account blocked: " + blocked);

            boolean unblocked = accountService.unblockAccount(accountId);
            System.out.println("Account unblocked: " + unblocked);
        }

        // -------------------------------------------------------
        // TEST 14 — Fraud detection
        // -------------------------------------------------------
        System.out.println("\n=== Test 14: Fraud Detection ===");
        if (accountId == -1) {
            System.out.println("SKIP — No account available.");
        } else {
            FraudDetectionService.FraudCheckResult result =
                    fraudService.analyze(accountId, 1,
                            new BigDecimal("99999.00"), "withdrawal");

            System.out.println("Fraud check completed.");
            System.out.println("Flagged: " + result.isFlagged());
            System.out.println("Blocked: " + result.blocked);

            if (result.isFlagged()) {
                result.triggeredRules.forEach(r ->
                        System.out.println("Rule triggered: " + r));
            }

            List<FraudAlert> alerts = fraudService.getOpenAlertsForBranch(1);
            System.out.println("Open alerts for branch: " + alerts.size());
        }

        // -------------------------------------------------------
        // TEST 15 — Admin view all accounts
        // -------------------------------------------------------
        System.out.println("\n=== Test 15: Manager View All Accounts ===");
        List<Account> allAccounts = accountService.getAllAccountsForBranch(1);
        System.out.println("Total accounts for branch: " + allAccounts.size());
        allAccounts.forEach(a -> System.out.println("  "
                + accountService.formatAccountNumber(a.getAccountId())
                + " | " + a.getAccountType()
                + " | $" + a.getBalance()
                + " | " + a.getStatus()));

        System.out.println("\n=== ALL TESTS COMPLETE ===");
    }
}