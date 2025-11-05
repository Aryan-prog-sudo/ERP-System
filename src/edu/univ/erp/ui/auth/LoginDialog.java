package edu.univ.erp.ui.auth;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A "beautified" modal dialog for user login.
 * This version does NOT ask for the user's role.
 */
public class LoginDialog extends JDialog {

    // --- Define our "beautified" styles ---
    private static final Color COLOR_BACKGROUND = new Color(245, 248, 252);
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(220, 225, 230);
    private static final Color COLOR_TEXT_PRIMARY = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_SECONDARY = new Color(100, 100, 100);
    private static final Color COLOR_BUTTON_PRIMARY = new Color(0, 116, 217);

    private static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    private static final Font FONT_SUBTITLE = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    private static final Font FONT_LABEL = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    private static final Font FONT_FIELD = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private static final Font FONT_BUTTON = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    // A clean, flat border for our text fields
    private static final Border FIELD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(8, 12, 8, 12) // top, left, bottom, right padding
    );
    // --- End of styles ---

    private JTextField emailField;
    private JPasswordField passwordField;
    // --- No more roleComboBox ---
    // private JComboBox<String> roleComboBox;

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        initUI();
    }

    private void initUI() {
        // Set up the main dialog window
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(420, 520); // Made height slightly smaller
        setLocationRelativeTo(getOwner());
        setTitle("ERP System Login");

        // 1. Create the main content pane
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_BACKGROUND);
        setContentPane(contentPane);

        // 2. Create a holder panel to center the white card
        JPanel centerHolder = new JPanel(new GridBagLayout());
        centerHolder.setOpaque(false);
        contentPane.add(centerHolder, BorderLayout.CENTER);

        // 3. Create the central white login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(COLOR_CARD);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                new EmptyBorder(35, 35, 35, 35) // Generous internal padding
        ));
        centerHolder.add(loginPanel, new GridBagConstraints());

        // 4. Add components to the login panel
        addLoginComponents(loginPanel);
    }

    private void addLoginComponents(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // All components will fill horizontally
        gbc.gridx = 0;
        gbc.weightx = 1.0; // Allow horizontal growth

        // 0. Icon (Using a Unicode character)
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 52));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(iconLabel, gbc);

        // 1. Title
        JLabel titleLabel = new JLabel("ERP System");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(titleLabel, gbc);

        // 2. Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to access your educational portal");
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setForeground(COLOR_TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 25, 0); // More space after subtitle
        panel.add(subtitleLabel, gbc);

        // 3. Email Label
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(FONT_LABEL);
        emailLabel.setForeground(COLOR_TEXT_PRIMARY);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 5, 0);
        panel.add(emailLabel, gbc);

        // 4. Email Field
        emailField = new JTextField("student@university.edu");
        emailField.setFont(FONT_FIELD);
        emailField.setBorder(FIELD_BORDER);
        emailField.setCaretColor(COLOR_TEXT_PRIMARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0); // Space after field
        panel.add(emailField, gbc);

        // 5. Password Label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(FONT_LABEL);
        passwordLabel.setForeground(COLOR_TEXT_PRIMARY);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(passwordLabel, gbc);

        // 6. Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(FONT_FIELD);
        passwordField.setBorder(FIELD_BORDER);
        passwordField.setCaretColor(COLOR_TEXT_PRIMARY);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(passwordField, gbc);

        // --- Role Label and ComboBox have been removed ---

        // 9. Sign In Button
        JButton signInButton = new JButton("Log In");
        signInButton.setFont(FONT_BUTTON);
        signInButton.setBackground(COLOR_BUTTON_PRIMARY);
        signInButton.setForeground(Color.WHITE);
        signInButton.setFocusPainted(false);
        signInButton.setBorderPainted(false);
        signInButton.setOpaque(true); // Required for background on some L&Fs
        signInButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInButton.setPreferredSize(new Dimension(100, 45)); // Taller button
        gbc.gridy = 9; // Updated gridy
        gbc.insets = new Insets(20, 0, 0, 0); // More top margin
        panel.add(signInButton, gbc);

        // Action listener for the button
        signInButton.addActionListener(this::performLogin);
    }

    private void performLogin(ActionEvent e) {
        // In a real app, you would call:
        // api.auth.login(emailField.getText(), new String(passwordField.getPassword()));
        // The backend would return the user's role.

        System.out.println("Email: " + emailField.getText());
        System.out.println("Password: [REDACTED]");

        JOptionPane.showMessageDialog(this,
                "Login button clicked!\nEmail: " + emailField.getText(),
                "Login Action",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}