package edu.univ.erp.ui.student;

import edu.univ.erp.domain.SectionView;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

/**
 * Course Catalog Panel.
 * UPDATED: Now shows "Credits" column and is beautified.
 * FIXED: 'final' keyword bug for button hover.
 */
public class CourseCatalogPanel extends JPanel {

    // --- Color Theme ---
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_DROP = new Color(220, 50, 50);
    private static final Color COLOR_DROP_DARK = new Color(200, 40, 40);

    private JTable courseTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private List<SectionView> sectionList;

    public CourseCatalogPanel(Runnable onGoBack, StudentService studentService) {
        this.studentService = studentService;

        setLayout(new BorderLayout(0, 15));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));
        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
    }

    /**
     * UPDATED: Now adds 'section.credits()' to the table row.
     */
    private void loadData() {
        this.sectionList = studentService.getCourseCatalog();
        tableModel.setRowCount(0);

        for (SectionView section : sectionList) {
            tableModel.addRow(new Object[]{
                    section.courseCode(),
                    section.courseTitle(),
                    section.credits(), // <-- NEW
                    section.instructorName(),
                    section.timeSlot(),
                    section.enrolled() + " / " + section.capacity(),
                    section.isEnrolled() ? "Drop" : "Register"
            });
        }
    }

    /**
     * UPDATED: createHeaderPanel, now styled.
     */
    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);

        JButton goBackButton = createModernButton("← Go Back", false, false);
        goBackButton.addActionListener(e -> onGoBack.run());

        JLabel titleLabel = new JLabel("Course Catalog");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton refreshButton = createModernButton("Refresh", false, false);
        refreshButton.addActionListener(e -> loadData());

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * UPDATED: createTablePanel with new "Credits" column
     * and fixed column indices for renderers.
     */
    private JScrollPane createTablePanel() {
        // --- UPDATED: Added "Credits" column ---
        String[] columnNames = {"Course Code", "Title", "Credits", "Instructor", "Time", "Seats", "Actions"};

        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        courseTable.setRowHeight(40);
        courseTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        courseTable.setGridColor(COLOR_BORDER);
        courseTable.setIntercellSpacing(new Dimension(0, 0));

        // --- UPDATED: Column indices are now 5 and 6 ---
        courseTable.getColumnModel().getColumn(5).setCellRenderer(new SeatsAvailableRenderer());
        courseTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonColumnRenderer());
        courseTable.addMouseListener(new JTableButtonMouseListener(courseTable));

        // Set column widths
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Code
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Title
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Credits
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Instructor
        courseTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Time
        courseTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Seats
        courseTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Actions


        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        return scrollPane;
    }

    // --- Inner class for Table (ButtonColumnRenderer) ---
    // UPDATED: Now styled
    private class ButtonColumnRenderer extends DefaultTableCellRenderer {
        private final JButton registerButton;
        private final JButton dropButton;

        public ButtonColumnRenderer() {
            registerButton = createModernButton("Register", true, true);
            dropButton = createModernButton("Drop", false, true);
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
            // Return an empty panel for spacing, otherwise it can mess up layout
            JPanel spacer = new JPanel();
            spacer.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return spacer;
        }
    }

    // --- Inner class for Table (SeatsAvailableRenderer) ---
    // (Unchanged, but I've added a check for 0)
    private class SeatsAvailableRenderer extends DefaultTableCellRenderer {
        public SeatsAvailableRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String seatsStr = value.toString();
            try {
                int seats = Integer.parseInt(seatsStr.split("/")[0].trim());
                if (seats == 0) {
                    setText("<html><font color='red'><b>Full</b></font></html>");
                } else if (seats <= 5) {
                    setText("<html><font color='#E67E22'>" + seatsStr + "</font></html>"); // Orange
                } else {
                    setText(seatsStr);
                }
            } catch (Exception e) {
                setText(seatsStr);
            }
            return c;
        }
    }

    /**
     * Mouse listener (Functionality unchanged)
     */
    private class JTableButtonMouseListener extends MouseAdapter {
        private final JTable table;
        public JTableButtonMouseListener(JTable table) { this.table = table; }

        @Override
        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / table.getRowHeight();

            // --- UPDATED: Column index is now 6 ---
            if (row < table.getRowCount() && row >= 0 && column == 6) {
                SectionView selectedSection = sectionList.get(row);
                int sectionId = selectedSection.sectionId();
                String action = table.getValueAt(row, column).toString();

                String resultMessage = "";
                if ("Register".equals(action)) {
                    resultMessage = studentService.registerForSection(sectionId);
                } else if ("Drop".equals(action)) {
                    resultMessage = studentService.dropSection(sectionId);
                }

                JOptionPane.showMessageDialog(table, resultMessage);
                loadData(); // Refresh table
            }
        }
    }

    // --- Helper Method for Styling ---

    /**
     * FIXED: Added 'final' keyword to variables used in the inner class.
     */
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

        // --- FIX IS HERE ---
        // These variables are now 'final' so the MouseAdapter inner class can access them.
        final Color bg;
        final Color fg;
        final Color bgHover;

        if (text.equals("Drop")) {
            bg = COLOR_DROP;
            fg = Color.WHITE;
            bgHover = COLOR_DROP_DARK;
        } else if (isPrimary) {
            bg = COLOR_PRIMARY;
            fg = Color.WHITE;
            bgHover = COLOR_PRIMARY_DARK;
        } else {
            bg = COLOR_BACKGROUND;
            fg = COLOR_TEXT_DARK;
            bgHover = new Color(240, 240, 240);
        }
        // --- END OF FIX ---

        button.setBackground(bg);
        button.setForeground(fg);
        if (!isPrimary && !text.equals("Drop")) {
            button.setBorder(new LineBorder(COLOR_BORDER, 1));
        }

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgHover); // This now works
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bg); // This now works
            }
        });
        return button;
    }
}