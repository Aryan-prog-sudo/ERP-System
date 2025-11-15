package edu.univ.erp.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
//These manage the database related stuff

public class DatabaseUtil {
    //Auth Database connection
    private static final String AuthDB_URL = "jdbc:mysql://localhost:3306/AuthDB";
    private static final String AuthDB_User = "AuthUser";
    private static final String AuthDB_PassWord = "AUTHPASSWORD";

    //Student Database connection
    private static final String StudentDB_URL = "jdbc:mysql://localhost:3306/StudentDB";
    private static final String StudentDB_User = "StudentUser";
    private static final String StudentDB_PassWord = "STUDENTPASSWORD";

    static {
        try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            throw new RuntimeException("MySQL driver not found, ", e);
        }
    }

    public static Connection GetAuthConnection() throws SQLException{
        return DriverManager.getConnection(AuthDB_URL, AuthDB_User, AuthDB_PassWord);
    }

    public static Connection GetStudentConnection() throws SQLException{
        return DriverManager.getConnection(StudentDB_URL, StudentDB_User, StudentDB_PassWord);
    }
}
