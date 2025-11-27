package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.AdminSectionView;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Admin panel for managing sections.
 * UPDATED: Now includes "Remove" button with validation logic.
 */
//This is the admin panel for managing the sections
public class SectionManagementPanel extends JPanel {

    //Color themes
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_TEXT_FIELD_BG = new Color(248, 248, 248);
    private static final Color COLOR_DELETE = new Color(220, 50, 50);

    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private JComboBox<Object> courseComboBox;
    private JComboBox<Object> instructorComboBox;
    private JTextField sectionNumField, timeField, capacityField;
    private AdminService adminService;

    private List<AdminSectionView> currentSectionList; //This stores the data to look up the sections

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
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadAllData();
            }
        });
        loadAllData();
    }

    private void loadAllData() {
        loadDropdownData();
        loadSectionsData();
    }

    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);

        JButton goBackButton = createModernButton("← Go Back", false);
        goBackButton.addActionListener(e -> onGoBack.run());

        JLabel titleLabel = new JLabel("Manage Sections");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton refreshButton = createModernButton("Refresh", false);
        refreshButton.addActionListener(e -> loadAllData());

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

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
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;

        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Create New Section");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, gbc);

        //The course buttons
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Course"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        courseComboBox = new JComboBox<>();
        courseComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        courseComboBox.setBackground(COLOR_BACKGROUND);
        panel.add(courseComboBox, gbc);

        //The section number input
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Section Number (e.g., 001)"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        sectionNumField = createModernTextField(20);
        panel.add(sectionNumField, gbc);

        //The part to take time schedule as input
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Time (e.g., MWF 9:00 AM)"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        timeField = createModernTextField(20);
        panel.add(timeField, gbc);

        //The part to take
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Capacity"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        capacityField = createModernTextField(20);
        panel.add(capacityField, gbc);

        // --- Instructor ComboBox ---
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Instructor"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        instructorComboBox = new JComboBox<>();
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

    //This creates the table on the right of the sections
    //It now also contains the option to delete sections
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel title = new JLabel("Existing Sections");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        //The actions table basically adds the option to remove the sections
        String[] columnNames = {"Course", "Section", "Time", "Capacity", "Instructor", "Actions"};

        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sectionsTable = new JTable(tableModel);
        sectionsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sectionsTable.setRowHeight(35);
        sectionsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        //Add Button Renderer
        sectionsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        sectionsTable.addMouseListener(new TableButtonListener(sectionsTable));

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    //This code handles the logic for dropdowns
    private void loadDropdownData() {
        java.util.List<Course> courses = adminService.getAllCourses();
        courseComboBox.removeAllItems();
        if (courses.isEmpty()) courseComboBox.addItem(COURSE_EMPTY);
        else {
            courseComboBox.addItem(COURSE_PROMPT);
            for (Course c : courses) courseComboBox.addItem(c);
        }
        java.util.List<Instructor> instructors = adminService.getAllInstructors();
        instructorComboBox.removeAllItems();
        if (instructors.isEmpty()) instructorComboBox.addItem(INST_EMPTY);
        else {
            instructorComboBox.addItem(INST_PROMPT);
            for (Instructor i : instructors) instructorComboBox.addItem(i);
        }
    }


    //This calls the method in admin service class to get info about all the sections
    private void loadSectionsData() {
        this.currentSectionList = adminService.GetAllSectionsForView(); // Store ref
        tableModel.setRowCount(0);

        for (AdminSectionView s : currentSectionList) {
            tableModel.addRow(new Object[]{
                    s.CourseCode(),
                    s.SectionNumber(),
                    s.TimeSlot(),
                    s.EnrolledCount() + " / " + s.Capacity(), //Total enrolled out of the total capacity
                    s.InstructorName(),
                    "Remove"
                    // Button text
            });
        }
    }

    //This is used to create sections
    private void onCreateSection() {
        Object courseObj = courseComboBox.getSelectedItem();
        Object instObj = instructorComboBox.getSelectedItem();
        String section = sectionNumField.getText();
        String time = timeField.getText();
        String capacityStr = capacityField.getText();

        if (!(courseObj instanceof Course) || !(instObj instanceof Instructor)) {
            JOptionPane.showMessageDialog(this, "Please select a valid course and instructor.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Course course = (Course) courseObj;
        Instructor instructor = (Instructor) instObj;

        if (section.isEmpty() || time.isEmpty() || capacityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int Confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to create this course: "+ section +"with the course:" +course.courseCode() , "Confirm course creation", JOptionPane.YES_NO_OPTION);
        if(Confirm!= JOptionPane.YES_OPTION){
            return;
        };

        boolean success = adminService.createNewSection(course, instructor, section, time, capacityStr);

        if (success) {
            JOptionPane.showMessageDialog(this, "Section Added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllData();
            sectionNumField.setText("");
            timeField.setText("");
            capacityField.setText("");
            courseComboBox.setSelectedIndex(0);
            instructorComboBox.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create section.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    class ButtonRenderer extends DefaultTableCellRenderer {
        JButton renderButton;
        public ButtonRenderer() {
            renderButton = new JButton("Remove");
            renderButton.setOpaque(true);
            renderButton.setBackground(COLOR_DELETE);
            renderButton.setForeground(Color.WHITE);
            renderButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return renderButton;
        }
    }


    class TableButtonListener extends MouseAdapter {
        private final JTable table;
        public TableButtonListener(JTable table) { this.table = table; }

        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / table.getRowHeight();

            if (row < table.getRowCount() && row >= 0 && column == 5) {
                // Retrieve the ID using the row index
                AdminSectionView section = currentSectionList.get(row);

                // Confirm deletion
                int confirm = JOptionPane.showConfirmDialog(table,
                        "Are you sure you want to remove section " + section.SectionNumber() + " of " + section.CourseCode() + "?",
                        "Confirm Removal", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // CALL SERVICE with ID and Count
                    String result = adminService.RemoveSection(section.SectionID(), section.EnrolledCount());

                    if ("Success".equals(result)) {
                        JOptionPane.showMessageDialog(table, "Section Removed.");
                        loadAllData();
                    } else {
                        // Show warning if not empty
                        JOptionPane.showMessageDialog(table, result, "Cannot Remove", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }

    // --- Helper Methods ---

    private JButton createModernButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final Color bg = isPrimary ? COLOR_PRIMARY : COLOR_BACKGROUND;
        final Color fg = isPrimary ? Color.WHITE : COLOR_TEXT_DARK;
        final Color bgHover = isPrimary ? COLOR_PRIMARY_DARK : new Color(240, 240, 240);

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