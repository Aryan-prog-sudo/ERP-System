package edu.univ.erp.domain;

/**
 * A record to hold data for the Admin's "Existing Sections" table.
 */
public record AdminSectionView(
        String CourseCode,
        String SectionNumber,
        String TimeSlot,
        String Capacity,
        String InstructorName
) {}