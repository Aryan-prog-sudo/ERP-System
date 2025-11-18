package edu.univ.erp.service;

import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.domain.GradebookEntry;
import edu.univ.erp.domain.SectionView;

import java.text.DecimalFormat;
import com.opencsv.CSVWriter;
import java.io.Writer;
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
    //This function basically calculates the final grade and saves all the grades into the database
    public boolean saveAndCalculateGrades(int sectionId, List<GradebookEntry> gradebook) {
        if (settingsDAO.IsMaintenanceModeOn()) {
            logger.warning("Grade update failed: System is in Maintenance Mode.");
            return false;
        }
        try {
            for (GradebookEntry entry : gradebook) {
                // --- Business Logic (Bonus) ---
                // Per your design, weights are 20% quiz, 30% midterm, 50% final
                double finalScore = (entry.quizScore() * 0.20) + (entry.midtermScore() * 0.30) + (entry.finalScore() * 0.50);
                String finalGrade = CalculateLetterGrade(finalScore);

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
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //Used to convert grades to LetterGrade
    private String CalculateLetterGrade(double Score) {
        if (Score >= 90) return "A";
        if (Score >= 80) return "B";
        if (Score >= 70) return "C";
        if (Score >= 60) return "D";
        if (Score < 0) return "-";
        return "F";
    }


    //This method fetches all the gradebook data and writes it into the CSV
    public void exportGradebookToCsv(int sectionId, Writer writer) throws Exception {
        // 1. Get the data (we already have a method for this)
        List<GradebookEntry> gradebook = getGradebook(sectionId);

        // 2. Use CSVWriter to write the data
        try (CSVWriter csvWriter = new CSVWriter(writer)) {

            // 3. Write the header row
            csvWriter.writeNext(new String[]{
                    "Student ID", "Student Name", "Quiz Score",
                    "Midterm Score", "Final Score", "Final Grade"
            });

            // 4. Write all data rows
            for (GradebookEntry entry : gradebook) {
                csvWriter.writeNext(new String[]{
                        String.valueOf(entry.studentId()),
                        entry.studentName(),
                        String.valueOf(entry.quizScore()),
                        String.valueOf(entry.midtermScore()),
                        String.valueOf(entry.finalScore()),
                        entry.finalGrade() != null ? entry.finalGrade() : "" // Handle null grades
                });
            }
        }
        // Let the exception bubble up to the UI to be handled
    }

    //This would be added to the UI panel to prevent the user from even writing in the columns for grade
    public boolean SystemInMaintenance(){
        return settingsDAO.IsMaintenanceModeOn();
    }

}