package com.bank.ui;

import com.bank.dao.BranchDAO;
import com.bank.db.Session;
import com.bank.models.Branch;
import com.bank.ui.utils.UIConstants;
import com.bank.ui.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BranchSelectionFrame extends JFrame {

    private JComboBox<Branch> branchComboBox;
    private JButton           selectButton;
    private JLabel            statusLabel;
    private BranchDAO         branchDAO;

    public BranchSelectionFrame() {
        branchDAO = new BranchDAO();
        initComponents();
        loadBranches();
        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);
        setSize(520, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle(UIConstants.BANK_NAME
                + " — Branch Selection");
    }

    private void initComponents() {
        // Main panel with background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Background gradient
                GradientPaint gradient =
                        new GradientPaint(
                                0, 0,
                                UIConstants.PRIMARY_DARK,
                                0, getHeight(),
                                UIConstants.PRIMARY_LIGHT);
                g2.setPaint(gradient);
                g2.fillRect(0, 0,
                        getWidth(), getHeight());

                // Decorative gold line at top
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
        gbc.insets = new Insets(8, 40, 8, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        // -----------------------------------------------
        // BANK NAME
        // -----------------------------------------------
        JLabel bankNameLabel = new JLabel(
                UIConstants.BANK_NAME,
                SwingConstants.CENTER);
        bankNameLabel.setFont(
                UIConstants.FONT_BANK_NAME);
        bankNameLabel.setForeground(
                UIConstants.TEXT_LIGHT);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 4, 40);
        mainPanel.add(bankNameLabel, gbc);

        // Tagline
        JLabel taglineLabel = new JLabel(
                UIConstants.BANK_TAGLINE,
                SwingConstants.CENTER);
        taglineLabel.setFont(
                UIConstants.FONT_BANK_TAGLINE);
        taglineLabel.setForeground(
                UIConstants.GOLD_LIGHT);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 30, 40);
        mainPanel.add(taglineLabel, gbc);

        // Gold decorative separator
        JPanel separator = createGoldSeparator();
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 60, 30, 60);
        mainPanel.add(separator, gbc);

        // -----------------------------------------------
        // SELECT BRANCH CARD
        // -----------------------------------------------
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 =
                        (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(
                        255, 255, 255, 240));
                g2.fillRoundRect(0, 0,
                        getWidth(), getHeight(),
                        12, 12);

                // Gold top border
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(20, 0,
                        getWidth() - 20, 0);

                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setLayout(
                new GridBagLayout());

        GridBagConstraints cardGbc =
                new GridBagConstraints();
        cardGbc.insets =
                new Insets(8, 20, 8, 20);
        cardGbc.fill =
                GridBagConstraints.HORIZONTAL;
        cardGbc.weightx = 1.0;

        // Card title
        JLabel cardTitle = new JLabel(
                "SELECT YOUR BRANCH",
                SwingConstants.CENTER);
        cardTitle.setFont(new Font(
                "Georgia", Font.BOLD, 14));
        cardTitle.setForeground(
                UIConstants.PRIMARY);

        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.insets =
                new Insets(24, 20, 4, 20);
        cardPanel.add(cardTitle, cardGbc);

        // Instruction label
        JLabel instructionLabel = new JLabel(
                "Please select the branch you "
                        + "wish to access",
                SwingConstants.CENTER);
        instructionLabel.setFont(
                UIConstants.FONT_SMALL);
        instructionLabel.setForeground(
                UIConstants.TEXT_SECONDARY);

        cardGbc.gridy = 1;
        cardGbc.insets =
                new Insets(0, 20, 16, 20);
        cardPanel.add(instructionLabel, cardGbc);

        // Branch label
        JLabel branchLabel = new JLabel(
                "Branch Location");
        branchLabel.setFont(
                UIConstants.FONT_BODY);
        branchLabel.setForeground(
                UIConstants.TEXT_SECONDARY);

        cardGbc.gridy = 2;
        cardGbc.insets =
                new Insets(4, 20, 4, 20);
        cardPanel.add(branchLabel, cardGbc);

        // Branch combo box
        branchComboBox = new JComboBox<>();
        branchComboBox.setFont(
                UIConstants.FONT_INPUT);
        branchComboBox.setBackground(
                UIConstants.SURFACE);
        branchComboBox.setForeground(
                UIConstants.TEXT_PRIMARY);
        branchComboBox.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIConstants.BORDER, 1),
                        BorderFactory.createEmptyBorder(
                                4, 8, 4, 8)));
        branchComboBox.setPreferredSize(
                new Dimension(300,
                        UIConstants.INPUT_HEIGHT));
        branchComboBox.setRenderer(
                new BranchListRenderer());

        cardGbc.gridy = 3;
        cardGbc.insets =
                new Insets(0, 20, 20, 20);
        cardPanel.add(branchComboBox, cardGbc);

        // Select button
        selectButton = UIUtils.createGoldButton(
                "PROCEED TO LOGIN");
        selectButton.setPreferredSize(
                new Dimension(300, 45));
        selectButton.addActionListener(
                e -> handleBranchSelection());

        cardGbc.gridy = 4;
        cardGbc.insets =
                new Insets(0, 20, 24, 20);
        cardPanel.add(selectButton, cardGbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 30, 8, 30);
        mainPanel.add(cardPanel, gbc);

        // -----------------------------------------------
        // STATUS LABEL
        // -----------------------------------------------
        statusLabel = new JLabel(
                " ", SwingConstants.CENTER);
        statusLabel.setFont(UIConstants.FONT_SMALL);
        statusLabel.setForeground(
                new Color(255, 200, 150));

        gbc.gridy = 5;
        gbc.insets = new Insets(4, 40, 10, 40);
        mainPanel.add(statusLabel, gbc);

        // -----------------------------------------------
        // FOOTER
        // -----------------------------------------------
        /*JLabel footerLabel = new JLabel(
                "© 2026 "
                        + UIConstants.BANK_NAME
                        + ". All Rights Reserved.",
                SwingConstants.CENTER);
        footerLabel.setFont(new Font(
                "Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(
                new Color(180, 180, 180));

        gbc.gridy = 6;
        gbc.insets = new Insets(8, 40, 20, 40);
        mainPanel.add(footerLabel, gbc);*/
    }

    // -------------------------------------------------------
    // LOAD BRANCHES FROM DATABASE
    // -------------------------------------------------------
    private void loadBranches() {
        try {
            List<Branch> branches =
                    branchDAO.getAllBranches();

            if (branches.isEmpty()) {
                statusLabel.setText(
                        "No branches found. "
                                + "Please contact support.");
                selectButton.setEnabled(false);
                return;
            }

            for (Branch branch : branches) {
                branchComboBox.addItem(branch);
            }

            statusLabel.setText(
                    branches.size()
                            + " branch(es) available");

        } catch (Exception e) {
            statusLabel.setText(
                    "Error loading branches: "
                            + e.getMessage());
            selectButton.setEnabled(false);
        }
    }

    // -------------------------------------------------------
    // HANDLE BRANCH SELECTION
    // -------------------------------------------------------
    private void handleBranchSelection() {
        Branch selectedBranch =
                (Branch) branchComboBox
                        .getSelectedItem();

        if (selectedBranch == null) {
            UIUtils.showError(this,
                    "Please select a branch "
                            + "to continue.");
            return;
        }

        // Store selected branch in session
        Session.getInstance()
                .setSelectedBranch(selectedBranch);

        // Open mode selection screen
        ModeSelectionFrame modeFrame =
                new ModeSelectionFrame();
        modeFrame.setVisible(true);

        // Close this frame
        this.dispose();
    }

    // -------------------------------------------------------
    // GOLD SEPARATOR
    // -------------------------------------------------------
    private JPanel createGoldSeparator() {
        JPanel sep = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 =
                        (Graphics2D) g.create();

                // Left line
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(0,
                        getHeight() / 2,
                        getWidth() / 2 - 15,
                        getHeight() / 2);

                // Center diamond
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int[] xp = {cx, cx + 6,
                        cx, cx - 6};
                int[] yp = {cy - 6, cy,
                        cy + 6, cy};
                g2.setColor(UIConstants.GOLD);
                g2.fillPolygon(xp, yp, 4);

                // Right line
                g2.drawLine(
                        getWidth() / 2 + 15,
                        getHeight() / 2,
                        getWidth(),
                        getHeight() / 2);

                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(
                new Dimension(300, 20));
        return sep;
    }

    // -------------------------------------------------------
    // BRANCH LIST RENDERER
    // -------------------------------------------------------
    private static class BranchListRenderer
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            super.getListCellRendererComponent(
                    list, value, index,
                    isSelected, cellHasFocus);

            if (value instanceof Branch) {
                Branch branch = (Branch) value;
                setText(branch.getBranchName()
                        + "  —  "
                        + branch.getBranchCode());
                setFont(UIConstants.FONT_BODY);
                setBorder(
                        BorderFactory.createEmptyBorder(
                                6, 10, 6, 10));

                if (isSelected) {
                    setBackground(
                            UIConstants.PRIMARY);
                    setForeground(
                            UIConstants.TEXT_LIGHT);
                } else {
                    setBackground(
                            UIConstants.SURFACE);
                    setForeground(
                            UIConstants.TEXT_PRIMARY);
                }
            }
            return this;
        }
    }
}