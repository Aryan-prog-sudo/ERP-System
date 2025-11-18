package edu.univ.erp.service;

import edu.univ.erp.auth.AuthResult;
import edu.univ.erp.auth.UserDAO;
import edu.univ.erp.data.SettingsDAO;
import java.util.logging.Logger;
import java.util.logging.Level;

//This class handles the auth services on the frontend that are the login dialog and the change password
public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;

    public AuthService(UserDAO userDAO, SettingsDAO settingsDAO) {
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
    }


    //This method gets an AuthResult object from the CheckLogin method in UserDAO class
    //The AuthResult contains the Role and the userID which is then converted to LoginResult that contains the message and a boolean indicating weather the login attempt was success
    //This method is called in the Login dialog of the UI
    public LoginResult login(String email, String password) {
        logger.info("Login attempt for user: " + email);
        System.out.println("Login attempt for the user: "+ email);
        AuthResult authResult = userDAO.CheckLogin(email, password);
        if (authResult == null) {
            logger.warning("Failed login for user: " + email + " (Incorrect password/email)");
            return new LoginResult(-1, null, "Incorrect username or password.");
        }
        logger.info("Login successful for user: " + email + ", Role: " + authResult.role());
        return new LoginResult(authResult.userId(), authResult.role(), "Login successful.");
    }


    //This method is used to change the password of user
    //This method is called in the ChangePasswordDialog in UI
    //The checkLogin method is basically used to check if the OldPassword is correct
    //If password is false then return false and error pop up, else continue to change password
    public boolean ChangePassword(String email, String oldPassword, String newPassword) {
        String role = userDAO.CheckLogin(email, oldPassword).role();
        if (role == null) {
            logger.warning("Password change failed for " + email + ": Incorrect old password.");
            return false;
        }
        boolean success = userDAO.ChangePassword(email, newPassword);
        if (success) {
            logger.info("Password changed successfully for " + email);
        }
        else {
            logger.severe("Password change FAILED for " + email + " during database update.");
        }
        return success;
    }
}