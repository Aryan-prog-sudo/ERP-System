package edu.univ.erp.data;

import edu.univ.erp.domain.EnrolledSection;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.SectionView;
import edu.univ.erp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//This class handes all the operations for the Student User in the StudentDBw
public class StudentDAO {
    //This method fetches all the sections for the course catalog
    //It also checks if the student is enrolled in the course
    public List<SectionView> getAvailableSections(int studentId) {
        List<SectionView> sections = new ArrayList<>();
        // This is a complex query that joins 4 tables
        String sql = """
            SELECT 
                s.SectionID, c.CourseCode, c.CourseTitle, c.Credits, i.FullName, s.TimeSlot, s.EnrolledCount, s.Capacity,
                (SELECT COUNT(*) FROM Enrollments e WHERE e.SectionID = s.SectionID AND e.StudentID = ?) AS IsEnrolled
            FROM Sections s
            JOIN Course c ON s.CourseID = c.CourseID
            LEFT JOIN Instructors i ON s.InstructorID = i.InstructorID
            """;
        //Select all the details about the sections using the Enrollment table
        //The enrollment table contains all the enrollments ie a mapping of studentId with the SectionId they are enrolled in
        try (Connection conn = DatabaseUtil.GetStudentConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(new SectionView(
                            rs.getInt("SectionID"),
                            rs.getString("CourseCode"),
                            rs.getString("CourseTitle"),
                            rs.getInt("Credits"),
                            rs.getString("FullName") != null ? rs.getString("FullName") : "TBA",
                            rs.getString("TimeSlot"),
                            rs.getInt("EnrolledCount"),
                            rs.getInt("Capacity"),
                            rs.getInt("IsEnrolled") > 0 // Converts count (0 or 1) to boolean
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sections;
    }


    //This fetches the timetable of the student
    public List<EnrolledSection> getTimetable(int studentId) {
        List<EnrolledSection> schedule = new ArrayList<>();
        // UPDATED SQL: Removed any reference to Location
        String sql = """
            SELECT s.SectionID, c.CourseCode, c.CourseTitle, c.Credits, i.FullName, s.TimeSlot
            FROM Enrollments e
            JOIN Sections s ON e.SectionID = s.SectionID
            JOIN Course c ON s.CourseID = c.CourseID
            LEFT JOIN Instructors i ON s.InstructorID = i.InstructorID
            WHERE e.StudentID = ?
            ORDER BY s.TimeSlot
            """;
        try (Connection conn = DatabaseUtil.GetStudentConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schedule.add(new EnrolledSection(
                            rs.getInt("SectionID"),
                            rs.getString("CourseCode"),
                            rs.getString("CourseTitle"),
                            rs.getInt("Credits"),
                            rs.getString("FullName") != null ? rs.getString("FullName") : "TBA",
                            rs.getString("TimeSlot")
                            // REMOVED: Location argument
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedule;
    }


    //This code fetches the grades of the student
    public List<Grade> getGrades(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = """
            SELECT 
                c.CourseCode, c.CourseTitle, c.Credits, g.FinalGrade
            FROM Enrollments e
            JOIN Sections s ON e.SectionID = s.SectionID
            JOIN Course c ON s.CourseID = c.CourseID
            LEFT JOIN Grades g ON e.StudentID = g.StudentID AND e.SectionID = g.SectionID
            WHERE e.StudentID = ?
            """;
        try (Connection conn = DatabaseUtil.GetStudentConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                            rs.getString("CourseCode"),
                            rs.getString("CourseTitle"),
                            rs.getInt("Credits"),
                            rs.getString("FinalGrade") != null ? rs.getString("FinalGrade") : "In Progress"
                            //If the FinalGrade is null in the table it shows the student that grades are in progress
                    ));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return grades;
    }


    //This method finds the StudentID using the UserID
    //The StudentID is in the Student Table while the UserID is in the User table
    public int getStudentIdFromUserId(int userId) {
        String sql = "SELECT StudentID FROM Students WHERE UserID = ?";
        try (Connection conn = DatabaseUtil.GetStudentConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("StudentID");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }
}