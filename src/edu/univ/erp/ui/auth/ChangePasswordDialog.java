package edu.univ.erp.ui.auth;

import edu.univ.erp.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;


//This handles the dialog (the UI not the backend) for changing the password
public class ChangePasswordDialog extends JDialog {

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    private AuthService authService;
    private String userEmail;

    public ChangePasswordDialog(JFrame parent, AuthService authService, String userEmail) {
        super(parent, "Change Password", true);
        this.authService = authService;
        this.userEmail = userEmail;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

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

        cancelButton.addActionListener(e -> dispose());
        submitButton.addActionListener(e -> onSubmit());

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    //This piece of code is run when the submit button is clicked
    private void onSubmit() {
        char[] oldPass = oldPasswordField.getPassword();
        char[] newPass = newPasswordField.getPassword();
        char[] confirmPass = confirmPasswordField.getPassword();
        //This checks that the new password field input and the confirm password field input match
        if (!Arrays.equals(newPass, confirmPass)) {
            JOptionPane.showMessageDialog(this, "The new passwords do not match.", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            clearPasswords(oldPass, newPass, confirmPass);
            return;
        }
        //The new password cannot be empty
        if (newPass.length == 0) {
            JOptionPane.showMessageDialog(this, "The new password cannot be empty.", "Invalid Password", JOptionPane.ERROR_MESSAGE);
            clearPasswords(oldPass, newPass, confirmPass);
            return;
        }
        //Backend logic call
        boolean success = authService.ChangePassword(
                this.userEmail,
                new String(oldPass),
                new String(newPass)
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearPasswords(oldPass, newPass, confirmPass);
            dispose(); // Close the dialog on success
        }
        else {
            JOptionPane.showMessageDialog(this, "The old password you entered is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            clearPasswords(oldPass, newPass, confirmPass);
        }
    }

    //Clear array, we cannot store the passwords
     void clearPasswords(char[]... passwordArrays) {
        for (char[] password : passwordArrays) {
            Arrays.fill(password, '0');
        }
    }
}