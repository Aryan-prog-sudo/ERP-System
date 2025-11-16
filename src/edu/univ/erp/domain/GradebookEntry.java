package edu.univ.erp.domain;

public record GradebookEntry(
        int studentId,
        String studentName,
        double quizScore,
        double midtermScore,
        double finalScore,
        String finalGrade
) {}