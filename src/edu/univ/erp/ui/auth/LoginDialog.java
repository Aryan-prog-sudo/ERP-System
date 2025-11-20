package edu.univ.erp.ui.auth;

import edu.univ.erp.ui.Main;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.LoginResult;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Arrays;

//This handles the logic for the login frontend
public class LoginDialog extends JDialog {

    //Color themes
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_TEXT_FIELD_BG = new Color(248, 248, 248);

    // Fields
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JLabel messageLabel;
    private JCheckBox showPasswordCheckBox;

    private Main mainApp;
    private AuthService authService;

    //Counter to check the failed login attempts
    //If it fails 5 times repeatedly then it blocks the login page
    private int failedAttempts = 0;

    public LoginDialog(Main parent, AuthService authService) {
        super(parent, "ERP System Login", true);
        this.mainApp = parent;
        this.authService = authService;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //On closing the window stop the code
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel iconLabel = createIconLabel();
        panel.add(iconLabel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy++;
        JLabel titleLabel = new JLabel("ERP System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Sign in to your portal");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(COLOR_TEXT_LIGHT);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(subtitleLabel, gbc);

        gbc.gridy++;
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        messageLabel.setForeground(COLOR_PRIMARY);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(messageLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy++; gbc.insets = new Insets(15, 0, 0, 0);
        JLabel emailLabel = new JLabel("UseName");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        emailLabel.setForeground(COLOR_TEXT_DARK);
        panel.add(emailLabel, gbc);

        gbc.gridy++; gbc.insets = new Insets(4, 0, 0, 0);
        emailField = createModernTextField(25);
        panel.add(emailField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 0, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passLabel.setForeground(COLOR_TEXT_DARK);
        panel.add(passLabel, gbc);

        gbc.gridy++; gbc.insets = new Insets(4, 0, 0, 0);
        passwordField = createModernPasswordField();
        panel.add(passwordField, gbc);

        gbc.gridy++; gbc.insets = new Insets(5, 0, 0, 0);
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        showPasswordCheckBox.setBackground(COLOR_BACKGROUND);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) passwordField.setEchoChar((char) 0);
            else passwordField.setEchoChar('*');
        });
        panel.add(showPasswordCheckBox, gbc);

        gbc.gridy++; gbc.insets = new Insets(20, 0, 0, 0);
        signInButton = createModernButton("Sign In");
        panel.add(signInButton, gbc);

        signInButton.addActionListener(e -> onSignIn());
        getRootPane().setDefaultButton(signInButton);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }


    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 40));
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setBackground(COLOR_PRIMARY_DARK); }
            public void mouseExited(MouseEvent evt) { button.setBackground(COLOR_PRIMARY); }
        });
        return button;
    }

    private JTextField createModernTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(COLOR_TEXT_FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1), new EmptyBorder(8, 8, 8, 8)));
        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(COLOR_TEXT_FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1), new EmptyBorder(8, 8, 8, 8)));
        return field;
    }

    private JLabel createIconLabel() {
        try {
            URL iconUrl = getClass().getResource("/icons/iiitd_logo.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception e) { e.printStackTrace(); }
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 64));
        iconLabel.setForeground(COLOR_PRIMARY);
        return iconLabel;
    }

    public void setLogoutMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setForeground(COLOR_PRIMARY);
    }


    //Logic to count the number of failed attempts
    //Calls the login function of the backend which compares the username and the password to AuthDB
    private void onSignIn() {
        //On 5 attempts stop the login access and give ERROR_MESSAGE
        if (failedAttempts >= 5) {
            JOptionPane.showMessageDialog(this,
                    "Access Locked: Too many failed attempts.\nPlease restart the application.",
                    "Login Blocked",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = emailField.getText(); //Input of the username field
        char[] password = passwordField.getPassword(); //Input of the password field
        LoginResult result = authService.login(username, new String(password));
        if (result.isSuccess) {
            this.dispose();
            mainApp.onLoginSuccess(result.Role, username, result.userId);
        }
        else {//Increment failed attempts failure
            failedAttempts++;
            int remaining = 5 - failedAttempts;
            String errorMsg = result.Message;
            if (remaining > 0) {
                errorMsg += "\nAttempts remaining: " + remaining;
            }
            else {
                errorMsg += "\nAccount is now LOCKED.";
                signInButton.setEnabled(false); // Disable button visually
                signInButton.setBackground(Color.GRAY);
            }
            JOptionPane.showMessageDialog(this,
                    errorMsg,
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
        Arrays.fill(password, '0');
    }
}