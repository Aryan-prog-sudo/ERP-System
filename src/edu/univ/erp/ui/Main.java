package edu.univ.erp.ui;

import edu.univ.erp.ui.auth.LoginDialog;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // --- START: Beautification ---
        // Try to set the "Nimbus" Look and Feel for a more modern appearance
        // This is the single biggest improvement we can make.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, try to use the system's default L&F
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // If that fails, we'll just use the cross-platform default
                System.err.println("Failed to set Look and Feel: " + ex.getMessage());
            }
        }
        // --- END: Beautification ---


        // Run the UI code on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Create a temporary invisible frame as the owner for the modal dialog
            JFrame tempOwner = new JFrame();
            tempOwner.setUndecorated(true);
            tempOwner.setLocationRelativeTo(null);
            tempOwner.setVisible(true);

            // Create and show the login dialog
            LoginDialog loginDialog = new LoginDialog(tempOwner);
            loginDialog.setVisible(true);

            // After the dialog is closed, the program flow continues.
            tempOwner.dispose();
            System.exit(0);
        });
    }
}