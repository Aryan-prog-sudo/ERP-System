package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;

//This class handles the connection to the StudentDB

public class AdminDAO {
    public boolean CreateStudentProfile(Connection StudentDBConnection, int UserID, String FullName, String Email) throws Exception{
        String SQL = "INSERT INTO"
    }
}
