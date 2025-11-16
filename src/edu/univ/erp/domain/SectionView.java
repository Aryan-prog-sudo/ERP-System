package edu.univ.erp.domain;

/**
 * A record to hold data for the student's Course Catalog view.
 * This is a "ViewModel" that joins data from multiple tables.
 */
public record SectionView(
        int sectionId,
        String courseCode,
        String courseTitle,
        String instructorName,
        String timeSlot,
        int enrolled,
        int capacity,
        boolean isEnrolled // Is the *current* student enrolled in this?
) {}