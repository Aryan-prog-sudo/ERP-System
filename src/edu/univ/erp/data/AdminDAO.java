package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.util.DatabaseUtil;
import edu.univ.erp.domain.AdminSectionView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


//This class handles the connection to the StudentDB
//These connections would mainly be used by the user Admin thus the name of the class
public class AdminDAO {
    //This method adds the student profile to the students table in the StudentDB
    //This method uses the INSERT Query to insert into the table
    //This method is called in the createNewUser in AdminService while inserting a new student
    public boolean CreateStudentProfile(Connection StudentDBConnection, int UserID, String FullName, String Email) throws Exception{
        String SQL = "INSERT INTO Students (UserID, FullName, Email) VALUES (?,?,?)";
        try(PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            Statement.setInt(1,UserID);
            Statement.setString(2, FullName);
            Statement.setString(3, Email);
            boolean Is_Inserted = Statement.executeUpdate()>0;
            if(Is_Inserted){
                System.out.println("Student: "+FullName+" Inserted in the Students table of StudentDB");
            }
            else{
                System.out.println("Student: "+ FullName+ " Not insterted in student table");
                System.out.println("Method: CreateStudentProfile, Class: AdminDAO");
            }
            return Is_Inserted;
            //This returns true if row was inserted in the table Students
        }
    }


