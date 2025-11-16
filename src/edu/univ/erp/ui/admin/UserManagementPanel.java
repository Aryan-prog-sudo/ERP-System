package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService; // <-- IMPORT THE SERVICE
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Admin panel for managing users.
 * UPDATED: Now calls AdminService to add users.
 */
public class UserManagementPanel extends JPanel {

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField;
    private JComboBox<String> roleComboBox;

    private AdminService adminService; // <-- 1. ADD THIS FIELD

    /**
     * 2. UPDATED: Constructor now accepts AdminService
     */
    public UserManagementPanel(Runnable onGoBack, AdminService adminService) {
        this.adminService = adminService; // <-- 3. STORE SERVICE

        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 40, 40, 40));
        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.weightx = 0.4; gbc.insets = new Insets(0, 0, 0, 20);
        mainContentPanel.add(createFormPanel(), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6; gbc.insets = new Insets(0, 20, 0, 0);
        mainContentPanel.add(createTablePanel(), gbc);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    // (createHeaderPanel method is the same)
    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton goBackButton = new JButton("← Go Back");
        goBackButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        goBackButton.addActionListener(e -> onGoBack.run());
        goBackButton.setBorderPainted(false);
        goBackButton.setContentAreaFilled(false);
        goBackButton.setForeground(Color.BLUE.darker());
        goBackButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(goBackButton.getPreferredSize()), BorderLayout.EAST);
        return headerPanel;
    }

    // (createFormPanel method is the same)
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Add New User");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, gbc);
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Name"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(nameField, gbc);
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Email"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(emailField, gbc);
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Role"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        String[] roles = {"Select role", "Student", "Instructor", "Admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(roleComboBox, gbc);
        gbc.gridy++; gbc.insets = new Insets(10, 0, 0, 0);
        JButton addUserButton = new JButton("Add User");
        addUserButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addUserButton.setBackground(new Color(0, 82, 204));
        addUserButton.setForeground(Color.WHITE);
        addUserButton.setPreferredSize(new Dimension(100, 40));
        addUserButton.addActionListener(e -> onAddUser()); // This calls the updated method
        panel.add(addUserButton, gbc);
        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);
        return panel;
    }

    // (createTablePanel method is the same)
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        JLabel title = new JLabel("Current Users");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);
        String[] columnNames = {"Name", "Email", "Role"};
        // --- TODO: Load this data from your service layer ---
        Object[][] data = {
                {"John Student", "student@university.edu", "Student"},
                {"Dr. Sarah Professor", "instructor@university.edu", "Instructor"},
                {"Admin User", "admin@university.edu", "Admin"}
        };
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(tableModel);
        usersTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usersTable.setRowHeight(35);
        usersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 4. UPDATED: Logic for the "Add User" button.
     */
    private void onAddUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String role = (String) roleComboBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || "Select role".equals(role)) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generate a default password
        String defaultPassword = "defaultPassword123";

        // --- THIS IS THE REAL BACKEND CALL ---
        boolean success = adminService.createNewUser(name, email, role, defaultPassword);

        if (success) {
            // Add to table and clear fields
            tableModel.addRow(new Object[]{name, email, role});
            nameField.setText("");
            emailField.setText("");
            roleComboBox.setSelectedIndex(0);

            JOptionPane.showMessageDialog(this,
                    "User '" + name + "' added successfully!\nDefault Password: " + defaultPassword,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create user. The email may already be in use.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}