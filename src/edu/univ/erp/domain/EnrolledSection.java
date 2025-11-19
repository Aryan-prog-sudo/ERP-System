package edu.univ.erp.domain;

public record EnrolledSection(
        int sectionId,
        String courseCode,
        String courseTitle,
        int credits,
        String instructorName,
        String timeSlot
) {}