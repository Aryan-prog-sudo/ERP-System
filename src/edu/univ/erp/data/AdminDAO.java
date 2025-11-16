package edu.univ.erp.data;

// --- NEW IMPORTS ---
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // <-- NEW IMPORT
import java.util.ArrayList; // <-- NEW IMPORT
import java.util.List; // <-- NEW IMPORT
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
//This class handles the connection to the StudentDB

public class AdminDAO {
    public boolean CreateStudentProfile(Connection StudentDBConnection, int UserID, String FullName, String Email) throws Exception{
        String SQL = "INSERT INTO Students (UserID, FullName, Email) VALUES (?,?,?)";
        try(PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            Statement.setInt(1,UserID);
            Statement.setString(2, FullName);
            Statement.setString(3, Email);
            return Statement.executeUpdate()>0; //This returns true if row was inserted in the table Students
        }
    }

    public boolean CreateInstructorProfile(Connection StudentDBConnection, int UserID, String FullName, String Email) throws Exception{
        String SQL = "INSERT INTO Instructors (UserID, FullName, Email, Department) VALUES (?, ?, ?, ?)";
        try(PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            Statement.setInt(1,UserID);
            Statement.setString(2, FullName);
            Statement.setString(3, Email);
            Statement.setString(4, "Not Assigned");
            return Statement.executeUpdate()>0;
        }
    }

    public boolean CreateCourse(String Code, String Title, int Credits){
        String SQL = "INSERT INTO Course (CourseCode, CourseTitle, Credits) VALUES (?, ?, ?)";
        try(Connection StudentDBConnection = edu.univ.erp.util.DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            Statement.setString(1, Code);
            Statement.setString(2, Title);
            Statement.setInt(3, Credits);
            return Statement.executeUpdate()>0;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT CourseID, CourseCode, CourseTitle, Credits FROM Course";

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseCode"),
                        rs.getString("CourseTitle"),
                        rs.getInt("Credits")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    // --- NEW METHOD 2 ---
    /**
     * Fetches all instructors from the DB to populate a JComboBox.
     */
    public List<Instructor> getAllInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT InstructorID, FullName, Email FROM Instructors";

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                instructors.add(new Instructor(
                        rs.getInt("InstructorID"),
                        rs.getString("FullName"),
                        rs.getString("Email")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instructors;
    }

    public boolean CreateSection(int courseId, int instructorId, String sectionNum, String time, int capacity) {
        String sql = "INSERT INTO Sections (CourseID, InstructorID, SectionNumber, TimeSlot, Capacity, EnrolledCount) " +
                "VALUES (?, ?, ?, ?, ?, 0)"; // Default EnrolledCount to 0

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, sectionNum);
            stmt.setString(4, time);
            stmt.setInt(5, capacity);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


