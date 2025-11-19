package edu.univ.erp.domain;

/**
 * A record to hold data for the Admin's "Existing Sections" table.
 */
public record AdminSectionView(
        int SectionID,
        String CourseCode,
        String SectionNumber,
        String TimeSlot,
        int EnrolledCount,
        int Capacity,
        String InstructorName
) {}