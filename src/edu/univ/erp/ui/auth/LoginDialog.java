package edu.univ.erp.ui.auth;

import edu.univ.erp.ui.Main;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * Modal Login Dialog.
 * *** UPDATED: Added 'admin' role to onSignIn. ***
 */
public class LoginDialog extends JDialog {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JLabel messageLabel;
    private JCheckBox showPasswordCheckBox;
    private Main mainApp;

    public LoginDialog(Main parent) {
        super(parent, "ERP System Login", true);
        this.mainApp = parent;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // ... (All layout code is identical to the previous version) ...
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

    private void onSignIn() {
        String username = emailField.getText();
        char[] password = passwordField.getPassword();

        String role = null;
        if (username.equals("student@university.edu") && "student123".equals(new String(password))) {
            role = "student";
        } else if (username.equals("instructor@university.edu") && "instructor123".equals(new String(password))) {
            role = "instructor";
        } else if (username.equals("admin@university.edu") && "admin123".equals(new String(password))) { // NEW
            role = "admin";
        }

        if (role != null) {
            this.dispose();
            mainApp.onLoginSuccess(role, username);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Incorrect username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }

        Arrays.fill(password, '0');
    }
}