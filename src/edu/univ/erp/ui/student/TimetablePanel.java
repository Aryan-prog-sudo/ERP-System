package edu.univ.erp.ui.student;

import edu.univ.erp.domain.EnrolledSection;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Timetable Panel.
 * UPDATED: Now calls StudentService to get data.
 */
public class TimetablePanel extends JPanel {

    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;

    // 1. Constructor updated to accept StudentService
    public TimetablePanel(Runnable onGoBack, StudentService studentService) {
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
        List<EnrolledSection> timetable = studentService.getTimetable();
        tableModel.setRowCount(0); // Clear table
        for (EnrolledSection section : timetable) {
            tableModel.addRow(new Object[]{
                    section.courseCode(),
                    section.courseTitle(),
                    section.timeSlot(),
                    section.location() // This comes from the EnrolledSection record
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

        JLabel titleLabel = new JLabel("My Timetable");
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
        String[] columnNames = {"Course Code", "Title", "Time", "Location"};
        tableModel = new DefaultTableModel(null, columnNames) { // Start with no data
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timetableTable = new JTable(tableModel);
        timetableTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timetableTable.setRowHeight(35);
        timetableTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        return new JScrollPane(timetableTable);
    }
}