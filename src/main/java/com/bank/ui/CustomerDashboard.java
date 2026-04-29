package com.bank.ui;

import com.bank.db.Session;
import com.bank.models.*;
import com.bank.services.*;
import com.bank.ui.utils.UIConstants;
import com.bank.ui.utils.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;

public class CustomerDashboard extends JFrame {

    // Services
    private AccountService          accountService;
    private TransactionService      transactionService;
    private CardService             cardService;
    private AuthService             authService;
    private AccountBalanceOverviewService
            balanceOverviewService;
    private CardExpiryService       cardExpiryService;

    // Session data
    private Customer loggedInCustomer;
    private int      branchId;

    // UI Components
    private JPanel   contentPanel;
    private JPanel   sidebarPanel;
    private String   activeMenu = "overview";

    // Sidebar buttons
    private JButton  btnOverview;
    private JButton  btnAccounts;
    private JButton  btnTransactions;
    private JButton  btnTransfer;
    private JButton  btnCards;
    private JButton  btnLogout;

    public CustomerDashboard() {
        this.accountService =
                new AccountService();
        this.transactionService =
                new TransactionService();
        this.cardService =
                new CardService();
        this.authService =
                new AuthService();
        this.balanceOverviewService =
                new AccountBalanceOverviewService();
        this.cardExpiryService =
                new CardExpiryService();
        this.loggedInCustomer =
                Session.getInstance()
                        .getLoggedInCustomer();
        this.branchId =
                Session.getInstance()
                        .getSelectedBranch()
                        .getBranchId();

        initComponents();
        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);
        setSize(UIConstants.WINDOW_WIDTH,
                UIConstants.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setTitle(UIConstants.BANK_NAME
                + " — Customer Portal");
        setMinimumSize(new Dimension(900, 600));
    }

    // -------------------------------------------------------
    // INIT COMPONENTS
    // -------------------------------------------------------
    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        add(createHeader(),
                BorderLayout.NORTH);

        // Main content area
        JPanel mainArea = new JPanel(
                new BorderLayout());
        mainArea.setBackground(
                UIConstants.BACKGROUND);

        // Sidebar
        sidebarPanel = createSidebar();
        mainArea.add(sidebarPanel,
                BorderLayout.WEST);

        // Content panel
        contentPanel = new JPanel(
                new BorderLayout());
        contentPanel.setBackground(
                UIConstants.BACKGROUND);
        mainArea.add(contentPanel,
                BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);

