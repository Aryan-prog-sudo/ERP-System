package edu.univ.erp.domain;

// A record is a simple data-holder class.
public record Course(
        int courseId,
        String courseCode,
        String courseTitle,
        int credits
) {

    //This is to support JComboBox
    @Override
    public String toString() {
        return courseCode + " - " + courseTitle;
    }
}