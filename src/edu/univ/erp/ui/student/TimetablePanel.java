package edu.univ.erp.ui.student;

import edu.univ.erp.domain.EnrolledSection;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

//This is the timetable panel in the Dashboard
public class TimetablePanel extends JPanel {

    //Color Theme
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);

    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;

    public TimetablePanel(Runnable onGoBack, StudentService studentService) {
        this.studentService = studentService;

        setLayout(new BorderLayout(0, 15));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        //Auto-refresh when shown
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadData();
            }
        });

        loadData();
    }

    private void loadData() {
        List<EnrolledSection> sections = studentService.getTimetable();
        tableModel.setRowCount(0);
        for (EnrolledSection sec : sections) {
            tableModel.addRow(new Object[]{
                    sec.courseCode(),
                    sec.courseTitle(),
                    sec.credits(),
                    sec.instructorName(),
                    sec.timeSlot()
            });
        }
    }

    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);

        JButton goBackButton = createModernButton("← Go Back");
        goBackButton.addActionListener(e -> onGoBack.run());

        JLabel titleLabel = new JLabel("My Timetable");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton refreshButton = createModernButton("Refresh");
        refreshButton.addActionListener(e -> loadData());

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    //This creates table for the timetables
    private JScrollPane createTablePanel() {
        String[] columnNames = {"Course Code", "Title", "Credits", "Instructor", "Time"};

        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        timetableTable = new JTable(tableModel);
        timetableTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timetableTable.setRowHeight(35);
        timetableTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        timetableTable.setGridColor(COLOR_BORDER);
        timetableTable.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        return scrollPane;
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBackground(COLOR_BACKGROUND);
        button.setForeground(COLOR_TEXT_DARK);
        button.setBorder(new LineBorder(COLOR_BORDER, 1));
        button.setPreferredSize(new Dimension(100, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(COLOR_BACKGROUND);
            }
        });
        return button;
    }
}