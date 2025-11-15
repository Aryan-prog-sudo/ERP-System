package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Grades Panel.
 * *** UPDATED with styled "Go Back" button. ***
 * Uses ONLY built-in Java components.
 */
public class GradesPanel extends JPanel {

    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private Runnable onGoBack;

    public GradesPanel(Runnable onGoBack) {
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

        // --- Title ---
        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // --- "Download" Button ---
        JButton downloadButton = new JButton("Download Transcript");
        downloadButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        downloadButton.setBackground(new Color(0, 82, 204));
        downloadButton.setForeground(Color.WHITE);

        downloadButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Downloading transcript...");
        });

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(downloadButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Course Code", "Title", "Credits", "Grade"};
        Object[][] data = {
                {"CS-101", "Intro to Programming", 3, "A"},
                {"MATH-201", "Calculus II", 4, "B+"},
                {"ENG-105", "English Composition", 3, "A-"},
                {"PHYS-101", "Physics I", 4, "B"},
                {"HIST-201", "World History", 3, "A"}
        };

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gradesTable.setRowHeight(35);
        gradesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        return new JScrollPane(gradesTable);
    }
}