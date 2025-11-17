package edu.univ.erp.auth;

public record AuthUserInfo(
        int userId,
        String email,
        String role
){}

