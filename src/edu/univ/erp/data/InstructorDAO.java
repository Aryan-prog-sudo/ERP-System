package edu.univ.erp.data;

import edu.univ.erp.domain.GradebookEntry;
import edu.univ.erp.domain.SectionView;
import edu.univ.erp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the Instructor role from StudentDB.
 */
public class InstructorDAO {

    /**
     * Fetches all sections assigned to a specific instructor.
     */
    public List<SectionView> getAssignedSections(int instructorId) {
        List<SectionView> sections = new ArrayList<>();
        String sql = """
            SELECT s.SectionID, c.CourseCode, c.CourseTitle, s.TimeSlot, s.EnrolledCount, s.Capacity
            FROM Sections s
            JOIN Course c ON s.CourseID = c.CourseID
            WHERE s.InstructorID = ?
            """;

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(new SectionView(
                            rs.getInt("SectionID"),
                            rs.getString("CourseCode"),
                            rs.getString("CourseTitle"),
                            null, // Instructor name is not needed here
                            rs.getString("TimeSlot"),
                            rs.getInt("EnrolledCount"),
                            rs.getInt("Capacity"),
                            false // Not relevant for instructor
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sections;
    }

    /**
     * Fetches the full gradebook for a given section.
     */
    public List<GradebookEntry> getGradebook(int sectionId) {
        List<GradebookEntry> entries = new ArrayList<>();
        // This query joins Enrollments, Students, and Grades
        String sql = """
            SELECT 
                e.StudentID, s.FullName, g.QuizScore, g.MidtermScore, g.FinalScore, g.FinalGrade
            FROM Enrollments e
            JOIN Students s ON e.StudentID = s.StudentID
            LEFT JOIN Grades g ON e.StudentID = g.StudentID AND e.SectionID = g.SectionID
            WHERE e.SectionID = ?
            """;

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(new GradebookEntry(
                            rs.getInt("StudentID"),
                            rs.getString("FullName"),
                            rs.getDouble("QuizScore"),
                            rs.getDouble("MidtermScore"),
                            rs.getDouble("FinalScore"),
                            rs.getString("FinalGrade")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    /**
     * Updates or Inserts a grade for a student in a section.
     * This is an "UPSERT" operation.
     */
    public boolean updateGrade(int studentId, int sectionId, double quiz, double midterm, double finalScore, String finalGrade) {
        // This query tries to insert. If it fails (due to UNIQUE key), it updates instead.
        String sql = """
            INSERT INTO Grades (StudentID, SectionID, QuizScore, MidtermScore, FinalScore, FinalGrade)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                QuizScore = VALUES(QuizScore),
                MidtermScore = VALUES(MidtermScore),
                FinalScore = VALUES(FinalScore),
                FinalGrade = VALUES(FinalGrade)
            """;

        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.setDouble(3, quiz);
            stmt.setDouble(4, midterm);
            stmt.setDouble(5, finalScore);
            stmt.setString(6, finalGrade);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}