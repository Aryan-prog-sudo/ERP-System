package edu.univ.erp.service;

import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.domain.GradebookEntry;
import edu.univ.erp.domain.SectionView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles all business logic for the Instructor role.
 */
public class InstructorService {
    private static final Logger logger = Logger.getLogger(InstructorService.class.getName());

    private InstructorDAO instructorDAO;
    private SettingsDAO settingsDAO;

    private int currentInstructorId;

    public InstructorService(InstructorDAO instructorDAO, SettingsDAO settingsDAO) {
        this.instructorDAO = instructorDAO;
        this.settingsDAO = settingsDAO;
    }

    // This method will be called by Main after login
    public void setCurrentInstructor(int instructorId) {
        this.currentInstructorId = instructorId;
    }

    public List<SectionView> getAssignedSections() {
        return instructorDAO.getAssignedSections(this.currentInstructorId);
    }

    public List<GradebookEntry> getGradebook(int sectionId) {
        // TODO: Add a check here to ensure this.currentInstructorId
        // is actually assigned to this sectionId, as per the brief.
        return instructorDAO.getGradebook(sectionId);
    }

    /**
     * Calculates the final grade and saves all scores.
     * This includes the bonus logic for grade calculation.
     */
    public boolean saveAndCalculateGrades(int sectionId, List<GradebookEntry> gradebook) {
        // --- GUARD CLAUSE (Bonus Feature) ---
        if (settingsDAO.IsMaintenanceModeOn()) {
            logger.warning("Grade update failed: System is in Maintenance Mode.");
            return false;
        }

        try {
            for (GradebookEntry entry : gradebook) {
                // --- Business Logic (Bonus) ---
                // Per your design, weights are 20% quiz, 30% midterm, 50% final
                double finalScore = (entry.quizScore() * 0.20) +
                        (entry.midtermScore() * 0.30) +
                        (entry.finalScore() * 0.50);

                String finalGrade = calculateLetterGrade(finalScore);

                // Now, call the DAO to save everything
                instructorDAO.updateGrade(
                        entry.studentId(),
                        sectionId,
                        entry.quizScore(),
                        entry.midtermScore(),
                        entry.finalScore(),
                        finalGrade
                );
            }
            logger.info("Grades successfully updated for section " + sectionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to convert a numeric score to a letter grade.
     */
    private String calculateLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        if (score < 0) return "-"; // Not graded yet
        return "F";
    }
}