package edu.univ.erp.ui.auth;

import edu.univ.erp.service.AuthService; // <-- IMPORT THE SERVICE
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * Modal dialog for changing a user's password.
 * UPDATED: Now calls the AuthService to change the password.
 */
public class ChangePasswordDialog extends JDialog {

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    private AuthService authService; // <-- 1. ADD THIS FIELD
    private String userEmail; // <-- 2. ADD THIS FIELD

    /**
     * 3. UPDATED: Constructor now accepts AuthService and the user's email.
     */
    public ChangePasswordDialog(JFrame parent, AuthService authService, String userEmail) {
        super(parent, "Change Password", true);
        this.authService = authService;
        this.userEmail = userEmail;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // (Layout code is the same)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(titleLabel, gbc);
        JLabel subtitleLabel = new JLabel("Enter your old password and choose a new one.");
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(subtitleLabel, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Old Password"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        oldPasswordField = new JPasswordField(25);
        oldPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(oldPasswordField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("New Password"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        newPasswordField = new JPasswordField(25);
        newPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(newPasswordField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Confirm New Password"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        confirmPasswordField = new JPasswordField(25);
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(confirmPasswordField, gbc);
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
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(buttonPanel, gbc);

        // --- Action Listeners ---
        cancelButton.addActionListener(e -> dispose());
        submitButton.addActionListener(e -> onSubmit());

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * 4. UPDATED: Called when the "Submit" button is clicked.
     */
    private void onSubmit() {
        char[] oldPass = oldPasswordField.getPassword();
        char[] newPass = newPasswordField.getPassword();
        char[] confirmPass = confirmPasswordField.getPassword();

        // 1. Check if new passwords match
        if (!Arrays.equals(newPass, confirmPass)) {
            JOptionPane.showMessageDialog(this, "The new passwords do not match.", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            clearPasswords(oldPass, newPass, confirmPass);
            return;
        }

        // 2. Check if new password is empty
        if (newPass.length == 0) {
            JOptionPane.showMessageDialog(this, "The new password cannot be empty.", "Invalid Password", JOptionPane.ERROR_MESSAGE);
            clearPasswords(oldPass, newPass, confirmPass);
            return;
        }

        // 3. --- Call the Authentication Service ---
        boolean success = authService.changePassword(
                this.userEmail,
                new String(oldPass),
                new String(newPass)
        );

        // 4. Show result
        if (success) {
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearPasswords(oldPass, newPass, confirmPass);
            dispose(); // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "The old password you entered is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            clearPasswords(oldPass, newPass, confirmPass);
        }
    }

    /**
     * Helper method to securely clear password arrays.
     */
    private void clearPasswords(char[]... passwordArrays) {
        for (char[] password : passwordArrays) {
            Arrays.fill(password, '0');
        }
    }
}