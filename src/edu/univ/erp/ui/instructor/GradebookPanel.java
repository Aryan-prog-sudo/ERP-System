package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.GradebookEntry;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Instructor Gradebook Panel.
 * UPDATED: Now calls InstructorService to get/save grades.
 */
public class GradebookPanel extends JPanel {

    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private InstructorService instructorService;
    private JLabel titleLabel;
    private JLabel classAverageLabel;
    private int currentSectionId = -1; // To store which section we're grading

    // 1. Constructor updated
    public GradebookPanel(Runnable onGoBack, InstructorService instructorService) {
        this.instructorService = instructorService;

        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 40, 40, 40));
        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    /**
     * 2. Public method called by Main to load data
     */
    public void loadGradebook(int sectionId) {
        this.currentSectionId = sectionId;
        titleLabel.setText("Gradebook (Section " + sectionId + ")");

        List<GradebookEntry> entries = instructorService.getGradebook(sectionId);

        tableModel.setRowCount(0); // Clear table
        for (GradebookEntry entry : entries) {
            tableModel.addRow(new Object[]{
                    entry.studentId(),
                    entry.studentName(),
                    entry.quizScore(),
                    entry.midtermScore(),
                    entry.finalScore(),
                    entry.finalGrade() != null ? entry.finalGrade() : "-"
            });
        }

        // TODO: Calculate and set class average
        classAverageLabel.setText("Class Average: -");
    }

    private String[] getColumnNames() {
        return new String[]{
                "Student ID",
                "Student Name",
                "Quiz (20%)",
                "Midterm (30%)",
                "Final (50%)",
                "Final Grade"
        };
    }

    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        JButton goBackButton = new JButton("← Go Back");
        goBackButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        goBackButton.addActionListener(e -> onGoBack.run());
        goBackButton.setBorderPainted(false);
        goBackButton.setContentAreaFilled(false);
        goBackButton.setForeground(Color.BLUE.darker());
        goBackButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titleLabel = new JLabel("Gradebook");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        classAverageLabel = new JLabel("Class Average: -");
        classAverageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        classAverageLabel.setForeground(Color.GRAY);
        titlePanel.add(titleLabel);
        titlePanel.add(classAverageLabel);

        // 3. Button now calls onSaveAndCalculate
        JButton calculateButton = new JButton("Save & Calculate Grades");
        calculateButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        calculateButton.setBackground(new Color(0, 82, 204));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setPreferredSize(new Dimension(200, 40));
        calculateButton.addActionListener(e -> onSaveAndCalculate());

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(calculateButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(null, getColumnNames()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing for scores (cols 2, 3, 4)
                return column == 2 || column == 3 || column == 4;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gradesTable.setRowHeight(40);
        gradesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JTextField textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        DefaultCellEditor cellEditor = new DefaultCellEditor(textField);

        gradesTable.getColumnModel().getColumn(2).setCellEditor(cellEditor); // Quiz
        gradesTable.getColumnModel().getColumn(3).setCellEditor(cellEditor); // Midterm
        gradesTable.getColumnModel().getColumn(4).setCellEditor(cellEditor); // Final

        return new JScrollPane(gradesTable);
    }

    /**
     * 4. UPDATED: Logic for the "Save & Calculate" button.
     */
    private void onSaveAndCalculate() {
        if (currentSectionId == -1) return;

        // Stop any cell editing to save the current value
        if (gradesTable.isEditing()) {
            gradesTable.getCellEditor().stopCellEditing();
        }

        // 1. Read all data from the JTable into a list
        List<GradebookEntry> gradebook = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            try {
                int studentId = (int) tableModel.getValueAt(row, 0);
                String studentName = (String) tableModel.getValueAt(row, 1);
                // Handle null or empty strings before parsing
                double quiz = parseDouble(tableModel.getValueAt(row, 2));
                double midterm = parseDouble(tableModel.getValueAt(row, 3));
                double finalScore = parseDouble(tableModel.getValueAt(row, 4));

                gradebook.add(new GradebookEntry(
                        studentId, studentName, quiz, midterm, finalScore, null
                ));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid score for student: " + tableModel.getValueAt(row, 1) +
                                "\nPlease enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop processing
            }
        }

        // 2. --- REAL BACKEND CALL ---
        boolean success = instructorService.saveAndCalculateGrades(currentSectionId, gradebook);

        if (success) {
            JOptionPane.showMessageDialog(this, "Grades saved and calculated successfully.");
            // 3. Refresh the table to show the new final grades
            loadGradebook(currentSectionId);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save grades.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper to prevent crash on empty cells
    private double parseDouble(Object obj) {
        if (obj == null || obj.toString().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(obj.toString());
    }
}