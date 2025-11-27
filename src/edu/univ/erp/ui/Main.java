package edu.univ.erp.ui;

//UI Imports
import edu.univ.erp.ui.auth.LoginDialog;
import edu.univ.erp.ui.auth.ChangePasswordDialog;
import edu.univ.erp.ui.student.*;
import edu.univ.erp.ui.instructor.*;
import edu.univ.erp.ui.admin.*;

//Backend Imports
import edu.univ.erp.auth.UserDAO;
import edu.univ.erp.data.*;
import edu.univ.erp.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List; // For notifications
import java.util.function.Consumer;

public class Main extends JFrame {
    //Layout
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel welcomeLabel;

    //DAOs
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private SettingsDAO settingsDAO;
    private StudentDAO studentDAO;
    private InstructorDAO instructorDAO;
    private NotificationDAO notificationDAO; // <-- NEW

    // Services
    private AuthService authService;
    private AdminService adminService;
    private StudentService studentService;
    private InstructorService instructorService;
    private TranscriptService transcriptService;

    //States
    private String loggedInUserEmail = null;
    private int loggedInUserId = -1;

    //Panels
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
        //Window setup
        setTitle("University ERP"); //Title of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Basically exit the program on closing the swing window
        setMinimumSize(new Dimension(1024, 768)); //Minimum size of the window
        setLocationRelativeTo(null); //Align window to the center

        //Create instances of all the DAO
        this.userDAO = new UserDAO();
        this.adminDAO = new AdminDAO();
        this.settingsDAO = new SettingsDAO();
        this.studentDAO = new StudentDAO();
        this.instructorDAO = new InstructorDAO();
        this.notificationDAO = new NotificationDAO(); // <-- NEW

        //Create instances of all the Services
        this.authService = new AuthService(userDAO, settingsDAO);
        this.adminService = new AdminService(userDAO, adminDAO, settingsDAO, notificationDAO);
        this.studentService = new StudentService(studentDAO, settingsDAO);
        this.instructorService = new InstructorService(instructorDAO, settingsDAO);
        this.transcriptService = new TranscriptService(studentDAO);

        //UI Setup
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        //Navigation Commands
        Runnable showStudentHome = () -> cardLayout.show(mainPanel, "student_dashboard");
        Runnable showCatalog = () -> cardLayout.show(mainPanel, "catalog");
        Runnable showTimetable = () -> cardLayout.show(mainPanel, "timetable");
        Runnable showGrades = () -> cardLayout.show(mainPanel, "grades");
        Runnable showInstructorHome = () -> cardLayout.show(mainPanel, "my_sections");

        Consumer<Integer> showGradebookForSection = (sectionId) -> {
            try {
                System.out.println("Attempting to load gradebook for section: " + sectionId);
                gradebookPanel.loadGradebook(sectionId);
                cardLayout.show(mainPanel, "gradebook");
            }
            catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(mainPanel,
                        "Could not open gradebook. An internal error occurred:\n" + e.getMessage(),
                        "Panel Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        Runnable showAdminHome = () -> cardLayout.show(mainPanel, "admin_dashboard");
        Runnable showUserManagement = () -> cardLayout.show(mainPanel, "admin_users");
        Runnable showCourseManagement = () -> cardLayout.show(mainPanel, "admin_courses");
        Runnable showSectionManagement = () -> cardLayout.show(mainPanel, "admin_sections");

        //Initialize Panels
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

        //Add Panels to CardLayout
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
        //Create the top menu bar(Notification and file)
        createMenuBar();
    }

    //This handles the operations on logging in and
    public void onLoginSuccess(String role, String username, int userId) {
        this.loggedInUserEmail = username;
        this.loggedInUserId = userId; // UserID from AuthDB

        String displayName = "User";
        String dashboardName = "student_dashboard";

        if ("student".equalsIgnoreCase(role)) {
            // Look up Real Student ID
            int studentId = studentDAO.getStudentIdFromUserId(userId);
            if (studentId == -1) {
                JOptionPane.showMessageDialog(this, "Login failed: No student profile found.", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            displayName = "Student";
            studentService.setCurrentStudent(studentId);
            dashboardName = "student_dashboard";

        } else if ("instructor".equalsIgnoreCase(role)) {
            // Look up Real Instructor ID
            int instructorId = instructorDAO.getInstructorIdFromUserId(userId);
            if (instructorId == -1) {
                JOptionPane.showMessageDialog(this, "Login failed: No instructor profile found.", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            displayName = "Instructor";
            instructorService.setCurrentInstructor(instructorId);
            dashboardName = "my_sections";

        } else if ("admin".equalsIgnoreCase(role)) {
            displayName = "Admin User";
            dashboardName = "admin_dashboard";
        }

        welcomeLabel.setText("Welcome, " + displayName + "  ");
        cardLayout.show(mainPanel, dashboardName);
        this.setVisible(true);
    }


    //This creates the menu bar on the top of all pages with "file" adn notification
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("SansSerif", Font.PLAIN, 14));

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
        logoutItem.addActionListener(e -> logout());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(changePasswordItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        //Space in the middle
        menuBar.add(Box.createHorizontalGlue());

        // Welcome Label (Center/Rightish)
        welcomeLabel = new JLabel("Welcome!  ");
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        menuBar.add(welcomeLabel);

        // Spacer before notifications
        menuBar.add(Box.createRigidArea(new Dimension(20, 0)));

        //Right Side: Notifications Button
        JButton notifButton = new JButton("🔔 Notifications");
        notifButton.setBorderPainted(false);
        notifButton.setContentAreaFilled(false);
        notifButton.setFocusPainted(false);
        notifButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        notifButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        notifButton.addActionListener(e -> showNotifications(notifButton));

        menuBar.add(notifButton);
        menuBar.add(Box.createRigidArea(new Dimension(15, 0))); // Padding

        setJMenuBar(menuBar);
    }

    //Notification panel
    private void showNotifications(JButton source) {
        JPopupMenu popup = new JPopupMenu();
        popup.setPreferredSize(new Dimension(350, 250)); // Size of popup

        List<String> msgs = notificationDAO.GetRecentNotifications();

        if (msgs.isEmpty()) {
            JMenuItem item = new JMenuItem("No new notifications");
            item.setEnabled(false);
            popup.add(item);
        }
        else {
            JLabel header = new JLabel("  Recent Updates");
            header.setFont(new Font("SansSerif", Font.BOLD, 12));
            header.setForeground(Color.GRAY);
            header.setBorder(new EmptyBorder(5, 0, 5, 0));
            popup.add(header);
            popup.addSeparator();
            for (String msg : msgs) {
                // HTML for word wrapping
                JMenuItem item = new JMenuItem("<html><body style='width: 280px'>" + msg + "</body></html>");
                item.setBackground(Color.WHITE);
                popup.add(item);
            }
        }
        popup.show(source, 0, source.getHeight());
    }


    private void logout() {
        this.loggedInUserEmail = null;
        this.loggedInUserId = -1;
        this.setVisible(false);
        this.dispose();
        showLoginDialog(true);
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
        SwingUtilities.invokeLater(() -> showLoginDialog(false));
    }


    private static void showLoginDialog(boolean isLogout) {
        Main mainApp = new Main();
        LoginDialog loginDialog = new LoginDialog(mainApp, mainApp.authService);
        if (isLogout) {
            loginDialog.setLogoutMessage("You have been logged out.");
        }
        loginDialog.setVisible(true);

        //If dialog closes without login, exit app
        if (!mainApp.isVisible()) {
            System.exit(0);
        }
    }
}