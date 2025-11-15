package edu.univ.erp.ui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Admin panel for managing courses (add/view).
 * Corresponds to: edu.univ.erp.ui.admin.CourseManagementPanel
 * Design: image_3eda7c.png
 * INCLUDES "Go Back" button.
 */
public class CourseManagementPanel extends JPanel {

    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private JTextField codeField, titleField, creditsField;

    public CourseManagementPanel(Runnable onGoBack) {
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 40, 40, 40));

        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.4; gbc.insets = new Insets(0, 0, 0, 20);
        mainContentPanel.add(createFormPanel(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.6; gbc.insets = new Insets(0, 20, 0, 0);
        mainContentPanel.add(createTablePanel(), gbc);

        add(mainContentPanel, BorderLayout.CENTER);
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

        JLabel titleLabel = new JLabel("Manage Courses");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(goBackButton.getPreferredSize()), BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;

        gbc.gridy = 0; gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Create New Course");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Course Code"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        codeField = new JTextField();
        codeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(codeField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Course Title"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        titleField = new JTextField();
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(titleField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Credits"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        creditsField = new JTextField();
        creditsField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(creditsField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 0, 0);
        JButton createButton = new JButton("Create Course");
        createButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createButton.setBackground(new Color(0, 82, 204));
        createButton.setForeground(Color.WHITE);
        createButton.setPreferredSize(new Dimension(100, 40));
        createButton.addActionListener(e -> onCreateCourse());
        panel.add(createButton, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel title = new JLabel("Existing Courses");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Code", "Title", "Credits"};
        // --- TODO: Load this data from your service layer ---
        Object[][] data = {
                {"CS-101", "Intro to Programming", 3},
                {"MATH-201", "Calculus II", 4},
                {"ENG-105", "English Literature", 3}
        };

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        coursesTable.setRowHeight(35);
        coursesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void onCreateCourse() {
        String code = codeField.getText();
        String title = titleField.getText();
        String creditsStr = creditsField.getText();

        if (code.isEmpty() || title.isEmpty() || creditsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int credits = Integer.parseInt(creditsStr);
            // --- TODO: Call your service layer ---
            // adminService.createCourse(code, title, credits);

            // --- Placeholder Logic ---
            tableModel.addRow(new Object[]{code, title, credits});
            codeField.setText("");
            titleField.setText("");
            creditsField.setText("");
            JOptionPane.showMessageDialog(this, "Course '" + code + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Credits must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}