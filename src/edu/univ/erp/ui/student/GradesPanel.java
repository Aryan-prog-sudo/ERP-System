package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.TranscriptService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Grades Panel.
 * UPDATED: Now calls StudentService and TranscriptService.
 */
public class GradesPanel extends JPanel {

    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private TranscriptService transcriptService;

    // 1. Constructor updated to accept both services
    public GradesPanel(Runnable onGoBack, StudentService studentService, TranscriptService transcriptService) {
        this.studentService = studentService;
        this.transcriptService = transcriptService;

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
        List<Grade> grades = studentService.getGrades();
        tableModel.setRowCount(0); // Clear table
        for (Grade grade : grades) {
            tableModel.addRow(new Object[]{
                    grade.courseCode(),
                    grade.courseTitle(),
                    grade.credits(),
                    grade.letterGrade()
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

        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // 4. Download Transcript Button (now functional)
        JButton downloadButton = new JButton("Download Transcript (CSV)");
        downloadButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        downloadButton.setBackground(new Color(0, 82, 204));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.addActionListener(e -> onDownloadTranscript());

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(downloadButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Course Code", "Title", "Credits", "Grade"};
        tableModel = new DefaultTableModel(null, columnNames) { // Start with no data
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

    /**
     * 5. NEW: Logic for the "Download Transcript" button (Bonus Feature)
     * This fixes your 'getCurrentStudentId' error.
     */
    private void onDownloadTranscript() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new File("transcript.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {

                // --- REAL BACKEND CALL ---
                // Get the student's ID (which the service knows)
                int studentId = studentService.getCurrentStudentId();

                // Call the transcript service
                transcriptService.generateCsvTranscript(studentId, writer);

                JOptionPane.showMessageDialog(this, "Transcript saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}