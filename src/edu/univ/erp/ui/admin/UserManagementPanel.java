package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.UserView; // <-- NEW IMPORT
import edu.univ.erp.service.AdminService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List; // <-- NEW IMPORT

//This is the admin panel that helps in managing the users
public class UserManagementPanel extends JPanel {

    //Color Theme
    private static final Color COLOR_PRIMARY = new Color(0, 82, 204);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 62, 184);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT_DARK = new Color(30, 30, 30);
    private static final Color COLOR_TEXT_LIGHT = new Color(140, 140, 140);
    private static final Color COLOR_BORDER = new Color(220, 220, 220);
    private static final Color COLOR_TEXT_FIELD_BG = new Color(248, 248, 248);

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField;
    private JComboBox<String> roleComboBox;

    private AdminService adminService;

    public UserManagementPanel(Runnable onGoBack, AdminService adminService) {
        this.adminService = adminService;
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 40, 40, 40));
        add(createHeaderPanel(onGoBack), BorderLayout.NORTH);
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        gbc.gridx = 0; gbc.weightx = 0.4; gbc.insets = new Insets(0, 0, 0, 20);
        mainContentPanel.add(createFormPanel(), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6; gbc.insets = new Insets(0, 20, 0, 0);
        mainContentPanel.add(createTablePanel(), gbc);
        add(mainContentPanel, BorderLayout.CENTER);
        loadData();
    }

    //This loads all the data regarding all the users
    //Calls the GetAllUsers method of the service class
    private void loadData() {
        List<UserView> users = adminService.GetAllUsers();
        tableModel.setRowCount(0);
        for (UserView user : users) {
            tableModel.addRow(new Object[]{
                    user.fullName(),
                    user.email(),
                    user.role()
            });
        }
    }

    //To create header panel
    private JPanel createHeaderPanel(Runnable onGoBack) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BACKGROUND);

        JButton goBackButton = createModernButton("← Go Back", false);
        goBackButton.addActionListener(e -> onGoBack.run());

        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(goBackButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Invisible spacer to center the title
        JButton spacer = createModernButton("← Go Back", false);
        spacer.setVisible(false);
        headerPanel.add(spacer, BorderLayout.EAST);

        return headerPanel;
    }

    //This creates the form panel, ie the form we fill on the left to add new user
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;

        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel title = new JLabel("Add New User");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Full Name"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        nameField = createModernTextField(20);
        panel.add(nameField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("UserName"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0);
        emailField = createModernTextField(20);
        panel.add(emailField, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(new JLabel("Role"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        String[] roles = {"Select role", "Student", "Instructor", "Admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleComboBox.setBackground(COLOR_BACKGROUND);
        panel.add(roleComboBox, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 0, 0);
        JButton addUserButton = createModernButton("Add User", true);
        addUserButton.addActionListener(e -> onAddUser());
        panel.add(addUserButton, gbc);

        gbc.gridy++; gbc.weighty = 1.0; // Pushes everything up
        panel.add(Box.createGlue(), gbc);

        return panel;
    }


    //Create the table on the right of UserManagementPanel that shows all the users
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel title = new JLabel("Current Users");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        String[] columnNames = {"Name", "UserName", "Role"};

        tableModel = new DefaultTableModel(null, columnNames) {
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
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    private void onAddUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String role = (String) roleComboBox.getSelectedItem();
        //All the fields are to be filled, and non can be empty
        if (name.isEmpty() || email.isEmpty() || "Select role".equals(role)) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String defaultPassword = "defaultPassword123"; //This is the default password provided everytime the user is created
        //Backend Call to the service call
        boolean success = adminService.createNewUser(name, email, role, defaultPassword); //This returns true if the user is created
        if (success) {
            loadData();
            nameField.setText("");
            emailField.setText("");
            roleComboBox.setSelectedIndex(0);
            JOptionPane.showMessageDialog(this,
                    "User '" + name + "' added successfully!\nDefault Password: " + defaultPassword,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create user. The UserName may already be in use.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


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

    private JTextField createModernTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBackground(COLOR_TEXT_FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(8, 8, 8, 8) // Internal padding
        ));
        return field;
    }
}