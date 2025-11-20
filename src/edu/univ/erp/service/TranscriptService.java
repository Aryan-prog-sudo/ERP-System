package edu.univ.erp.service;

import com.opencsv.CSVWriter;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.domain.Grade;

import java.io.Writer;
import java.util.List;
import java.util.logging.Logger;

//This class handles the bonus feature of handling transcripts export and import ie the CSV Files
public class TranscriptService {
    private static final Logger logger = Logger.getLogger(TranscriptService.class.getName());
    private StudentDAO studentDAO;

    public TranscriptService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    //Generates a CSV transcript and writes it to the provided Writer.
    public void generateCsvTranscript(int studentId, Writer writer) {
        try (CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            String[] header = {"Course Code", "Course Title", "Credits", "Grade"};
            csvWriter.writeNext(header);
            List<Grade> grades = studentDAO.getGrades(studentId);
            for (Grade grade : grades) {
                String[] row = {
                        grade.courseCode(),
                        grade.courseTitle(),
                        String.valueOf(grade.credits()),
                        grade.letterGrade()
                };
                csvWriter.writeNext(row);
            }
            logger.info("CSV Transcript generated for student " + studentId);
        }
        catch (Exception e) {
            logger.severe("Failed to generate CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}