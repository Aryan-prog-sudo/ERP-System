package edu.univ.erp.auth;

import edu.univ.erp.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

//This class handles the SQL connections for the AuthDB

public class UserDAO {
    //The CheckLogin returns the user role on success and null in case some error occurs
    //The CheckLogin would use the Email aas key to find the role
    //The SQL string "Select PasswordHash, Role FROM Users WHERE Email = ?" is used to find the PasswordHash and the Role from corresponding Email
    //The Email is marked as ? since we have to take it as argument
    //A connection is set up to the AuthDB as the AuthDBConnector
    //Then prepare the Statement using the PrepareStatement(SQL)
    //The setString(1, Email) binds the Email to the SQL statement
    //Then the Password has and the Role from the table
    public String CheckLogin(String Email, String Password){
        String SQL = "SELECT PasswordHash, Role FROM Users WHERE Email = ?";
        try(Connection AuthDBConnector = DatabaseUtil.GetAuthConnection(); PreparedStatement Statment = AuthDBConnector.prepareStatement(SQL)){
            Statment.setString(1, Email);
            try(ResultSet Result = Statment.executeQuery()){
                if(Result.next()){
                    String StoredHash = Result.getString("PasswordHash");
                    String Role = Result.getString("Role");
                    //BCrypt compares the provided password with the PasswordHash
                    if(BCrypt.checkpw(Password, StoredHash)){
                        return Role;
                    }
                }
            }
            return null;
            //In case User not found
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //This function creates a new user in the AuthDB
    public int CreateAuthDBUser(Connection AuthDBConnection, String Email, String Password, String Role) throws Exception{
        String PasswordHash = BCrypt.hashpw(Password, BCrypt.gensalt());
        String SQL = "INSERT INTO Users (Email, PasswordHash, Role) VALUES (?, ?, ?)";
        try(PreparedStatement Statement = AuthDBConnection.prepareStatement(SQL, java.sql.Statement.RETURN_GENERATED_KEYS)){
            Statement.setString(1, Email);
            Statement.setString(2, PasswordHash);
            Statement.setString(3, Role);
            Statement.executeUpdate();
            try(ResultSet Result = Statement.getGeneratedKeys()){
                if(Result.next()){
                    return Result.getInt(1);
                }
                else{
                    throw new Exception("Failed to create AuthDB user");
                }
            }
        }
    }

}
