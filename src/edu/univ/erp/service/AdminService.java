package edu.univ.erp.service;

import edu.univ.erp.auth.UserDAO;
import edu.univ.erp.data.AdminDAO;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.util.DatabaseUtil;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles business logic for the Admin role.
 * This is the "brain" for all Admin panels.
 */
public class AdminService {

    private static final Logger logger = Logger.getLogger(AdminService.class.getName());

    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private SettingsDAO settingsDAO;

    // Constructor now accepts all three DAOs
    public AdminService(UserDAO userDAO, AdminDAO adminDAO, SettingsDAO settingsDAO) {
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
        this.settingsDAO = settingsDAO;
    }

    // --- METHODS FOR MAINTENANCE MODE ---
    public boolean getMaintenanceModeState() {
        return settingsDAO.IsMaintenanceModeOn();
    }

    public boolean toggleMaintenanceMode(boolean newState) {
        logger.info("Admin setting Maintenance Mode to: " + newState);
        return settingsDAO.SetMaintenanceMode(newState);
    }

    /**
     * Creates a new user in BOTH databases as a single transaction.
     */
    public boolean createNewUser(String fullName, String email, String role, String defaultPassword) {

        Connection authConn = null;
        Connection erpConn = null;

        try {
            // 1. Get connections to BOTH databases
            authConn = DatabaseUtil.GetAuthConnection();
            erpConn = DatabaseUtil.GetStudentConnection(); // Using your method name

            // 2. Start the transaction
            authConn.setAutoCommit(false);
            erpConn.setAutoCommit(false);

            // 3. STEP 1: Create user in 'AuthDB'
            int newUserId = userDAO.CreateAuthDBUser(authConn, email, defaultPassword, role);

            // 4. STEP 2: Create profile in 'StudentDB'
            if ("Student".equals(role)) {
                adminDAO.CreateStudentProfile(erpConn, newUserId, fullName, email);
            } else if ("Instructor".equals(role)) {
                adminDAO.CreateInstructorProfile(erpConn, newUserId, fullName, email);
            }

            // 5. Commit (save) changes to BOTH databases
            authConn.commit();
            erpConn.commit();

            logger.info("Admin successfully created new user: " + email + ", Role: " + role);
            return true; // Success!

        } catch (Exception e) {
            logger.severe("Transaction FAILED for createNewUser: " + e.getMessage());
            e.printStackTrace();
            // 6. If ANY error happens, roll back
            try {
                if (authConn != null) authConn.rollback();
                if (erpConn != null) erpConn.rollback();
            } catch (Exception re) {
                re.printStackTrace();
            }
            return false; // Failure

        } finally {
            // 7. Always close connections
            try {
                if (authConn != null) authConn.close();
                if (erpConn != null) erpConn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new course. Called by the CourseManagementPanel.
     */
    public boolean createNewCourse(String code, String title, String creditsStr) {
        try {
            int credits = Integer.parseInt(creditsStr);
            if (credits <= 0) return false; // Validation

            logger.info("Admin creating new course: " + code);
            // Use your method name: CreateCourse
            return adminDAO.CreateCourse(code, title, credits);

        } catch (NumberFormatException e) {
            return false; // Credits was not a number
        }
    }

    /**
     * Gets all courses for the UI dropdown.
     */
    public List<Course> getAllCourses() {
        return adminDAO.getAllCourses();
    }

    /**
     * Gets all instructors for the UI dropdown.
     */
    public List<Instructor> getAllInstructors() {
        return adminDAO.getAllInstructors();
    }

    /**
     * Creates a new section.
     * This is the one and only copy of this method.
     */
    public boolean createNewSection(Course course, Instructor instructor, String sectionNum, String time, String capacityStr) {

        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0 || course == null || instructor == null || sectionNum.isEmpty() || time.isEmpty()) {
                return false; // Validation
            }

            logger.info("Admin creating new section for: " + course.courseCode());

            // This now calls your AdminDAO.CreateSection method
            return adminDAO.CreateSection(
                    course.courseId(),
                    instructor.instructorId(),
                    sectionNum,
                    time,
                    capacity
            );

        } catch (NumberFormatException e) {
            return false; // Capacity was not a number
        }
    }
}