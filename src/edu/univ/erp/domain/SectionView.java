package edu.univ.erp.domain;

//This used to hold the data for the sections table on the student side of application, the table on the course catalog
public record SectionView(
        int sectionId,
        String courseCode,
        String courseTitle,
        int credits,
        String instructorName,
        String timeSlot,
        int enrolled,
        int capacity,
        boolean isEnrolled // Is the *current* student enrolled in this?
) {}