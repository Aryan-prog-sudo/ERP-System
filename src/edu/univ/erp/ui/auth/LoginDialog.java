package edu.univ.erp.ui.auth;

import edu.univ.erp.ui.Main;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.LoginResult;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder; // For modern text fields
import java.awt.*;
import java.awt.event.MouseAdapter; // For button hover
import java.awt.event.MouseEvent; // For button hover
import java.net.URL; // To load the icon
import java.util.Arrays;

/**
 * Modal Login Dialog.
 * UPDATED: Beautified with a modern look and feel.
 * FIXED: Field declarations and case-sensitivity in onSignIn.
 */
public class LoginDialog extends JDialog {

    // --- Color Theme ---
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_TEXT_FIELD_BG = new Color(248, 248, 248);
    private static final Color COLOR_DEMO_BG = new Color(245, 245, 245);

    // --- Field Declarations ---
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JLabel messageLabel;
    private JCheckBox showPasswordCheckBox;

    private Main mainApp;
    private AuthService authService;

    public LoginDialog(Main parent, AuthService authService) {
        super(parent, "ERP System Login", true);
        this.mainApp = parent;
        this.authService = authService;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // --- Main Panel ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        // --- 1. The Icon (Replaces "[ICON]") ---
        // This will load an image from your project's "resources" folder.
        // (See instructions below on how to add this image).
        JLabel iconLabel = createIconLabel();
        panel.add(iconLabel, gbc);

        // --- 2. Titles ---
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

        // --- 3. Message Label ---
        gbc.gridy++;
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        messageLabel.setForeground(COLOR_PRIMARY);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(messageLabel, gbc);

        // --- 4. Form Fields ---
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 0, 0);
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        emailLabel.setForeground(COLOR_TEXT_DARK);
        panel.add(emailLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 0, 0);
        emailField = createModernTextField(25);
        panel.add(emailField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passLabel.setForeground(COLOR_TEXT_DARK);
        panel.add(passLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 0, 0);
        passwordField = createModernPasswordField();
        panel.add(passwordField, gbc);

        // --- 5. Show Password Checkbox ---
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 0, 0);
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        showPasswordCheckBox.setBackground(COLOR_BACKGROUND);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) passwordField.setEchoChar((char) 0);
            else passwordField.setEchoChar('*');
        });
        panel.add(showPasswordCheckBox, gbc);

        // --- 6. Sign In Button ---
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        signInButton = createModernButton("Sign In");
        panel.add(signInButton, gbc);

        // --- 7. Demo Credentials Area ---
        gbc.gridy++;
        gbc.insets = new Insets(25, 0, 0, 0);
        panel.add(createDemoCredentialsPanel(), gbc);

        // --- Set Actions ---
        signInButton.addActionListener(e -> onSignIn());
        getRootPane().setDefaultButton(signInButton);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Helper method to create a modern button with hover effects.
     */
    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 40));
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(COLOR_PRIMARY_DARK);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(COLOR_PRIMARY);
            }
        });
        return button;
    }

    /**
     * Helper method to create a modern text field.
     */
    private JTextField createModernTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(COLOR_TEXT_FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(8, 8, 8, 8) // Internal padding
        ));
        return field;
    }

    /**
     * Helper method to create a modern password field.
     */
    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(COLOR_TEXT_FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(8, 8, 8, 8) // Internal padding
        ));
        return field;
    }

    /**
     * Helper method to create the styled demo credentials panel.
     */
    private JPanel createDemoCredentialsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_DEMO_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea demoCreds = new JTextArea(
                "Demo Credentials:\n" +
                        "Student: student@university.edu / student123\n" +
                        "Instructor: instructor@university.edu / instructor123\n" +
                        "Admin: admin@university.edu / admin123"
        );
        demoCreds.setEditable(false);
        demoCreds.setOpaque(false); // Transparent
        demoCreds.setForeground(COLOR_TEXT_LIGHT);
        demoCreds.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(demoCreds);
        return panel;
    }

    /**
     * Helper method to load the icon image.
     */
    private JLabel createIconLabel() {
        try {
            // Try to load the image from the resources folder
            URL iconUrl = getClass().getResource("/icons/university_icon.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                // Resize the icon to be 64x64
                Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception e) {
            // Image not found, log it and fall back
            e.printStackTrace();
        }

        // Fallback: If the image fails, use a Unicode character
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 64));
        iconLabel.setForeground(COLOR_PRIMARY);
        return iconLabel;
    }

    public void setLogoutMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setForeground(COLOR_PRIMARY);
    }

    /**
     * UPDATED onSignIn method
     * (Functionality is identical, but fixed case-sensitivity bugs)
     */
    private void onSignIn() {
        String username = emailField.getText();
        char[] password = passwordField.getPassword();

        LoginResult result = authService.login(username, new String(password));

        if (result.isSuccess) {
            this.dispose();
            // --- UPDATED: Pass the REAL UserID to Main ---
            // Fixed: result.Role -> result.role
            mainApp.onLoginSuccess(result.Role, username, result.userId);
        } else {
            // Fixed: result.Message -> result.message
            JOptionPane.showMessageDialog(this,
                    result.Message,
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }

        Arrays.fill(password, '0');
    }
}