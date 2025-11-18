package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Admin home page dashboard.
 * FIXED: Replaced Date Spinner with JFormattedTextField (No arrows).
 */
public class AdminDashboardPanel extends JPanel {

    //Color themes
    //These are used on the panels - CourseManagementPanel, UserManagementPanel, SectionManagementPanel
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    //These are used on the settings panels - Maintenance Mode, Deadline
    private static final Color COLOR_SETTINGS_BG = new Color(250, 250, 250);
    private static final Color COLOR_GREEN_ON = new Color(0, 150, 50);

    private AdminService adminService; //Service connects the backend to frontend

    public AdminDashboardPanel(Runnable onManageUsers, Runnable onManageCourses, Runnable onManageSections, AdminService adminService) {
        this.adminService = adminService;

        setLayout(new BorderLayout(40, 30));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));

        //Header
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

        // 1. Add Settings Panel (Maintenance + Deadline)
        centerWrapper.add(createSettingsPanel());
        centerWrapper.add(Box.createRigidArea(new Dimension(0, 30)));

        // 2. Add Navigation Cards
        centerWrapper.add(createCardsPanel(onManageUsers, onManageCourses, onManageSections));
        add(centerWrapper, BorderLayout.CENTER);
    }

    //This functions creates the settings panel ie the panel that contains the maintenance and Deadline
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0)); // 2 Columns
        panel.setOpaque(false);

        //Left: Maintenance Mode
        JPanel maintPanel = new JPanel(new BorderLayout());
        maintPanel.setBackground(COLOR_SETTINGS_BG);
        maintPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLOR_BORDER), new EmptyBorder(15, 15, 15, 15)));

        JLabel maintTitle = new JLabel("System Maintenance");
        maintTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        maintTitle.setForeground(COLOR_TEXT_DARK);
        JLabel maintDesc = new JLabel("Disable non-admin access");
        maintDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        maintDesc.setForeground(COLOR_TEXT_LIGHT);
        JPanel maintText = new JPanel();
        maintText.setOpaque(false);
        maintText.setLayout(new BoxLayout(maintText, BoxLayout.Y_AXIS));
        maintText.add(maintTitle);
        maintText.add(maintDesc);

        // Toggle Button
        JToggleButton toggleButton = new JToggleButton("OFF");
        toggleButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setPreferredSize(new Dimension(80, 40));
        // Load initial state
        if (adminService.getMaintenanceModeState()) {
            toggleButton.setText("ON");
            toggleButton.setSelected(true);
            toggleButton.setBackground(COLOR_GREEN_ON);
            toggleButton.setForeground(Color.WHITE);
        } else {
            toggleButton.setBackground(COLOR_BACKGROUND);
            toggleButton.setForeground(COLOR_TEXT_DARK);
        }

        toggleButton.addActionListener(e -> {
            boolean newState = toggleButton.isSelected();
            if(adminService.toggleMaintenanceMode(newState)) {
                // Update Style
                toggleButton.setText(newState ? "ON" : "OFF");
                toggleButton.setBackground(newState ? COLOR_GREEN_ON : COLOR_BACKGROUND);
                toggleButton.setForeground(newState ? Color.WHITE : COLOR_TEXT_DARK);

                //Show Warning Message
                if (newState) {
                    JOptionPane.showMessageDialog(this,
                            "Maintenance Mode is now ON.\nStudents and Instructors cannot make changes.",
                            "Maintenance Enabled",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Maintenance Mode is now OFF.\nSystem is fully accessible.",
                            "Maintenance Disabled",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                toggleButton.setSelected(!newState); // Revert on fail
                JOptionPane.showMessageDialog(this, "Failed to update setting.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        maintPanel.add(maintText, BorderLayout.CENTER);
        maintPanel.add(toggleButton, BorderLayout.EAST);

        //Right: Create Deadline
        JPanel deadlinePanel = new JPanel(new BorderLayout());
        deadlinePanel.setBackground(COLOR_SETTINGS_BG);
        deadlinePanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLOR_BORDER), new EmptyBorder(15, 15, 15, 15)));
        JLabel dateTitle = new JLabel("Registration Deadline");
        dateTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        dateTitle.setForeground(COLOR_TEXT_DARK);
        JLabel dateDesc = new JLabel("Set the cutoff date (YYYY-MM-DD)");
        dateDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateDesc.setForeground(COLOR_TEXT_LIGHT);

        JPanel dateText = new JPanel();
        dateText.setOpaque(false);
        dateText.setLayout(new BoxLayout(dateText, BoxLayout.Y_AXIS));
        dateText.add(dateTitle);
        dateText.add(dateDesc);

        // --- UPDATED: Use JFormattedTextField instead of JSpinner ---
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JFormattedTextField dateField = new JFormattedTextField(dateFormat);
        dateField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dateField.setColumns(10); // Set width
        dateField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));

        // Load current deadline
        try {
            String currentDeadline = adminService.GetSystemDeadline();
            Date date = dateFormat.parse(currentDeadline);
            dateField.setValue(date);
        } catch (Exception e) {
            dateField.setValue(new Date()); // Fallback to today
        }

        JButton setButton = createModernButton("Set", true);
        setButton.setPreferredSize(new Dimension(60, 30));
        setButton.addActionListener(e -> {
            // Get value from text field
            Date selectedDate = (Date) dateField.getValue();
            if (selectedDate != null) {
                String dateStr = dateFormat.format(selectedDate);
                if(adminService.SetSystemDeadline(dateStr)) {
                    JOptionPane.showMessageDialog(this, "Registration deadline updated to: " + dateStr);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel controlPanel = new JPanel(new BorderLayout(10, 0));
        controlPanel.setOpaque(false);
        controlPanel.add(dateField, BorderLayout.CENTER); // Changed from dateSpinner to dateField
        controlPanel.add(setButton, BorderLayout.EAST);

        deadlinePanel.add(dateText, BorderLayout.CENTER);
        deadlinePanel.add(controlPanel, BorderLayout.SOUTH);

        // Add both panels to the wrapper
        panel.add(maintPanel);
        panel.add(deadlinePanel);

        return panel;
    }


    //This creates the three main panels on the dashboard
    //One panel to access the UserManagementPanel, one for CourseManagementPanel and one for SectionManagementPanel
    private JPanel createCardsPanel(Runnable onManageUsers, Runnable onManageCourses, Runnable onManageSections) {
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();

        JButton userButton = createModernButton("Open User Management", false);
        userButton.addActionListener(e -> onManageUsers.run());
        JPanel card1 = createDashboardCard(
                createIconLabel("users.png", "👥"),
                "Manage Users",
                "Add students, instructors, and manage roles",
                userButton
        );

        JButton courseButton = createModernButton("Open Course Management", false);
        courseButton.addActionListener(e -> onManageCourses.run());
        JPanel card2 = createDashboardCard(
                createIconLabel("book.png", "📚"),
                "Manage Courses",
                "Create new courses for the curriculum",
                courseButton
        );

        JButton sectionButton = createModernButton("Open Section Management", false);
        sectionButton.addActionListener(e -> onManageSections.run());
        JPanel card3 = createDashboardCard(
                createIconLabel("sections.png", "🏫"),
                "Manage Sections",
                "Schedule classes and assign instructors",
                sectionButton
        );
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 10);
        cardsPanel.add(card1, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.insets = new Insets(0, 10, 0, 10);
        cardsPanel.add(card2, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.insets = new Insets(0, 10, 0, 0);
        cardsPanel.add(card3, gbc);

        return cardsPanel;
    }


    //This creates the dashboard card
    private JPanel createDashboardCard(JLabel icon, String title, String description, JButton button) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        topPanel.add(icon);
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
        buttonPanel.setOpaque(false);
        buttonPanel.add(button);

        card.add(buttonPanel, BorderLayout.SOUTH);
        card.add(Box.createGlue(), BorderLayout.CENTER);
        return card;
    }


    //This is used to modify the buttons
    //The mouse hover is used to highlight the button if mouse hovers over it
    private JButton createModernButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final Color bg;
        final Color fg;
        final Color bgHover;

        if (isPrimary) {
            bg = COLOR_PRIMARY;
            fg = Color.WHITE;
            bgHover = COLOR_PRIMARY_DARK;
        } else {
            bg = COLOR_BACKGROUND;
            fg = COLOR_TEXT_DARK;
            bgHover = new Color(240, 240, 240);
        }

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


    private JLabel createIconLabel(String fileName, String fallbackText) {
        try {
            URL iconUrl = getClass().getResource("/icons/" + fileName);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        JLabel iconLabel = new JLabel(fallbackText);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        iconLabel.setForeground(COLOR_PRIMARY);
        return iconLabel;
    }
}