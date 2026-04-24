package com.bank;

import com.bank.ui.BranchSelectionFrame;
import javax.swing.*;

public class AppLauncher {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager
                            .getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            BranchSelectionFrame frame =
                    new BranchSelectionFrame();
            frame.setVisible(true);
        });
    }
}