package edu.univ.erp.ui;

import edu.univ.erp.ui.auth.LoginDialog;
import edu.univ.erp.ui.auth.ChangePasswordDialog;

// Student panels
import edu.univ.erp.ui.student.CourseCatalogPanel;
import edu.univ.erp.ui.student.GradesPanel;
import edu.univ.erp.ui.student.StudentDashboardPanel;
import edu.univ.erp.ui.student.TimetablePanel;

// Instructor panels
import edu.univ.erp.ui.instructor.GradebookPanel;
import edu.univ.erp.ui.instructor.MySectionsPanel;

// NEW: Admin panels
import edu.univ.erp.ui.admin.AdminDashboardPanel;
import edu.univ.erp.ui.admin.UserManagementPanel;
import edu.univ.erp.ui.admin.CourseManagementPanel;
import edu.univ.erp.ui.admin.SectionManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Main application frame.
 * *** UPDATED: Now handles Admin role and all Admin panels. ***
 */
public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel welcomeLabel;

    // Student Panels
    private StudentDashboardPanel studentDashboard;
    private CourseCatalogPanel courseCatalog;
    private TimetablePanel timetablePanel;
    private GradesPanel gradesPanel;

    // Instructor Panels
    private MySectionsPanel mySectionsPanel;
    private GradebookPanel gradebookPanel;

    // NEW: Admin Panels
    private AdminDashboardPanel adminDashboard;
    private UserManagementPanel userManagementPanel;
    private CourseManagementPanel courseManagementPanel;
    private SectionManagementPanel sectionManagementPanel;

    public Main() {
        setTitle("University ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- Create Navigation Callbacks ---

        // Student Navigation
        Runnable showStudentHome = () -> cardLayout.show(mainPanel, "student_dashboard");
        Runnable showCatalog = () -> cardLayout.show(mainPanel, "catalog");
        Runnable showTimetable = () -> cardLayout.show(mainPanel, "timetable");
        Runnable showGrades = () -> cardLayout.show(mainPanel, "grades");

        // Instructor Navigation
        Runnable showInstructorHome = () -> cardLayout.show(mainPanel, "my_sections");
        Consumer<String> showGradebookForCourse = (courseCode) -> {
            gradebookPanel.loadCourse(courseCode);
            cardLayout.show(mainPanel, "gradebook");
        };

        // NEW: Admin Navigation
        Runnable showAdminHome = () -> cardLayout.show(mainPanel, "admin_dashboard");
        Runnable showUserManagement = () -> cardLayout.show(mainPanel, "admin_users");
        Runnable showCourseManagement = () -> cardLayout.show(mainPanel, "admin_courses");
        Runnable showSectionManagement = () -> cardLayout.show(mainPanel, "admin_sections");


        // --- Initialize All Panels ---
        studentDashboard = new StudentDashboardPanel(showCatalog, showTimetable, showGrades);
        courseCatalog = new CourseCatalogPanel(showStudentHome);
        timetablePanel = new TimetablePanel(showStudentHome);
        gradesPanel = new GradesPanel(showStudentHome);

        mySectionsPanel = new MySectionsPanel(showGradebookForCourse);
        gradebookPanel = new GradebookPanel(showInstructorHome);

        // NEW: Instantiate Admin Panels
        adminDashboard = new AdminDashboardPanel(showUserManagement, showCourseManagement, showSectionManagement);
        userManagementPanel = new UserManagementPanel(showAdminHome); // Pass "Go Back" function
        courseManagementPanel = new CourseManagementPanel(showAdminHome); // Pass "Go Back" function
        sectionManagementPanel = new SectionManagementPanel(showAdminHome); // Pass "Go Back" function


        // --- Add All Panels to CardLayout ---
        mainPanel.add(studentDashboard, "student_dashboard");
        mainPanel.add(courseCatalog, "catalog");
        mainPanel.add(timetablePanel, "timetable");
        mainPanel.add(gradesPanel, "grades");

        mainPanel.add(mySectionsPanel, "my_sections");
        mainPanel.add(gradebookPanel, "gradebook");

        // NEW: Add Admin Panels
        mainPanel.add(adminDashboard, "admin_dashboard");
        mainPanel.add(userManagementPanel, "admin_users");
        mainPanel.add(courseManagementPanel, "admin_courses");
        mainPanel.add(sectionManagementPanel, "admin_sections");

        add(mainPanel);
        setJMenuBar(createMainMenuBar());
    }

    /**
     * Called by LoginDialog after a successful login.
     * *** UPDATED: Handles 'admin' role. ***
     */
    public void onLoginSuccess(String role, String username) {
        String displayName = "User";
        if ("student".equals(role)) {
            displayName = "John Student";
            cardLayout.show(mainPanel, "student_dashboard");
        } else if ("instructor".equals(role)) {
            displayName = "Dr. Sarah Professor";
            cardLayout.show(mainPanel, "my_sections");
        } else if ("admin".equals(role)) { // NEW
            displayName = "Admin User";
            cardLayout.show(mainPanel, "admin_dashboard");
        }

        welcomeLabel.setText("Welcome, " + displayName + "  ");
        this.setVisible(true);
    }

    private JMenuBar createMainMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(Box.createHorizontalGlue());

        welcomeLabel = new JLabel("Welcome!  ");
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.PLAIN, 14f));
        menuBar.add(welcomeLabel);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(fileMenu.getFont().deriveFont(Font.PLAIN, 14f));

        JMenuItem changePasswordItem = new JMenuItem("Change Password");
        changePasswordItem.addActionListener(e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(this);
            dialog.setVisible(true);
        });

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            this.setVisible(false);
            this.dispose();
            showLoginDialog(true);
        });

        fileMenu.add(changePasswordItem);
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        return menuBar;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { /* Fallback */ }

        SwingUtilities.invokeLater(() -> {
            showLoginDialog(false);
        });
    }

    private static void showLoginDialog(boolean isLogout) {
        Main mainApp = new Main();
        LoginDialog loginDialog = new LoginDialog(mainApp);
        if (isLogout) {
            loginDialog.setLogoutMessage("You have been logged out.");
        }
        loginDialog.setVisible(true);
        if (!mainApp.isVisible()) {
            System.exit(0);
        }
    }
}