package edu.univ.erp.service;

import edu.univ.erp.auth.AuthResult; // <-- NEW IMPORT
import edu.univ.erp.auth.UserDAO;
import edu.univ.erp.data.SettingsDAO;
import java.util.logging.Logger;
import java.util.logging.Level;


public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;

    public AuthService(UserDAO userDAO, SettingsDAO settingsDAO) {
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
    }

    /**
     * UPDATED: Now gets the AuthResult object from the DAO
     * and passes the real UserID to the LoginResult.
     */
    public LoginResult login(String email, String password) {
        logger.info("Login attempt for user: " + email);

        // 1. Check credentials
        AuthResult authResult = userDAO.CheckLogin(email, password); // <-- UPDATED

        if (authResult == null) {
            logger.warning("Failed login for user: " + email + " (Incorrect password/email)");
            return new LoginResult(-1, null, "Incorrect username or password.");
        }

        // 2. Check Maintenance Mode
        boolean isMaintenance = settingsDAO.IsMaintenanceModeOn();

        // 3. Check if allowed
        if (isMaintenance && !authResult.role().equalsIgnoreCase("Admin")) {
            logger.warning("Login denied for user " + email + " due to Maintenance Mode.");
            return new LoginResult(-1, null, "System is in maintenance mode. Please try again later.");
        }

        logger.info("Login successful for user: " + email + ", Role: " + authResult.role());
        // UPDATED: Pass the real UserID
        return new LoginResult(authResult.userId(), authResult.role(), "Login successful.");
    }

    // ... (your changePassword method is perfect, no changes) ...
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        String role = userDAO.CheckLogin(email, oldPassword).role();
        if (role == null) {
            logger.warning("Password change failed for " + email + ": Incorrect old password.");
            return false;
        }
        boolean success = userDAO.ChangePassword(email, newPassword);
        if (success) {
            logger.info("Password changed successfully for " + email);
        } else {
            logger.severe("Password change FAILED for " + email + " during database update.");
        }
        return success;
    }
}