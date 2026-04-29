package com.bank.ui;

import com.bank.db.Session;
import com.bank.models.Branch;
import com.bank.models.Customer;
import com.bank.services.AuthService;
import com.bank.ui.utils.UIConstants;
import com.bank.ui.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {

    private AuthService authService;
    private Branch      selectedBranch;
    private LoginFrame  parentLoginFrame;

    private JTextField     firstNameField;
    private JTextField     lastNameField;
    private JTextField     emailField;
    private JTextField     phoneField;
    private JTextField     nationalIdField;

    private JTextField     streetField;
    private JTextField     cityField;
    private JTextField     stateField;
    private JTextField     pinCodeField;

    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton        registerButton;
    private JLabel         statusLabel;

    public RegisterFrame(LoginFrame parentLoginFrame) {
        this.authService = new AuthService();
        this.selectedBranch =Session.getInstance().getSelectedBranch();
        this.parentLoginFrame =parentLoginFrame;

        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(540, 780);
        setLocationRelativeTo(parentLoginFrame);
        setResizable(false);
        setTitle(UIConstants.BANK_NAME+ " — Create New Account");

        addWindowListener(
                new java.awt.event
                        .WindowAdapter() {
                    @Override
                    public void windowClosing(
                            java.awt.event
                                    .WindowEvent e) {
                        parentLoginFrame.setVisible(true);
                        goBackToLogin();
                    }
                });
    }

    //INIT COMPONENTS
    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 =(Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient =new GradientPaint(0, 0,UIConstants.PRIMARY_DARK, 0, getHeight(),UIConstants.PRIMARY_LIGHT);
                g2.setPaint(gradient);
                g2.fillRect(0, 0,getWidth(), getHeight());
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        JScrollPane scrollPane =new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane,BorderLayout.CENTER);

        GridBagConstraints gbc =new GridBagConstraints();
        gbc.fill =GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets =new Insets(6, 30, 6, 30);


        JLabel bankLabel = new JLabel(UIConstants.BANK_NAME, SwingConstants.CENTER);
        bankLabel.setFont(new Font("Georgia", Font.BOLD, 15));
        bankLabel.setForeground(UIConstants.TEXT_LIGHT);
        gbc.gridy = 0;
        gbc.insets =
                new Insets(24, 30, 2, 30);
        contentPanel.add(bankLabel, gbc);

        String branchText =selectedBranch != null? selectedBranch.getBranchName(): "";
        JLabel branchLabel = new JLabel(branchText,SwingConstants.CENTER);
        branchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        branchLabel.setForeground(UIConstants.GOLD_LIGHT);
        gbc.gridy = 1;
        gbc.insets =new Insets(0, 30, 14, 30);
        contentPanel.add(branchLabel, gbc);

        JPanel card = buildRegistrationCard();
        gbc.gridy = 2;
        gbc.insets =
                new Insets(0, 20, 20, 20);
        contentPanel.add(card, gbc);
    }

    //BUILDING REGISTRATION CARD
    private JPanel buildRegistrationCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =(Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.SURFACE);
                g2.fillRoundRect(0, 0,getWidth(),getHeight(), 10, 10);
                g2.setColor(UIConstants.SUCCESS);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(20, 0,getWidth() - 20, 0);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());

        GridBagConstraints cc =new GridBagConstraints();
        cc.fill =GridBagConstraints.HORIZONTAL;
        cc.weightx = 1.0;
        cc.gridx = 0;
        cc.insets = new Insets(4, 20, 4, 20);

        JLabel cardTitle = new JLabel("CREATE NEW ACCOUNT",SwingConstants.CENTER);
        cardTitle.setFont(new Font("Georgia", Font.BOLD, 14));
        cardTitle.setForeground(UIConstants.SUCCESS);
        cc.gridy = 0;
        cc.insets =new Insets(18, 20, 4, 20);
        card.add(cardTitle, cc);

        JLabel cardSub = new JLabel("Fill in your details to register as a customer", SwingConstants.CENTER);
        cardSub.setFont(new Font( "Segoe UI", Font.PLAIN, 11));
        cardSub.setForeground(UIConstants.TEXT_SECONDARY);
        cc.gridy = 1;
        cc.insets =new Insets(0, 20, 12, 20);
        card.add(cardSub, cc);

        JSeparator div = new JSeparator();
        div.setForeground(UIConstants.BORDER);
        cc.gridy = 2;
        cc.insets =new Insets(0, 20, 12, 20);
        card.add(div, cc);

        // SECTION: PERSONAL DETAILS
        card.add(createSectionLabel("Personal Details"),sectionGbc(cc, 3));

        JPanel nameRow = new JPanel(new GridLayout(1, 2, 10, 0));
        nameRow.setOpaque(false);
        firstNameField =UIUtils.createTextField("First name");
        lastNameField =UIUtils.createTextField("Last name");
        nameRow.add(buildSubField("First Name *",firstNameField));
        nameRow.add(buildSubField("Last Name *",lastNameField));
        cc.gridy = 4;
        cc.insets = new Insets(0, 20, 10, 20);
        card.add(nameRow, cc);

        emailField =UIUtils.createTextField("email@example.com");
        cc.gridy = 5;
        card.add(buildField("Email Address *",emailField), cc);

        phoneField =UIUtils.createTextField("Phone number");
        cc.gridy = 6;
        card.add(buildField("Phone Number *",phoneField), cc);

        nationalIdField =UIUtils.createTextField("Aadhar Card number");
        cc.gridy = 7;
        card.add(buildField("Aadhar Card number *",nationalIdField), cc);

        // SECTION: ADDRESS DETAILS
        card.add(createSectionLabel("Address Details"),sectionGbc(cc, 8));

        streetField =UIUtils.createTextField("Street address");
        cc.gridy = 9;
        card.add(buildField("Street *",streetField), cc);

        JPanel cityStateRow = new JPanel( new GridLayout(1, 2, 10, 0));
        cityStateRow.setOpaque(false);
        cityField =UIUtils.createTextField("City");
        stateField =UIUtils.createTextField("State");
        cityStateRow.add(buildSubField("City *", cityField));
        cityStateRow.add(buildSubField("State *", stateField));
        cc.gridy = 10;
        cc.insets =new Insets(0, 20, 10, 20);
        card.add(cityStateRow, cc);

        pinCodeField =UIUtils.createTextField("PIN code");
        cc.gridy = 11;
        card.add(buildField("PIN Code *", pinCodeField), cc);

        // SECTION: SECURITY
        card.add(createSectionLabel(
                        "Security"),
                sectionGbc(cc, 12));

        JPanel pwRow = new JPanel(
                new GridLayout(1, 2, 10, 0));
        pwRow.setOpaque(false);
        passwordField = UIUtils.createPasswordField();
        confirmPasswordField =UIUtils.createPasswordField();
        pwRow.add(buildSubField("Password *",passwordField));
        pwRow.add(buildSubField("Confirm Password *",confirmPasswordField));
        cc.gridy = 13;
        cc.insets =new Insets(0, 20, 4, 20);
        card.add(pwRow, cc);

        JLabel pwHint = new JLabel("Minimum 6 characters");
        pwHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        pwHint.setForeground(UIConstants.TEXT_MUTED);
        cc.gridy = 14;
        cc.insets =new Insets(0, 20, 12, 20);
        card.add(pwHint, cc);

        statusLabel = new JLabel(" ",SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground( UIConstants.DANGER);
        cc.gridy = 15;
        cc.insets =new Insets(0, 20, 4, 20);
        card.add(statusLabel, cc);

        registerButton =UIUtils.createSuccessButton("CREATE ACCOUNT");
        registerButton.setPreferredSize(new Dimension(400, 44));
        registerButton.addActionListener(e -> handleRegister());
        cc.gridy = 16;
        cc.insets = new Insets(4, 20, 12, 20);
        card.add(registerButton, cc);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backPanel.setOpaque(false);

        JLabel alreadyLabel = new JLabel("Already have an account?");
        alreadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        alreadyLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JButton backBtn = new JButton( "Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 =(Graphics2D) g.create();
                g2.setFont(new Font("Segoe UI",Font.BOLD, 11));
                g2.setColor(getModel().isRollover()? UIConstants.PRIMARY_DARK: UIConstants.PRIMARY);
                g2.drawString(getText(), 0,g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        backBtn.setPreferredSize(new Dimension(50, 18));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor( Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBackToLogin());

        backPanel.add(alreadyLabel);
        backPanel.add(backBtn);
        cc.gridy = 17;
        cc.insets = new Insets(0, 20, 18, 20);
        card.add(backPanel, cc);

        return card;
    }

    // HANDLE REGISTER
    private void handleRegister() {
        String firstName =firstNameField.getText().trim();
        String lastName =lastNameField.getText().trim();
        String email =emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String nationalId =nationalIdField.getText().trim();
        String street =streetField.getText().trim();
        String city =cityField.getText().trim();
        String state =stateField.getText().trim();
        String pinCode =pinCodeField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String( confirmPasswordField.getPassword());

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || phone.isEmpty()
                || nationalId.isEmpty()
                || street.isEmpty()
                || city.isEmpty()
                || state.isEmpty()
                || pinCode.isEmpty()
                || password.isEmpty()
                || confirm.isEmpty()) {
            showStatus("Please fill in all fields.",false);
            return;
        }

        if (!email.contains("@")|| !email.contains(".")) {
            showStatus("Please enter a valid email address.",false);
            return;
        }

        if (password.length() < 6) {
            showStatus("Password must be at least 6 characters.",false);
            return;
        }

        if (!password.equals(confirm)) {
            showStatus("Passwords do not match.",false);
            confirmPasswordField.setText("");
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setText("Creating account...");

        SwingWorker<Integer, Void> worker =
                new SwingWorker<>() {
                    @Override
                    protected Integer doInBackground() {
                        Customer customer =new Customer();
                        customer.setBranchId(selectedBranch.getBranchId());
                        customer.setFirstName( firstName);
                        customer.setLastName( lastName);
                        customer.setEmail(email);
                        customer.setPhone(phone);
                        customer.setNationalId( nationalId);
                        customer.setStreet(street);
                        customer.setCity(city);
                        customer.setState(state);
                        customer.setPinCode(pinCode);

                        return authService.registerCustomer(customer, password);
                    }

                    @Override
                    protected void done() {
                        try {
                            int customerId = get();

                            if (customerId != -1) {
                                JOptionPane.showMessageDialog(RegisterFrame.this,
                                                "Account created successfully!\n\n"
                                                        + "Your Customer ID is:\n\n"+ customerId
                                                        + "\n\nPlease save this ID. You will need it to log in.",
                                                "Registration Successful",
                                                JOptionPane .INFORMATION_MESSAGE);
                                goBackToLogin(customerId);

                            } else {
                                showStatus("Registration failed. Email or National ID may already exist.",false);
                                registerButton.setEnabled(true);
                                registerButton.setText("CREATE ACCOUNT");
                            }
                        } catch (Exception e) {
                            showStatus("Error: "+ e.getMessage(),false);
                            registerButton.setEnabled(true);
                            registerButton.setText("CREATE ACCOUNT");
                        }
                    }
                };
        worker.execute();
    }

    // NAVIGATION
    private void goBackToLogin(
            int customerId) {
        parentLoginFrame.setVisible(true);
        parentLoginFrame.prefillCustomerId( customerId);
        parentLoginFrame.setVisible(true);
        this.dispose();
    }
    private void goBackToLogin() {
        parentLoginFrame.setVisible(true);
        this.dispose();
    }
    // UI HELP
    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setForeground(success
                ? UIConstants.SUCCESS
                : UIConstants.DANGER);
    }

    private JPanel buildField(String labelText,JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.FONT_SMALL);
        label.setForeground( UIConstants.TEXT_SECONDARY);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSubField(String labelText,JComponent field) {
        JPanel panel = new JPanel( new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label =new JLabel(labelText);
        label.setFont(UIConstants.FONT_SMALL);
        label.setForeground( UIConstants.TEXT_SECONDARY);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel( text.toUpperCase());
        label.setFont(new Font("Georgia", Font.BOLD, 11));
        label.setForeground(UIConstants.GOLD_DARK);
        label.setBorder(BorderFactory.createEmptyBorder(8, 20, 2, 20));
        return label;
    }

    private GridBagConstraints sectionGbc(GridBagConstraints cc, int row) {
        GridBagConstraints g = (GridBagConstraints) cc.clone();
        g.gridy = row;
        g.insets = new Insets(10, 20, 4, 20);
        return g;
    }
}