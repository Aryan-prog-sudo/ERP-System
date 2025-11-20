package edu.univ.erp.domain;

//This holds the combined info on the userpanel
public record UserView(
        String fullName,
        String email,
        String role
) {}