        // Load default panel
        showPanel("overview");
    }

    // -------------------------------------------------------
    // HEADER
    // -------------------------------------------------------
    private JPanel createHeader() {
        JPanel header = new JPanel(
                new BorderLayout()) {
            @Override
            protected void paintComponent(
                    Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setColor(
                        UIConstants.PRIMARY_DARK);
                g2.fillRect(0, 0,
                        getWidth(), getHeight());
                // Gold bottom border
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, getHeight() - 1,
                        getWidth(),
                        getHeight() - 1);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(
                0, UIConstants.HEADER_HEIGHT));
        header.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 20, 0, 20));

        // Left — bank name
        JLabel bankLabel = new JLabel(
                UIConstants.BANK_NAME);
        bankLabel.setFont(new Font(
                "Georgia", Font.BOLD, 16));
        bankLabel.setForeground(
                UIConstants.TEXT_LIGHT);
        header.add(bankLabel,
                BorderLayout.WEST);

        // Center — portal label
        JLabel portalLabel = new JLabel(
                "CUSTOMER PORTAL",
                SwingConstants.CENTER);
        portalLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        portalLabel.setForeground(
                UIConstants.GOLD_LIGHT);
        header.add(portalLabel,
                BorderLayout.CENTER);

        // Right — customer name
        String name = loggedInCustomer != null
                ? loggedInCustomer.getFullName()
                : "Customer";
        JLabel nameLabel = new JLabel(
                "Welcome, " + name + "  ",
                SwingConstants.RIGHT);
        nameLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 12));
        nameLabel.setForeground(
                UIConstants.GOLD_LIGHT);
        header.add(nameLabel,
                BorderLayout.EAST);

        return header;
    }

    // -------------------------------------------------------
    // SIDEBAR
    // -------------------------------------------------------
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setColor(
                        UIConstants.SIDEBAR_BG);
                g2.fillRect(0, 0,
                        getWidth(), getHeight());
                // Gold right border
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(getWidth() - 1, 0,
                        getWidth() - 1,
                        getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(
                sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(
                UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 0, 20, 0));

        // Customer info section
        JPanel customerInfo = new JPanel();
        customerInfo.setLayout(new BoxLayout(
                customerInfo,
                BoxLayout.Y_AXIS));
        customerInfo.setOpaque(false);
        customerInfo.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 16, 16, 16));
        customerInfo.setAlignmentX(
                Component.LEFT_ALIGNMENT);

        // Avatar circle
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.GOLD);
                g2.fillOval(0, 0,
                        getWidth(), getHeight());
                g2.setColor(
                        UIConstants.PRIMARY_DARK);
                g2.setFont(new Font(
                        "Georgia",
                        Font.BOLD, 20));
                String initials = loggedInCustomer
                        != null
                        ? String.valueOf(
                        loggedInCustomer
                                .getFirstName()
                                .charAt(0))
                        : "C";
                FontMetrics fm =
                        g2.getFontMetrics();
                int x = (getWidth()
                        - fm.stringWidth(
                        initials)) / 2;
                int y = (getHeight()
                        + fm.getAscent()
                        - fm.getDescent()) / 2;
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(
                new Dimension(48, 48));
        avatar.setMaximumSize(
                new Dimension(48, 48));

        JPanel avatarWrapper = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT, 0, 0));
        avatarWrapper.setOpaque(false);
        avatarWrapper.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        avatarWrapper.add(avatar);
        customerInfo.add(avatarWrapper);
        customerInfo.add(
                Box.createVerticalStrut(8));

        String fullName = loggedInCustomer != null
                ? loggedInCustomer.getFullName()
                : "Customer";
        JLabel nameLabel = new JLabel(fullName);
        nameLabel.setFont(new Font(
                "Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(
                UIConstants.TEXT_LIGHT);
        nameLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        customerInfo.add(nameLabel);

        JLabel roleLabel = new JLabel(
                "Personal Banking");
        roleLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(
                UIConstants.TEXT_MUTED);
        roleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        customerInfo.add(roleLabel);

        sidebar.add(customerInfo);

        // Gold divider
        sidebar.add(createSidebarDivider());
        sidebar.add(
                Box.createVerticalStrut(8));

        // Navigation label
        JLabel navLabel = new JLabel(
                "  NAVIGATION");
        navLabel.setFont(new Font(
                "Segoe UI", Font.BOLD, 10));
        navLabel.setForeground(
                UIConstants.TEXT_MUTED);
        navLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        sidebar.add(navLabel);
        sidebar.add(
                Box.createVerticalStrut(4));

        // Menu buttons
        btnOverview = createSidebarButton(
                "Overview", "overview");
        btnAccounts = createSidebarButton(
                "My Accounts", "accounts");
        btnTransactions = createSidebarButton(
                "Transactions", "transactions");
        btnTransfer = createSidebarButton(
                "Fund Transfer", "transfer");
        btnCards = createSidebarButton(
                "My Cards", "cards");

        sidebar.add(btnOverview);
        sidebar.add(btnAccounts);
        sidebar.add(btnTransactions);
        sidebar.add(btnTransfer);
        sidebar.add(btnCards);

        // Push logout to bottom
        sidebar.add(Box.createVerticalGlue());

        sidebar.add(createSidebarDivider());
        sidebar.add(
                Box.createVerticalStrut(8));

        btnLogout = createSidebarButton(
                "Sign Out", "logout");
        sidebar.add(btnLogout);

        return sidebar;
    }

    // -------------------------------------------------------
    // SIDEBAR BUTTON
    // -------------------------------------------------------
    private JButton createSidebarButton(
            String text, String key) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isActive =
                        activeMenu.equals(key);
                boolean isLogout =
                        key.equals("logout");

                if (isActive && !isLogout) {
                    g2.setColor(
                            UIConstants
                                    .SIDEBAR_ACTIVE_BG);
                    g2.fillRect(0, 0,
                            getWidth(),
                            getHeight());
                    // Gold left indicator
                    g2.setColor(UIConstants.GOLD);
                    g2.fillRect(0, 0,
                            3, getHeight());
                } else if (getModel()
                        .isRollover()) {
                    g2.setColor(new Color(
                            255, 255, 255, 20));
                    g2.fillRect(0, 0,
                            getWidth(),
                            getHeight());
                }

                g2.setFont(isActive
                        ? UIConstants
                        .FONT_SIDEBAR_ACTIVE
                        : UIConstants.FONT_SIDEBAR);
                g2.setColor(isLogout
                        ? UIConstants.DANGER
                        .brighter()
                        : isActive
                        ? UIConstants.GOLD
                        : UIConstants
                        .TEXT_LIGHT);

                g2.drawString(getText(),
                        20, getHeight() / 2
                                + g2.getFontMetrics()
                                .getAscent() / 2 - 2);
                g2.dispose();
            }
        };
        button.setPreferredSize(
                new Dimension(
                        UIConstants.SIDEBAR_WIDTH, 44));
        button.setMaximumSize(
                new Dimension(
                        UIConstants.SIDEBAR_WIDTH, 44));
        button.setMinimumSize(
                new Dimension(
                        UIConstants.SIDEBAR_WIDTH, 44));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(
                SwingConstants.LEFT);
        button.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(
                Cursor.HAND_CURSOR));
        button.addActionListener(
                e -> handleMenuClick(key));
        return button;
    }

    private JPanel createSidebarDivider() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                g.setColor(new Color(
                        255, 255, 255, 30));
                g.drawLine(16, 0,
                        getWidth() - 16, 0);
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(
                new Dimension(
                        UIConstants.SIDEBAR_WIDTH, 1));
        divider.setMaximumSize(
                new Dimension(
                        UIConstants.SIDEBAR_WIDTH, 1));
        divider.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        return divider;
    }

    // -------------------------------------------------------
    // MENU CLICK HANDLER
    // -------------------------------------------------------
    private void handleMenuClick(String key) {
        if (key.equals("logout")) {
            handleLogout();
            return;
        }
        activeMenu = key;
        refreshSidebarButtons();
        showPanel(key);
    }

    private void refreshSidebarButtons() {
        btnOverview.repaint();
        btnAccounts.repaint();
        btnTransactions.repaint();
        btnTransfer.repaint();
        btnCards.repaint();
    }

    private void showPanel(String key) {
        contentPanel.removeAll();
        switch (key) {
            case "overview":
                contentPanel.add(
                        createOverviewPanel(),
                        BorderLayout.CENTER);
                break;
            case "accounts":
                contentPanel.add(
                        createAccountsPanel(),
                        BorderLayout.CENTER);
                break;
            case "transactions":
                contentPanel.add(
                        createTransactionsPanel(),
                        BorderLayout.CENTER);
                break;
            case "transfer":
                contentPanel.add(
                        createTransferPanel(),
                        BorderLayout.CENTER);
                break;
            case "cards":
                contentPanel.add(
                        createCardsPanel(),
                        BorderLayout.CENTER);
                break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // -------------------------------------------------------
    // OVERVIEW PANEL
    // -------------------------------------------------------
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground( UIConstants.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Page title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Account Overview");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.PRIMARY);
        titlePanel.add(titleLabel,BorderLayout.WEST);

        String branchName = Session.getInstance().getSelectedBranch().getBranchName();
        JLabel branchLabel = new JLabel(branchName);
        branchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        branchLabel.setForeground( UIConstants.TEXT_SECONDARY);
        titlePanel.add(branchLabel,BorderLayout.EAST);

        panel.add(titlePanel,BorderLayout.NORTH);

        // Summary cards row
        JPanel cardsRow = new JPanel( new GridLayout(1, 3, 16, 0));
        cardsRow.setOpaque(false);

        // Get data
        List<AccountBalanceOverview> overviews =balanceOverviewService.getBalanceOverviewForCustomer(loggedInCustomer.getCustomerId());

        BigDecimal totalBalance =BigDecimal.ZERO;
        int totalAccounts = overviews.size();
        int activeAccounts = 0;

        for (AccountBalanceOverview o: overviews) {
            if (o.getBalance() != null) {
                totalBalance =totalBalance.add(o.getBalance());
            }
            if ("active".equals(o.getStatus())) {
                activeAccounts++;
            }
        }

        // Card 1 — Total Balance
        cardsRow.add(createSummaryCard("Total Balance","$" + String.format("%.2f", totalBalance),UIConstants.PRIMARY,UIConstants.GOLD));

        // Card 2 — Total Accounts
        cardsRow.add(createSummaryCard("Total Accounts",String.valueOf(totalAccounts),UIConstants.SUCCESS,UIConstants.PRIMARY_DARK));

        // Card 3 — Active Accounts
        cardsRow.add(createSummaryCard("Active Accounts", String.valueOf(activeAccounts),UIConstants.INFO,UIConstants.PRIMARY_DARK));

        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setOpaque(false);
        cardsWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        cardsWrapper.add(cardsRow,BorderLayout.CENTER);

        // Center content
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent,BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);
        centerContent.add(cardsWrapper);

        // Account balance overview table
        JPanel tableSection = createCardPanel("Account Balance Overview");
        tableSection.setLayout(new BorderLayout());
        tableSection.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] cols = {"Account Number", "Account Type", "Balance","Status", "Category"};
        DefaultTableModel model =new DefaultTableModel( cols, 0) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };

        for (AccountBalanceOverview o : overviews) {
            model.addRow(new Object[]{
                    o.getAccountNumber(),
                    o.getAccountType().toUpperCase(),
                    "$" + String.format("%.2f", o.getBalance()),o.getStatus() .toUpperCase(),
                    o.getBalanceCategory()
            });
        }

        JTable overviewTable = new JTable(model);
        UIUtils.styleTable(overviewTable);
        overviewTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = UIUtils.createScrollPane(overviewTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));

        tableSection.add(scrollPane,BorderLayout.CENTER);

        JPanel tableSectionWrapper =new JPanel(new BorderLayout());
        tableSectionWrapper.setOpaque(false);
        tableSectionWrapper.add( tableSection,BorderLayout.CENTER);

        centerContent.add(tableSectionWrapper);

        panel.add(centerContent,BorderLayout.CENTER);

        // Quick actions panel
        JPanel quickActions =createQuickActionsPanel();
        panel.add(quickActions, BorderLayout.SOUTH);

        return panel;
    }

    // -------------------------------------------------------
    // SUMMARY CARD
    // -------------------------------------------------------
    private JPanel createSummaryCard(
            String title,
            String value,
            Color accentColor,
            Color valueColor) {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.SURFACE);
                g2.fillRoundRect(0, 0,
                        getWidth(), getHeight(),
                        8, 8);
                // Top color bar
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0,
                        getWidth(), 4, 4, 4);
                // Border
                g2.setColor(UIConstants.BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        8, 8);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(
                card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(
                BorderFactory.createEmptyBorder(
                        16, 16, 16, 16));

        JLabel titleLabel =
                new JLabel(title);
        titleLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        titleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(
                "Georgia", Font.BOLD, 22));
        valueLabel.setForeground(valueColor);
        valueLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        card.add(valueLabel);

        return card;
    }

    // -------------------------------------------------------
    // QUICK ACTIONS PANEL
    // -------------------------------------------------------
    private JPanel createQuickActionsPanel() {
        JPanel panel = createCardPanel(
                "Quick Actions");
        panel.setLayout(new FlowLayout(
                FlowLayout.LEFT, 12, 12));

        JButton depositBtn =
                UIUtils.createSuccessButton(
                        "Deposit");
        depositBtn.addActionListener(
                e -> showDepositDialog());
        panel.add(depositBtn);

        JButton withdrawBtn =
                UIUtils.createWarningButton(
                        "Withdraw");
        withdrawBtn.addActionListener(
                e -> showWithdrawDialog());
        panel.add(withdrawBtn);

        JButton transferBtn =
                UIUtils.createPrimaryButton(
                        "Transfer");
        transferBtn.addActionListener(e -> {
            activeMenu = "transfer";
            refreshSidebarButtons();
            showPanel("transfer");
        });
        panel.add(transferBtn);

        JButton cardBtn =
                UIUtils.createGoldButton(
                        "Apply for Card");
        cardBtn.addActionListener(
                e -> showApplyCardDialog());
        panel.add(cardBtn);

        JButton newAccountBtn =
                UIUtils.createOutlineButton(
                        "New Account");
        newAccountBtn.addActionListener(
                e -> showNewAccountDialog());
        panel.add(newAccountBtn);

        return panel;
    }

    // -------------------------------------------------------
    // ACCOUNTS PANEL
    // -------------------------------------------------------
    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(
                new BorderLayout());
        panel.setBackground(
                UIConstants.BACKGROUND);
        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        24, 24, 24, 24));

        panel.add(createPageTitle(
                        "My Accounts",
                        "All your bank accounts"),
                BorderLayout.NORTH);

        // Get accounts
        List<Account> accounts =
                accountService
                        .getAllAccountsForCustomer(
                                loggedInCustomer
                                        .getCustomerId());

        String[] cols = {"Account Number",
                "Type", "Balance", "Status"};
        DefaultTableModel model =
                new DefaultTableModel(
                        cols, 0) {
                    @Override
                    public boolean isCellEditable(
                            int r, int c) {
                        return false;
                    }
                };

        for (Account a : accounts) {
            model.addRow(new Object[]{
                    accountService
                            .formatAccountNumber(
                            a.getAccountId()),
                    a.getAccountType()
                            .toUpperCase(),
                    "$" + String.format(
                            "%.2f", a.getBalance()),
                    a.getStatus().toUpperCase()
            });
        }

        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        table.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION);

        JScrollPane scroll =
                UIUtils.createScrollPane(table);

        JPanel tableCard = createCardPanel(
                "Account List");
        tableCard.setLayout(
                new BorderLayout());
        tableCard.setBorder(
                BorderFactory.createEmptyBorder(
                        16, 16, 16, 16));
        tableCard.add(scroll,
                BorderLayout.CENTER);

        // Action bar
        JPanel actionBar = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT, 8, 12));
        actionBar.setOpaque(false);

        JButton requestBtn =
                UIUtils.createPrimaryButton(
                        "Request New Account");
        requestBtn.addActionListener(
                e -> showNewAccountDialog());
        actionBar.add(requestBtn);

        tableCard.add(actionBar,
                BorderLayout.SOUTH);

        panel.add(tableCard,
                BorderLayout.CENTER);

        return panel;
    }

    // -------------------------------------------------------
    // TRANSACTIONS PANEL
    // -------------------------------------------------------
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(
                new BorderLayout());
        panel.setBackground(UIConstants.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        panel.add(createPageTitle( "Transaction History","View all transactions across your accounts"),
                BorderLayout.NORTH);

        // Account selector
        List<Account> accounts =accountService.getActiveAccountsForCustomer(loggedInCustomer.getCustomerId());

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selectorPanel.setOpaque(false);
        selectorPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel selectLabel = new JLabel("Select Account:");
        selectLabel.setFont(UIConstants.FONT_BODY);
        selectLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JComboBox<String> accountCombo =UIUtils.createComboBox();
        for (Account a : accounts) {
            accountCombo.addItem(accountService.formatAccountNumber(a.getAccountId())
                            + " — "+ a.getAccountType().toUpperCase());
        }

        selectorPanel.add(selectLabel);
        selectorPanel.add(accountCombo);

        // Transaction table
        String[] cols = {"Date","Type", "Amount","Balance After","Description"};
        DefaultTableModel model =new DefaultTableModel(cols, 0) {
                    @Override
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };

        JTable table = new JTable(model);
        UIUtils.styleTable(table);

        // Load transactions for first account
        if (!accounts.isEmpty()) {
            loadTransactions(model,accounts.get(0).getAccountId());
        }

        // On account change reload transactions
        accountCombo.addActionListener(e -> {
            int idx =accountCombo.getSelectedIndex();
            if (idx >= 0 && idx < accounts.size()) {
                model.setRowCount(0);
                loadTransactions(model,accounts.get(idx).getAccountId());
            }
        });

        JScrollPane scroll =UIUtils.createScrollPane(table);

        JPanel tableCard = createCardPanel("Transactions");
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tableCard.add(selectorPanel,BorderLayout.NORTH);
        tableCard.add(scroll,BorderLayout.CENTER);

        // Action buttons
        JPanel actionBar = new JPanel( new FlowLayout(FlowLayout.LEFT, 8, 12));
        actionBar.setOpaque(false);

        JButton depositBtn =UIUtils.createSuccessButton("Deposit");
        depositBtn.addActionListener( e -> showDepositDialog());

        JButton withdrawBtn =UIUtils.createWarningButton( "Withdraw");
        withdrawBtn.addActionListener(e -> showWithdrawDialog());

        actionBar.add(depositBtn);
        actionBar.add(withdrawBtn);
        tableCard.add(actionBar,BorderLayout.SOUTH);

        panel.add(tableCard,BorderLayout.CENTER);

        return panel;
    }

    private void loadTransactions(
            DefaultTableModel model,
            int accountId) {
        List<Transaction> transactions =
                transactionService
                        .getTransactionHistory(
                                accountId);
        for (Transaction t : transactions) {
            String timeAtStr = t.getTimestamp() != null
                    ? t.getTimestamp().toString()
                    : "N/A";
            // Trim to 19 chars only if longer (handles LocalDate, Timestamp variants, etc.)
            if (timeAtStr.length() > 19) {
                timeAtStr = timeAtStr.substring(0, 19);
            }
            model.addRow(new Object[]{
                    timeAtStr,
                    t.getTransactionType()
                            .replace("_", " ")
                            .toUpperCase(),
                    "$" + String.format(
                            "%.2f", t.getAmount()),
                    "$" + String.format(
                            "%.2f", t.getBalanceAfter()),
                    t.getDescription() != null
                            ? t.getDescription()
                            : ""
            });
        }
    }

    // -------------------------------------------------------
    // TRANSFER PANEL
    // -------------------------------------------------------
    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(
                new BorderLayout());
        panel.setBackground(
                UIConstants.BACKGROUND);
        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        24, 24, 24, 24));

        panel.add(createPageTitle(
                        "Fund Transfer",
                        "Transfer money between accounts"),
                BorderLayout.NORTH);

        // Transfer form card
        JPanel formCard = createCardPanel(
                "New Transfer");
        formCard.setLayout(
                new GridBagLayout());
        formCard.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 24, 20, 24));

        GridBagConstraints c =
                new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.insets = new Insets(6, 0, 6, 0);

        // From account
        List<Account> accounts =
                accountService
                        .getActiveAccountsForCustomer(
                                loggedInCustomer
                                        .getCustomerId());

        JLabel fromLabel = new JLabel(
                "From Account");
        fromLabel.setFont(UIConstants.FONT_BODY);
        fromLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        c.gridy = 0;
        formCard.add(fromLabel, c);

        JComboBox<String> fromCombo =
                UIUtils.createComboBox();
        fromCombo.setPreferredSize(
                new Dimension(400,
                        UIConstants.INPUT_HEIGHT));
        for (Account a : accounts) {
            fromCombo.addItem(
                    accountService
                            .formatAccountNumber(
                                    a.getAccountId()));
        }
        c.gridy = 1;
        formCard.add(fromCombo, c);

        // To account number
        JLabel toLabel = new JLabel(
                "To Account Number");
        toLabel.setFont(UIConstants.FONT_BODY);
        toLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        c.gridy = 2;
        c.insets = new Insets(
                16, 0, 6, 0);
        formCard.add(toLabel, c);

        JTextField toAccountField =
                UIUtils.createTextField(
                        "e.g. ACC000000002");
        toAccountField.setPreferredSize(
                new Dimension(400,
                        UIConstants.INPUT_HEIGHT));
        c.gridy = 3;
        c.insets = new Insets(0, 0, 6, 0);
        formCard.add(toAccountField, c);

        // Amount
        JLabel amountLabel = new JLabel(
                "Transfer Amount ($)");
        amountLabel.setFont(
                UIConstants.FONT_BODY);
        amountLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        c.gridy = 4;
        c.insets = new Insets(
                16, 0, 6, 0);
        formCard.add(amountLabel, c);

        JTextField amountField =
                UIUtils.createTextField(
                        "0.00");
        amountField.setPreferredSize(
                new Dimension(400,
                        UIConstants.INPUT_HEIGHT));
        c.gridy = 5;
        c.insets = new Insets(0, 0, 6, 0);
        formCard.add(amountField, c);

        // Status label
        JLabel transferStatus = new JLabel(" ");
        transferStatus.setFont(
                UIConstants.FONT_SMALL);
        transferStatus.setForeground(
                UIConstants.DANGER);
        c.gridy = 6;
        c.insets = new Insets(4, 0, 4, 0);
        formCard.add(transferStatus, c);

        // Transfer button
        JButton transferBtn =
                UIUtils.createGoldButton(
                        "CONFIRM TRANSFER");
        transferBtn.setPreferredSize(
                new Dimension(400, 44));
        transferBtn.addActionListener(e -> {
            String toAccStr =
                    toAccountField
                            .getText().trim();
            String amtStr =
                    amountField.getText().trim();

            if (toAccStr.isEmpty()
                    || amtStr.isEmpty()) {
                transferStatus.setText(
                        "Please fill in all fields.");
                return;
            }

            try {
                BigDecimal amount =
                        new BigDecimal(amtStr);
                int fromIdx = fromCombo
                        .getSelectedIndex();
                if (fromIdx < 0
                        || fromIdx >=
                        accounts.size()) {
                    transferStatus.setText(
                            "Please select "
                                    + "a source account.");
                    return;
                }
                int fromId = accounts
                        .get(fromIdx)
                        .getAccountId();

                Account toAccount =
                        accountService
                                .findAccountByFormattedNumber(
                                        toAccStr);
                if (toAccount == null) {
                    transferStatus.setText(
                            "Destination account "
                                    + "not found.");
                    return;
                }

                if (UIUtils.showConfirm(
                        panel,
                        "Transfer $"
                                + String.format(
                                "%.2f", amount)
                                + " to "
                                + toAccStr
                                + "?")) {
                    boolean ok =
                            transactionService
                                    .transfer(fromId,
                                            toAccount
                                                    .getAccountId(),
                                            amount);
                    if (ok) {
                        UIUtils.showSuccess(
                                panel,
                                "Transfer completed "
                                        + "successfully.");
                        toAccountField
                                .setText("");
                        amountField.setText("");
                        transferStatus
                                .setText(" ");
                        showPanel("transfer");
                    } else {
                        transferStatus.setText(
                                "Transfer failed. "
                                        + "Check balance "
                                        + "or account status.");
                    }
                }
            } catch (NumberFormatException ex) {
                transferStatus.setText(
                        "Please enter a valid amount.");
            }
        });
        c.gridy = 7;
        c.insets = new Insets(8, 0, 0, 0);
        formCard.add(transferBtn, c);

        JPanel formWrapper = new JPanel(
                new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.add(formCard,
                BorderLayout.NORTH);

        panel.add(formWrapper,
                BorderLayout.CENTER);

        return panel;
    }

    // -------------------------------------------------------
    // CARDS PANEL
    // -------------------------------------------------------
    private JPanel createCardsPanel() {
        JPanel panel = new JPanel(
                new BorderLayout());
        panel.setBackground(
                UIConstants.BACKGROUND);
        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        24, 24, 24, 24));

        panel.add(createPageTitle(
                        "My Cards",
                        "View and manage your cards"),
                BorderLayout.NORTH);

        // Get all accounts to find cards
        List<Account> accounts =
                accountService
                        .getActiveAccountsForCustomer(
                                loggedInCustomer
                                        .getCustomerId());

        String[] cols = {"Card Number",
                "Type", "Account",
                "Expiry Date",
                "Expiry Status", "Status"};
        DefaultTableModel model =
                new DefaultTableModel(
                        cols, 0) {
                    @Override
                    public boolean isCellEditable(
                            int r, int c) {
                        return false;
                    }
                };

        // Load card expiry status
        List<CardExpiryStatus> cards =
                cardExpiryService
                        .getCardExpiryStatusForCustomer(
                                loggedInCustomer
                                        .getCustomerId());

        for (CardExpiryStatus c : cards) {
            model.addRow(new Object[]{
                    c.getCardNumber(),
                    c.getCardType()
                            .toUpperCase(),
                    c.getAccountNumber(),
                    c.getExpiryDate() != null
                            ? c.getExpiryDate()
                            .toString()
                            : "N/A",
                    c.getExpiryStatus(),
                    c.getStatus().toUpperCase()
            });
        }

        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        table.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION);

        JScrollPane scroll =
                UIUtils.createScrollPane(table);

        JPanel tableCard = createCardPanel(
                "Card Overview");
        tableCard.setLayout(
                new BorderLayout());
        tableCard.setBorder(
                BorderFactory.createEmptyBorder(
                        16, 16, 16, 16));
        tableCard.add(scroll,
                BorderLayout.CENTER);

        // Action bar
        JPanel actionBar = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT, 8, 12));
        actionBar.setOpaque(false);

        JButton applyBtn =
                UIUtils.createGoldButton(
                        "Apply for New Card");
        applyBtn.addActionListener(
                e -> showApplyCardDialog());
        actionBar.add(applyBtn);

        tableCard.add(actionBar,
                BorderLayout.SOUTH);

        panel.add(tableCard,
                BorderLayout.CENTER);

        return panel;
    }

    // -------------------------------------------------------
    // DIALOGS
    // -------------------------------------------------------
    private void showDepositDialog() {
        List<Account> accounts =
                accountService
                        .getActiveAccountsForCustomer(
                                loggedInCustomer
                                        .getCustomerId());
        if (accounts.isEmpty()) {
            UIUtils.showError(this,
                    "No active accounts found.");
            return;
        }

        JPanel form = new JPanel(
                new GridLayout(4, 2, 10, 10));

        JLabel acctLabel = new JLabel(
                "Account:");
        JComboBox<String> acctCombo =
                new JComboBox<>();
        for (Account a : accounts) {
            acctCombo.addItem(
                    accountService
                            .formatAccountNumber(
                                    a.getAccountId()));
        }

        JLabel amtLabel = new JLabel(
                "Amount ($):");
        JTextField amtField =
                new JTextField();

        JLabel descLabel = new JLabel(
                "Description:");
        JTextField descField =
                new JTextField("Deposit");

        form.add(acctLabel);
        form.add(acctCombo);
        form.add(amtLabel);
        form.add(amtField);
        form.add(descLabel);
        form.add(descField);

        int result =
                JOptionPane.showConfirmDialog(
                        this, form,
                        "Make a Deposit",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                BigDecimal amount =
                        new BigDecimal(
                                amtField.getText()
                                        .trim());
                int idx = acctCombo
                        .getSelectedIndex();
                int accountId = accounts
                        .get(idx)
                        .getAccountId();
                boolean ok =
                        transactionService
                                .deposit(accountId,
                                        amount,
                                        descField.getText());
                if (ok) {
                    UIUtils.showSuccess(this,
                            "Deposit of $"
                                    + String.format(
                                    "%.2f", amount)
                                    + " successful.");
                    showPanel(activeMenu);
                } else {
                    UIUtils.showError(this,
                            "Deposit failed. "
                                    + "Please try again.");
                }
            } catch (NumberFormatException e) {
                UIUtils.showError(this,
                        "Invalid amount.");
            }
        }
    }

    private void showWithdrawDialog() {
        List<Account> accounts =
                accountService
                        .getActiveAccountsForCustomer(
                                loggedInCustomer
                                        .getCustomerId());
        if (accounts.isEmpty()) {
            UIUtils.showError(this,
                    "No active accounts found.");
            return;
        }

        JPanel form = new JPanel(
                new GridLayout(4, 2, 10, 10));

        JLabel acctLabel = new JLabel(
                "Account:");
        JComboBox<String> acctCombo =
                new JComboBox<>();
        for (Account a : accounts) {
            acctCombo.addItem(
                    accountService
                            .formatAccountNumber(
                                    a.getAccountId())
                            + " ($"
                            + String.format(
                            "%.2f", a.getBalance())
                            + ")");
        }

        JLabel amtLabel = new JLabel(
                "Amount ($):");
        JTextField amtField =
                new JTextField();

        JLabel descLabel = new JLabel(
                "Description:");
        JTextField descField =
                new JTextField("Withdrawal");

        form.add(acctLabel);
        form.add(acctCombo);
        form.add(amtLabel);
        form.add(amtField);
        form.add(descLabel);
        form.add(descField);

        int result =
                JOptionPane.showConfirmDialog(
                        this, form,
                        "Make a Withdrawal",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                BigDecimal amount =
                        new BigDecimal(
                                amtField.getText()
                                        .trim());
                int idx = acctCombo
                        .getSelectedIndex();
                int accountId = accounts
                        .get(idx)
                        .getAccountId();
                boolean ok =
                        transactionService
                                .withdraw(accountId,
                                        amount,
                                        descField.getText());
                if (ok) {
                    UIUtils.showSuccess(this,
                            "Withdrawal of $"
                                    + String.format(
                                    "%.2f", amount)
                                    + " successful.");
                    showPanel(activeMenu);
                } else {
                    UIUtils.showError(this,
                            "Withdrawal failed. "
                                    + "Check balance.");
                }
            } catch (NumberFormatException e) {
                UIUtils.showError(this,
                        "Invalid amount.");
            }
        }
    }

    private void showApplyCardDialog() {
        List<Account> accounts =
                accountService
                        .getActiveAccountsForCustomer(
                                loggedInCustomer
                                        .getCustomerId());
        if (accounts.isEmpty()) {
            UIUtils.showError(this,
                    "No active accounts. "
                            + "Please open an account first.");
            return;
        }

        JPanel form = new JPanel(
                new GridLayout(4, 2, 10, 10));

        JLabel acctLabel = new JLabel(
                "Account:");
        JComboBox<String> acctCombo =
                new JComboBox<>();
        for (Account a : accounts) {
            acctCombo.addItem(
                    accountService
                            .formatAccountNumber(
                                    a.getAccountId()));
        }

        JLabel typeLabel = new JLabel(
                "Card Type:");
        JComboBox<String> typeCombo =
                new JComboBox<>(new String[]{
                        "debit", "credit"});

        form.add(acctLabel);
        form.add(acctCombo);
        form.add(typeLabel);
        form.add(typeCombo);

        int result =
                JOptionPane.showConfirmDialog(
                        this, form,
                        "Apply for a New Card",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int idx = acctCombo
                    .getSelectedIndex();
            int accountId = accounts
                    .get(idx)
                    .getAccountId();
            String cardType =
                    (String) typeCombo
                            .getSelectedItem();
            boolean ok = cardService
                    .applyForCard(
                            accountId, cardType);
            if (ok) {
                UIUtils.showSuccess(this,
                        "Card application submitted. "
                                + "Pending manager approval.");
                showPanel(activeMenu);
            } else {
                UIUtils.showError(this,
                        "Card application failed.");
            }
        }
    }

    private void showNewAccountDialog() {
        JPanel form = new JPanel(
                new GridLayout(2, 2, 10, 10));

        JLabel typeLabel = new JLabel(
                "Account Type:");
        JComboBox<String> typeCombo =
                new JComboBox<>(new String[]{
                        "savings", "checking"});

        form.add(typeLabel);
        form.add(typeCombo);

        int result =
                JOptionPane.showConfirmDialog(
                        this, form,
                        "Request New Account",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String accountType =
                    (String) typeCombo
                            .getSelectedItem();
            boolean ok =
                    accountService
                            .requestNewAccount(
                                    loggedInCustomer
                                            .getCustomerId(),
                                    branchId,
                                    accountType);
            if (ok) {
                UIUtils.showSuccess(this,
                        "Account request submitted. "
                                + "Pending manager approval.");
                showPanel(activeMenu);
            } else {
                UIUtils.showError(this,
                        "Account request failed.");
            }
        }
    }

    // -------------------------------------------------------
    // LOGOUT
    // -------------------------------------------------------
    private void handleLogout() {
        if (UIUtils.showConfirm(this,
                "Are you sure you want "
                        + "to sign out?")) {
            authService.logout();
            BranchSelectionFrame frame =
                    new BranchSelectionFrame();
            frame.setVisible(true);
            this.dispose();
        }
    }

    // -------------------------------------------------------
    // HELPER METHODS
    // -------------------------------------------------------
    private JPanel createPageTitle(
            String title, String subtitle) {
        JPanel panel = new JPanel(
                new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 0, 20, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(
                UIConstants.FONT_TITLE);
        titleLabel.setForeground(
                UIConstants.PRIMARY);
        panel.add(titleLabel,
                BorderLayout.WEST);

        JLabel subtitleLabel =
                new JLabel(subtitle);
        subtitleLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        panel.add(subtitleLabel,
                BorderLayout.EAST);

        return panel;
    }

    private JPanel createCardPanel(
            String title) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.SURFACE);
                g2.fillRoundRect(0, 0,
                        getWidth(), getHeight(),
                        6, 6);
                g2.setColor(UIConstants.BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        6, 6);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        // Card title bar
        JPanel titleBar = new JPanel(
                new BorderLayout()) {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setColor(UIConstants
                        .GOLD_SUBTLE);
                g2.fillRoundRect(0, 0,
                        getWidth(),
                        getHeight(), 6, 6);
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(0,
                        getHeight() - 1,
                        getWidth(),
                        getHeight() - 1);
                g2.dispose();
            }
        };
        titleBar.setOpaque(false);
        titleBar.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 16, 10, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(
                "Georgia", Font.BOLD, 13));
        titleLabel.setForeground(
                UIConstants.PRIMARY);
        titleBar.add(titleLabel,
                BorderLayout.WEST);

        panel.add(titleBar, BorderLayout.NORTH);

        return panel;
    }
}