package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.SectionView;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

//This is basically the dashboard panel for the instructor and it displays only the sections that the instructor is assigned to
public class MySectionsPanel extends JPanel {

    //Color themes
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);

    private Consumer<Integer> onOpenGradebook; // Takes SectionID
    private InstructorService instructorService;
    private JPanel cardsPanel; // A field so loadData() can update it

    public MySectionsPanel(Consumer<Integer> onOpenGradebook, InstructorService instructorService) {
        this.onOpenGradebook = onOpenGradebook;
        this.instructorService = instructorService;

        setLayout(new BorderLayout(0, 30)); // Reduced gap
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(), BorderLayout.NORTH);

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(COLOR_BACKGROUND);
        add(cardsPanel, BorderLayout.CENTER);
        // Load data immediately
        loadData();
    }


    //This method loads the data about the sections that this instructor is assigned to
    //It then clears the old card(All the cards just in case some section was deleted)
    //It then creates a new card for each of the section
    private void loadData() {
        List<SectionView> sections = instructorService.getAssignedSections();
        cardsPanel.removeAll(); // Clear old cards
        for (SectionView section : sections) {
            // Create a card for each section
            JPanel card = createCourseCard(
                    createIconLabel("course.png", "🏫"), // Use icon helper
                    section.courseCode() + " (" + section.courseTitle() + ")",
                    section.timeSlot(),
                    section.enrolled() + " / ".concat(String.valueOf(section.capacity())) + " students",
                    section.sectionId() // Pass the section ID to the button
            );
            cardsPanel.add(card);
        }
        // Refresh the panel
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    //Header panel
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false); // Transparent
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("My Courses");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(COLOR_TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Select a course to manage grades");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(COLOR_TEXT_LIGHT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(subtitleLabel);

        // Use the modern button helper
        JButton refreshButton = createModernButton("Refresh", false);
        refreshButton.addActionListener(e -> loadData());

        headerPanel.add(textPanel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    //Stylises course card creation
    private JPanel createCourseCard(JLabel iconLabel, String title, String time, String enrollment, int sectionId) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER), // Lighter border
                new EmptyBorder(25, 25, 25, 25)
        ));
        // We still use the original preferredSize to help the FlowLayout
        card.setPreferredSize(new Dimension(400, 180)); // Slightly taller for new button

        card.add(iconLabel, BorderLayout.EAST);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_TEXT_DARK);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        timeLabel.setForeground(COLOR_TEXT_DARK);

        JLabel enrollmentLabel = new JLabel(enrollment);
        enrollmentLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        enrollmentLabel.setForeground(COLOR_TEXT_LIGHT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(timeLabel);
        textPanel.add(Box.createVerticalGlue()); // Keeps pushing enrollment down
        textPanel.add(enrollmentLabel);
        card.add(textPanel, BorderLayout.CENTER);

        // Use the modern button helper
        JButton openButton = createModernButton("Open Gradebook", true);

        // This is the original, unchanged functionality
        openButton.addActionListener(e -> onOpenGradebook.accept(sectionId));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(openButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }



    //Helper method to create a modern button with hover effects.
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