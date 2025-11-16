package edu.univ.erp.domain;

public record Instructor(
        int instructorId,
        String fullName,
        String email
) {
    // Override toString() to look nice in the JComboBox
    @Override
    public String toString() {
        return fullName;
    }
}