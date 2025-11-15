package edu.univ.erp.ui.instructor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Instructor Gradebook Panel.
 * Corresponds to: edu.univ.erp.ui.instructor.GradebookPanel
 * Design: image_3ec2db.png
 * INCLUDES "Go Back" button and an editable JTable.
 */
public class GradebookPanel extends JPanel {

    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private Runnable onGoBack;
    private JLabel titleLabel;
    private JLabel classAverageLabel;

    // Store weights
    private double quizWeight = 0.20;
    private double midtermWeight = 0.30;
    private double finalWeight = 0.50;

    public GradebookPanel(Runnable onGoBack) {
        this.onGoBack = onGoBack;
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Public method called by Main to load data for a specific course.
     */
    public void loadCourse(String courseCode) {
        titleLabel.setText(courseCode + " Gradebook");

        // --- TODO: Call your service layer here ---
        // Example: GradebookData data = gradebookService.getGradebook(courseCode);
        // For now, load dummy data

        Object[][] data = {
                {"Alice Johnson", "85", "78", "92", "-"},
                {"Bob Smith", "92", "88", "85", "-"},
                {"Carol White", "78", "82", "88", "-"}
        };
        tableModel.setDataVector(data, getColumnNames());
        classAverageLabel.setText("Class Average: 86.0%"); // Placeholder
    }

    private String[] getColumnNames() {
        // Create column names with weights
        return new String[]{
                "Student Name",
                String.format("Quiz (%.0f%%)", quizWeight * 100),
                String.format("Midterm (%.0f%%)", midtermWeight * 100),
                String.format("Final (%.0f%%)", finalWeight * 100),
                "Calculated Grade"
        };
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));

        // --- "Go Back" Button (Styled as a link) ---
        JButton goBackButton = new JButton("← Go Back");
        goBackButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        goBackButton.addActionListener(e -> onGoBack.run());
        goBackButton.setBorderPainted(false);
        goBackButton.setContentAreaFilled(false);
        goBackButton.setForeground(Color.BLUE.darker());
        goBackButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // --- Title Panel (Center) ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("CS-101 Gradebook"); // Will be updated by loadCourse()
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        classAverageLabel = new JLabel("Class Average: 86.0%");
        classAverageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        classAverageLabel.setForeground(Color.GRAY);

        titlePanel.add(titleLabel);
        titlePanel.add(classAverageLabel);

        // --- "Calculate" Button (Right) ---
        JButton calculateButton = new JButton("Calculate Final Grades");
        calculateButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        calculateButton.setOpaque(true);
        calculateButton.setBackground(new Color(0, 82, 204));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setPreferredSize(new Dimension(200, 40));

        calculateButton.addActionListener(e -> calculateGrades());

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(calculateButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {

        tableModel = new DefaultTableModel(null, getColumnNames()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing for Quiz, Midterm, and Final columns
                return column == 1 || column == 2 || column == 3;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gradesTable.setRowHeight(40); // Taller rows to match design
        gradesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        // Make the editable cells look like the text fields in the design
        // by using a custom editor that sets the font and a border.
        JTextField textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        DefaultCellEditor cellEditor = new DefaultCellEditor(textField);

        // Apply this editor to the editable columns
        gradesTable.getColumnModel().getColumn(1).setCellEditor(cellEditor);
        gradesTable.getColumnModel().getColumn(2).setCellEditor(cellEditor);
        gradesTable.getColumnModel().getColumn(3).setCellEditor(cellEditor);


        return new JScrollPane(gradesTable);
    }

    /**
     * Logic for the "Calculate Final Grades" button.
     */
    private void calculateGrades() {
        // --- TODO: This should call the service layer ---
        // service.calculateFinalGrades(courseCode, tableModel.getDataVector());
        // For now, we'll do the calculation directly in the UI

        DecimalFormat df = new DecimalFormat("0.0"); // Format to one decimal place

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            try {
                // Get values from table (they are Strings)
                String quizStr = tableModel.getValueAt(row, 1).toString();
                String midtermStr = tableModel.getValueAt(row, 2).toString();
                String finalStr = tableModel.getValueAt(row, 3).toString();

                // Convert to numbers
                double quiz = Double.parseDouble(quizStr);
                double midterm = Double.parseDouble(midtermStr);
                double finalScore = Double.parseDouble(finalStr);

                // Calculate weighted grade
                double calculatedGrade = (quiz * quizWeight) +
                        (midterm * midtermWeight) +
                        (finalScore * finalWeight);

                // Set the new value in the "Calculated Grade" column
                tableModel.setValueAt(df.format(calculatedGrade), row, 4);

            } catch (NumberFormatException e) {
                // Handle cases where the text is not a valid number
                tableModel.setValueAt("Error", row, 4);
                JOptionPane.showMessageDialog(this,
                        "Invalid score for student: " + tableModel.getValueAt(row, 0) +
                                "\nPlease enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}