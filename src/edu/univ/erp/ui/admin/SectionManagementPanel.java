package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.AdminSectionView; // <-- NEW
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter; // <-- NEW
import java.awt.event.ComponentEvent; // <-- NEW
import java.awt.event.MouseAdapter; // <-- NEW
import java.awt.event.MouseEvent; // <-- NEW
import java.util.List;

/**
 * Admin panel for managing sections.
 * UPDATED: Beautified and all 4 bugs fixed.
 */
public class SectionManagementPanel extends JPanel {

    // --- Color Theme ---
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_TEXT_FIELD_BG = new Color(248, 248, 248);

    private JTable sectionsTable;
    private DefaultTableModel tableModel;

    // --- UPDATED: ComboBoxes are now <Object> to hold custom strings ---
    private JComboBox<Object> courseComboBox;
    private JComboBox<Object> instructorComboBox;

    private JTextField sectionNumField, timeField, capacityField;
    private AdminService adminService;

    // --- Custom String placeholders for dropdowns ---
    private final String COURSE_PROMPT = "--- Select a Course ---";
    private final String COURSE_EMPTY = "x---Empty (Create a Course First)---x";
    private final String INST_PROMPT = "--- Select an Instructor ---";
    private final String INST_EMPTY = "x---Empty (Create an Instructor First)---x";


    public SectionManagementPanel(Runnable onGoBack, AdminService adminService) {
        this.adminService = adminService;

        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.4; gbc.insets = new Insets(0, 0, 0, 20);
        mainContentPanel.add(createFormPanel(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.6; gbc.insets = new Insets(0, 20, 0, 0);
        mainContentPanel.add(createTablePanel(), gbc);

        add(mainContentPanel, BorderLayout.CENTER);

        // --- FIX 1: Add a ComponentListener to reload data when panel is shown ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("Section panel shown, refreshing all data...");
                loadAllData();
            }
        });

        // Initial load
        loadAllData();
    }

    /**
     * NEW: Helper to reload all data for the panel.
     */
    private void loadAllData() {
        loadDropdownData();
        loadSectionsData();
    }

    /**
     * UPDATED: createHeaderPanel, now styled and has Refresh button
     */
    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);

        JButton goBackButton = createModernButton("← Go Back", false);
        goBackButton.addActionListener(e -> onGoBack.run());

        JLabel titleLabel = new JLabel("Manage Sections");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // --- FIX 1: Add a Refresh Button ---
        JButton refreshButton = createModernButton("Refresh", false);
        refreshButton.addActionListener(e -> loadAllData());

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * UPDATED: createFormPanel, now styled
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;

        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Create New Section");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, gbc);

        // --- Course ComboBox ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Course"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        courseComboBox = new JComboBox<>(); // Now JComboBox<Object>
        courseComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        courseComboBox.setBackground(COLOR_BACKGROUND);
        panel.add(courseComboBox, gbc);

        // --- Section Number ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Section Number (e.g., 001)"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        sectionNumField = createModernTextField(20);
        panel.add(sectionNumField, gbc);

        // --- Time ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Time (e.g., MWF 9:00 AM)"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        timeField = createModernTextField(20);
        panel.add(timeField, gbc);

        // --- Capacity ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Capacity"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        capacityField = createModernTextField(20);
        panel.add(capacityField, gbc);

        // --- Instructor ComboBox ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Instructor"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        instructorComboBox = new JComboBox<>(); // Now JComboBox<Object>
        instructorComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        instructorComboBox.setBackground(COLOR_BACKGROUND);
        panel.add(instructorComboBox, gbc);

        // --- Create Button ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 0, 0);
        JButton createButton = createModernButton("Create Section", true);
        createButton.addActionListener(e -> onCreateSection());
        panel.add(createButton, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);

        return panel;
    }

    /**
     * UPDATED: createTablePanel, now loads no data by default
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel title = new JLabel("Existing Sections");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Course", "Section", "Time", "Capacity", "Instructor"};

        // --- UPDATED: No more hardcoded data ---
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        sectionsTable = new JTable(tableModel);
        sectionsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sectionsTable.setRowHeight(35);
        sectionsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * UPDATED: Loads data into JComboBoxes with smart logic.
     * Fixes Bugs 2 & 3.
     */
    private void loadDropdownData() {
        // --- Load Courses ---
        List<Course> courses = adminService.getAllCourses();
        courseComboBox.removeAllItems(); // Clear old items

        if (courses.isEmpty()) {
            courseComboBox.addItem(COURSE_EMPTY);
        } else {
            courseComboBox.addItem(COURSE_PROMPT);
            for (Course c : courses) {
                courseComboBox.addItem(c);
            }
        }

        // --- Load Instructors ---
        List<Instructor> instructors = adminService.getAllInstructors();
        instructorComboBox.removeAllItems(); // Clear old items

        if (instructors.isEmpty()) {
            instructorComboBox.addItem(INST_EMPTY);
        } else {
            instructorComboBox.addItem(INST_PROMPT);
            for (Instructor i : instructors) {
                instructorComboBox.addItem(i);
            }
        }
    }

    /**
     * NEW: Loads data from the service into the table.
     * Fixes Bug 4 (stale table).
     */
    private void loadSectionsData() {
        List<AdminSectionView> sections = adminService.getAllSectionsForView();
        tableModel.setRowCount(0); // Clear table

        for (AdminSectionView s : sections) {
            tableModel.addRow(new Object[]{
                    s.CourseCode(),
                    s.SectionNumber(),
                    s.TimeSlot(),
                    s.Capacity(),
                    s.InstructorName()
            });
        }
    }

    /**
     * UPDATED: Calls the AdminService and now validates dropdowns.
     * Fixes Bug 4 (refreshing).
     */
    private void onCreateSection() {
        // --- UPDATED: Validate dropdowns ---
        Object courseObj = courseComboBox.getSelectedItem();
        Object instObj = instructorComboBox.getSelectedItem();
        String section = sectionNumField.getText();
        String time = timeField.getText();
        String capacityStr = capacityField.getText();

        // Check if a valid item is selected
        if (!(courseObj instanceof Course) || !(instObj instanceof Instructor)) {
            JOptionPane.showMessageDialog(this, "Please select a valid course and instructor.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Safe to cast
        Course course = (Course) courseObj;
        Instructor instructor = (Instructor) instObj;

        if (section.isEmpty() || time.isEmpty() || capacityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = adminService.createNewSection(course, instructor, section, time, capacityStr);

        if (success) {
            JOptionPane.showMessageDialog(this, "Section '" + course.courseCode() + "-" + section + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // --- FIX 4: Refresh all data (including table) ---
            loadAllData();

            // Clear form fields
            sectionNumField.setText("");
            timeField.setText("");
            capacityField.setText("");
            courseComboBox.setSelectedIndex(0);
            instructorComboBox.setSelectedIndex(0);

        } else {
            JOptionPane.showMessageDialog(this, "Failed to create section. Section number may already exist for this course.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // --- Helper Methods (Copied from other styled panels) ---

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

    private JTextField createModernTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(COLOR_TEXT_FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }
}