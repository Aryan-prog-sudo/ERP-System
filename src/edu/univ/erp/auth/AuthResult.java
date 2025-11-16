package edu.univ.erp.auth;

/**
 * A simple data-holder to return two values from the UserDAO.
 * We can use a 'record' for this.
 */
public record AuthResult(
        int userId,
        String role
) {}