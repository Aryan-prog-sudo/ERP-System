package edu.univ.erp.ui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Admin home page dashboard.
 * Corresponds to: edu.univ.erp.ui.admin.AdminDashboardPanel
 * Design: image_3ed281.png
 * This is the "home page" for admins, so it has NO "Go Back" button.
 */
public class AdminDashboardPanel extends JPanel {

    public AdminDashboardPanel(Runnable onManageUsers, Runnable onManageCourses, Runnable onManageSections) {
        setLayout(new BorderLayout(40, 30));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS)); // Stack vertically

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

        // 1. Settings Panel
        centerWrapper.add(createSettingsPanel());
        centerWrapper.add(Box.createRigidArea(new Dimension(0, 30))); // Gap

        // 2. Cards Panel
        centerWrapper.add(createCardsPanel(onManageUsers, onManageCourses, onManageSections));

        add(centerWrapper, BorderLayout.CENTER);
    }

    /**
     * Creates the "System Settings" panel with the Maintenance Mode toggle.
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));

        // Style the panel with a border and light yellow background
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(240, 230, 200)), // Yellowish border
                new EmptyBorder(20, 20, 20, 20))
        );
        panel.setBackground(new Color(255, 253, 248)); // Light yellow background

        // --- Text content (Left) ---
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // Make transparent

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

        // --- Toggle Button (Right) ---
        // A JToggleButton is the built-in Swing equivalent of a slider
        JToggleButton toggleButton = new JToggleButton("OFF");
        toggleButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        toggleButton.setPreferredSize(new Dimension(80, 40));

        // TODO: Load initial state from service/settings

        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                toggleButton.setText("ON");
                toggleButton.setBackground(Color.GREEN.darker());
                toggleButton.setForeground(Color.WHITE);
                // --- TODO: Call service layer ---
                // settingsService.setMaintenanceMode(true);
                JOptionPane.showMessageDialog(this,
                        "Maintenance Mode is now ON.\nStudents and instructors cannot make changes.",
                        "Maintenance Mode Enabled",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                toggleButton.setText("OFF");
                toggleButton.setBackground(UIManager.getColor("Button.background"));
                toggleButton.setForeground(UIManager.getColor("Button.foreground"));
                // --- TODO: Call service layer ---
                // settingsService.setMaintenanceMode(false);
                JOptionPane.showMessageDialog(this,
                        "Maintenance Mode is now OFF.",
                        "Maintenance Mode Disabled",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Center the toggle button vertically
        JPanel toggleWrapper = new JPanel(new GridBagLayout());
        toggleWrapper.setOpaque(false); // Make transparent
        toggleWrapper.add(toggleButton);
        panel.add(toggleWrapper, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the 3-card panel (Users, Courses, Sections)
     */
    private JPanel createCardsPanel(Runnable onManageUsers, Runnable onManageCourses, Runnable onManageSections) {
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Card 1: Manage Users ---
        JButton userButton = new JButton("Open User Management");
        userButton.addActionListener(e -> onManageUsers.run());
        userButton.setBackground(new Color(0, 82, 204)); // Primary blue
        userButton.setForeground(Color.WHITE);
        userButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel card1 = createDashboardCard(
                "[Icon]",
                "Manage Users",
                "Add students, instructors, and manage roles",
                userButton
        );

        // --- Card 2: Manage Courses ---
        JButton courseButton = new JButton("Open Course Management");
        courseButton.addActionListener(e -> onManageCourses.run());
        courseButton.setFont(new Font("SansSerif", Font.BOLD, 14)); // Default style

        JPanel card2 = createDashboardCard(
                "[Icon]",
                "Manage Courses",
                "Create new courses for the curriculum",
                courseButton
        );

        // --- Card 3: Manage Sections ---
        JButton sectionButton = new JButton("Open Section Management");
        sectionButton.addActionListener(e -> onManageSections.run());
        sectionButton.setFont(new Font("SansSerif", Font.BOLD, 14)); // Default style

        JPanel card3 = createDashboardCard(
                "[Icon]",
                "Manage Sections",
                "Schedule classes and assign instructors",
                sectionButton
        );

        // --- Add cards to GridBagLayout ---
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 10);
        cardsPanel.add(card1, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.insets = new Insets(0, 10, 0, 10);
        cardsPanel.add(card2, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.insets = new Insets(0, 10, 0, 0);
        cardsPanel.add(card3, gbc);

        return cardsPanel;
    }

    /**
     * Helper method to create a dashboard card (copied from StudentDashboardPanel)
     */
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