package edu.univ.erp.domain;

public record Grade(
        String courseCode,
        String courseTitle,
        int credits,
        String letterGrade // The final, calculated letter grade
) {}