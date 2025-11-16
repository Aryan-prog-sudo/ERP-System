package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin panel for managing sections.
 * UPDATED: Now fully connected to the AdminService.
 * Dropdowns are populated from the database.
 */
public class SectionManagementPanel extends JPanel {

    private JTable sectionsTable;
    private DefaultTableModel tableModel;

    // --- UPDATED: Fields for the form ---
    private JComboBox<Course> courseComboBox;
    private JComboBox<Instructor> instructorComboBox;
    private JTextField sectionNumField, timeField, capacityField;

    private AdminService adminService;

    public SectionManagementPanel(Runnable onGoBack, AdminService adminService) {
        this.adminService = adminService;

        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.4; gbc.insets = new Insets(0, 0, 0, 20);
        mainContentPanel.add(createFormPanel(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.6; gbc.insets = new Insets(0, 20, 0, 0);
        mainContentPanel.add(createTablePanel(), gbc);

        add(mainContentPanel, BorderLayout.CENTER);

        // --- NEW: Load data into dropdowns after panel is built ---
        loadDropdownData();
    }

    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout());

        JButton goBackButton = new JButton("← Go Back");
        goBackButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        goBackButton.addActionListener(e -> onGoBack.run());
        goBackButton.setBorderPainted(false);
        goBackButton.setContentAreaFilled(false);
        goBackButton.setForeground(Color.BLUE.darker());
        goBackButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel("Manage Sections");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(goBackButton.getPreferredSize()), BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * UPDATED: Now uses JComboBox<Course> and JComboBox<Instructor>
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;

        gbc.gridy = 0; gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Create New Section");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, gbc);

        // --- Course ComboBox ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Course"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        courseComboBox = new JComboBox<>(); // Empty for now
        courseComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(courseComboBox, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Section Number"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        sectionNumField = new JTextField();
        sectionNumField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(sectionNumField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Time (e.g., MWF 9:00 AM)"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        timeField = new JTextField();
        timeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(timeField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Capacity"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        capacityField = new JTextField();
        capacityField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(capacityField, gbc);

        // --- Instructor ComboBox ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Instructor"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        instructorComboBox = new JComboBox<>(); // Empty for now
        instructorComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(instructorComboBox, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 0, 0);
        JButton createButton = new JButton("Create Section");
        createButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createButton.setBackground(new Color(0, 82, 204));
        createButton.setForeground(Color.WHITE);
        createButton.setPreferredSize(new Dimension(100, 40));
        createButton.addActionListener(e -> onCreateSection());
        panel.add(createButton, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        // ... (This method is the same as before, no changes) ...
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        JLabel title = new JLabel("Existing Sections");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);
        String[] columnNames = {"Course", "Section", "Time", "Capacity", "Instructor"};
        Object[][] data = { /* TODO: Load this from service */ };
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        sectionsTable = new JTable(tableModel);
        sectionsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sectionsTable.setRowHeight(35);
        sectionsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * NEW: Loads data from the service into the JComboBoxes.
     */
    private void loadDropdownData() {
        // Load Courses
        List<Course> courses = adminService.getAllCourses();
        courseComboBox.addItem(null); // Add a "Select" option
        for (Course c : courses) {
            courseComboBox.addItem(c);
        }

        // Load Instructors
        List<Instructor> instructors = adminService.getAllInstructors();
        instructorComboBox.addItem(null); // Add a "Select" option
        for (Instructor i : instructors) {
            instructorComboBox.addItem(i);
        }
    }

    /**
     * UPDATED: Calls the AdminService to create the section.
     */
    private void onCreateSection() {
        // Get the selected objects from the dropdowns
        Course course = (Course) courseComboBox.getSelectedItem();
        Instructor instructor = (Instructor) instructorComboBox.getSelectedItem();
        String section = sectionNumField.getText();
        String time = timeField.getText();
        String capacityStr = capacityField.getText();

        if (course == null || instructor == null || section.isEmpty() || time.isEmpty() || capacityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- THIS IS THE REAL BACKEND CALL ---
        boolean success = adminService.createNewSection(course, instructor, section, time, capacityStr);

        if (success) {
            // TODO: Add to table and clear form
            JOptionPane.showMessageDialog(this, "Section '" + course.courseCode() + "-" + section + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create section.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}