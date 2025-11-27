package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.TranscriptService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.List;


public class GradesPanel extends JPanel {
    //Color Theme
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);

    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private TranscriptService transcriptService;

    public GradesPanel(Runnable onGoBack, StudentService studentService, TranscriptService transcriptService) {
        this.studentService = studentService;
        this.transcriptService = transcriptService;

        setLayout(new BorderLayout(0, 15));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));
        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
    }


    private void loadData() {
        // This will now work correctly thanks to the DAO fix
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


    //This creates the header panel on the top of grades panel
    //It also contains the download transcript option
    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(COLOR_BACKGROUND);

        JButton goBackButton = createModernButton("← Go Back", false);
        goBackButton.addActionListener(e -> onGoBack.run());

        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // A panel to hold the two right-side buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton refreshButton = createModernButton("Refresh", false);
        refreshButton.addActionListener(e -> loadData());

        JButton downloadButton = createModernButton("Download Transcript (CSV)", true);
        downloadButton.addActionListener(e -> onDownloadTranscript());

        buttonPanel.add(refreshButton);
        buttonPanel.add(downloadButton);

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    //Creates the tables
    private JScrollPane createTablePanel() {
        String[] columnNames = {"Course Code", "Title", "Credits", "Grade"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gradesTable.setRowHeight(35);
        gradesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        gradesTable.setGridColor(COLOR_BORDER);
        gradesTable.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        return scrollPane;
    }


    //This is the code for the button download transcript
    //It basically calls the JFileChooser and rest of the logic is handled in transcript service
    private void onDownloadTranscript() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new File("transcript.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                int studentId = studentService.getCurrentStudentId();
                transcriptService.generateCsvTranscript(studentId, writer);
                JOptionPane.showMessageDialog(this, "Transcript saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    //Helper Method for Styling
    private JButton createModernButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color bg = isPrimary ? COLOR_PRIMARY : COLOR_BACKGROUND;
        Color fg = isPrimary ? Color.WHITE : COLOR_TEXT_DARK;
        Color bgHover = isPrimary ? COLOR_PRIMARY_DARK : new Color(240, 240, 240);
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