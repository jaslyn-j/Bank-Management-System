package com.bank.ui;

import com.bank.db.Session;
import com.bank.models.Branch;
import com.bank.ui.utils.UIConstants;
import com.bank.ui.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ModeSelectionFrame extends JFrame {

    private Branch selectedBranch;

    public ModeSelectionFrame() {
        this.selectedBranch =
                Session.getInstance()
                        .getSelectedBranch();
        initComponents();
        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);
        setSize(520, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(UIConstants.BANK_NAME
                + " — Select Access Mode");
    }

    private void initComponents() {

        // MAIN PANEL
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0,UIConstants.PRIMARY_DARK,0, getHeight(), UIConstants.PRIMARY_LIGHT);
                g2.setPaint(gradient);
                g2.fillRect(0, 0,getWidth(), getHeight());

                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(0, 0,
                        getWidth(), 0);

                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        GridBagConstraints gbc =new GridBagConstraints();
        gbc.insets = new Insets(8, 40, 8, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        // HEADER — BANK NAME
        JLabel bankNameLabel = new JLabel( UIConstants.BANK_NAME,SwingConstants.CENTER);
        bankNameLabel.setFont(
                new Font("Georgia", Font.BOLD, 18));
        bankNameLabel.setForeground(
                UIConstants.TEXT_LIGHT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(35, 40, 2, 40);
        mainPanel.add(bankNameLabel, gbc);

        String branchText = selectedBranch != null
                ? selectedBranch.getBranchName()
                : "Unknown Branch";

        JLabel branchLabel = new JLabel(branchText, SwingConstants.CENTER);
        branchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        branchLabel.setForeground(
                UIConstants.GOLD_LIGHT);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 4, 40);
        mainPanel.add(branchLabel, gbc);

        JPanel sep = createGoldSeparator();
        gbc.gridy = 2;
        gbc.insets = new Insets(8, 80, 20, 80);
        mainPanel.add(sep, gbc);

        // SELECT ACCESS TYPE LABEL
        JLabel selectLabel = new JLabel(
                "SELECT ACCESS TYPE",
                SwingConstants.CENTER);
        selectLabel.setFont(new Font(
                "Georgia", Font.BOLD, 13));
        selectLabel.setForeground(
                UIConstants.GOLD_LIGHT);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 40, 20, 40);
        mainPanel.add(selectLabel, gbc);

        // CUSTOMER MODE CARD
        JPanel customerCard =
                createModeCard(
                        "CUSTOMER PORTAL",
                        "Access your accounts, make\n"
                                + "transactions and manage your\n"
                                + "banking services",
                        UIConstants.GOLD,
                        UIConstants.GOLD_SUBTLE,
                        UIConstants.PRIMARY,
                        () -> openLogin("CUSTOMER"));

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 30, 12, 30);
        mainPanel.add(customerCard, gbc);

        // MANAGER MODE CARD
        JPanel managerCard =
                createModeCard(
                        "MANAGER PORTAL",
                        "Manage branch accounts,\n"
                                + "approvals and monitor\n"
                                + "banking operations",
                        UIConstants.PRIMARY,
                        new Color(220, 230, 245),
                        UIConstants.PRIMARY,
                        () -> openLogin("MANAGER"));

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 30, 16, 30);
        mainPanel.add(managerCard, gbc);

        // BACK BUTTON
        JButton backButton = new JButton(
                "← Change Branch") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color( 255, 255, 255, 0));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(getModel().isRollover() ? UIConstants.GOLD_LIGHT: new Color(180, 190, 210));
                FontMetrics fm =g2.getFontMetrics();
                int x = (getWidth()- fm.stringWidth( getText())) / 2;
                int y = (getHeight()
                        + fm.getAscent()- fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        backButton.setPreferredSize(new Dimension(200, 30));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor( Cursor.HAND_CURSOR));
        backButton.addActionListener(
                e -> goBack());

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 40, 20, 40);
        mainPanel.add(backButton, gbc);
    }

    // CREATE MODE CARD
    private JPanel createModeCard(
            String title,
            String description,
            Color accentColor,
            Color bgColor,
            Color textColor,
            Runnable onSelect) {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 =(Graphics2D) g.create();
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0,getWidth(), getHeight(),10, 10);

                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0,5, getHeight(), 4, 4);

                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        10, 10);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 0));
        card.setCursor(new Cursor(
                Cursor.HAND_CURSOR));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(
                content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(6));

        for (String line :description.split("\n")) {
            JLabel descLabel = new JLabel(line);
            descLabel.setFont(new Font(
                    "Segoe UI", Font.PLAIN, 11));
            descLabel.setForeground(
                    UIConstants.TEXT_SECONDARY);
            descLabel.setAlignmentX(
                    Component.LEFT_ALIGNMENT);
            content.add(descLabel);
        }

        content.add(Box.createVerticalStrut(10));

        JSeparator divider = new JSeparator();
        divider.setForeground(UIConstants.BORDER);
        divider.setMaximumSize(new Dimension( Integer.MAX_VALUE, 1));
        divider.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        content.add(divider);
        content.add(Box.createVerticalStrut(8));


        JButton selectBtn = new JButton("Enter " + title + "  →") {
            @Override
            protected void paintComponent(
                    Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(
                            accentColor.darker());
                } else {
                    g2.setColor(accentColor);
                }
                g2.fillRoundRect(0, 0,
                        getWidth(),
                        getHeight(), 6, 6);
                g2.setColor(
                        accentColor.equals(
                                UIConstants.GOLD)
                                ? UIConstants.PRIMARY
                                : UIConstants.TEXT_LIGHT);
                g2.setFont(new Font(
                        "Segoe UI",
                        Font.BOLD, 12));
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
        selectBtn.setPreferredSize(
                new Dimension(200, 36));
        selectBtn.setMaximumSize(
                new Dimension(200, 36));
        selectBtn.setBorderPainted(false);
        selectBtn.setContentAreaFilled(false);
        selectBtn.setFocusPainted(false);
        selectBtn.setCursor(new Cursor(
                Cursor.HAND_CURSOR));
        selectBtn.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        selectBtn.addActionListener(
                e -> onSelect.run());
        content.add(selectBtn);

        card.add(content, BorderLayout.CENTER);

        card.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            MouseEvent e) {
                        onSelect.run();
                    }
                });

        return card;
    }

    // NAVIGATION
    private void openLogin(String mode) {
        Session.getInstance().setUserMode(mode);
        LoginFrame loginFrame =
                new LoginFrame(mode);
        loginFrame.setVisible(true);
        this.dispose();
    }

    private void goBack() {
        BranchSelectionFrame branchFrame =
                new BranchSelectionFrame();
        branchFrame.setVisible(true);
        this.dispose();
    }

    // GOLD SEPARATOR
    private JPanel createGoldSeparator() {
        JPanel sep = new JPanel() {
            @Override
            protected void paintComponent(
                    Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(1));

                g2.drawLine(0,
                        getHeight() / 2,
                        getWidth() / 2 - 10,
                        getHeight() / 2);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int[] xp = {cx, cx + 5,
                        cx, cx - 5};
                int[] yp = {cy - 5, cy,
                        cy + 5, cy};
                g2.fillPolygon(xp, yp, 4);

                g2.drawLine(
                        getWidth() / 2 + 10,
                        getHeight() / 2,
                        getWidth(),
                        getHeight() / 2);

                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(
                new Dimension(200, 16));
        return sep;
    }
}