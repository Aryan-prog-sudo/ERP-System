package edu.univ.erp.service;

import edu.univ.erp.auth.UserDAO;
import edu.univ.erp.auth.AuthUserInfo;
import edu.univ.erp.data.AdminDAO;
import edu.univ.erp.data.ProfileInfo;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.domain.UserView;
import edu.univ.erp.util.DatabaseUtil;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.AdminSectionView;
import edu.univ.erp.data.NotificationDAO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

//This class basically controls all the logic inside the admin pages
public class AdminService {
    //The attributes needed by the Admin
    //These attributes are instances of all the DAOs that the Admin would use
    private static final Logger logger = Logger.getLogger(AdminService.class.getName());
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private SettingsDAO settingsDAO;
    private NotificationDAO notificationDAO;

    public AdminService(UserDAO userDAO, AdminDAO adminDAO, SettingsDAO settingsDAO, NotificationDAO notificationDAO){
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
        this.settingsDAO = settingsDAO;
        this.notificationDAO = notificationDAO;
    }

    //Method that checks the maintenance mode in the settingsDAO ie the settings table in the StudentDB
    public boolean getMaintenanceModeState() {
        return settingsDAO.IsMaintenanceModeOn();
    }

    //This method is used to change the maintenance mode
    //NewState means the new state of the maintenance mode (ON or OFF)
    public boolean toggleMaintenanceMode(boolean NewState) {
        logger.info("Admin setting Maintenance Mode to: " + NewState);
        boolean Success = settingsDAO.SetMaintenanceMode(NewState);
        if(Success) {//Send Notification
            String Status = NewState ? "ON" : "OFF";
            notificationDAO.AddNotification("Maintenance Mode: " + Status);
        }
        return Success;
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
            //If error happens, roll back
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
            boolean Success = adminDAO.CreateCourse(Code, Title, Credits);
            if(Success) {//Send Notification
                notificationDAO.AddNotification("New Course Added" + Code + "-" + Title);
            }
            return Success;
        }
        catch (NumberFormatException e) {
            return false;
            //Credits was not a number
        }
    }

    //This just lists down all the courses for the dropdown panel
    public List<Course> getAllCourses() {
        return adminDAO.GetAllCourses();
    }


    //This just lists down all the instructors for the dropdown panel
    public List<Instructor> getAllInstructors() {
        return adminDAO.GetAllInstructors();
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
            boolean Success = adminDAO.CreateSection(course.courseId(), instructor.instructorId(), sectionNum, time, capacity);
            if(Success){//Send Notification
                notificationDAO.AddNotification("New Section Added: "+ course.courseCode()+"("+sectionNum+")");
            }
            return Success;
        }
        catch (NumberFormatException e) {
            return false;
            //Capacity was not a number
        }
    }


    //This method loads all the users from the Database and is used mainly in the display for users in UserManagementPanel
    //It returns an ArrayList of UserView record that contains the FullName, Email and the Role
    //Gets all the students and instructor from the methods that were defined in AdminDAO
    //Store both of them in a map(HashMap) that stores the users with a mapping from userID to name
    //Currently the name of each Admin is default as the Admin User
    //To change that we would have to add another table in database
    public List<UserView> GetAllUsers() {
        List<ProfileInfo> Students = adminDAO.GetAllStudentProfiles();
        List<ProfileInfo> Instructors = adminDAO.GetAllInstructorProfiles();
        Map<Integer, String> profileMap = new HashMap<>();
        for (ProfileInfo Profile : Students) {
            profileMap.put(Profile.userId(), Profile.fullName());
        }
        for (ProfileInfo Profile : Instructors) {
            profileMap.put(Profile.userId(), Profile.fullName());
        }

        List<AuthUserInfo> authUsers = userDAO.GetAllAuthUsers();

        List<UserView> AllUsers = new ArrayList<>();
        for (AuthUserInfo AuthUser : authUsers) {
            String fullName = profileMap.get(AuthUser.userId());
            if (fullName == null && AuthUser.role().equalsIgnoreCase("Admin")) {
                fullName = "Admin User";
                //Default name for Admins
            }
            else if (fullName == null) {
                fullName = "N/A (Profile Error)";
                //Should not happen as only the admin need not have name
            }
            AllUsers.add(new UserView(fullName, AuthUser.email(), AuthUser.role()));
        }
        return AllUsers;
    }


    //This method returns all the sections for the
    public List<AdminSectionView> GetAllSectionsForView() {
        return adminDAO.getAllSectionsForView();
    }


    //This method is basically wrapper for the setDeadline in the DAO
    public boolean SetSystemDeadline(String DateString){
        return settingsDAO.SetDeadline(DateString);
    }


    public String GetSystemDeadline(){
        return settingsDAO.GetDeadline();
    }
}