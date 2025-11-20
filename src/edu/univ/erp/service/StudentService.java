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
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;


//This class connects the UI on the frontend to the backend logic
//This basically contains wrapper for the functions in the DAO files
public class StudentService {
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());
    private StudentDAO studentDAO;
    private SettingsDAO settingsDAO;
    private int currentStudentId;

    public StudentService(StudentDAO studentDAO, SettingsDAO settingsDAO) {
        this.studentDAO = studentDAO;
        this.settingsDAO = settingsDAO;
    }

    //Called by Main after login
    public void setCurrentStudent(int studentId) {
        this.currentStudentId = studentId;
    }


    public int getCurrentStudentId() {
        return this.currentStudentId;
    }


    public List<SectionView> getCourseCatalog() {
        return studentDAO.getAvailableSections(this.currentStudentId);
    }


    public List<EnrolledSection> getTimetable() {
        return studentDAO.getTimetable(this.currentStudentId);
    }


    public List<Grade> getGrades() {
        return studentDAO.getGrades(this.currentStudentId);
    }


    //This method is used to register for cases by the students
    //It is called in the CourseCatalogPanel when registering for courses
    public String RegisterForSection(int sectionId) {
        if (settingsDAO.IsMaintenanceModeOn()) { //Check for maintenance mode
            return "Registration failed: System is in Maintenance Mode.";
        }
        if(DeadlinePassed()){ //Check for deadline
            return "Registration deadline has passed. The deadline was: "+ GetDeadlineString() +"Now no longer allowed to register";
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
        }
        catch (Exception e) {
            try {
                if(conn != null) {
                    conn.rollback();
                }
            }
            catch (Exception re) {
                re.printStackTrace();
            }
            if (e.getMessage().contains("Duplicate entry")) {
                return "Registration failed: You are already enrolled in this section.";
            }
            e.printStackTrace();
            return "Registration failed: A database error occurred.";
        }
        finally {
            try {
                if(conn != null){
                    conn.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //This function removes the section from the user
    public String dropSection(int sectionId) {
        if (settingsDAO.IsMaintenanceModeOn()) {
            return "Drop failed: System is in Maintenance Mode.";
        }
        if(DeadlinePassed()){
            return "Drop deadline has passed. The deadline was: "+ GetDeadlineString() +"Now no longer allowed to Drop";
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
        }
        catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception re) { re.printStackTrace(); }
            e.printStackTrace();
            return "Drop failed: " + e.getMessage();
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //This method returns false if today's date is after the deadline
    //If today's date is after deadline it returns true(No longer dropping or adding courses) else returns false
    private boolean DeadlinePassed(){
        try{
            String DeadlineString = settingsDAO.GetDeadline();
            LocalDate Deadline = LocalDate.parse(DeadlineString);
            LocalDate TodayDate = LocalDate.now();
            return TodayDate.isAfter(Deadline);
        }
        catch (Exception e){
            return false;
            //In case of failure of date parsing it allows access
        }
    }


    //Wrapper for GetDeadline
    public String GetDeadlineString(){
        return settingsDAO.GetDeadline();
    }
}