package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService; // <-- IMPORT THE SERVICE
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Admin home page dashboard.
 * UPDATED: Now connects to AdminService for Maintenance Mode.
 */
public class AdminDashboardPanel extends JPanel {

    private AdminService adminService; // <-- 1. ADD THIS FIELD

    /**
     * 2. UPDATED: Constructor now accepts AdminService
     */
    public AdminDashboardPanel(Runnable onManageUsers, Runnable onManageCourses,
                               Runnable onManageSections, AdminService adminService) {
        this.adminService = adminService; // <-- 3. STORE SERVICE

        setLayout(new BorderLayout(40, 30));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Admin Control Panel");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        JLabel subtitleLabel = new JLabel("Manage the entire university system");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Center Panel (Settings + Cards) ---
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));

        // 4. This method now uses the service
        centerWrapper.add(createSettingsPanel());
        centerWrapper.add(Box.createRigidArea(new Dimension(0, 30)));
        centerWrapper.add(createCardsPanel(onManageUsers, onManageCourses, onManageSections));

        add(centerWrapper, BorderLayout.CENTER);
    }

    /**
     * 5. UPDATED: This method now reads and writes the maintenance mode
     * setting using the AdminService.
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(240, 230, 200)),
                new EmptyBorder(20, 20, 20, 20))
        );
        panel.setBackground(new Color(255, 253, 248));

        // Text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel title = new JLabel("System Settings");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel modeLabel = new JLabel("Maintenance Mode");
        modeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel descLabel = new JLabel("Disable all changes across the system");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);
        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(modeLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(descLabel);
        panel.add(textPanel, BorderLayout.CENTER);

        // Toggle Button
        JToggleButton toggleButton = new JToggleButton();
        toggleButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        toggleButton.setPreferredSize(new Dimension(80, 40));

        // --- NEW: Get initial state from the service ---
        boolean initialState = adminService.getMaintenanceModeState();
        if (initialState) {
            toggleButton.setText("ON");
            toggleButton.setSelected(true);
            toggleButton.setBackground(Color.GREEN.darker());
            toggleButton.setForeground(Color.WHITE);
        } else {
            toggleButton.setText("OFF");
            toggleButton.setSelected(false);
        }

        // UPDATED: Action listener now calls the service
        toggleButton.addActionListener(e -> {
            boolean isSelected = toggleButton.isSelected();
            // Call the service to update the database
            boolean success = adminService.toggleMaintenanceMode(isSelected);

            if (success) {
                if (isSelected) {
                    toggleButton.setText("ON");
                    toggleButton.setBackground(Color.GREEN.darker());
                    toggleButton.setForeground(Color.WHITE);
                    JOptionPane.showMessageDialog(this,
                            "Maintenance Mode is now ON.",
                            "Maintenance Mode Enabled",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    toggleButton.setText("OFF");
                    toggleButton.setBackground(UIManager.getColor("Button.background"));
                    toggleButton.setForeground(UIManager.getColor("Button.foreground"));
                    JOptionPane.showMessageDialog(this,
                            "Maintenance Mode is now OFF.",
                            "Maintenance Mode Disabled",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Revert the button if the update failed
                toggleButton.setSelected(!isSelected);
                JOptionPane.showMessageDialog(this, "Could not update setting.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel toggleWrapper = new JPanel(new GridBagLayout());
        toggleWrapper.setOpaque(false);
        toggleWrapper.add(toggleButton);
        panel.add(toggleWrapper, BorderLayout.EAST);

        return panel;
    }

    // (createCardsPanel method is the same)
    private JPanel createCardsPanel(Runnable onManageUsers, Runnable onManageCourses, Runnable onManageSections) {
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JButton userButton = new JButton("Open User Management");
        userButton.addActionListener(e -> onManageUsers.run());
        userButton.setBackground(new Color(0, 82, 204));
        userButton.setForeground(Color.WHITE);
        userButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel card1 = createDashboardCard("[Icon]", "Manage Users", "Add students, instructors, and manage roles", userButton);
        JButton courseButton = new JButton("Open Course Management");
        courseButton.addActionListener(e -> onManageCourses.run());
        courseButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel card2 = createDashboardCard("[Icon]", "Manage Courses", "Create new courses for the curriculum", courseButton);
        JButton sectionButton = new JButton("Open Section Management");
        sectionButton.addActionListener(e -> onManageSections.run());
        sectionButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel card3 = createDashboardCard("[Icon]", "Manage Sections", "Schedule classes and assign instructors", sectionButton);
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 10);
        cardsPanel.add(card1, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.insets = new Insets(0, 10, 0, 10);
        cardsPanel.add(card2, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.insets = new Insets(0, 10, 0, 0);
        cardsPanel.add(card3, gbc);
        return cardsPanel;
    }

    // (createDashboardCard helper method is the same)
    private JPanel createDashboardCard(String iconText, String title, String description, JButton button) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("SansSerif", Font.BOLD, 24));
        topPanel.add(icon);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);
        topPanel.add(descLabel);
        card.add(topPanel, BorderLayout.NORTH);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(button);
        card.add(buttonPanel, BorderLayout.SOUTH);
        card.add(Box.createGlue(), BorderLayout.CENTER);
        return card;
    }
}