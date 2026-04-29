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
import java.util.List;

public class AdminDashboard extends JFrame {

    // Services
    private AccountService       accountService;
    private CustomerService      customerService;
    private CardService          cardService;
    private FraudDetectionService fraudService;
    private AuthService          authService;
    private ApprovalService      approvalService;
    private BranchActivityService branchActivityService;
    private ProcedureService     procedureService;

    // Session data
    private Manager loggedInManager;
    private int     branchId;

    // UI Components
    private JPanel  contentPanel;
    private String  activeMenu = "overview";

    // Sidebar buttons
    private JButton btnOverview;
    private JButton btnAccounts;
    private JButton btnCustomers;
    private JButton btnApprovals;
    private JButton btnCards;
    private JButton btnFraud;
    private JButton btnLogout;

    public AdminDashboard() {
        this.accountService =
                new AccountService();
        this.customerService =
                new CustomerService();
        this.cardService =
                new CardService();
        this.fraudService =
                new FraudDetectionService();
        this.authService =
                new AuthService();
        this.approvalService =
                new ApprovalService();
        this.branchActivityService =
                new BranchActivityService();
        this.procedureService =
                new ProcedureService();
        this.loggedInManager =
                Session.getInstance()
                        .getLoggedInManager();
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
                + " — Manager Portal");
        setMinimumSize(
                new Dimension(900, 600));
    }

    // -------------------------------------------------------
    // INIT COMPONENTS
    // -------------------------------------------------------
    private void initComponents() {
        setLayout(new BorderLayout());
        add(createHeader(),
                BorderLayout.NORTH);

        JPanel mainArea = new JPanel(
                new BorderLayout());
        mainArea.setBackground(
                UIConstants.BACKGROUND);
        mainArea.add(createSidebar(),
                BorderLayout.WEST);

        contentPanel = new JPanel(
                new BorderLayout());
        contentPanel.setBackground(
                UIConstants.BACKGROUND);
        mainArea.add(contentPanel,
                BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);
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

        JLabel bankLabel = new JLabel(
                UIConstants.BANK_NAME);
        bankLabel.setFont(new Font(
                "Georgia", Font.BOLD, 16));
        bankLabel.setForeground(
                UIConstants.TEXT_LIGHT);
        header.add(bankLabel,
                BorderLayout.WEST);

        JLabel portalLabel = new JLabel(
                "MANAGER PORTAL",
                SwingConstants.CENTER);
        portalLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        portalLabel.setForeground(
                UIConstants.GOLD_LIGHT);
        header.add(portalLabel,
                BorderLayout.CENTER);

        // Right side — manager name and branch
        JPanel rightPanel = new JPanel(
                new BorderLayout());
        rightPanel.setOpaque(false);

        String managerName =
                loggedInManager != null
                        ? loggedInManager.getFullName()
                        : "Manager";
        JLabel nameLabel = new JLabel(
                managerName + "  ",
                SwingConstants.RIGHT);
        nameLabel.setFont(new Font(
                "Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(
                UIConstants.GOLD_LIGHT);
        rightPanel.add(nameLabel,
                BorderLayout.NORTH);

        String branchName = Session
                .getInstance()
                .getSelectedBranch()
                .getBranchName();
        JLabel branchLabel = new JLabel(
                branchName + "  ",
                SwingConstants.RIGHT);
        branchLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 10));
        branchLabel.setForeground(
                UIConstants.TEXT_MUTED);
        rightPanel.add(branchLabel,
                BorderLayout.SOUTH);

        header.add(rightPanel,
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

        // Manager info
        JPanel managerInfo = new JPanel();
        managerInfo.setLayout(new BoxLayout(
                managerInfo, BoxLayout.Y_AXIS));
        managerInfo.setOpaque(false);
        managerInfo.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 16, 16, 16));
        managerInfo.setAlignmentX(
                Component.LEFT_ALIGNMENT);

        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // Navy background
                g2.setColor(
                        UIConstants.PRIMARY_LIGHT);
                g2.fillOval(0, 0,
                        getWidth(), getHeight());
                // Gold border
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(1, 1,
                        getWidth() - 2,
                        getHeight() - 2);
                // Manager initial
                g2.setColor(UIConstants.GOLD);
                g2.setFont(new Font(
                        "Georgia",
                        Font.BOLD, 20));
                String initial =
                        loggedInManager != null
                                ? String.valueOf(
                                loggedInManager
                                        .getFullName()
                                        .charAt(0))
                                : "M";
                FontMetrics fm =
                        g2.getFontMetrics();
                g2.drawString(initial,
                        (getWidth()
                                - fm.stringWidth(
                                initial)) / 2,
                        (getHeight()
                                + fm.getAscent()
                                - fm.getDescent()) / 2);
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
        managerInfo.add(avatarWrapper);
        managerInfo.add(
                Box.createVerticalStrut(8));

        String fullName =
                loggedInManager != null
                        ? loggedInManager.getFullName()
                        : "Manager";
        JLabel nameLabel = new JLabel(
                fullName);
        nameLabel.setFont(new Font(
                "Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(
                UIConstants.TEXT_LIGHT);
        nameLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        managerInfo.add(nameLabel);

        JLabel roleLabel = new JLabel(
                "Branch Manager");
        roleLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(
                UIConstants.GOLD_LIGHT);
        roleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        managerInfo.add(roleLabel);

        sidebar.add(managerInfo);
        sidebar.add(createSidebarDivider());
        sidebar.add(
                Box.createVerticalStrut(8));

        // Nav label
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

        // Pending count badge
        int pendingCount =
                approvalService
                        .getPendingApprovalCount(
                                branchId);
        int alertCount =
                fraudService
                        .getOpenAlertCount(branchId);

        btnOverview = createSidebarButton(
                "Overview", "overview", 0);
        btnAccounts = createSidebarButton(
                "All Accounts", "accounts", 0);
        btnCustomers = createSidebarButton(
                "Customers", "customers", 0);
        btnApprovals = createSidebarButton(
                "Approvals", "approvals",
                pendingCount);
        btnCards = createSidebarButton(
                "Cards", "cards", 0);
        btnFraud = createSidebarButton(
                "Alerts", "fraud",
                alertCount);

        sidebar.add(btnOverview);
        sidebar.add(btnAccounts);
        sidebar.add(btnCustomers);
        sidebar.add(btnApprovals);
        sidebar.add(btnCards);
        sidebar.add(btnFraud);

        sidebar.add(
                Box.createVerticalGlue());
        sidebar.add(createSidebarDivider());
        sidebar.add(
                Box.createVerticalStrut(8));

        btnLogout = createSidebarButton(
                "Sign Out", "logout", 0);
        sidebar.add(btnLogout);

        return sidebar;
    }

    // -------------------------------------------------------
    // SIDEBAR BUTTON WITH BADGE
    // -------------------------------------------------------
    private JButton createSidebarButton(
            String text,
            String key,
            int badgeCount) {

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

                // Badge
                if (badgeCount > 0) {
                    int bx = getWidth() - 30;
                    int by = getHeight() / 2
                            - 9;
                    g2.setColor(
                            UIConstants.DANGER);
                    g2.fillOval(bx, by,
                            18, 18);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font(
                            "Segoe UI",
                            Font.BOLD, 9));
                    String bc = badgeCount > 99
                            ? "99+"
                            : String.valueOf(
                            badgeCount);
                    FontMetrics fm =
                            g2.getFontMetrics();
                    g2.drawString(bc,
                            bx + (18
                                    - fm.stringWidth(bc))
                                    / 2,
                            by + (18
                                    + fm.getAscent()
                                    - fm.getDescent())
                                    / 2);
                }

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
        JPanel d = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                g.setColor(new Color(
                        255, 255, 255, 30));
                g.drawLine(16, 0,
                        getWidth() - 16, 0);
            }
        };
        d.setOpaque(false);
        d.setPreferredSize(new Dimension(
                UIConstants.SIDEBAR_WIDTH, 1));
        d.setMaximumSize(new Dimension(
                UIConstants.SIDEBAR_WIDTH, 1));
        d.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        return d;
    }

    // -------------------------------------------------------
    // MENU CLICK
    // -------------------------------------------------------
    private void handleMenuClick(String key) {
        if (key.equals("logout")) {
            handleLogout();
            return;
        }
        activeMenu = key;
        repaintSidebarButtons();
        showPanel(key);
    }

    private void repaintSidebarButtons() {
        btnOverview.repaint();
        btnAccounts.repaint();
        btnCustomers.repaint();
        btnApprovals.repaint();
        btnCards.repaint();
        btnFraud.repaint();
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
            case "customers":
                contentPanel.add(
                        createCustomersPanel(),
                        BorderLayout.CENTER);
                break;
            case "approvals":
                contentPanel.add(
                        createApprovalsPanel(),
                        BorderLayout.CENTER);
                break;
            case "cards":
                contentPanel.add(
                        createCardsPanel(),
                        BorderLayout.CENTER);
                break;
            case "fraud":
                contentPanel.add(
                        createFraudPanel(),
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
        panel.setBackground(UIConstants.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        panel.add(createPageTitle("Branch Overview","Summary of all branch activity"),BorderLayout.NORTH);

        BranchActivity activity =branchActivityService.getBranchActivity(branchId);

        // Summary cards
        JPanel cardsRow = new JPanel(new GridLayout(2, 3, 16, 16));
        cardsRow.setOpaque(false);

        if (activity != null) {
            cardsRow.add(createSummaryCard("Total Customers", String.valueOf(activity.getTotalCustomers()),UIConstants.PRIMARY, UIConstants.GOLD));
            cardsRow.add(createSummaryCard("Total Accounts",String.valueOf(activity.getTotalAccounts()),
                    UIConstants.SUCCESS,
                    UIConstants.SUCCESS));

            cardsRow.add(createSummaryCard("Total Cards", String.valueOf(activity.getTotalCards()),
                    UIConstants.INFO,
                    UIConstants.INFO));

            cardsRow.add(createSummaryCard("Total Balance Held","$" + String.format("%.2f",activity.getTotalBranchBalance()),
                    UIConstants.GOLD_DARK,
                    UIConstants.GOLD_DARK));

            cardsRow.add(createSummaryCard("Pending Approvals",
                    String.valueOf(activity.getTotalPendingItems()),
                    activity.getPendingAccounts() > 0|| activity.getPendingCards() > 0? UIConstants.WARNING: UIConstants.SUCCESS,
                    activity.getPendingAccounts() > 0|| activity.getPendingCards() > 0? UIConstants.WARNING: UIConstants.SUCCESS));

            cardsRow.add(createSummaryCard(
                    "Open Alerts", String.valueOf(activity.getOpenFraudAlerts()),
                    activity.getOpenFraudAlerts() > 0 ? UIConstants.DANGER : UIConstants.SUCCESS,
                    activity.getOpenFraudAlerts() > 0? UIConstants.DANGER: UIConstants.SUCCESS));
        }

        JPanel cardsWrapper = new JPanel( new BorderLayout());
        cardsWrapper.setOpaque(false);
        cardsWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        cardsWrapper.add(cardsRow, BorderLayout.CENTER);

        // Quick actions
        JPanel actionsCard = createCardPanel("Management Actions");
        actionsCard.setLayout( new FlowLayout( FlowLayout.LEFT, 12, 12));

        JButton approvalsBtn =UIUtils.createGoldButton("View Approvals");
        approvalsBtn.addActionListener(e -> {activeMenu = "approvals";
            repaintSidebarButtons();
            showPanel("approvals");
        });

        JButton fraudBtn =UIUtils.createDangerButton( "Alerts");
        fraudBtn.addActionListener(e -> {activeMenu = "fraud";
            repaintSidebarButtons();
            showPanel("fraud");
        });

        JButton interestBtn =UIUtils.createPrimaryButton("Apply Interest");
        interestBtn.addActionListener(
                e -> handleApplyInterest());

        JButton closeZeroBtn =
                UIUtils.createWarningButton(
                        "Close Zero Accounts");
        closeZeroBtn.addActionListener(
                e -> handleCloseZeroAccounts());

        actionsCard.add(approvalsBtn);
        actionsCard.add(fraudBtn);
        actionsCard.add(interestBtn);
        actionsCard.add(closeZeroBtn);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(
                center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(cardsWrapper);
        center.add(actionsCard);

        panel.add(center,
                BorderLayout.CENTER);

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
                        "All Accounts",
                        "Manage all branch accounts"),
                BorderLayout.NORTH);

        List<Account> accounts =
                accountService
                        .getAllAccountsForBranch(
                                branchId);

        String[] cols = {"Account Number",
                "Customer ID", "Type",
                "Balance", "Status"};
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
                    a.getCustomerId(),
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
                "Account List ("
                        + accounts.size() + ")");
        tableCard.setLayout(
                new BorderLayout());
        tableCard.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 0, 0, 0));
        tableCard.add(scroll,
                BorderLayout.CENTER);

        // Action bar
        JPanel actionBar = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT, 8, 12));
        actionBar.setBackground(
                UIConstants.SURFACE);
        actionBar.setBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0,
                        UIConstants.BORDER));

        JButton blockBtn =
                UIUtils.createDangerButton(
                        "Block Account");
        blockBtn.addActionListener(e ->
                handleAccountAction(
                        table, model,
                        accounts, "block"));

        JButton unblockBtn =
                UIUtils.createSuccessButton(
                        "Unblock Account");
        unblockBtn.addActionListener(e ->
                handleAccountAction(
                        table, model,
                        accounts, "unblock"));

        JButton deleteBtn =
                UIUtils.createDangerButton(
                        "Delete Account");
        deleteBtn.addActionListener(e ->
                handleAccountAction(
                        table, model,
                        accounts, "delete"));

        actionBar.add(blockBtn);
        actionBar.add(unblockBtn);
        actionBar.add(deleteBtn);

        tableCard.add(actionBar,
                BorderLayout.SOUTH);

        panel.add(tableCard,
                BorderLayout.CENTER);

        return panel;
    }

    // -------------------------------------------------------
    // CUSTOMERS PANEL
    // -------------------------------------------------------
    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(
                new BorderLayout());
        panel.setBackground(
                UIConstants.BACKGROUND);
        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        24, 24, 24, 24));

        panel.add(createPageTitle(
                        "Customers",
                        "Manage branch customers"),
                BorderLayout.NORTH);

        List<Customer> customers =
                customerService
                        .getCustomersForBranch(branchId);

        // Updated columns with split address
        String[] cols = {
                "Customer ID", "Full Name",
                "Email", "Phone",
                "Street", "City",
                "State", "PIN Code",
                "Status"};

        DefaultTableModel model =
                new DefaultTableModel(
                        cols, 0) {
                    @Override
                    public boolean isCellEditable(
                            int r, int c) {
                        return false;
                    }
                };

        for (Customer c : customers) {
            model.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getFullName(),
                    c.getEmail(),
                    c.getPhone() != null
                            ? c.getPhone() : "N/A",
                    c.getStreet() != null
                            ? c.getStreet() : "",
                    c.getCity() != null
                            ? c.getCity() : "",
                    c.getState() != null
                            ? c.getState() : "",
                    c.getPinCode() != null
                            ? c.getPinCode() : "",
                    c.getStatus().toUpperCase()
            });
        }

        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        table.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION);

        // Set preferred column widths
        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(80);
        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(130);
        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(160);
        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(100);
        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(140);
        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(80);
        table.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(80);
        table.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(80);
        table.getColumnModel()
                .getColumn(8)
                .setPreferredWidth(80);

        JScrollPane scroll =
                UIUtils.createScrollPane(table);

        JPanel tableCard = createCardPanel(
                "Customer List ("
                        + customers.size() + ")");
        tableCard.setLayout(
                new BorderLayout());
        tableCard.add(scroll,
                BorderLayout.CENTER);

        JPanel actionBar = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT, 8, 12));
        actionBar.setBackground(
                UIConstants.SURFACE);
        actionBar.setBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0,
                        UIConstants.BORDER));

        JButton blockBtn =
                UIUtils.createDangerButton(
                        "Block Customer");
        blockBtn.addActionListener(e ->
                handleCustomerAction(
                        table, customers, "block"));

        JButton unblockBtn =
                UIUtils.createSuccessButton(
                        "Unblock Customer");
        unblockBtn.addActionListener(e ->
                handleCustomerAction(
                        table, customers, "unblock"));

        JButton deleteBtn =
                UIUtils.createDangerButton(
                        "Delete Customer");
        deleteBtn.addActionListener(e ->
                handleCustomerAction(
                        table, customers, "delete"));

        actionBar.add(blockBtn);
        actionBar.add(unblockBtn);
        actionBar.add(deleteBtn);
        tableCard.add(actionBar,
                BorderLayout.SOUTH);

        panel.add(tableCard,
                BorderLayout.CENTER);

        return panel;
    }
    // -------------------------------------------------------
    // APPROVALS PANEL
    // -------------------------------------------------------
    private JPanel createApprovalsPanel() {
        JPanel panel = new JPanel(
                new BorderLayout());
        panel.setBackground(
                UIConstants.BACKGROUND);
        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        24, 24, 24, 24));

        panel.add(createPageTitle(
                        "Pending Approvals",
                        "Review account and card requests"),
                BorderLayout.NORTH);

        List<PendingApproval> approvals =
                approvalService
                        .getAllPendingApprovalsForBranch(
                                branchId);

        String[] cols = {"Type",
                "Reference", "Customer",
                "Detail", "Action"};
        DefaultTableModel model =
                new DefaultTableModel(
                        cols, 0) {
                    @Override
                    public boolean isCellEditable(
                            int r, int c) {
                        return false;
                    }
                };

        for (PendingApproval p : approvals) {
            model.addRow(new Object[]{
                    p.getRequestType(),
                    p.getReference(),
                    p.getFullName(),
                    p.getDetail()
                            .toUpperCase(),
                    "Pending"
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
                "Pending Requests ("
                        + approvals.size() + ")");
        tableCard.setLayout(
                new BorderLayout());
        tableCard.add(scroll,
                BorderLayout.CENTER);

        // Action bar
        JPanel actionBar = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT, 8, 12));
        actionBar.setBackground(
                UIConstants.SURFACE);
        actionBar.setBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0,
                        UIConstants.BORDER));

        JButton approveBtn =
                UIUtils.createSuccessButton(
                        "Approve");
        approveBtn.addActionListener(e -> {
            int row = table
                    .getSelectedRow();
            if (row < 0) {
                UIUtils.showError(this,
                        "Please select a request.");
                return;
            }
            PendingApproval selected =
                    approvals.get(row);
            if (UIUtils.showConfirm(this,
                    "Approve this "
                            + selected
                            .getRequestType()
                            + "?")) {
                boolean ok =
                        approvalService
                                .approveRequest(
                                        selected,
                                        loggedInManager
                                                .getManagerId());
                if (ok) {
                    UIUtils.showSuccess(
                            this,
                            "Request approved.");
                    showPanel("approvals");
                } else {
                    UIUtils.showError(this,
                            "Approval failed.");
                }
            }
        });

        JButton declineBtn =
                UIUtils.createDangerButton(
                        "Decline");
        declineBtn.addActionListener(e -> {
            int row = table
                    .getSelectedRow();
            if (row < 0) {
                UIUtils.showError(this,
                        "Please select a request.");
                return;
            }
            PendingApproval selected =
                    approvals.get(row);
            if (UIUtils.showConfirm(this,
                    "Decline this "
                            + selected
                            .getRequestType()
                            + "?")) {
                boolean ok =
                        approvalService
                                .declineRequest(
                                        selected,
                                        loggedInManager
                                                .getManagerId());
                if (ok) {
                    UIUtils.showSuccess(
                            this,
                            "Request declined.");
                    showPanel("approvals");
                } else {
                    UIUtils.showError(this,
                            "Decline failed.");
                }
            }
        });

        JButton approveAllBtn =
                UIUtils.createGoldButton(
                        "Approve All");
        approveAllBtn.addActionListener(e -> {
            if (approvals.isEmpty()) {
                UIUtils.showError(this,
                        "No pending requests.");
                return;
            }
            if (UIUtils.showConfirm(this,
                    "Approve ALL "
                            + approvals.size()
                            + " pending requests?")) {
                int count = 0;
                for (PendingApproval p
                        : approvals) {
                    if (approvalService
                            .approveRequest(
                                    p,
                                    loggedInManager
                                            .getManagerId())) {
                        count++;
                    }
                }
                UIUtils.showSuccess(this,
                        count
                                + " requests approved.");
                showPanel("approvals");
            }
        });

        actionBar.add(approveBtn);
        actionBar.add(declineBtn);
        actionBar.add(approveAllBtn);
        tableCard.add(actionBar,
                BorderLayout.SOUTH);

        panel.add(tableCard,
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
                        "Card Management",
                        "View and manage all branch cards"),
                BorderLayout.NORTH);

        List<Card> cards =
                cardService
                        .getAllCardsForBranch(branchId);

        String[] cols = {"Card Number",
                "Type", "Account",
                "Expiry", "Status"};
        DefaultTableModel model =
                new DefaultTableModel(
                        cols, 0) {
                    @Override
                    public boolean isCellEditable(
                            int r, int c) {
                        return false;
                    }
                };

        for (Card c : cards) {
            model.addRow(new Object[]{
                    c.getCardNumber(),
                    c.getCardType()
                            .toUpperCase(),
                    accountService
                            .formatAccountNumber(
                            c.getAccountId()),
                    c.getExpiryDate() != null
                            ? c.getExpiryDate()
                            .toString()
                            : "N/A",
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
                "All Cards ("
                        + cards.size() + ")");
        tableCard.setLayout(
                new BorderLayout());
        tableCard.add(scroll,
                BorderLayout.CENTER);

        // Action bar
        JPanel actionBar = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT, 8, 12));
        actionBar.setBackground(
                UIConstants.SURFACE);
        actionBar.setBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0,
                        UIConstants.BORDER));

        JButton blockBtn =
                UIUtils.createDangerButton(
                        "Block Card");
        blockBtn.addActionListener(e ->
                handleCardAction(
                        table, cards, "block"));

        JButton unblockBtn =
                UIUtils.createSuccessButton(
                        "Unblock Card");
        unblockBtn.addActionListener(e ->
                handleCardAction(
                        table, cards, "unblock"));

        actionBar.add(blockBtn);
        actionBar.add(unblockBtn);
        tableCard.add(actionBar,
                BorderLayout.SOUTH);

        panel.add(tableCard,
                BorderLayout.CENTER);

        return panel;
    }

    // -------------------------------------------------------
    // FRAUD PANEL
    // -------------------------------------------------------
    private JPanel createFraudPanel() {
        JPanel panel = new JPanel(
                new BorderLayout());
        panel.setBackground(
                UIConstants.BACKGROUND);
        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        24, 24, 24, 24));

        panel.add(createPageTitle(
                        "Alerts",
                        "Monitor and review alerts"),
                BorderLayout.NORTH);

        List<FraudAlert> alerts =
                fraudService
                        .getAllAlertsForBranch(
                                branchId);

        String[] cols = {
                "Alert ID",
                "Account",
                "Reason",
                "Severity",
                "Status",
                "Flagged At"};
        DefaultTableModel model =
                new DefaultTableModel(
                        cols, 0) {
                    @Override
                    public boolean isCellEditable(
                            int r, int c) {
                        return false;
                    }
                };

        for (FraudAlert fa : alerts) {
            String flaggedAtStr = fa.getFlaggedAt() != null
                    ? fa.getFlaggedAt().toString()
                    : "N/A";
            // Trim to 19 chars only if longer (handles LocalDate, Timestamp variants, etc.)
            if (flaggedAtStr.length() > 19) {
                flaggedAtStr = flaggedAtStr.substring(0, 19);
            }

            model.addRow(new Object[]{
                    fa.getAlertId(),
                    accountService.formatAccountNumber(fa.getAccountId()),
                    fa.getAlertReason().length() > 50
                            ? fa.getAlertReason().substring(0, 50) + "..."
                            : fa.getAlertReason(),
                    fa.getSeverity().toUpperCase(),
                    fa.getStatus().toUpperCase(),
                    flaggedAtStr   // <-- use the safe variable
            });
        }

        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        table.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION);

        JScrollPane scroll =
                UIUtils.createScrollPane(table);

        long openCount = alerts.stream()
                .filter(a -> a.getStatus()
                        .equals("open"))
                .count();

        JPanel tableCard = createCardPanel(
                "Open Alerts ("
                        + openCount + ")");
        tableCard.setLayout(
                new BorderLayout());
        tableCard.add(scroll,
                BorderLayout.CENTER);

        // Action bar
        JPanel actionBar = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT, 8, 12));
        actionBar.setBackground(
                UIConstants.SURFACE);
        actionBar.setBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0,
                        UIConstants.BORDER));

        JButton reviewBtn =
                UIUtils.createWarningButton(
                        "Mark Reviewed");
        reviewBtn.addActionListener(e -> {
            int row = table
                    .getSelectedRow();
            if (row < 0) {
                UIUtils.showError(this,
                        "Please select an alert.");
                return;
            }
            FraudAlert selected =
                    alerts.get(row);
            boolean ok =
                    fraudService.reviewAlert(
                            selected.getAlertId(),
                            "reviewed",
                            loggedInManager
                                    .getManagerId());
            if (ok) {
                UIUtils.showSuccess(this,
                        "Alert marked as reviewed.");
                showPanel("fraud");
            } else {
                UIUtils.showError(this,
                        "Failed to update alert.");
            }
        });

        JButton dismissBtn =
                UIUtils.createSuccessButton(
                        "Dismiss");
        dismissBtn.addActionListener(e -> {
            int row = table
                    .getSelectedRow();
            if (row < 0) {
                UIUtils.showError(this,
                        "Please select an alert.");
                return;
            }
            FraudAlert selected =
                    alerts.get(row);
            if (UIUtils.showConfirm(this,
                    "Dismiss this alert?")) {
                boolean ok =
                        fraudService.reviewAlert(
                                selected.getAlertId(),
                                "dismissed",
                                loggedInManager
                                        .getManagerId());
                if (ok) {
                    UIUtils.showSuccess(this,
                            "Alert dismissed.");
                    showPanel("fraud");
                } else {
                    UIUtils.showError(this,
                            "Failed to dismiss.");
                }
            }
        });

        JButton dismissAllBtn =
                UIUtils.createGoldButton(
                        "Dismiss All Reviewed");
        dismissAllBtn.addActionListener(e -> {
            if (UIUtils.showConfirm(this,
                    "Dismiss all reviewed "
                            + "alerts?")) {
                int count =
                        fraudService
                                .dismissAllReviewedAlerts(
                                        branchId);
                UIUtils.showSuccess(this,
                        count
                                + " alerts dismissed.");
                showPanel("fraud");
            }
        });

        actionBar.add(reviewBtn);
        actionBar.add(dismissBtn);
        actionBar.add(dismissAllBtn);
        tableCard.add(actionBar,
                BorderLayout.SOUTH);

        panel.add(tableCard,
                BorderLayout.CENTER);

        return panel;
    }

    // -------------------------------------------------------
    // ACTION HANDLERS
    // -------------------------------------------------------
    private void handleAccountAction(
            JTable table,
            DefaultTableModel model,
            List<Account> accounts,
            String action) {

        int row = table.getSelectedRow();
        if (row < 0) {
            UIUtils.showError(this,
                    "Please select an account.");
            return;
        }

        Account selected =
                accounts.get(row);
        String accountNum =
                accountService
                        .formatAccountNumber(
                                selected.getAccountId());

        boolean confirmed =
                UIUtils.showConfirm(this,
                        action.substring(0, 1)
                                .toUpperCase()
                                + action.substring(1)
                                + " account "
                                + accountNum + "?");

        if (!confirmed) return;

        boolean ok;
        switch (action) {
            case "block":
                ok = accountService
                        .blockAccount(
                                selected
                                        .getAccountId());
                break;
            case "unblock":
                ok = accountService
                        .unblockAccount(
                                selected
                                        .getAccountId());
                break;
            case "delete":
                ok = accountService
                        .deleteAccount(
                                selected
                                        .getAccountId());
                break;
            default:
                ok = false;
        }

        if (ok) {
            UIUtils.showSuccess(this,
                    "Account " + action
                            + "ed successfully.");
            showPanel("accounts");
        } else {
            UIUtils.showError(this,
                    "Action failed. "
                            + "Account may have "
                            + "transactions.");
        }
    }

    private void handleCustomerAction(
            JTable table,
            List<Customer> customers,
            String action) {

        int row = table.getSelectedRow();
        if (row < 0) {
            UIUtils.showError(this,
                    "Please select a customer.");
            return;
        }

        Customer selected =
                customers.get(row);
        boolean confirmed =
                UIUtils.showConfirm(this,
                        action.substring(0, 1)
                                .toUpperCase()
                                + action.substring(1)
                                + " customer "
                                + selected.getFullName()
                                + "?");

        if (!confirmed) return;

        boolean ok;
        switch (action) {
            case "block":
                ok = customerService
                        .blockCustomer(
                                selected
                                        .getCustomerId());
                break;
            case "unblock":
                ok = customerService
                        .unblockCustomer(
                                selected
                                        .getCustomerId());
                break;
            case "delete":
                ok = customerService
                        .deleteCustomer(
                                selected
                                        .getCustomerId());
                break;
            default:
                ok = false;
        }

        if (ok) {
            UIUtils.showSuccess(this,
                    "Customer " + action
                            + "ed successfully.");
            showPanel("customers");
        } else {
            UIUtils.showError(this,
                    "Action failed.");
        }
    }

    private void handleCardAction(
            JTable table,
            List<Card> cards,
            String action) {

        int row = table.getSelectedRow();
        if (row < 0) {
            UIUtils.showError(this,
                    "Please select a card.");
            return;
        }

        Card selected = cards.get(row);
        boolean confirmed =
                UIUtils.showConfirm(this,
                        action.substring(0, 1)
                                .toUpperCase()
                                + action.substring(1)
                                + " card "
                                + selected.getCardNumber()
                                + "?");

        if (!confirmed) return;

        boolean ok;
        switch (action) {
            case "block":
                ok = cardService.blockCard(
                        selected.getCardId());
                break;
            case "unblock":
                ok = cardService.unblockCard(
                        selected.getCardId());
                break;
            default:
                ok = false;
        }

        if (ok) {
            UIUtils.showSuccess(this,
                    "Card " + action
                            + "ed successfully.");
            showPanel("cards");
        } else {
            UIUtils.showError(this,
                    "Action failed.");
        }
    }

    private void handleApplyInterest() {
        if (UIUtils.showConfirm(this,
                "Apply monthly interest to "
                        + "all savings accounts?")) {
            int count =
                    procedureService
                            .applyMonthlyInterest(
                                    branchId);
            UIUtils.showSuccess(this,
                    "Interest applied to "
                            + count
                            + " account(s).");
            showPanel("overview");
        }
    }

    private void handleCloseZeroAccounts() {
        if (UIUtils.showConfirm(this,
                "Close all zero balance "
                        + "active accounts?")) {
            int count =
                    procedureService
                            .closeZeroBalanceAccounts(
                                    branchId);
            UIUtils.showSuccess(this,
                    count
                            + " account(s) closed.");
            showPanel("accounts");
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
            String title,
            String subtitle) {
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
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0,
                        getWidth(), 4, 4, 4);
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
                        14, 14, 14, 14));

        JLabel titleLabel =
                new JLabel(title);
        titleLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        titleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(
                "Georgia", Font.BOLD, 20));
        valueLabel.setForeground(valueColor);
        valueLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        card.add(valueLabel);

        return card;
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

        JPanel titleBar = new JPanel(
                new BorderLayout()) {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setColor(
                        UIConstants.GOLD_SUBTLE);
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

        panel.add(titleBar,
                BorderLayout.NORTH);

        return panel;
    }

}