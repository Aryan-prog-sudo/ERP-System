package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Timetable Panel.
 * *** UPDATED with styled "Go Back" button. ***
 * Uses ONLY built-in Java components.
 */
public class TimetablePanel extends JPanel {

    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private Runnable onGoBack;

    public TimetablePanel(Runnable onGoBack) {
        this.onGoBack = onGoBack;
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        // --- "Go Back" Button (Styled as a link) ---
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

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Course Code", "Title", "Time", "Location"};
        Object[][] data = {
                {"MATH-201", "Calculus II", "TTh 10:30 AM - 12:00 PM", "Building A, Room 301"},
                {"CS-205", "Data Structures", "MWF 1:00 PM - 2:30 PM", "Building B, Room 101"},
                {"ENG-110", "Writing Workshop", "W 3:00 PM - 5:00 PM", "Building C, Room 205"}
        };

        tableModel = new DefaultTableModel(data, columnNames) {
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