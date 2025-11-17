package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.GradebookEntry;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File; // <-- NEW IMPORT
import java.io.FileWriter; // <-- NEW IMPORT
import java.util.ArrayList;
import java.util.List;

/**
 * Instructor Gradebook Panel.
 * UPDATED: Beautified and now includes "Export CSV" button.
 */
public class GradebookPanel extends JPanel {

    // --- Color Theme ---
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_TEXT_FIELD_BG = new Color(248, 248, 248);

    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private InstructorService instructorService;
    private JLabel titleLabel;
    private JLabel classAverageLabel;
    private int currentSectionId = -1;

    public GradebookPanel(Runnable onGoBack, InstructorService instructorService) {
        this.instructorService = instructorService;

        setLayout(new BorderLayout(0, 15));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));
        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Public method called by Main to load data
     */
    public void loadGradebook(int sectionId) {
        this.currentSectionId = sectionId;

        List<GradebookEntry> entries = instructorService.getGradebook(sectionId);

        // Update title (We need to get the course name... for now, use ID)
        // TODO: Update service to return section name
        titleLabel.setText("Gradebook (Section " + sectionId + ")");

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
                "Student ID", "Student Name", "Quiz (20%)",
                "Midterm (30%)", "Final (50%)", "Final Grade"
        };
    }

    /**
     * UPDATED: createHeaderPanel, now styled and has Export button
     */
    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(COLOR_BACKGROUND);

        JButton goBackButton = createModernButton("← Go Back", false, false);
        goBackButton.addActionListener(e -> onGoBack.run());

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titleLabel = new JLabel("Gradebook");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        classAverageLabel = new JLabel("Class Average: -");
        classAverageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        classAverageLabel.setForeground(COLOR_TEXT_LIGHT);
        titlePanel.add(titleLabel);
        titlePanel.add(classAverageLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // --- NEW: Export CSV Button ---
        JButton exportButton = createModernButton("Export CSV", false, false);
        exportButton.addActionListener(e -> onExportCsv());

        JButton calculateButton = createModernButton("Save & Calculate Grades", true, false);
        calculateButton.addActionListener(e -> onSaveAndCalculate());

        buttonPanel.add(exportButton); // Add new button
        buttonPanel.add(calculateButton);

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * UPDATED: createTablePanel, now styled
     */
    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(null, getColumnNames()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3 || column == 4;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gradesTable.setRowHeight(40);
        gradesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        gradesTable.setGridColor(COLOR_BORDER);
        gradesTable.setIntercellSpacing(new Dimension(0, 0));

        // Custom cell editor for a modern feel
        JTextField textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setBackground(COLOR_TEXT_FIELD_BG);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_PRIMARY, 1), // Highlight when editing
                new EmptyBorder(5, 5, 5, 5)
        ));
        DefaultCellEditor cellEditor = new DefaultCellEditor(textField);

        gradesTable.getColumnModel().getColumn(2).setCellEditor(cellEditor); // Quiz
        gradesTable.getColumnModel().getColumn(3).setCellEditor(cellEditor); // Midterm
        gradesTable.getColumnModel().getColumn(4).setCellEditor(cellEditor); // Final

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        return scrollPane;
    }

    /**
     * Logic for the "Save & Calculate" button (Functionality unchanged)
     */
    private void onSaveAndCalculate() {
        if (currentSectionId == -1) return;
        if (gradesTable.isEditing()) {
            gradesTable.getCellEditor().stopCellEditing();
        }

        List<GradebookEntry> gradebook = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            try {
                int studentId = (int) tableModel.getValueAt(row, 0);
                String studentName = (String) tableModel.getValueAt(row, 1);
                double quiz = parseDouble(tableModel.getValueAt(row, 2));
                double midterm = parseDouble(tableModel.getValueAt(row, 3));
                double finalScore = parseDouble(tableModel.getValueAt(row, 4));

                gradebook.add(new GradebookEntry(
                        studentId, studentName, quiz, midterm, finalScore, null
                ));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid score for student: " + tableModel.getValueAt(row, 1) +
                                "\nPlease enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        boolean success = instructorService.saveAndCalculateGrades(currentSectionId, gradebook);

        if (success) {
            JOptionPane.showMessageDialog(this, "Grades saved and calculated successfully.");
            loadGradebook(currentSectionId);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save grades.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * NEW: Logic for the "Export CSV" button.
     */
    private void onExportCsv() {
        if (currentSectionId == -1) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Gradebook as CSV");
        fileChooser.setSelectedFile(new File("gradebook_section_" + currentSectionId + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {

                // --- REAL BACKEND CALL ---
                instructorService.exportGradebookToCsv(currentSectionId, writer);

                JOptionPane.showMessageDialog(this, "Gradebook exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Helper to prevent crash on empty cells
    private double parseDouble(Object obj) {
        if (obj == null || obj.toString().trim().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(obj.toString());
    }

    // --- Helper Method for Styling ---

    private JButton createModernButton(String text, boolean isPrimary, boolean isSmall) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, isSmall ? 12 : 14));
        button.setOpaque(true);
        if (isSmall) {
            button.setPreferredSize(new Dimension(80, 28));
            button.setBorder(new EmptyBorder(4, 8, 4, 8));
        } else {
            button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));
            button.setBorder(new EmptyBorder(5, 15, 5, 15));
        }
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final Color bg;
        final Color fg;
        final Color bgHover;

        if (isPrimary) {
            bg = COLOR_PRIMARY;
            fg = Color.WHITE;
            bgHover = COLOR_PRIMARY_DARK;
        } else {
            bg = COLOR_BACKGROUND;
            fg = COLOR_TEXT_DARK;
            bgHover = new Color(240, 240, 240);
        }

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
}