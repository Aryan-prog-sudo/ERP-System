package edu.univ.erp.ui.auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * Modal dialog for changing a user's password.
 * Corresponds to: edu.univ.erp.ui.auth.ChangePasswordDialog
 * Design: image_3ece63.png
 * Uses ONLY built-in Java components.
 */
public class ChangePasswordDialog extends JDialog {

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Password", true); // true = modal

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // --- Main Panel with GridBagLayout ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(25, 25, 25, 25)); // Padding
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Title ---
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span 2 columns
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(titleLabel, gbc);

        // --- Subtitle ---
        JLabel subtitleLabel = new JLabel("Enter your old password and choose a new one.");
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(subtitleLabel, gbc);

        // --- Reset GBC for form fields ---
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Old Password ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Old Password"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        oldPasswordField = new JPasswordField(25);
        oldPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(oldPasswordField, gbc);

        // --- New Password ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("New Password"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        newPasswordField = new JPasswordField(25);
        newPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(newPasswordField, gbc);

        // --- Confirm New Password ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Confirm New Password"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0); // Extra bottom padding
        confirmPasswordField = new JPasswordField(25);
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(confirmPasswordField, gbc);

        // --- Buttons Panel (Aligned Right) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.setOpaque(true);
        submitButton.setBackground(new Color(0, 82, 204));
        submitButton.setForeground(Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        // Add button panel to the grid
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(buttonPanel, gbc);

        // --- Action Listeners ---
        cancelButton.addActionListener(e -> dispose()); // Just close the dialog
        submitButton.addActionListener(e -> onSubmit());

        add(panel);
        pack(); // Size dialog to fit components
        setLocationRelativeTo(parent); // Center on parent window
    }

    /**
     * Called when the "Submit" button is clicked.
     */
    private void onSubmit() {
        char[] oldPass = oldPasswordField.getPassword();
        char[] newPass = newPasswordField.getPassword();
        char[] confirmPass = confirmPasswordField.getPassword();

        // 1. Check if new passwords match
        if (!Arrays.equals(newPass, confirmPass)) {
            JOptionPane.showMessageDialog(this,
                    "The new passwords do not match.",
                    "Password Mismatch",
                    JOptionPane.ERROR_MESSAGE);
            // Clear password arrays
            Arrays.fill(oldPass, '0');
            Arrays.fill(newPass, '0');
            Arrays.fill(confirmPass, '0');
            return; // Stop processing
        }

        // 2. Check if new password is empty
        if (newPass.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "The new password cannot be empty.",
                    "Invalid Password",
                    JOptionPane.ERROR_MESSAGE);
            Arrays.fill(oldPass, '0');
            Arrays.fill(newPass, '0');
            Arrays.fill(confirmPass, '0');
            return; // Stop processing
        }

        // 3. --- Call the Authentication Service ---
        // This is where you would call your auth.service layer, as per the project brief.
        // The service would securely check the old password hash and update to the new one.
        //
        // Example:
        // try {
        //     AuthService authService = new AuthServiceImpl();
        //     // We'd get the current user's ID from the SessionManager
        //     authService.changePassword(SessionManager.getInstance().getUserId(), oldPass, newPass);
        //
        //     JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        //     dispose();
        //
        // } catch (IncorrectPasswordException ex) {
        //     JOptionPane.showMessageDialog(this, "The old password you entered is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
        // } catch (Exception ex) {
        //     JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        // }


        // --- Placeholder Logic for this demo ---
        // (Simulating a successful password change)
        JOptionPane.showMessageDialog(this,
                "Password updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        // 4. Securely clear arrays and close
        Arrays.fill(oldPass, '0');
        Arrays.fill(newPass, '0');
        Arrays.fill(confirmPass, '0');

        dispose(); // Close the dialog
    }
}