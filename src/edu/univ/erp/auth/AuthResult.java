package edu.univ.erp.auth;

//This basically holds the values returned by the UserDAO class
//The UserDAO returns value in this format
public record AuthResult(
        int userId,
        String role
) {}