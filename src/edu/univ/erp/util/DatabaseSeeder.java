package edu.univ.erp.util;

import org.mindrot.jbcrypt.BCrypt; // We will use this to hash
import java.sql.Connection;
import java.sql.PreparedStatement; // Use PreparedStatement

/**
 * A standalone program to seed the database with correct demo users.
 * This version generates the hashes IN JAVA, just as you suggested.
 *
 * Run this file ONCE to fix your database passwords.
 */
public class DatabaseSeeder {

    public static void main(String[] args) {
        System.out.println("Starting database seeder (Java Hashing)...");

        // 1. We define the plain-text passwords
        String studentPass = "student123";
        String instructorPass = "instructor123";
        String adminPass = "admin123";

        // 2. We generate the hashes right here in Java
        // This is the same code your Admin panel will use
        String studentHash = BCrypt.hashpw(studentPass, BCrypt.gensalt());
        String instructorHash = BCrypt.hashpw(instructorPass, BCrypt.gensalt());
        String adminHash = BCrypt.hashpw(adminPass, BCrypt.gensalt());

        System.out.println("New hash for student: " + studentHash);

        // 3. Connect to the database
        try (Connection conn = DatabaseUtil.GetAuthConnection()) { // Use your method
            System.out.println("Connected to AuthDB...");

            // 4. Delete old users
            // We use PreparedStatement to be safe
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE Email IN (?, ?, ?)")) {
                stmt.setString(1, "student@university.edu");
                stmt.setString(2, "instructor@university.edu");
                stmt.setString(3, "admin@university.edu");
                int rowsDeleted = stmt.executeUpdate();
                System.out.println("Deleted " + rowsDeleted + " old users.");
            }

            // 5. Insert new users with our new, Java-generated hashes
            String sql = "INSERT INTO Users (Email, PasswordHash, Role) VALUES (?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Add student
                stmt.setString(1, "student@university.edu");
                stmt.setString(2, studentHash);
                stmt.setString(3, "student");
                stmt.executeUpdate();
                System.out.println("Created student.");

                // Add instructor
                stmt.setString(1, "instructor@university.edu");
                stmt.setString(2, instructorHash);
                stmt.setString(3, "instructor");
                stmt.executeUpdate();
                System.out.println("Created instructor.");

                // Add admin
                stmt.setString(1, "admin@university.edu");
                stmt.setString(2, adminHash);
                stmt.setString(3, "admin");
                stmt.executeUpdate();
                System.out.println("Created admin.");
            }

            System.out.println("-----------------------------------");
            System.out.println("✅ SUCCESS: Database has been seeded with new, Java-generated hashes.");
            System.out.println("-----------------------------------");

        } catch (Exception e) {
            System.out.println("-----------------------------------");
            System.out.println("❌ ERROR: Could not seed database.");
            System.out.println("THIS IS THE PROBLEM: Please check the 'AUTHPASSWORD' in your DatabaseUtil.java file.");
            System.out.println("-----------------------------------");
            e.printStackTrace();
        }
    }
}