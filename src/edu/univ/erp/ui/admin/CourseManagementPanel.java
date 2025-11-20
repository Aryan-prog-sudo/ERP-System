package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Course; // <-- NEW IMPORT
import edu.univ.erp.service.AdminService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List; // <-- NEW IMPORT


//This admin panel manages the Course
public class CourseManagementPanel extends JPanel {

    //Color Themes
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_TEXT_FIELD_BG = new Color(248, 248, 248);

    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private JTextField codeField, titleField, creditsField;

    private AdminService adminService; //This connects the frontend to the backend

    public CourseManagementPanel(Runnable onGoBack, AdminService adminService) {
        this.adminService = adminService;

        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);

        //Main content panel (Form + Table)
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.4; gbc.insets = new Insets(0, 0, 0, 20);
        mainContentPanel.add(createFormPanel(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.6; gbc.insets = new Insets(0, 20, 0, 0);
        mainContentPanel.add(createTablePanel(), gbc);

        add(mainContentPanel, BorderLayout.CENTER);

        // --- NEW: Load data when the panel opens ---
        loadData();
    }

    /**
     * NEW: Fetches all courses from the service and populates the table.
     */
    private void loadData() {
        // 1. Get data from the service
        List<Course> courses = adminService.getAllCourses();

        // 2. Clear the table
        tableModel.setRowCount(0);

        // 3. Repopulate the table
        for (Course course : courses) {
            tableModel.addRow(new Object[]{
                    course.courseCode(),
                    course.courseTitle(),
                    course.credits()
            });
        }
    }

    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);

        JButton goBackButton = createModernButton("← Go Back", false);
        goBackButton.addActionListener(e -> onGoBack.run());

        JLabel titleLabel = new JLabel("Manage Courses");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Add an invisible spacer to balance the "Go Back" button
        JButton spacer = createModernButton("← Go Back", false);
        spacer.setVisible(false);
        headerPanel.add(spacer, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;

        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Create New Course");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Course Code (e.g., CS-101)"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        codeField = createModernTextField(20);
        panel.add(codeField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Course Title"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        titleField = createModernTextField(20);
        panel.add(titleField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Credits"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        creditsField = createModernTextField(20);
        panel.add(creditsField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 0, 0);
        JButton createButton = createModernButton("Create Course", true);
        createButton.addActionListener(e -> onCreateCourse());
        panel.add(createButton, gbc);

        gbc.gridy++; gbc.weighty = 1.0; // Pushes everything up
        panel.add(Box.createGlue(), gbc);

        return panel;
    }

    //This method created table on the right of the section where we create the codes
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false); // Transparent background

        JLabel title = new JLabel("Existing Courses");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Code", "Title", "Credits"};

        //Table for Courses
        tableModel = new DefaultTableModel(null, columnNames) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        coursesTable.setRowHeight(35);
        coursesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    private void onCreateCourse() {
        String code = codeField.getText();
        String title = titleField.getText();
        String creditsStr = creditsField.getText();

        if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- THIS IS THE REAL BACKEND CALL ---
        boolean success = adminService.createNewCourse(code, title, creditsStr);

        if (success) {
            // --- UPDATED: Refresh table from database ---
            loadData();

            // Clear the form
            codeField.setText("");
            titleField.setText("");
            creditsField.setText("");

            JOptionPane.showMessageDialog(this, "Course '" + code + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create course.\nInvalid credits or course code already exists.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    //Helper Methods (Copied from other styled panels)
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
                new EmptyBorder(8, 8, 8, 8) // Internal padding
        ));
        return field;
    }
}