package edu.univ.erp.ui.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Admin panel for managing users (add/view).
 * Corresponds to: edu.univ.erp.ui.admin.UserManagementPanel
 * Design: image_3ed2a1.png
 * INCLUDES "Go Back" button.
 */
public class UserManagementPanel extends JPanel {

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField;
    private JComboBox<String> roleComboBox;

    public UserManagementPanel(Runnable onGoBack) {
        setLayout(new BorderLayout(0, 20)); // Gap
        setBorder(new EmptyBorder(20, 40, 40, 40)); // Padding

        // --- Header Panel (Go Back Button + Title) ---
        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);

        // --- Main Content (2-column layout) ---
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- Left Panel: Add New User ---
        gbc.gridx = 0;
        gbc.weightx = 0.4; // 40% width
        gbc.insets = new Insets(0, 0, 0, 20); // Right padding
        mainContentPanel.add(createFormPanel(), gbc);

        // --- Right Panel: Current Users ---
        gbc.gridx = 1;
        gbc.weightx = 0.6; // 60% width
        gbc.insets = new Insets(0, 20, 0, 0); // Left padding
        mainContentPanel.add(createTablePanel(), gbc);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the header with the "Go Back" button and title.
     */
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

        // Add an invisible component to balance the "Go Back" button
        headerPanel.add(Box.createRigidArea(goBackButton.getPreferredSize()), BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Creates the "Add New User" form panel (Left side).
     */
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

        // --- Title ---
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Add New User");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(title, gbc);

        // --- Name Field ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Name"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(nameField, gbc);

        // --- Email Field ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Email"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(emailField, gbc);

        // --- Role Field ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Role"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        String[] roles = {"Select role", "Student", "Instructor", "Admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(roleComboBox, gbc);

        // --- Add User Button ---
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        JButton addUserButton = new JButton("Add User");
        addUserButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addUserButton.setBackground(new Color(0, 82, 204));
        addUserButton.setForeground(Color.WHITE);
        addUserButton.setPreferredSize(new Dimension(100, 40));
        addUserButton.addActionListener(e -> onAddUser());
        panel.add(addUserButton, gbc);

        // --- Spacer to push form up ---
        gbc.gridy++;
        gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);

        return panel;
    }

    /**
     * Creates the "Current Users" table panel (Right side).
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false); // Transparent background

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
     * Logic for the "Add User" button.
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

        // --- TODO: Call your service layer ---
        // 1. A default password must be generated.
        // String defaultPassword = "defaultPassword123";
        // 2. Call the service
        // try {
        //    adminService.createUser(name, email, role, defaultPassword);
        //    // 3. On success, add to table
        //    tableModel.addRow(new Object[]{name, email, role});
        //    // 4. Clear fields
        //    nameField.setText("");
        //    emailField.setText("");
        //    roleComboBox.setSelectedIndex(0);
        //    JOptionPane.showMessageDialog(this, "User added successfully.\nDefault Password: " + defaultPassword, "Success", JOptionPane.INFORMATION_MESSAGE);
        // } catch (Exception ex) {
        //    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        // }

        // --- Placeholder Logic for this demo ---
        tableModel.addRow(new Object[]{name, email, role});
        nameField.setText("");
        emailField.setText("");
        roleComboBox.setSelectedIndex(0);
        JOptionPane.showMessageDialog(this,
                "User '" + name + "' added successfully!\n(This is a demo)", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}