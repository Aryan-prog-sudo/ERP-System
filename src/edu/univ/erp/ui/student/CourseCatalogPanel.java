package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Course Catalog Panel.
 * *** UPDATED with styled "Go Back" button. ***
 * Uses ONLY built-in Java components.
 */
public class CourseCatalogPanel extends JPanel {

    private JTable courseTable;
    private DefaultTableModel tableModel;
    private Runnable onGoBack;

    public CourseCatalogPanel(Runnable onGoBack) {
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

        // Style changes
        goBackButton.setBorderPainted(false);
        goBackButton.setContentAreaFilled(false);
        goBackButton.setForeground(Color.BLUE.darker());
        goBackButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel("Course Catalog");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Course Code", "Title", "Instructor", "Time", "Seats Available", "Actions"};
        Object[][] data = {
                {"CS-101", "Intro to Programming", "Dr. Smith", "MWF 9:00 AM", "15 / 50", "Register"},
                {"MATH-201", "Calculus II", "Prof. Johnson", "TTh 10:30 AM", "2 / 40", "Drop"},
                {"ENG-105", "English Literature", "Dr. Williams", "MWF 2:00 PM", "8 / 30", "Register"},
                {"PHYS-201", "Physics I", "Dr. Brown", "TTh 1:00 PM", "5 / 35", "Register"}
        };

        tableModel = new DefaultTableModel(data, columnNames) {
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
        courseTable.addMouseListener(new JTableButtonMouseListener(courseTable));

        return new JScrollPane(courseTable);
    }

    // --- Inner classes for Table (ButtonColumnRenderer, SeatsAvailableRenderer, JTableButtonMouseListener) ---
    // ... (These are unchanged from the previous "straight Swing" version) ...

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
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(table.getForeground());
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
            } catch (Exception e) { /* ignore */ }
            return c;
        }
    }

    private class JTableButtonMouseListener extends MouseAdapter {
        private final JTable table;
        public JTableButtonMouseListener(JTable table) { this.table = table; }

        @Override
        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / table.getRowHeight();

            if (row < table.getRowCount() && row >= 0 && column == table.getColumnCount() - 1) {
                String action = table.getValueAt(row, column).toString();
                String courseCode = table.getValueAt(row, 0).toString();

                if ("Register".equals(action)) {
                    JOptionPane.showMessageDialog(table, "Registered for: " + courseCode);
                } else if ("Drop".equals(action)) {
                    JOptionPane.showMessageDialog(table, "Dropped: " + courseCode);
                }
            }
        }
    }
}