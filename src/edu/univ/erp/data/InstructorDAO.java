package edu.univ.erp.data;

import edu.univ.erp.domain.GradebookEntry;
import edu.univ.erp.domain.SectionView;
import edu.univ.erp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//This class handles the Instructor action for the Instruction Table in StudentDB
//This is mostly used in the Instructor pages of the frontend thus the name
public class InstructorDAO {
    //This method basically fetches all the sections that are assigned to a specific instructor
    public List<SectionView> getAssignedSections(int instructorId) {
        List<SectionView> Sections = new ArrayList<>();
        String SQL = """
            SELECT s.SectionID, c.CourseCode, c.CourseTitle, c.Credits, s.TimeSlot, s.EnrolledCount, s.Capacity
            FROM Sections s
            JOIN Course c ON s.CourseID = c.CourseID
            WHERE s.InstructorID = ?
            """;
        try (Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement stmt = StudentDBConnection.prepareStatement(SQL)){
            stmt.setInt(1, instructorId);
            try (ResultSet Result = stmt.executeQuery()) {
                while (Result.next()) {
                    Sections.add(new SectionView(
                            Result.getInt("SectionID"),
                            Result.getString("CourseCode"),
                            Result.getString("CourseTitle"),
                            Result.getInt("Credits"),
                            null, // Instructor name is not needed here
                            Result.getString("TimeSlot"),
                            Result.getInt("EnrolledCount"),
                            Result.getInt("Capacity"),
                            false // Not relevant for instructor(isEnrolled)
                    ));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        return Sections;
    }


    //This method basically fetches the gradebook of an entire section
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
        try (Connection conn = DatabaseUtil.GetStudentConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }


    //This inserts the grades of student into the database
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
        try (Connection conn = DatabaseUtil.GetStudentConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.setDouble(3, quiz);
            stmt.setDouble(4, midterm);
            stmt.setDouble(5, finalScore);
            stmt.setString(6, finalGrade);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * NEW METHOD: Finds the Instructor's PRIMARY KEY (InstructorID)
     * using their foreign key (UserID from AuthDB).
     */
    public int getInstructorIdFromUserId(int userId) {
        String sql = "SELECT InstructorID FROM Instructors WHERE UserID = ?";
        try (Connection conn = DatabaseUtil.GetStudentConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("InstructorID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }
}