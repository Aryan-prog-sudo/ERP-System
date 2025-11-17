package edu.univ.erp.domain;

/**
 * A record to hold the combined user info for the Admin panel.
 */
public record UserView(
        String fullName,
        String email,
        String role
) {}