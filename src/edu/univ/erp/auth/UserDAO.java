package edu.univ.erp.auth;
//Change
import edu.univ.erp.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList; // <-- NEW IMPORT
import java.util.List; // <-- NEW IMPORT


//This class handles the SQL connections for the AuthDB
public class UserDAO {
    //The below method is used to check weather the password and email provided at the login time are valid
    public AuthResult CheckLogin(String Email, String Password){
        String SQL = "SELECT UserID, PasswordHash, Role FROM Users WHERE Email = ?";
        try(Connection AuthDBConnector = DatabaseUtil.GetAuthConnection();
            PreparedStatement Statment = AuthDBConnector.prepareStatement(SQL)){
            Statment.setString(1, Email);
            try(ResultSet Result = Statment.executeQuery()){
                if(Result.next()){
                    String StoredHash = Result.getString("PasswordHash");
                    String Role = Result.getString("Role");
                    int UserID = Result.getInt("UserID");
                    if(BCrypt.checkpw(Password, StoredHash)){
                        return new AuthResult(UserID, Role);
                    }
                }
            }
            return null;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //This method basically used to add new user into the AuthDB database
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

    //This helps to change the password in database
    public boolean ChangePassword(String Email, String NewPassword) {
        String NewPasswordHash = BCrypt.hashpw(NewPassword, BCrypt.gensalt());
        String SQL = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection AuthDBConnector = DatabaseUtil.GetAuthConnection(); PreparedStatement Statement = AuthDBConnector.prepareStatement(SQL)) {
            Statement.setString(1, NewPasswordHash);
            Statement.setString(2, Email);
            int rowsAffected = Statement.executeUpdate();
            return rowsAffected > 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * NEW METHOD: Fetches all users from the AuthDB.
     */
    public List<AuthUserInfo> GetAllAuthUsers() {
        List<AuthUserInfo> users = new ArrayList<>();
        String SQL = "SELECT UserID, Email, Role FROM Users";

        // This method can manage its own connection
        try (Connection conn = DatabaseUtil.GetAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new AuthUserInfo(
                        rs.getInt("UserID"),
                        rs.getString("Email"),
                        rs.getString("Role")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}