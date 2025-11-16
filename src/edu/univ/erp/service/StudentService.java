package edu.univ.erp.service;

import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.domain.EnrolledSection;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.SectionView;
import edu.univ.erp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Logger;

public class StudentService {
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());

    private StudentDAO studentDAO;
    private SettingsDAO settingsDAO;

    // This ID is set by Main.java after a successful login
    private int currentStudentId;

    public StudentService(StudentDAO studentDAO, SettingsDAO settingsDAO) {
        this.studentDAO = studentDAO;
        this.settingsDAO = settingsDAO;
    }

    // Called by Main after login
    public void setCurrentStudent(int studentId) {
        this.currentStudentId = studentId;
    }

    /**
     * NEW METHOD: This fixes the error from your screenshot.
     * GradesPanel calls this to know who to generate a transcript for.
     */
    public int getCurrentStudentId() {
        return this.currentStudentId;
    }

    // --- Public Methods for UI ---

    public List<SectionView> getCourseCatalog() {
        return studentDAO.getAvailableSections(this.currentStudentId);
    }

    public List<EnrolledSection> getTimetable() {
        return studentDAO.getTimetable(this.currentStudentId);
    }

    public List<Grade> getGrades() {
        return studentDAO.getGrades(this.currentStudentId);
    }

    public String registerForSection(int sectionId) {
        // ... (method is the same as before) ...
        if (settingsDAO.IsMaintenanceModeOn()) {
            return "Registration failed: System is in Maintenance Mode.";
        }
        Connection conn = null;
        try {
            conn = DatabaseUtil.GetStudentConnection();
            conn.setAutoCommit(false);
            String checkSql = "SELECT EnrolledCount, Capacity FROM Sections WHERE SectionID = ? FOR UPDATE";
            int enrolled = 0;
            int capacity = 0;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, sectionId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        enrolled = rs.getInt("EnrolledCount");
                        capacity = rs.getInt("Capacity");
                    } else {
                        throw new Exception("Section not found.");
                    }
                }
            }
            if (enrolled >= capacity) {
                conn.rollback();
                return "Registration failed: Class is full.";
            }
            String enrollSql = "INSERT INTO Enrollments (StudentID, SectionID, EnrollmentDate) VALUES (?, ?, CURDATE())";
            try (PreparedStatement enrollStmt = conn.prepareStatement(enrollSql)) {
                enrollStmt.setInt(1, this.currentStudentId);
                enrollStmt.setInt(2, sectionId);
                enrollStmt.executeUpdate();
            }
            String updateSql = "UPDATE Sections SET EnrolledCount = ? WHERE SectionID = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, enrolled + 1);
                updateStmt.setInt(2, sectionId);
                updateStmt.executeUpdate();
            }
            conn.commit();
            logger.info("Student " + currentStudentId + " registered for section " + sectionId);
            return "Successfully registered!";
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception re) { re.printStackTrace(); }
            if (e.getMessage().contains("Duplicate entry")) {
                return "Registration failed: You are already enrolled in this section.";
            }
            e.printStackTrace();
            return "Registration failed: A database error occurred.";
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public String dropSection(int sectionId) {
        // ... (method is the same as before) ...
        if (settingsDAO.IsMaintenanceModeOn()) {
            return "Drop failed: System is in Maintenance Mode.";
        }
        Connection conn = null;
        try {
            conn = DatabaseUtil.GetStudentConnection();
            conn.setAutoCommit(false);
            String deleteSql = "DELETE FROM Enrollments WHERE StudentID = ? AND SectionID = ?";
            int rowsDeleted = 0;
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, this.currentStudentId);
                deleteStmt.setInt(2, sectionId);
                rowsDeleted = deleteStmt.executeUpdate();
            }
            if (rowsDeleted == 0) {
                throw new Exception("Student was not enrolled in this section.");
            }
            String updateSql = "UPDATE Sections SET EnrolledCount = EnrolledCount - 1 WHERE SectionID = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, sectionId);
                updateStmt.executeUpdate();
            }
            conn.commit();
            logger.info("Student " + currentStudentId + " dropped section " + sectionId);
            return "Successfully dropped section.";
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception re) { re.printStackTrace(); }
            e.printStackTrace();
            return "Drop failed: " + e.getMessage();
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}