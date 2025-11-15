package edu.univ.erp.ui.instructor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Instructor dashboard panel ("My Courses" / "My Sections").
 * Corresponds to: edu.univ.erp.ui.instructor.MySectionsPanel
 * Design: image_3e7143.png
 * This is the "home page" for instructors, so it has NO "Go Back" button.
 */
public class MySectionsPanel extends JPanel {

    private Consumer<String> onOpenGradebook; // Function to call

    public MySectionsPanel(Consumer<String> onOpenGradebook) {
        this.onOpenGradebook = onOpenGradebook;

        setLayout(new BorderLayout(40, 30));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        // --- Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS)); // Stack vertically

        JLabel titleLabel = new JLabel("My Courses");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        JLabel subtitleLabel = new JLabel("Select a course to manage grades");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // --- Cards Panel ---
        // Use FlowLayout to place cards side-by-side and wrap if needed
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));

        // --- TODO: Load these from the service layer ---
        // For this demo, we'll create hardcoded cards

        JPanel card1 = createCourseCard(
                "[Icon]",
                "CS-101",
                "Intro to Programming",
                "35 students enrolled"
        );
        JPanel card2 = createCourseCard(
                "[Icon]",
                "CS-205",
                "Data Structures",
                "28 students enrolled"
        );

        cardsPanel.add(card1);
        cardsPanel.add(card2);

        add(cardsPanel, BorderLayout.CENTER);
    }

    /**
     * Helper method to create a course card.
     */
    private JPanel createCourseCard(String iconText, String courseCode, String courseTitle, String enrollmentInfo) {
        // Use BorderLayout to position elements
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));
        // Give the card a fixed size
        card.setPreferredSize(new Dimension(400, 150));

        // --- Icon (Top Right) ---
        JLabel iconLabel = new JLabel(iconText); // TODO: Replace with ImageIcon
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(230, 240, 255)); // Light blue bg
        iconLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.add(iconLabel, BorderLayout.EAST);

        // --- Text Content (Left/Center) ---
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel codeLabel = new JLabel(courseCode);
        codeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel titleLabel = new JLabel(courseTitle);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel enrollmentLabel = new JLabel(enrollmentInfo);
        enrollmentLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        enrollmentLabel.setForeground(Color.GRAY);

        textPanel.add(codeLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalGlue()); // Pushes enrollment info down
        textPanel.add(enrollmentLabel);

        card.add(textPanel, BorderLayout.CENTER);

        // --- Button (Bottom) ---
        JButton openButton = new JButton("Open Gradebook");
        openButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        openButton.setOpaque(true);
        openButton.setBackground(new Color(0, 82, 204));
        openButton.setForeground(Color.WHITE);
        openButton.setPreferredSize(new Dimension(100, 40));

        // Add listener to call the navigation function from Main
        openButton.addActionListener(e -> onOpenGradebook.accept(courseCode));

        // Wrap button in a panel to align it right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(openButton);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }
}