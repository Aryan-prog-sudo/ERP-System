package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.SectionView;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Instructor dashboard panel ("My Courses" / "My Sections").
 * UPDATED: Now calls InstructorService to get data.
 */
public class MySectionsPanel extends JPanel {

    private Consumer<Integer> onOpenGradebook; // Takes SectionID
    private InstructorService instructorService;
    private JPanel cardsPanel; // A field so loadData() can update it

    // 1. Constructor updated
    public MySectionsPanel(Consumer<Integer> onOpenGradebook, InstructorService instructorService) {
        this.onOpenGradebook = onOpenGradebook;
        this.instructorService = instructorService;

        setLayout(new BorderLayout(40, 30));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(), BorderLayout.NORTH);

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        add(cardsPanel, BorderLayout.CENTER);

        // 2. Load data immediately
        loadData();
    }

    /**
     * 3. New method to fetch data from the service
     */
    private void loadData() {
        List<SectionView> sections = instructorService.getAssignedSections();
        cardsPanel.removeAll(); // Clear old cards

        for (SectionView section : sections) {
            // 4. Create a card for each section
            JPanel card = createCourseCard(
                    "[Icon]",
                    section.courseCode() + " (" + section.courseTitle() + ")",
                    section.timeSlot(),
                    section.enrolled() + " / " + section.capacity() + " students",
                    section.sectionId() // Pass the section ID to the button
            );
            cardsPanel.add(card);
        }
        // Refresh the panel
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("My Courses");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        JLabel subtitleLabel = new JLabel("Select a course to manage grades");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(subtitleLabel);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        refreshButton.addActionListener(e -> loadData());

        headerPanel.add(textPanel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * 5. Helper method updated to use the SectionID
     */
    private JPanel createCourseCard(String iconText, String title, String time, String enrollment, int sectionId) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));
        card.setPreferredSize(new Dimension(400, 150));

        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        card.add(iconLabel, BorderLayout.EAST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JLabel enrollmentLabel = new JLabel(enrollment);
        enrollmentLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        enrollmentLabel.setForeground(Color.GRAY);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(timeLabel);
        textPanel.add(Box.createVerticalGlue());
        textPanel.add(enrollmentLabel);
        card.add(textPanel, BorderLayout.CENTER);

        JButton openButton = new JButton("Open Gradebook");
        openButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        openButton.setBackground(new Color(0, 82, 204));
        openButton.setForeground(Color.WHITE);
        openButton.setPreferredSize(new Dimension(100, 40));

        // This now calls the navigation function from Main with the Section's ID
        openButton.addActionListener(e -> onOpenGradebook.accept(sectionId));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(openButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }
}