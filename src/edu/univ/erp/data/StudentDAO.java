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

/**
 * Handles all database operations for the Student role from StudentDB.
 */
public class StudentDAO {

    /**
     * Fetches all sections for the Course Catalog.
     * It also checks which ones the *current* student is enrolled in.
     */
    public List<SectionView> getAvailableSections(int studentId) {
        List<SectionView> sections = new ArrayList<>();
        // This is a complex query that joins 4 tables!
        String sql = """
            SELECT 
                s.SectionID, c.CourseCode, c.CourseTitle, i.FullName, s.TimeSlot, s.EnrolledCount, s.Capacity,
                (SELECT COUNT(*) FROM Enrollments e WHERE e.SectionID = s.SectionID AND e.StudentID = ?) AS IsEnrolled
            FROM Sections s
            JOIN Course c ON s.CourseID = c.CourseID
            LEFT JOIN Instructors i ON s.InstructorID = i.InstructorID
            """;

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(new SectionView(
                            rs.getInt("SectionID"),
                            rs.getString("CourseCode"),
                            rs.getString("CourseTitle"),
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

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    /**
     * Fetches a student's final grades.
     */
    public List<Grade> getGrades(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = """
            SELECT c.CourseCode, c.CourseTitle, c.Credits, g.FinalGrade
            FROM Grades g
            JOIN Sections s ON g.SectionID = s.SectionID
            JOIN Course c ON s.CourseID = c.CourseID
            WHERE g.StudentID = ?
            """;

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                            rs.getString("CourseCode"),
                            rs.getString("CourseTitle"),
                            rs.getInt("Credits"),
                            rs.getString("FinalGrade") != null ? rs.getString("FinalGrade") : "In Progress"
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grades;
    }
}