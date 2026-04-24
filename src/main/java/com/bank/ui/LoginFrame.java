package com.bank.ui;

import com.bank.db.Session;
import com.bank.models.Branch;
import com.bank.services.AuthService;
import com.bank.ui.utils.UIConstants;
import com.bank.ui.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private String      userMode;
    private Branch      selectedBranch;
    private AuthService authService;

    // Input fields
    private JTextField     identifierField;
    private JPasswordField passwordField;
    private JButton        loginButton;
    private JButton        backButton;
    private JLabel         statusLabel;

    public LoginFrame(String userMode) {
        this.userMode       = userMode;
        this.selectedBranch =
                Session.getInstance()
                        .getSelectedBranch();
        this.authService    = new AuthService();
        initComponents();
        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(UIConstants.BANK_NAME
                + " — "
                + (userMode.equals("CUSTOMER")
                ? "Customer Login"
                : "Manager Login"));
    }

    // -------------------------------------------------------
    // INIT COMPONENTS
    // -------------------------------------------------------
    private void initComponents() {

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient =
                        new GradientPaint(
                                0, 0,
                                UIConstants.PRIMARY_DARK,
                                0, getHeight(),
                                UIConstants.PRIMARY_LIGHT);
                g2.setPaint(gradient);
                g2.fillRect(0, 0,
                        getWidth(), getHeight());
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(0, 0,
                        getWidth(), 0);
                g2.dispose();
            }
        };
        mainPanel.setLayout(
                new GridBagLayout());
        setContentPane(mainPanel);

        GridBagConstraints gbc =
                new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // -----------------------------------------------
        // BANK NAME HEADER
        // -----------------------------------------------
        JLabel bankLabel = new JLabel(
                UIConstants.BANK_NAME,
                SwingConstants.CENTER);
        bankLabel.setFont(new Font(
                "Georgia", Font.BOLD, 16));
        bankLabel.setForeground(
                UIConstants.TEXT_LIGHT);
        gbc.gridy = 0;
        gbc.insets = new Insets(28, 40, 2, 40);
        mainPanel.add(bankLabel, gbc);

        String branchText = selectedBranch != null
                ? selectedBranch.getBranchName()
                : "";
        JLabel branchLabel = new JLabel(
                branchText,
                SwingConstants.CENTER);
        branchLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        branchLabel.setForeground(
                UIConstants.GOLD_LIGHT);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 16, 40);
        mainPanel.add(branchLabel, gbc);

        // -----------------------------------------------
        // LOGIN CARD
        // -----------------------------------------------
        JPanel loginCard = createLoginCard();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 28, 10, 28);
        mainPanel.add(loginCard, gbc);

        // -----------------------------------------------
        // STATUS LABEL
        // -----------------------------------------------
        statusLabel = new JLabel(
                " ", SwingConstants.CENTER);
        statusLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(
                new Color(255, 180, 150));
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 40, 4, 40);
        mainPanel.add(statusLabel, gbc);

        // -----------------------------------------------
        // BACK BUTTON
        // -----------------------------------------------
        backButton = new JButton(
                "← Back to Mode Selection") {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setColor(
                        new Color(0, 0, 0, 0));
                g2.fillRect(0, 0,
                        getWidth(), getHeight());
                g2.setFont(new Font(
                        "Segoe UI",
                        Font.PLAIN, 11));
                g2.setColor(
                        getModel().isRollover()
                                ? UIConstants.GOLD_LIGHT
                                : new Color(
                                180, 190, 210));
                FontMetrics fm =
                        g2.getFontMetrics();
                int x = (getWidth()
                        - fm.stringWidth(
                        getText())) / 2;
                int y = (getHeight()
                        + fm.getAscent()
                        - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        backButton.setPreferredSize(
                new Dimension(250, 28));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(
                Cursor.HAND_CURSOR));
        backButton.addActionListener(
                e -> goBack());

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 40, 20, 40);
        mainPanel.add(backButton, gbc);

        // Enter key triggers login
        getRootPane().setDefaultButton(
                loginButton);
    }

    // -------------------------------------------------------
    // LOGIN CARD
    // -------------------------------------------------------
    private JPanel createLoginCard() {
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
                        10, 10);
                g2.setColor(
                        userMode.equals("CUSTOMER")
                                ? UIConstants.GOLD
                                : UIConstants.PRIMARY_DARK);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(20, 0,
                        getWidth() - 20, 0);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());

        GridBagConstraints c =
                new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        // Portal title
        boolean isCustomer =
                userMode.equals("CUSTOMER");
        String portalTitle = isCustomer
                ? "CUSTOMER LOGIN"
                : "MANAGER LOGIN";

        JLabel portalLabel = new JLabel( portalTitle,
                SwingConstants.CENTER);
        portalLabel.setFont(new Font(
                "Georgia", Font.BOLD, 14));
        portalLabel.setForeground(isCustomer
                ? UIConstants.GOLD_DARK
                : UIConstants.PRIMARY);
        c.gridy = 0;
        c.insets = new Insets(20, 20, 4, 20);
        card.add(portalLabel, c);

        // Subtitle
        String subtitle = isCustomer
                ? "Sign in with your email address"
                : "Sign in with your username";
        JLabel subtitleLabel = new JLabel(
                subtitle,
                SwingConstants.CENTER);
        subtitleLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        c.gridy = 1;
        c.insets = new Insets(0, 20, 16, 20);
        card.add(subtitleLabel, c);

        // Divider
        JSeparator div = new JSeparator();
        div.setForeground(UIConstants.BORDER);
        c.gridy = 2;
        c.insets = new Insets(0, 20, 16, 20);
        card.add(div, c);

        // Identifier label
        String identifierLabel = isCustomer
                ? "Email Address"
                : "Username";
        JLabel idLabel = new JLabel(
                identifierLabel);
        idLabel.setFont(UIConstants.FONT_BODY);
        idLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        c.gridy = 3;
        c.insets = new Insets(0, 20, 4, 20);
        card.add(idLabel, c);

        // Identifier field
        identifierField =
                UIUtils.createTextField(
                        identifierLabel);
        identifierField.setPreferredSize(
                new Dimension(400,
                        UIConstants.INPUT_HEIGHT));
        c.gridy = 4;
        c.insets = new Insets(0, 20, 12, 20);
        card.add(identifierField, c);

        // Password label
        JLabel pwLabel = new JLabel("Password");
        pwLabel.setFont(UIConstants.FONT_BODY);
        pwLabel.setForeground(
                UIConstants.TEXT_SECONDARY);
        c.gridy = 5;
        c.insets = new Insets(0, 20, 4, 20);
        card.add(pwLabel, c);

        // Password field row with show toggle
        JPanel passwordRow = new JPanel(
                new BorderLayout(8, 0));
        passwordRow.setOpaque(false);

        passwordField =
                UIUtils.createPasswordField();
        passwordField.setPreferredSize(
                new Dimension(400,
                        UIConstants.INPUT_HEIGHT));
        passwordRow.add(passwordField,
                BorderLayout.CENTER);

        JCheckBox showPw = new JCheckBox(
                "Show");
        showPw.setFont(new Font(
                "Segoe UI", Font.PLAIN, 11));
        showPw.setForeground(
                UIConstants.TEXT_SECONDARY);
        showPw.setOpaque(false);
        showPw.addActionListener(e ->
                passwordField.setEchoChar(
                        showPw.isSelected()
                                ? (char) 0 : '●'));
        passwordRow.add(showPw,
                BorderLayout.EAST);

        c.gridy = 6;
        c.insets = new Insets(0, 20, 20, 20);
        card.add(passwordRow, c);

        // Sign In button
        loginButton = UIUtils.createGoldButton(
                "SIGN IN");
        loginButton.setPreferredSize(
                new Dimension(400, 44));
        loginButton.addActionListener(
                e -> handleLogin());
        c.gridy = 7;
        c.insets = new Insets(0, 20, 12, 20);
        card.add(loginButton, c);

        // Register link — customer mode only
        if (isCustomer) {
            JPanel registerLinkPanel =
                    new JPanel(new FlowLayout(
                            FlowLayout.CENTER, 4, 0));
            registerLinkPanel.setOpaque(false);

            JLabel newCustomerLabel =
                    new JLabel("New customer?");
            newCustomerLabel.setFont(new Font(
                    "Segoe UI", Font.PLAIN, 11));
            newCustomerLabel.setForeground(
                    UIConstants.TEXT_SECONDARY);

            JButton createAccountBtn =
                    new JButton(
                            "Create Account") {
                        @Override
                        protected void paintComponent(
                                Graphics g) {
                            Graphics2D g2 =
                                    (Graphics2D) g.create();
                            g2.setFont(new Font(
                                    "Segoe UI",
                                    Font.BOLD, 11));
                            g2.setColor(
                                    getModel().isRollover()
                                            ? UIConstants.GOLD_DARK
                                            : UIConstants.GOLD);
                            g2.drawString(getText(), 0,
                                    g2.getFontMetrics()
                                            .getAscent());
                            g2.dispose();
                        }
                    };
            createAccountBtn.setPreferredSize(
                    new Dimension(110, 18));
            createAccountBtn
                    .setBorderPainted(false);
            createAccountBtn
                    .setContentAreaFilled(false);
            createAccountBtn
                    .setFocusPainted(false);
            createAccountBtn.setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR));

            // Opens RegisterFrame as
            // a separate window
            createAccountBtn
                    .addActionListener(e -> {
                        LoginFrame.this
                                .setVisible(false);
                        RegisterFrame registerFrame =
                                new RegisterFrame(
                                        LoginFrame.this);
                        registerFrame
                                .setVisible(true);
                    });

            registerLinkPanel.add(
                    newCustomerLabel);
            registerLinkPanel.add(
                    createAccountBtn);

            c.gridy = 8;
            c.insets = new Insets(
                    0, 20, 16, 20);
            card.add(registerLinkPanel, c);

        } else {
            // Spacer for manager login
            c.gridy = 8;
            c.insets = new Insets(
                    0, 20, 16, 20);
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            spacer.setPreferredSize(
                    new Dimension(1, 4));
            card.add(spacer, c);
        }

        return card;
    }

    // -------------------------------------------------------
    // HANDLE LOGIN
    // -------------------------------------------------------
    private void handleLogin() {
        String identifier =
                identifierField.getText().trim();
        String password = new String(
                passwordField.getPassword());

        if (identifier.isEmpty()
                || password.isEmpty()) {
            showStatus(
                    "Please enter all fields.",
                    false);
            return;
        }

        if (selectedBranch == null) {
            showStatus(
                    "No branch selected.",
                    false);
            return;
        }

        loginButton.setEnabled(false);
        identifierField.setEnabled(false);
        passwordField.setEnabled(false);
        loginButton.setText("Signing in...");

        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() {
                        if (userMode.equals(
                                "CUSTOMER")) {
                            return authService
                                    .loginCustomer(
                                            identifier,
                                            password,
                                            selectedBranch
                                                    .getBranchId());
                        } else {
                            return authService
                                    .loginManager(
                                            identifier,
                                            password,
                                            selectedBranch
                                                    .getBranchId());
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                showStatus(
                                        "Login successful."
                                                + " Loading...",
                                        true);
                                openDashboard();
                            } else {
                                showStatus(
                                        "Invalid credentials."
                                                + " Please try again.",
                                        false);
                                loginButton
                                        .setEnabled(true);
                                identifierField
                                        .setEnabled(true);
                                passwordField
                                        .setEnabled(true);
                                loginButton.setText(
                                        "SIGN IN");
                                passwordField.setText(
                                        "");
                            }
                        } catch (Exception e) {
                            showStatus(
                                    "Login error: "
                                            + e.getMessage(),
                                    false);
                            loginButton.setEnabled(
                                    true);
                            identifierField
                                    .setEnabled(true);
                            passwordField
                                    .setEnabled(true);
                            loginButton.setText(
                                    "SIGN IN");
                        }
                    }
                };
        worker.execute();
    }

    // -------------------------------------------------------
    // OPEN DASHBOARD
    // -------------------------------------------------------
    private void openDashboard() {
        Timer timer = new Timer(800, e -> {
            JFrame dashboard;
            if (userMode.equals("CUSTOMER")) {
                dashboard =
                        new CustomerDashboard();
            } else {
                dashboard =
                        new AdminDashboard();
            }
            dashboard.setVisible(true);
            this.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // -------------------------------------------------------
    // PREFILL EMAIL — called by RegisterFrame
    // after successful registration
    // -------------------------------------------------------
    public void prefillEmail(String email) {
        if (identifierField != null
                && email != null
                && !email.isEmpty()) {
            identifierField.setText(email);
            passwordField.setText("");
            passwordField.requestFocus();
            showStatus(
                    "Account created! "
                            + "Please sign in.",
                    true);
        }
    }

    // -------------------------------------------------------
    // HELPER METHODS
    // -------------------------------------------------------
    private void showStatus(
            String message,
            boolean success) {
        statusLabel.setText(message);
        statusLabel.setForeground(success
                ? UIConstants.SUCCESS
                : new Color(255, 180, 150));
    }

    private void goBack() {
        ModeSelectionFrame modeFrame =
                new ModeSelectionFrame();
        modeFrame.setVisible(true);
        this.dispose();
    }
}