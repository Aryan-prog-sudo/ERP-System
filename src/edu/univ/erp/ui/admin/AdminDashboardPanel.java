package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Admin home page dashboard.
 * UPDATED: Beautified with a modern look and feel.
 */
public class AdminDashboardPanel extends JPanel {

    // --- Color Theme ---
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_SETTINGS_BG = new Color(250, 250, 250); // A light, clean gray
    private static final Color COLOR_GREEN_ON = new Color(0, 150, 50); // A nice green for "ON"

    private AdminService adminService;

    public AdminDashboardPanel(Runnable onManageUsers, Runnable onManageCourses,
                               Runnable onManageSections, AdminService adminService) {
        this.adminService = adminService;

        setLayout(new BorderLayout(40, 30));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BACKGROUND);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Admin Control Panel");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(COLOR_TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Manage the entire university system");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(COLOR_TEXT_LIGHT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Center Panel (Settings + Cards) ---
        JPanel centerWrapper = new JPanel();
        centerWrapper.setBackground(COLOR_BACKGROUND);
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));

        centerWrapper.add(createSettingsPanel());
        centerWrapper.add(Box.createRigidArea(new Dimension(0, 30)));
        centerWrapper.add(createCardsPanel(onManageUsers, onManageCourses, onManageSections));

        add(centerWrapper, BorderLayout.CENTER);
    }

    /**
     * UPDATED: createSettingsPanel, now styled
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER), // Use standard light border
                new EmptyBorder(20, 20, 20, 20))
        );
        panel.setBackground(COLOR_SETTINGS_BG); // Use the new light gray

        // Text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // Transparent background

        JLabel title = new JLabel("System Settings");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(COLOR_TEXT_DARK);

        JLabel modeLabel = new JLabel("Maintenance Mode");
        modeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        modeLabel.setForeground(COLOR_TEXT_DARK);

        JLabel descLabel = new JLabel("Disable all non-admin changes across the system");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(COLOR_TEXT_LIGHT);

        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(modeLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(descLabel);
        panel.add(textPanel, BorderLayout.CENTER);

        // --- Toggle Button, now styled ---
        JToggleButton toggleButton = new JToggleButton();
        toggleButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        toggleButton.setPreferredSize(new Dimension(80, 40));
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setOpaque(true);

        // Get initial state from the service
        boolean initialState = adminService.getMaintenanceModeState();
        if (initialState) {
            setToggleStateOn(toggleButton);
        } else {
            setToggleStateOff(toggleButton);
        }

        // Action listener (functionality is identical)
        toggleButton.addActionListener(e -> {
            boolean isSelected = toggleButton.isSelected();
            boolean success = adminService.toggleMaintenanceMode(isSelected);

            if (success) {
                if (isSelected) {
                    setToggleStateOn(toggleButton);
                    JOptionPane.showMessageDialog(this,
                            "Maintenance Mode is now ON.",
                            "Maintenance Mode Enabled",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    setToggleStateOff(toggleButton);
                    JOptionPane.showMessageDialog(this,
                            "Maintenance Mode is now OFF.",
                            "Maintenance Mode Disabled",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                toggleButton.setSelected(!isSelected); // Revert
                JOptionPane.showMessageDialog(this, "Could not update setting.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel toggleWrapper = new JPanel(new GridBagLayout());
        toggleWrapper.setOpaque(false); // Transparent
        toggleWrapper.add(toggleButton);
        panel.add(toggleWrapper, BorderLayout.EAST);

        return panel;
    }

    // Helper methods for the toggle button styles
    private void setToggleStateOn(JToggleButton button) {
        button.setText("ON");
        button.setSelected(true);
        button.setBackground(COLOR_GREEN_ON);
        button.setForeground(Color.WHITE);
        button.setBorder(null);
    }

    private void setToggleStateOff(JToggleButton button) {
        button.setText("OFF");
        button.setSelected(false);
        button.setBackground(COLOR_BACKGROUND);
        button.setForeground(COLOR_TEXT_DARK);
        button.setBorder(new LineBorder(COLOR_BORDER, 1));
    }

    /**
     * UPDATED: createCardsPanel, now styled
     */
    private JPanel createCardsPanel(Runnable onManageUsers, Runnable onManageCourses, Runnable onManageSections) {
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();

        // Use the modern button helper
        JButton userButton = createModernButton("Open User Management", true);
        userButton.addActionListener(e -> onManageUsers.run());
        JPanel card1 = createDashboardCard(
                createIconLabel("users.png", "👥"), // Users icon
                "Manage Users",
                "Add students, instructors, and manage roles",
                userButton
        );

        JButton courseButton = createModernButton("Open Course Management", false);
        courseButton.addActionListener(e -> onManageCourses.run());
        JPanel card2 = createDashboardCard(
                createIconLabel("book.png", "📚"), // Courses icon
                "Manage Courses",
                "Create new courses for the curriculum",
                courseButton
        );

        JButton sectionButton = createModernButton("Open Section Management", false);
        sectionButton.addActionListener(e -> onManageSections.run());
        JPanel card3 = createDashboardCard(
                createIconLabel("sections.png", "🏫"), // Sections icon
                "Manage Sections",
                "Schedule classes and assign instructors",
                sectionButton
        );

        // Layout logic is identical
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 10);
        cardsPanel.add(card1, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.insets = new Insets(0, 10, 0, 10);
        cardsPanel.add(card2, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.insets = new Insets(0, 10, 0, 0);
        cardsPanel.add(card3, gbc);

        return cardsPanel;
    }

    /**
     * UPDATED: createDashboardCard, now styled
     */
    private JPanel createDashboardCard(JLabel icon, String title, String description, JButton button) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER), // Lighter border
                new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false); // Transparent background
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        topPanel.add(icon); // Add the icon label
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        topPanel.add(titleLabel);

        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(COLOR_TEXT_LIGHT);
        topPanel.add(descLabel);

        card.add(topPanel, BorderLayout.NORTH);

        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false); // Transparent
        buttonPanel.add(button);

        card.add(buttonPanel, BorderLayout.SOUTH);

        // Invisible spacer (functionality unchanged)
        card.add(Box.createGlue(), BorderLayout.CENTER);
        return card;
    }

    // --- Helper Methods (Copied from previous examples) ---

    /**
     * Helper method to create a modern button with hover effects.
     */
    private JButton createModernButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color bg = isPrimary ? COLOR_PRIMARY : COLOR_BACKGROUND;
        Color fg = isPrimary ? Color.WHITE : COLOR_TEXT_DARK;
        Color bgHover = isPrimary ? COLOR_PRIMARY_DARK : new Color(240, 240, 240);

        button.setBackground(bg);
        button.setForeground(fg);
        if (!isPrimary) {
            button.setBorder(new LineBorder(COLOR_BORDER, 1));
        }

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgHover);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bg);
            }
        });
        return button;
    }

    /**
     * Helper method to load an icon.
     */
    private JLabel createIconLabel(String fileName, String fallbackText) {
        try {
            URL iconUrl = getClass().getResource("/icons/" + fileName);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel iconLabel = new JLabel(fallbackText);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        iconLabel.setForeground(COLOR_PRIMARY);
        return iconLabel;
    }
}