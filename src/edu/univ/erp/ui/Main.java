package edu.univ.erp.ui;

// ... (all your UI imports)
import edu.univ.erp.ui.auth.LoginDialog;
import edu.univ.erp.ui.auth.ChangePasswordDialog;
import edu.univ.erp.ui.student.*;
import edu.univ.erp.ui.instructor.*;
import edu.univ.erp.ui.admin.*;

// --- BACKEND IMPORTS ---
import edu.univ.erp.auth.UserDAO;
import edu.univ.erp.data.*;
import edu.univ.erp.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * UPDATED: Now uses REAL UserIDs from login.
 */
public class Main extends JFrame {

    // ... (all UI and Backend fields are the same)
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private SettingsDAO settingsDAO;
    private StudentDAO studentDAO;
    private InstructorDAO instructorDAO;
    private AuthService authService;
    private AdminService adminService;
    private StudentService studentService;
    private InstructorService instructorService;
    private TranscriptService transcriptService;
    private String loggedInUserEmail = null;
    private int loggedInUserId = -1;
    private StudentDashboardPanel studentDashboard;
    private CourseCatalogPanel courseCatalog;
    private TimetablePanel timetablePanel;
    private GradesPanel gradesPanel;
    private MySectionsPanel mySectionsPanel;
    private GradebookPanel gradebookPanel;
    private AdminDashboardPanel adminDashboard;
    private UserManagementPanel userManagementPanel;
    private CourseManagementPanel courseManagementPanel;
    private SectionManagementPanel sectionManagementPanel;


