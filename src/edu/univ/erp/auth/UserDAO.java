package edu.univ.erp.auth;

import edu.univ.erp.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

//This class handles the SQL connections for the AuthDB
public class UserDAO {

    /**
     * UPDATED: Now returns an AuthResult (UserID + Role) on success.
     */
    public AuthResult CheckLogin(String Email, String Password){
        // UPDATED: Now selects UserID as well
        String SQL = "SELECT UserID, PasswordHash, Role FROM Users WHERE Email = ?";

        try(Connection AuthDBConnector = DatabaseUtil.GetAuthConnection();
            PreparedStatement Statment = AuthDBConnector.prepareStatement(SQL)){

            Statment.setString(1, Email);
            try(ResultSet Result = Statment.executeQuery()){
                if(Result.next()){
                    String StoredHash = Result.getString("PasswordHash");
                    String Role = Result.getString("Role");
                    int UserID = Result.getInt("UserID"); // <-- GET THE REAL ID

                    if(BCrypt.checkpw(Password, StoredHash)){
                        // Return the new object with both ID and Role
                        return new AuthResult(UserID, Role);
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

    //... (Your CreateAuthDBUser method is perfect, no changes) ...
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

    //... (Your ChangePassword method is perfect, no changes) ...
    public boolean ChangePassword(String Email, String NewPassword) {
        String NewPasswordHash = BCrypt.hashpw(NewPassword, BCrypt.gensalt());
        String SQL = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";

        try (Connection AuthDBConnector = DatabaseUtil.GetAuthConnection();
             PreparedStatement Statement = AuthDBConnector.prepareStatement(SQL)) {

            Statement.setString(1, NewPasswordHash);
            Statement.setString(2, Email);

            int rowsAffected = Statement.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}