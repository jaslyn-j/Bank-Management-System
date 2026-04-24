package com.bank.ui.utils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class UIUtils {

    // -------------------------------------------------------
    // BUTTONS
    // -------------------------------------------------------

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(
                text,
                UIConstants.PRIMARY,
                UIConstants.PRIMARY_HOVER,
                UIConstants.PRIMARY_DARK,
                UIConstants.TEXT_LIGHT);
    }

    public static JButton createGoldButton(String text) {
        return createStyledButton(
                text,
                UIConstants.GOLD,
                UIConstants.GOLD_LIGHT,
                UIConstants.GOLD_DARK,
                UIConstants.PRIMARY);
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(
                text,
                UIConstants.DANGER,
                UIConstants.DANGER.brighter(),
                UIConstants.DANGER.darker(),
                UIConstants.TEXT_LIGHT);
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(
                text,
                UIConstants.SUCCESS,
                UIConstants.SUCCESS.brighter(),
                UIConstants.SUCCESS.darker(),
                UIConstants.TEXT_LIGHT);
    }

    public static JButton createWarningButton(String text) {
        return createStyledButton(
                text,
                UIConstants.WARNING,
                UIConstants.WARNING.brighter(),
                UIConstants.WARNING.darker(),
                UIConstants.TEXT_LIGHT);
    }

    public static JButton createOutlineButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(UIConstants.GOLD_SUBTLE);
                } else {
                    g2.setColor(UIConstants.SURFACE);
                }
                g2.fillRoundRect(0, 0,
                        getWidth(), getHeight(),
                        UIConstants.BORDER_RADIUS,
                        UIConstants.BORDER_RADIUS);
                g2.setColor(UIConstants.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        UIConstants.BORDER_RADIUS,
                        UIConstants.BORDER_RADIUS);
                g2.setFont(UIConstants.FONT_BUTTON);
                g2.setColor(UIConstants.PRIMARY);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()
                        - fm.stringWidth(getText())) / 2;
                int y = (getHeight()
                        + fm.getAscent()
                        - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        styleButtonBase(button);
        return button;
    }

    private static JButton createStyledButton(
            String text,
            Color normal,
            Color hover,
            Color pressed,
            Color textColor) {

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(pressed);
                } else if (getModel().isRollover()) {
                    g2.setColor(hover);
                } else {
                    g2.setColor(normal);
                }
                g2.fillRoundRect(0, 0,
                        getWidth(), getHeight(),
                        UIConstants.BORDER_RADIUS,
                        UIConstants.BORDER_RADIUS);
                g2.setColor(textColor);
                g2.setFont(UIConstants.FONT_BUTTON);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()
                        - fm.stringWidth(getText())) / 2;
                int y = (getHeight()
                        + fm.getAscent()
                        - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        styleButtonBase(button);
        return button;
    }

    private static void styleButtonBase(JButton button) {
        button.setPreferredSize(new Dimension(
                150, UIConstants.BUTTON_HEIGHT));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
    }

    // -------------------------------------------------------
    // INPUT FIELDS
    // -------------------------------------------------------

    public static JTextField createTextField(
            String placeholder) {
        JTextField field = new JTextField();
        styleInputField(field);
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        styleInputField(field);
        return field;
    }

    private static void styleInputField(JTextField field) {
        field.setFont(UIConstants.FONT_INPUT);
        field.setForeground(UIConstants.TEXT_PRIMARY);
        field.setBackground(UIConstants.SURFACE);
        field.setBorder(createInputBorder(false));
        field.setPreferredSize(new Dimension(
                200, UIConstants.INPUT_HEIGHT));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(
                        createInputBorder(true));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(
                        createInputBorder(false));
            }
        });
    }

    private static Border createInputBorder(
            boolean focused) {
        Color borderColor = focused
                ? UIConstants.BORDER_FOCUS
                : UIConstants.BORDER;
        int thickness = focused ? 2 : 1;
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        borderColor, thickness),
                BorderFactory.createEmptyBorder(
                        8, 12, 8, 12));
    }

    // -------------------------------------------------------
    // LABELS
    // -------------------------------------------------------

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_TITLE);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createHeadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_HEADING);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_BODY);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_SMALL);
        label.setForeground(UIConstants.TEXT_MUTED);
        return label;
    }

    public static JLabel createGoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_HEADING);
        label.setForeground(UIConstants.GOLD);
        return label;
    }

    // -------------------------------------------------------
    // PANELS
    // -------------------------------------------------------

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER, 1),
                BorderFactory.createEmptyBorder(
                        UIConstants.CARD_PADDING,
                        UIConstants.CARD_PADDING,
                        UIConstants.CARD_PADDING,
                        UIConstants.CARD_PADDING)));
        return panel;
    }

    public static JPanel createSectionHeader(
            String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(
                0, 0, 16, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.FONT_SUBTITLE);
        titleLabel.setForeground(UIConstants.PRIMARY);
        panel.add(titleLabel, BorderLayout.WEST);

        // Gold accent line under title
        JPanel accentLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UIConstants.GOLD);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, 0,
                        getWidth(), 0);
                g2.dispose();
            }
        };
        accentLine.setBackground(UIConstants.SURFACE);
        accentLine.setPreferredSize(
                new Dimension(0, 2));
        panel.add(accentLine, BorderLayout.SOUTH);

        return panel;
    }

    // Status badge
    public static JLabel createStatusBadge(
            String status) {
        JLabel badge = new JLabel(
                " " + status.toUpperCase() + " ");
        badge.setFont(UIConstants.FONT_SMALL);
        badge.setHorizontalAlignment(
                SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(
                3, 8, 3, 8));

        switch (status.toLowerCase()) {
            case "active":
                badge.setBackground(
                        UIConstants.SUCCESS_LIGHT);
                badge.setForeground(UIConstants.SUCCESS);
                break;
            case "blocked":
                badge.setBackground(
                        UIConstants.DANGER_LIGHT);
                badge.setForeground(UIConstants.DANGER);
                break;
            case "pending":
                badge.setBackground(
                        UIConstants.WARNING_LIGHT);
                badge.setForeground(UIConstants.WARNING);
                break;
            case "closed":
            case "cancelled":
                badge.setBackground(
                        new Color(235, 233, 228));
                badge.setForeground(
                        UIConstants.TEXT_SECONDARY);
                break;
            case "open":
                badge.setBackground(
                        UIConstants.DANGER_LIGHT);
                badge.setForeground(UIConstants.DANGER);
                break;
            case "reviewed":
                badge.setBackground(
                        UIConstants.INFO_LIGHT);
                badge.setForeground(UIConstants.INFO);
                break;
            case "dismissed":
                badge.setBackground(
                        new Color(235, 233, 228));
                badge.setForeground(
                        UIConstants.TEXT_SECONDARY);
                break;
            default:
                badge.setBackground(
                        UIConstants.GOLD_SUBTLE);
                badge.setForeground(UIConstants.GOLD_DARK);
                break;
        }
        return badge;
    }

    // -------------------------------------------------------
    // TABLES
    // -------------------------------------------------------

    public static void styleTable(JTable table) {
        table.setFont(UIConstants.FONT_TABLE_BODY);
        table.setForeground(UIConstants.TEXT_PRIMARY);
        table.setBackground(UIConstants.SURFACE);
        table.setRowHeight(
                UIConstants.TABLE_ROW_HEIGHT);
        table.setGridColor(UIConstants.BORDER);
        table.setSelectionBackground(
                UIConstants.TABLE_SELECTION);
        table.setSelectionForeground(
                UIConstants.TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(
                new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(UIConstants.FONT_TABLE_HEADER);
        header.setBackground(
                UIConstants.TABLE_HEADER_BG);
        header.setForeground(
                UIConstants.PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, UIConstants.GOLD));
        header.setPreferredSize(
                new Dimension(0, 42));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class,
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
                        Component c =
                                super.getTableCellRendererComponent(
                                        t, value, isSelected,
                                        hasFocus, row, column);
                        if (!isSelected) {
                            c.setBackground(row % 2 == 0
                                    ? UIConstants.SURFACE
                                    : UIConstants.TABLE_ROW_ALT);
                            c.setForeground(
                                    UIConstants.TEXT_PRIMARY);
                        }
                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 12, 0, 12));
                        return c;
                    }
                });
    }

    // -------------------------------------------------------
    // SCROLL PANE
    // -------------------------------------------------------

    public static JScrollPane createScrollPane(
            Component component) {
        JScrollPane scrollPane =
                new JScrollPane(component);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER, 1));
        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);
        scrollPane.setBackground(UIConstants.SURFACE);
        return scrollPane;
    }

    // -------------------------------------------------------
    // COMBO BOX
    // -------------------------------------------------------

    public static <T> JComboBox<T> createComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(UIConstants.FONT_INPUT);
        combo.setBackground(UIConstants.SURFACE);
        combo.setForeground(UIConstants.TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(
                UIConstants.BORDER, 1));
        combo.setPreferredSize(new Dimension(
                200, UIConstants.INPUT_HEIGHT));
        return combo;
    }

    // -------------------------------------------------------
    // DIALOGS
    // -------------------------------------------------------

    public static void showSuccess(
            Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent, message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(
            Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent, message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirm(
            Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
                parent, message,
                "Confirm Action",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    // -------------------------------------------------------
    // FORM ROW HELPER
    // -------------------------------------------------------

    public static JPanel createFormRow(
            String labelText,
            JComponent inputComponent) {

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(UIConstants.SURFACE);
        row.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 45));

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.FONT_BODY);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        label.setPreferredSize(new Dimension(140, 40));
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(label, BorderLayout.WEST);
        row.add(inputComponent, BorderLayout.CENTER);

        return row;
    }
}