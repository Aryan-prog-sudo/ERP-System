package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Student home page dashboard.
 * UPDATED: Beautified with a modern look and feel.
 */
public class StudentDashboardPanel extends JPanel {

    // --- Color Theme ---
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);

    public StudentDashboardPanel(Runnable onGoToCatalog, Runnable onGoToTimetable, Runnable onGoToGrades) {
        setLayout(new BorderLayout(40, 30));
        // Use a clean white background
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));

        // --- Welcome Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BACKGROUND);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        // This label should be updated by Main.java after login
        JLabel welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        welcomeLabel.setForeground(COLOR_TEXT_DARK);

        JLabel subtitleLabel = new JLabel("What would you like to do today?");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(COLOR_TEXT_LIGHT);

        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // --- Cards Panel ---
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Card 1: Register for Courses ---
        JButton registerButton = createModernButton("Course Catalog", true);
        registerButton.addActionListener(e -> onGoToCatalog.run());
        JPanel card1 = createDashboardCard(
                createIconLabel("book.png", "📚"), // Icon for courses
                "Register for Courses",
                "Browse and enroll in available courses",
                registerButton
        );

        // --- Card 2: View My Timetable ---
        JButton scheduleButton = createModernButton("View Schedule", false);
        scheduleButton.addActionListener(e -> onGoToTimetable.run());
        JPanel card2 = createDashboardCard(
                createIconLabel("calendar.png", "📅"), // Icon for calendar
                "View My Timetable",
                "Check your class schedule",
                scheduleButton
        );

        // --- Card 3: Check My Grades ---
        JButton gradesButton = createModernButton("View Grades", false);
        gradesButton.addActionListener(e -> onGoToGrades.run());
        JPanel card3 = createDashboardCard(
                createIconLabel("grades.png", "📈"), // Icon for grades
                "Check My Grades",
                "View your academic performance",
                gradesButton
        );

        // --- Add cards to GridBagLayout ---
        // (Functionality is the same, just adding the cards)
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        cardsPanel.add(card1, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        cardsPanel.add(card2, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 0);
        cardsPanel.add(card3, gbc);

        add(cardsPanel, BorderLayout.CENTER);
    }

    /**
     * Helper method to create one of the dashboard cards.
     * (Functionality is the same, just styled)
     */
    private JPanel createDashboardCard(JLabel icon, String title, String description, JButton button) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER), // Lighter border
                new EmptyBorder(25, 25, 25, 25)
        ));

        // --- Top content (Icon, Title, Desc) ---
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

        // --- Button ---
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false); // Transparent background
        buttonPanel.add(button);

        card.add(buttonPanel, BorderLayout.SOUTH);

        // This invisible spacer pushes NORTH to top and SOUTH to bottom
        card.add(Box.createGlue(), BorderLayout.CENTER);

        return card;
    }

    /**
     * Helper method to create a modern button with hover effects.
     * @param text The button's text
     * @param isPrimary Whether to use the primary (blue) style
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

        // Add hover effect
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
     * @param fileName The name of the file in /icons/
     * @param fallbackText The emoji/text to use if the icon fails to load
     */
    private JLabel createIconLabel(String fileName, String fallbackText) {
        try {
            // Try to load the image from src/main/resources/icons/
            URL iconUrl = getClass().getResource("/icons/" + fileName);
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                // Resize the icon to be 48x48
                Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback if the image fails to load
        JLabel iconLabel = new JLabel(fallbackText);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        iconLabel.setForeground(COLOR_PRIMARY);
        return iconLabel;
    }
}