    //This method adds the instructor profile to the instructor table in the StudentDB
    public boolean CreateInstructorProfile(Connection StudentDBConnection, int UserID, String FullName, String Email) throws Exception {
        //SQL Query no longer includes the Department column
        String SQL = "INSERT INTO Instructors (UserID, FullName, Email) VALUES (?, ?, ?)";
        try (PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)) {
            Statement.setInt(1, UserID);
            Statement.setString(2, FullName);
            Statement.setString(3, Email);
            // The line for "Department" has been removed
            return Statement.executeUpdate() > 0;
        }
    }

    //This adds a new course to the course table in the StudentDB
    public boolean CreateCourse(String CourseCode, String Title, int Credits){
        String SQL = "INSERT INTO Course (CourseCode, CourseTitle, Credits) VALUES (?, ?, ?)";
        try(Connection StudentDBConnection = edu.univ.erp.util.DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            Statement.setString(1, CourseCode);
            Statement.setString(2, Title);
            Statement.setInt(3, Credits);
            boolean Is_Inserted = Statement.executeUpdate()>0;
            if(Is_Inserted){
                System.out.println("Course: "+ CourseCode+ " Inserted in the course table");
            }
            else{
                System.out.println("Course: "+ CourseCode+ " Not inserted in the table");
                System.out.println("Method: CreateCourse, Class: AdminDAO");
            }
            return Is_Inserted;
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Some failure occurred in CreateCourse");
            return false;
        }
    }


    //This method creates a new section into the section table in the StudentDB
    public boolean CreateSection(int CourseID, int InstructorID, String SectionNum, String Time, int Capacity) {
        String sql = "INSERT INTO Sections (CourseID, InstructorID, SectionNumber, TimeSlot, Capacity, EnrolledCount) " + "VALUES (?, ?, ?, ?, ?, 0)"; // Default EnrolledCount to 0
        try (Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(sql)) {
            Statement.setInt(1, CourseID);
            Statement.setInt(2, InstructorID);
            Statement.setString(3, SectionNum);
            Statement.setString(4, Time);
            Statement.setInt(5, Capacity);
            boolean Is_Inserted = Statement.executeUpdate()>0;
            if(Is_Inserted){
                System.out.println("Section: "+CourseID+"-"+SectionNum +" Inserted in the section table");
            }
            else{
                System.out.println("Section: "+CourseID+"-"+SectionNum+" Not Inserted in the table");
                System.out.println("Method: CreateSection, Class: AdminDAO");
            }
            return Is_Inserted;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Some failute occures in CreateSection");
            return false;
        }
    }


    //This function basically lists all the courses in the Courses table of the StudentDB
    //This is used to list all the courses somewhere in the dropdown of sectionManagementPanel
    //The ArrayList is of Course that has same values as the columns of the course table in StudentDB
    //But it is not directly in the dropdown, it is first used in AdminService
    public List<Course> GetAllCourses() {
        List<Course> Courses = new ArrayList<>();
        String SQL = "SELECT CourseID, CourseCode, CourseTitle, Credits FROM Course";
        try (Connection conn = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = conn.prepareStatement(SQL); ResultSet Result = Statement.executeQuery()) {
            while (Result.next()) {
                Courses.add(new Course(Result.getInt("CourseID"), Result.getString("CourseCode"), Result.getString("CourseTitle"), Result.getInt("Credits")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in Method: GetAllCourses, Method: AdminDAO");
        }
        return Courses;
    }


    //This function basically returns an ArrayList of all the instructors that is taken from the Instructors
    //It is also used in the dropdown of the SectionManagementPanel but not directly, it is first called in the AdminService
    public List<Instructor> GetAllInstructors() {
        List<Instructor> Instructors = new ArrayList<>();
        String SQL = "SELECT InstructorID, FullName, Email FROM Instructors";
        try (Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL); ResultSet Result = Statement.executeQuery()) {
            while (Result.next()) {
                Instructors.add(new Instructor(Result.getInt("InstructorID"), Result.getString("FullName"), Result.getString("Email")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in Method: GetAllInstructors");
        }
        return Instructors;
    }


    //This method returns the StudentProfiles of the all students in the Students table in the StudentDB
    //The record ProfileInfo basically contains all the fields that are the same as the columns of that table
    public List<ProfileInfo> GetAllStudentProfiles() {
        List<ProfileInfo> Profiles = new ArrayList<>();
        String sql = "SELECT UserID, FullName FROM Students";
        try (Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(sql); ResultSet Result = Statement.executeQuery()) {
            while (Result.next()) {
                Profiles.add(new ProfileInfo(Result.getInt("UserID"), Result.getString("FullName")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Profiles;
    }


    //This does the same as the above method but for instructors
    public List<ProfileInfo> GetAllInstructorProfiles() {
        List<ProfileInfo> Profiles = new ArrayList<>();
        String SQL = "SELECT UserID, FullName FROM Instructors";
        try (Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL); ResultSet Result = Statement.executeQuery()) {
            while (Result.next()) {
                Profiles.add(new ProfileInfo(Result.getInt("UserID"), Result.getString("FullName")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Profiles;
    }


    //This method is used to display the section on the admin SectionManagementPanel
    //It returns all the sections as arraylist of record AdminSectionView
    public List<AdminSectionView> getAllSectionsForView() {
        List<AdminSectionView> sections = new ArrayList<>();
        String sql = """
            SELECT s.SectionID, c.CourseCode, s.SectionNumber, s.TimeSlot, s.EnrolledCount, s.Capacity, i.FullName
            FROM Sections s
            JOIN Course c ON s.CourseID = c.CourseID
            LEFT JOIN Instructors i ON s.InstructorID = i.InstructorID
            ORDER BY c.CourseCode, s.SectionNumber
            """;
        //This query selects items from multiple tables in the database
        //It selects the SectionID, SectionNumber, TimeSlot, EnrollmentCount and Capacity from the sections Table (s)
        //It selects Course code from the course table and FullName of instructor form the instructor table
        try (Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(sql); ResultSet Result = Statement.executeQuery()) {
            while (Result.next()) {
                sections.add(new AdminSectionView(
                        Result.getInt("SectionID"),
                        Result.getString("CourseCode"),
                        Result.getString("SectionNumber"),
                        Result.getString("TimeSlot"),
                        Result.getInt("EnrolledCount"),
                        Result.getInt("Capacity"),
                        Result.getString("FullName") != null ? Result.getString("FullName") : "TBA"
                ));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sections;
    }


    //Removes a section from the database
    //Passes the delete query to the section table
    public boolean DeleteSection(int SectionID){
        String SQL = "DELETE FROM Sections WHERE SectionID = ?";
        try(Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            Statement.setInt(1, SectionID);
            return Statement.executeUpdate()>0;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


