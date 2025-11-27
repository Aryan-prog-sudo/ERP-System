package edu.univ.erp.util;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * A standalone program to completely reset and seed the database.
 *
 * This program will:
 * 1. WIPE ALL DATA from all tables.
 * 2. Create ONLY the 4 new test users (admin1, inst1, stu1, stu2).
 * 3. Create their corresponding profiles in the StudentDB.
 *
 * Run this file ONCE to reset your database for final testing.
 */
public class DatabaseSeeder {

    public static void main(String[] args) {
        System.out.println("--- Starting Database Seeder (4-User Version) ---");

        Connection authConn = null;
        Connection studentConn = null;

        try {
            // 1. Get connections
            authConn = DatabaseUtil.GetAuthConnection();
            studentConn = DatabaseUtil.GetStudentConnection();
            System.out.println("Connections established...");

            // 2. Set transaction mode
            authConn.setAutoCommit(false);
            studentConn.setAutoCommit(false);
            System.out.println("Transaction mode set.");

            // 3. WIPE ALL EXISTING DATA
            wipeAllTables(authConn, studentConn);
            System.out.println("All tables wiped.");

            // 4. Re-seed default settings
            seedSystemSettings(studentConn);
            System.out.println("System settings seeded.");

            // 5. Create ONLY your 4 test users
            createUser(authConn, studentConn, "Stu One", "stu1@university.edu", "stu123", "Student");
            createUser(authConn, studentConn, "Stu Two", "stu2@university.edu", "stu123", "Student");
            createUser(authConn, studentConn, "Inst One", "inst1@university.edu", "inst123", "Instructor");
            createUser(authConn, studentConn, "Admin One", "admin1@university.edu", "admin123", "Admin");

            System.out.println("Your 4 test users have been created.");

            // 6. Commit the changes
            authConn.commit();
            studentConn.commit();

            System.out.println("-----------------------------------");
            System.out.println("✅ SUCCESS: Database has been reset with your 4 users.");
            System.out.println("You can now run Main.java.");
            System.out.println("-----------------------------------");

        } catch (Exception e) {
            // 7. Rollback on error
            System.out.println("-----------------------------------");
            System.out.println("❌ ERROR: Seeding failed. Rolling back all changes.");
            System.out.println("Please check your DatabaseUtil.java passwords.");
            System.out.println("-----------------------------------");
            e.printStackTrace();
            try {
                if (authConn != null) authConn.rollback();
                if (studentConn != null) studentConn.rollback();
            } catch (Exception re) {
                System.out.println("Rollback failed!");
                re.printStackTrace();
            }
        } finally {
            // 8. Close connections
            try {
                if (authConn != null) authConn.close();
                if (studentConn != null) studentConn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void wipeAllTables(Connection authConn, Connection studentConn) throws Exception {
        try (Statement authStmt = authConn.createStatement();
             Statement studentStmt = studentConn.createStatement()) {

            studentStmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            studentStmt.execute("TRUNCATE TABLE Grades;");
            studentStmt.execute("TRUNCATE TABLE Enrollments;");
            studentStmt.execute("TRUNCATE TABLE Sections;");
            studentStmt.execute("TRUNCATE TABLE Students;");
            studentStmt.execute("TRUNCATE TABLE Instructors;");
            studentStmt.execute("TRUNCATE TABLE Course;");
            studentStmt.execute("TRUNCATE TABLE SystemSettings;");
            authStmt.execute("TRUNCATE TABLE Users;");
            studentStmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
        }
    }

    /**
     * Re-inserts the default maintenance mode setting.
     */
    private static void seedSystemSettings(Connection studentConn) throws Exception {
        String sql = "INSERT INTO SystemSettings (SettingKey, SettingValue) VALUES ('MaintenanceMode','false');";
        try (PreparedStatement stmt = studentConn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    /**
     * Helper method to create a new user in both databases.
     */
    private static void createUser(Connection authConn, Connection studentConn,
                                   String fullName, String email, String password, String role) throws Exception {

        // --- Step 1: Create user in AuthDB ---
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sqlAuth = "INSERT INTO Users (Email, PasswordHash, Role) VALUES (?, ?, ?)";
        int newUserId = -1;

        try (PreparedStatement stmt = authConn.prepareStatement(sqlAuth, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);
            stmt.executeUpdate();

            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    newUserId = rs.getInt(1);
                } else {
                    throw new Exception("Failed to get UserID for " + email);
                }
            }
        }

        // --- Step 2: Create profile in StudentDB (if needed) ---
        if (role.equalsIgnoreCase("Student")) {
            String sqlStudent = "INSERT INTO Students (UserID, FullName, Email) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = studentConn.prepareStatement(sqlStudent)) {
                stmt.setInt(1, newUserId);
                stmt.setString(2, fullName);
                stmt.setString(3, email);
                stmt.executeUpdate();
            }
        } else if (role.equalsIgnoreCase("Instructor")) {
            String sqlInstructor = "INSERT INTO Instructors (UserID, FullName, Email, Department) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = studentConn.prepareStatement(sqlInstructor)) {
                stmt.setInt(1, newUserId);
                stmt.setString(2, fullName);
                stmt.setString(3, email);
                stmt.setString(4, "Computer Science"); // Default department
                stmt.executeUpdate();
            }
        }

        System.out.println("Successfully created user: " + email);
    }
}