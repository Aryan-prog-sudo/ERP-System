package edu.univ.erp.domain;

public record EnrolledSection(
        String courseCode,
        String courseTitle,
        String timeSlot,
        String location // (Note: We'll add 'location' to your Sections table)
) {}