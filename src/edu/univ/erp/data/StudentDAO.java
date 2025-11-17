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

    /**
     * Fetches a student's timetable.
     * (We'll assume a 'Location' column exists in Sections for this)
     */
    public List<EnrolledSection> getTimetable(int studentId) {
        List<EnrolledSection> timetable = new ArrayList<>();
        String sql = """
            SELECT c.CourseCode, c.CourseTitle, s.TimeSlot, 'TBA' AS Location 
            FROM Enrollments e
            JOIN Sections s ON e.SectionID = s.SectionID
            JOIN Course c ON s.CourseID = c.CourseID
            WHERE e.StudentID = ?
            """;
        // Note: I've hardcoded Location as 'TBA'.
        // You would need to add a Location column to your Sections table to fix this.

        try (Connection conn = DatabaseUtil.GetStudentConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timetable.add(new EnrolledSection(
                            rs.getString("CourseCode"),
                            rs.getString("CourseTitle"),
                            rs.getString("TimeSlot"),
                            rs.getString("Location")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timetable;
    }

    //This code fetches the grades of the student
    /**
     * UPDATED: Fetches a student's grades.
     * This query now starts from Enrollments and LEFT JOINs Grades
     * to show courses even if they are still "In Progress".
     */
    public List<Grade> getGrades(int studentId) {
        List<Grade> grades = new ArrayList<>();
        // --- THIS IS THE FIXED QUERY ---
        String sql = """
            SELECT 
                c.CourseCode, c.CourseTitle, c.Credits, g.FinalGrade
            FROM Enrollments e
            JOIN Sections s ON e.SectionID = s.SectionID
            JOIN Course c ON s.CourseID = c.CourseID
            LEFT JOIN Grades g ON e.StudentID = g.StudentID AND e.SectionID = g.SectionID
            WHERE e.StudentID = ?
            """;
        // --- END OF FIX ---

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                            rs.getString("CourseCode"),
                            rs.getString("CourseTitle"),
                            rs.getInt("Credits"),
                            // This logic correctly handles NULL grades
                            rs.getString("FinalGrade") != null ? rs.getString("FinalGrade") : "In Progress"
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grades;
    }

    /**
     * NEW METHOD: Finds the Student's PRIMARY KEY (StudentID)
     * using their foreign key (UserID from AuthDB).
     */
    public int getStudentIdFromUserId(int userId) {
        String sql = "SELECT StudentID FROM Students WHERE UserID = ?";
        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("StudentID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }
}