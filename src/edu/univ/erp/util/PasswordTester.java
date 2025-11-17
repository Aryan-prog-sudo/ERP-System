package edu.univ.erp.util;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * A standalone program to test *only* the password.
 * Run this file to find the problem.
 */
public class PasswordTester {

    public static void main(String[] args) {
        String emailToTest = "student@university.edu";
        String passwordToTest = "student123";
        String storedHash = null;

        System.out.println("--- Password Tester Starting ---");

        // 1. Try to connect to the database
        try (Connection conn = DatabaseUtil.GetAuthConnection()) {
            System.out.println("✅ CONNECTION SUCCESSFUL (DatabaseUtil.java is correct)");

            // 2. Try to find the user
            String sql = "SELECT PasswordHash FROM Users WHERE Email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, emailToTest);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        storedHash = rs.getString("PasswordHash");
                        System.out.println("✅ USER FOUND: student@university.edu");
                        System.out.println("   Database Hash: " + storedHash);
                    } else {
                        System.out.println("❌ TEST FAILED: User 'student@university.edu' does not exist in the database.");
                        return;
                    }
                }
            }

            // 3. Test the password
            if (storedHash != null) {
                System.out.println("...Checking password...");

                // This is the same check your UserDAO does
                if (BCrypt.checkpw(passwordToTest, storedHash)) {
                    System.out.println("-------------------------------------------------");
                    System.out.println("✅✅✅ PASSWORD IS A MATCH! ✅✅✅");
                    System.out.println("Your login problem is somewhere else (like Main.java or AuthService.java).");
                    System.out.println("-------------------------------------------------");
                } else {
                    System.out.println("-------------------------------------------------");
                    System.out.println("❌❌❌ TEST FAILED: PASSWORD HASH DOES NOT MATCH! ❌❌❌");
                    System.out.println("This is the problem. Your database has the wrong hash.");
                    System.out.println("SOLUTION: Run the 'DatabaseSeeder.java' program again.");
                    System.out.println("-------------------------------------------------");
                }
            }

        } catch (Exception e) {
            System.out.println("-------------------------------------------------");
            System.out.println("❌❌❌ TEST FAILED: CANNOT CONNECT TO DATABASE! ❌❌❌");
            System.out.println("This is the problem. Your 'DatabaseUtil.java' file is wrong.");
            System.out.println("Check your 'AUTHPASSWORD' and 'AUTH_USER' settings.");
            System.out.println("-------------------------------------------------");
            e.printStackTrace();
        }
    }
}