package edu.univ.erp.ui.student;

import edu.univ.erp.domain.SectionView;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Course Catalog Panel.
 * UPDATED: Now calls StudentService to get data and register/drop.
 */
public class CourseCatalogPanel extends JPanel {

    private JTable courseTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private List<SectionView> sectionList; // To store the data and get sectionId

    // 1. Constructor updated to accept StudentService
    public CourseCatalogPanel(Runnable onGoBack, StudentService studentService) {
        this.studentService = studentService;

        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 40, 40, 40));
        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        // 2. Load data immediately
        loadData();
    }

    /**
     * 3. New method to fetch data from the service
     */
    private void loadData() {
        // Call the service
        this.sectionList = studentService.getCourseCatalog();

        // Clear existing rows
        tableModel.setRowCount(0);

        // Populate table from the results
        for (SectionView section : sectionList) {
            tableModel.addRow(new Object[]{
                    section.courseCode(),
                    section.courseTitle(),
                    section.instructorName(),
                    section.timeSlot(),
                    section.enrolled() + " / " + section.capacity(),
                    section.isEnrolled() ? "Drop" : "Register" // Set button text
            });
        }
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

        JLabel titleLabel = new JLabel("Course Catalog");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add a refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        refreshButton.addActionListener(e -> loadData()); // Calls loadData()

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Course Code", "Title", "Instructor", "Time", "Seats", "Actions"};

        tableModel = new DefaultTableModel(null, columnNames) { // Start with no data
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        courseTable.setRowHeight(40);
        courseTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        courseTable.getColumnModel().getColumn(4).setCellRenderer(new SeatsAvailableRenderer());
        courseTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonColumnRenderer());
        // 4. This MouseListener is now fully functional
        courseTable.addMouseListener(new JTableButtonMouseListener(courseTable));

        return new JScrollPane(courseTable);
    }

    // --- Inner classes for Table (ButtonColumnRenderer, SeatsAvailableRenderer) ---
    // (These are unchanged)
    private class ButtonColumnRenderer extends DefaultTableCellRenderer {
        private final JButton registerButton;
        private final JButton dropButton;

        public ButtonColumnRenderer() {
            registerButton = new JButton("Register");
            registerButton.setBackground(new Color(0, 82, 204));
            registerButton.setForeground(Color.WHITE);
            registerButton.setFont(new Font("SansSerif", Font.BOLD, 12));

            dropButton = new JButton("Drop");
            dropButton.setBackground(Color.RED);
            dropButton.setForeground(Color.WHITE);
            dropButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String buttonText = (value == null) ? "" : value.toString();
            if ("Register".equals(buttonText)) {
                return registerButton;
            } else if ("Drop".equals(buttonText)) {
                return dropButton;
            }
            return new JLabel();
        }
    }

    private class SeatsAvailableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String seatsStr = value.toString();
            try {
                int seats = Integer.parseInt(seatsStr.split("/")[0].trim());
                if (seats <= 5) {
                    setText("<html><font color='red'>" + seatsStr + "</font></html>");
                } else {
                    setText(seatsStr);
                }
            } catch (Exception e) {
                setText(seatsStr);
            }
            return c;
        }
    }

    /**
     * 5. UPDATED: Mouse listener now calls the StudentService
     */
    private class JTableButtonMouseListener extends MouseAdapter {
        private final JTable table;
        public JTableButtonMouseListener(JTable table) { this.table = table; }

        @Override
        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / table.getRowHeight();

            if (row < table.getRowCount() && row >= 0 && column == table.getColumnCount() - 1) {
                // Get the section ID from our stored list
                SectionView selectedSection = sectionList.get(row);
                int sectionId = selectedSection.sectionId();
                String action = table.getValueAt(row, column).toString();

                String resultMessage = "";
                if ("Register".equals(action)) {
                    // --- REAL BACKEND CALL ---
                    resultMessage = studentService.registerForSection(sectionId);
                } else if ("Drop".equals(action)) {
                    // --- REAL BACKEND CALL ---
                    resultMessage = studentService.dropSection(sectionId);
                }

                // Show the result from the service
                JOptionPane.showMessageDialog(table, resultMessage);

                // Refresh the table to show new state
                loadData();
            }
        }
    }
}