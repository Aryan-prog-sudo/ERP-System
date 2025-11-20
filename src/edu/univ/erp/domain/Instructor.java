package edu.univ.erp.domain;

public record Instructor(
        int instructorId,
        String fullName,
        String email
) {

    //This is for the JComboBox
    @Override
    public String toString() {
        return fullName;
    }
}