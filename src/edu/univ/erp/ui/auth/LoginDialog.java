package edu.univ.erp.ui.auth;

import edu.univ.erp.ui.Main;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.LoginResult;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * Modal Login Dialog.
 * UPDATED: Now calls the AuthService to log in.
 * FIXED: Includes all field declarations.
 */
public class LoginDialog extends JDialog {

    // --- FIELD DECLARATIONS (This was the missing part) ---
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JLabel messageLabel;
    private JCheckBox showPasswordCheckBox;

    private Main mainApp;
    private AuthService authService;

    /**
     * UPDATED Constructor
     */
    public LoginDialog(Main parent, AuthService authService) {
        super(parent, "ERP System Login", true);
        this.mainApp = parent;
        this.authService = authService;

        // (All layout code is the same)
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        JLabel iconLabel = new JLabel("[ICON]");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        panel.add(iconLabel, gbc);
        gbc.gridy++;
        JLabel titleLabel = new JLabel("ERP System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, gbc);
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Sign in to your portal");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(subtitleLabel, gbc);
        gbc.gridy++;
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        messageLabel.setForeground(Color.BLUE);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(messageLabel, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.gridy++;
        panel.add(new JLabel("Email"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 0, 0);
        emailField = new JTextField(25);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(emailField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(new JLabel("Password"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 0, 0);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(passwordField, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 0, 0);
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) passwordField.setEchoChar((char) 0);
            else passwordField.setEchoChar('*');
        });
        panel.add(showPasswordCheckBox, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        signInButton = new JButton("Sign In");
        signInButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        signInButton.setOpaque(true);
        signInButton.setBackground(new Color(0, 82, 204));
        signInButton.setForeground(Color.WHITE);
        signInButton.setPreferredSize(new Dimension(100, 40));
        panel.add(signInButton, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        JTextArea demoCreds = new JTextArea(
                "Demo Credentials:\n" +
                        "Student: student@university.edu / student123\n" +
                        "Instructor: instructor@university.edu / instructor123\n" +
                        "Admin: admin@university.edu / admin123"
        );
        demoCreds.setEditable(false); demoCreds.setOpaque(false);
        demoCreds.setForeground(Color.GRAY);
        demoCreds.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(demoCreds, gbc);
        signInButton.addActionListener(e -> onSignIn());
        getRootPane().setDefaultButton(signInButton);
        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    public void setLogoutMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * UPDATED onSignIn method
     */
    private void onSignIn() {
        String username = emailField.getText();
        char[] password = passwordField.getPassword();

        LoginResult result = authService.login(username, new String(password));

        if (result.isSuccess) {
            this.dispose();
            // --- UPDATED: Pass the REAL UserID to Main ---
            mainApp.onLoginSuccess(result.Role, username, result.userId);
        } else {
            JOptionPane.showMessageDialog(this,
                    result.Message,
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }

        Arrays.fill(password, '0');
    }
}