    public Main() {
        // ... (window setup)
        setTitle("University ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);

        // --- 1. INSTANTIATE BACKEND (Same as before) ---
        this.userDAO = new UserDAO();
        this.adminDAO = new AdminDAO();
        this.settingsDAO = new SettingsDAO();
        this.studentDAO = new StudentDAO();
        this.instructorDAO = new InstructorDAO();
        this.authService = new AuthService(userDAO, settingsDAO);
        this.adminService = new AdminService(userDAO, adminDAO, settingsDAO);
        this.studentService = new StudentService(studentDAO, settingsDAO);
        this.instructorService = new InstructorService(instructorDAO, settingsDAO);
        this.transcriptService = new TranscriptService(studentDAO);

        // ... (UI Instantiation and Nav Creation is the same) ...
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        Runnable showStudentHome = () -> cardLayout.show(mainPanel, "student_dashboard");
        Runnable showCatalog = () -> cardLayout.show(mainPanel, "catalog");
        Runnable showTimetable = () -> cardLayout.show(mainPanel, "timetable");
        Runnable showGrades = () -> cardLayout.show(mainPanel, "grades");
        Runnable showInstructorHome = () -> cardLayout.show(mainPanel, "my_sections");
        Consumer<Integer> showGradebookForSection = (sectionId) -> {
            try {
                System.out.println("Attempting to load gradebook for section: " + sectionId);
                gradebookPanel.loadGradebook(sectionId); // 1. Try to load data

                System.out.println("Data loaded. Attempting to show 'gradebook' card...");
                cardLayout.show(mainPanel, "gradebook"); // 2. Try to show panel

                System.out.println("Card switch successful.");

            } catch (Exception e) {
                // 3. If anything fails, show an error and print the details
                System.err.println("--- FATAL ERROR: Could not open gradebook! ---");
                e.printStackTrace(); // THIS WILL TELL US THE EXACT BUG

                JOptionPane.showMessageDialog(mainPanel,
                        "Could not open gradebook. An internal error occurred:\n" + e.getMessage(),
                        "Panel Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        };        Runnable showAdminHome = () -> cardLayout.show(mainPanel, "admin_dashboard");
        Runnable showUserManagement = () -> cardLayout.show(mainPanel, "admin_users");
        Runnable showCourseManagement = () -> cardLayout.show(mainPanel, "admin_courses");
        Runnable showSectionManagement = () -> cardLayout.show(mainPanel, "admin_sections");
        studentDashboard = new StudentDashboardPanel(showCatalog, showTimetable, showGrades);
        courseCatalog = new CourseCatalogPanel(showStudentHome, studentService);
        timetablePanel = new TimetablePanel(showStudentHome, studentService);
        gradesPanel = new GradesPanel(showStudentHome, studentService, transcriptService);
        mySectionsPanel = new MySectionsPanel(showGradebookForSection, instructorService);
        gradebookPanel = new GradebookPanel(showInstructorHome, instructorService);
        adminDashboard = new AdminDashboardPanel(showUserManagement, showCourseManagement, showSectionManagement, adminService);
        userManagementPanel = new UserManagementPanel(showAdminHome, adminService);
        courseManagementPanel = new CourseManagementPanel(showAdminHome, adminService);
        sectionManagementPanel = new SectionManagementPanel(showAdminHome, adminService);
        mainPanel.add(studentDashboard, "student_dashboard");
        mainPanel.add(courseCatalog, "catalog");
        mainPanel.add(timetablePanel, "timetable");
        mainPanel.add(gradesPanel, "grades");
        mainPanel.add(mySectionsPanel, "my_sections");
        mainPanel.add(gradebookPanel, "gradebook");
        mainPanel.add(adminDashboard, "admin_dashboard");
        mainPanel.add(userManagementPanel, "admin_users");
        mainPanel.add(courseManagementPanel, "admin_courses");
        mainPanel.add(sectionManagementPanel, "admin_sections");

        add(mainPanel);
        setJMenuBar(createMainMenuBar());
    }

    /**
     * UPDATED: Now accepts the REAL UserID from LoginDialog.
     */
    /**
     * UPDATED: Now uses the DAO to look up the REAL StudentID/InstructorID
     * from the UserID provided by the AuthDB. (TYPO FIXED)
     */
    public void onLoginSuccess(String role, String username, int userId) {

        this.loggedInUserEmail = username;
        this.loggedInUserId = userId; // This is the UserID from AuthDB

        String displayName = "User";
        String dashboardName = "student_dashboard"; // Default

        // --- TODO: This should call a service to get profile info ---
        // e.g., String name = studentService.getStudentName(userId);

        if ("student".equalsIgnoreCase(role)) {

            // --- FIX FOR STUDENT ---
            int studentId = studentDAO.getStudentIdFromUserId(userId);
            if (studentId == -1) {
                JOptionPane.showMessageDialog(this, "Login failed: No student profile found for this user.", "Login Error", JOptionPane.ERROR_MESSAGE);
                // Go back to login
                // Note: You might need to make showLoginDialog non-static to call this
                // For now, we'll just prevent login.
                return;
            }

            displayName = "Student"; // Placeholder
            studentService.setCurrentStudent(studentId); // <-- Set the REAL StudentID
            dashboardName = "student_dashboard";

        } else if ("instructor".equalsIgnoreCase(role)) {

            // --- FIX FOR INSTRUCTOR ---
            int instructorId = instructorDAO.getInstructorIdFromUserId(userId);
            // The typo was here
            if (instructorId == -1) {
                JOptionPane.showMessageDialog(this, "Login failed: No instructor profile found for this user.", "Login Error", JOptionPane.ERROR_MESSAGE);
                // Go back to login
                return;
            }

            displayName = "Instructor"; // Placeholder
            instructorService.setCurrentInstructor(instructorId); // <-- Set the REAL InstructorID
            dashboardName = "my_sections"; // Instructor dashboard

        } else if ("admin".equalsIgnoreCase(role)) {
            displayName = "Admin User";
            dashboardName = "admin_dashboard";
        }

        // TODO: Use the ID to fetch the user's FullName from the StudentDB
        // and set it on the welcomeLabel.
        // String realName = ...
        // welcomeLabel.setText("Welcome, " + realName + "  ");
        welcomeLabel.setText("Welcome, " + displayName + "  "); // Using placeholder

        cardLayout.show(mainPanel, dashboardName);
        this.setVisible(true);
    }    // ... (createMainMenuBar is the same, it already works) ...
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
            if (loggedInUserEmail == null) {
                JOptionPane.showMessageDialog(this, "You must be logged in.");
                return;
            }
            ChangePasswordDialog dialog = new ChangePasswordDialog(this, authService, loggedInUserEmail);
            dialog.setVisible(true);
        });

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            this.loggedInUserEmail = null;
            this.loggedInUserId = -1;
            this.setVisible(false);
            this.dispose();
            showLoginDialog(true);
        });

        fileMenu.add(changePasswordItem);
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        return menuBar;
    }

    // ... (main and showLoginDialog are the same) ...
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { /* Fallback */ }
        SwingUtilities.invokeLater(() -> showLoginDialog(false));
    }

    private static void showLoginDialog(boolean isLogout) {
        Main mainApp = new Main();
        LoginDialog loginDialog = new LoginDialog(mainApp, mainApp.authService);
        if (isLogout) {
            loginDialog.setLogoutMessage("You have been logged out.");
        }
        loginDialog.setVisible(true);
        if (!mainApp.isVisible()) {
            System.exit(0);
        }
    }
}