package edu.univ.erp.service;

import edu.univ.erp.auth.UserDAO;
import edu.univ.erp.auth.AuthUserInfo; // <-- NEW IMPORT
import edu.univ.erp.data.AdminDAO;
import edu.univ.erp.data.ProfileInfo; // <-- NEW IMPORT
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.domain.UserView; // <-- NEW IMPORT
import edu.univ.erp.util.DatabaseUtil;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;

import java.sql.Connection;
import java.util.ArrayList; // <-- NEW IMPORT
import java.util.HashMap; // <-- NEW IMPORT
import java.util.List;
import java.util.Map; // <-- NEW IMPORT
import java.util.logging.Logger;

//This class basically controls all the logic inside the admin pages
public class AdminService {
    //The attributes needed by the Admin
    private static final Logger logger = Logger.getLogger(AdminService.class.getName());
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private SettingsDAO settingsDAO;

    public AdminService(UserDAO userDAO, AdminDAO adminDAO, SettingsDAO settingsDAO) {
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
        this.settingsDAO = settingsDAO;
    }

    //Method that checks the maintenance mode in the settingsDAO ie the settings table in the StudentDB
    public boolean getMaintenanceModeState() {
        return settingsDAO.IsMaintenanceModeOn();
    }

    //This method is used to change the maintenance mode
    public boolean toggleMaintenanceMode(boolean newState) {
        logger.info("Admin setting Maintenance Mode to: " + newState);
        return settingsDAO.SetMaintenanceMode(newState);
    }

    //This method is used to create a new user for the ERP system
    //This privilege of creating new user is given only to the Admin
    //This would be called by UserManagementPanel
    public boolean createNewUser(String FullName, String Email, String Role, String defaultPassword) {
        Connection AuthDBConnection = null;
        Connection StudentDBConnection = null;
        try {
            AuthDBConnection = DatabaseUtil.GetAuthConnection();
            StudentDBConnection = DatabaseUtil.GetStudentConnection();
            AuthDBConnection.setAutoCommit(false);
            StudentDBConnection.setAutoCommit(false);
            int newUserId = userDAO.CreateAuthDBUser(AuthDBConnection, Email, defaultPassword, Role);
            if ("Student".equals(Role)) {
                adminDAO.CreateStudentProfile(StudentDBConnection, newUserId, FullName, Email);
                System.out.println("New Student User created");
            }
            else if ("Instructor".equals(Role)) {
                adminDAO.CreateInstructorProfile(StudentDBConnection, newUserId, FullName, Email);
                System.out.println("New Instructor User Created");
            }
            AuthDBConnection.commit();
            StudentDBConnection.commit();
            logger.info("Admin successfully created new user: " + Email + ", Role: " + Role);
            return true;
            //This returns true if the user creation is success
        }
        catch (Exception e) {
            logger.severe("Transaction FAILED for createNewUser: " + e.getMessage());
            e.printStackTrace();
            //If ANY error happens, roll back
            System.out.println("User couldn't be added");
            try {
                if (AuthDBConnection != null){
                    AuthDBConnection.rollback();
                }
                if (StudentDBConnection != null){
                    StudentDBConnection.rollback();
                }
            }
            catch (Exception re) {
                re.printStackTrace();
            }
            return false;
            //Return False on Failiure
        }
        finally {
            //Always close connections for safety to the database
            try {
                if (AuthDBConnection != null){
                    AuthDBConnection.close();
                }
                if (StudentDBConnection != null){
                    StudentDBConnection.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //This method creates a new course and adds it to the database
    //It basically calls the CreateCourse method in AdminDAO
    //It would be called in the CourseManagementPanel
    public boolean createNewCourse(String Code, String Title, String CreditsString) {
        try{
            int Credits = Integer.parseInt(CreditsString);
            if (Credits <= 0){
                return false;
                //Credits cannot be less than 0 ie negative
            }
            logger.info("Admin creating new course: " + Code);
            System.out.println("Course"+ Code+ "Created successfully");
            return adminDAO.CreateCourse(Code, Title, Credits);
        }
        catch (NumberFormatException e) {
            return false;
            //Credits was not a number
        }
    }

    //This just lists down all the courses for the dropdown panel
    public List<Course> getAllCourses() {
        return adminDAO.getAllCourses();
    }

    //This just lists down all the instructors for the dropdown panel
    public List<Instructor> getAllInstructors() {
        return adminDAO.getAllInstructors();
    }


    //This method created a new section in the table of the database
    //It basically calls the CreateSection method in the AdminDAO
    //This would be used in the SectionManagementPanel
    public boolean createNewSection(Course course, Instructor instructor, String sectionNum, String time, String capacityStr) {
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0 || course == null || instructor == null || sectionNum.isEmpty() || time.isEmpty()) {
                return false;
                //Checks all the necessary conditions
            }
            logger.info("Admin creating new section for: " + course.courseCode());
            return adminDAO.CreateSection(course.courseId(), instructor.instructorId(), sectionNum, time, capacity);
        }
        catch (NumberFormatException e) {
            return false;
            //Capacity was not a number
        }
    }

    /**
     * NEW METHOD: Gets all users from both databases and merges them.
     * This fixes the "Cannot resolve AuthUserInfo" error.
     */
    public List<UserView> getAllUsers() {
        // 1. Get all profiles from StudentDB
        List<ProfileInfo> students = adminDAO.GetAllStudentProfiles();
        List<ProfileInfo> instructors = adminDAO.GetAllInstructorProfiles();

        // 2. Combine them into a fast-lookup map
        Map<Integer, String> profileMap = new HashMap<>();
        for (ProfileInfo p : students) {
            profileMap.put(p.userId(), p.fullName());
        }
        for (ProfileInfo p : instructors) {
            profileMap.put(p.userId(), p.fullName());
        }

        // 3. Get all users from AuthDB
        List<AuthUserInfo> authUsers = userDAO.GetAllAuthUsers();

        // 4. Merge the lists
        List<UserView> allUsers = new ArrayList<>();
        for (AuthUserInfo authUser : authUsers) {
            // Get the name from the map, or use a default
            String fullName = profileMap.get(authUser.userId());

            if (fullName == null && authUser.role().equalsIgnoreCase("Admin")) {
                fullName = "Admin User"; // Default name for Admins
            } else if (fullName == null) {
                fullName = "N/A (Profile Error)"; // Should not happen
            }

            allUsers.add(new UserView(fullName, authUser.email(), authUser.role()));
        }
        return allUsers;
    }
}