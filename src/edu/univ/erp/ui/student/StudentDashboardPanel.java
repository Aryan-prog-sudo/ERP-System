package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Student home page dashboard.
 * *** UPDATED: Cards now stretch vertically to fill the entire area. ***
 * Uses ONLY built-in Java components.
 */
public class StudentDashboardPanel extends JPanel {

    public StudentDashboardPanel(Runnable onGoToCatalog, Runnable onGoToTimetable, Runnable onGoToGrades) {
        setLayout(new BorderLayout(40, 30));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        // --- Welcome Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome back, John Student!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        JLabel subtitleLabel = new JLabel("What would you like to do today?");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);

        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // --- Cards Panel ---
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Card 1: Register for Courses ---
        JButton registerButton = new JButton("Go to Course Catalog");
        registerButton.addActionListener(e -> onGoToCatalog.run());
        registerButton.setBackground(new Color(0, 82, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel card1 = createDashboardCard(
                "[Icon]", // Placeholder. See LoginDialog for icon example
                "Register for Courses",
                "Browse and enroll in available courses",
                registerButton
        );

        // --- Card 2: View My Timetable ---
        JButton scheduleButton = new JButton("View Schedule");
        scheduleButton.addActionListener(e -> onGoToTimetable.run());
        scheduleButton.setFont(new Font("SansSerf", Font.BOLD, 14));

        JPanel card2 = createDashboardCard(
                "[Icon]",
                "View My Timetable",
                "Check your class schedule",
                scheduleButton
        );

        // --- Card 3: Check My Grades ---
        JButton gradesButton = new JButton("View Grades");
        gradesButton.addActionListener(e -> onGoToGrades.run());
        gradesButton.setFont(new Font("SansSerf", Font.BOLD, 14));

        JPanel card3 = createDashboardCard(
                "[Icon]",
                "Check My Grades",
                "View your academic performance",
                gradesButton
        );

        // --- Add cards to GridBagLayout ---
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; // Distribute extra horizontal space
        gbc.weighty = 1.0; // Distribute extra vertical space

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10); // Gap on the right
        cardsPanel.add(card1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 10); // Gaps on left and right
        cardsPanel.add(card2, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 0); // Gap on the left
        cardsPanel.add(card3, gbc);

        add(cardsPanel, BorderLayout.CENTER);
    }

    /**
     * Helper method to create one of the dashboard cards.
     * *** UPDATED: Added a spacer to BorderLayout.CENTER to make it stretch. ***
     */
    private JPanel createDashboardCard(String iconText, String title, String description, JButton button) {
        JPanel card = new JPanel(new BorderLayout(10, 10)); // Card uses BorderLayout
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // *** REMOVED: No longer needed, as we want the card to stretch
        // card.setPreferredSize(new Dimension(250, 300));

        // --- Top content (Icon, Title, Desc) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // See LoginDialog.java for a full example of how to load this
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

        // --- Button ---
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(button);

        card.add(buttonPanel, BorderLayout.SOUTH);

        // *** NEW: Add an invisible spacer to the CENTER. ***
        // This spacer will expand and soak up all extra vertical space,
        // pushing the NORTH and SOUTH panels to the top and bottom.
        card.add(Box.createGlue(), BorderLayout.CENTER);

        return card;
    }
}