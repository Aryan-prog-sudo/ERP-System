package edu.univ.erp.domain;

//This is the record that holds the return values necessary by the admin in the course table visible
public record AdminSectionView(
        int SectionID,
        String CourseCode,
        String SectionNumber,
        String TimeSlot,
        int EnrolledCount,
        int Capacity,
        String InstructorName
) {}