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
    //If it is valid then it returns an AuthResult object
    //The string SQL is basically the query that we would use to read the desired things from the table of Users in AuthDB
    //The query is prepared using the .preparedStatment() and the ? used as place-holders are set using the .setString method
    //This CheckLogin is used in the AuthService and basically checks if the Email and password used are valid
    //It returns the UserID and the role of the person trying to log in using the record AuthResult
    public AuthResult CheckLogin(String Email, String Password){
        String SQL = "SELECT UserID, PasswordHash, Role FROM Users WHERE Email = ?";
        try(Connection AuthDBConnector = DatabaseUtil.GetAuthConnection(); PreparedStatement Statement = AuthDBConnector.prepareStatement(SQL)){
            Statement.setString(1, Email);
            try(ResultSet Result = Statement.executeQuery()){
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
            System.out.println("Failure at CheckLogin in UserDAO");
            return null;
        }
    }


    //This method basically used to add new user into the AuthDB database
    //This method encrypts the password provided (that would initially be "defaultPassword123") using the BCrypt.hashpw
    //The .executeUpdate() basically runs the SQL query
    public int CreateAuthDBUser(Connection AuthDBConnection, String Email, String Password, String Role) throws Exception{
        String PasswordHash = BCrypt.hashpw(Password, BCrypt.gensalt());
        String SQL = "INSERT INTO Users (Email, PasswordHash, Role) VALUES (?, ?, ?)";
        try(PreparedStatement Statement = AuthDBConnection.prepareStatement(SQL, java.sql.Statement.RETURN_GENERATED_KEYS)){
            //The RETURN_GENERATED_KEYS returns the Identity (UserID in this case) of the Statement
            Statement.setString(1, Email);
            Statement.setString(2, PasswordHash);
            Statement.setString(3, Role);
            Statement.executeUpdate();
            try(ResultSet Result = Statement.getGeneratedKeys()){
                if(Result.next()){
                    //This means that the User has been inserted in the table of the database
                    return Result.getInt(1);
                }
                else{
                    System.out.println("Failed to create AuthDB user");
                    throw new Exception("Failed to create AuthDB user");
                }
            }
        }
    }


    //This helps to change the password in database
    //In this we encrypt the new password that would be provided in the change password page
    //After encryption we replace the encryption of old password with the new password's encryption
    //The executeUpdate() returns the total number of rows affected by the SQL query
    //The function returns weather the password was affected
    public boolean ChangePassword(String Email, String NewPassword) {
        String NewPasswordHash = BCrypt.hashpw(NewPassword, BCrypt.gensalt());
        String SQL = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection AuthDBConnector = DatabaseUtil.GetAuthConnection(); PreparedStatement Statement = AuthDBConnector.prepareStatement(SQL)) {
            Statement.setString(1, NewPasswordHash);
            Statement.setString(2, Email);
            int RowsAffected = Statement.executeUpdate();
            return RowsAffected > 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //This method basically returns all the users information in the AuthDB table Users
    //It returns the ArrayList of the record AuthUserInfo that has the same arguments as the columns of the table Ussers
    public List<AuthUserInfo> GetAllAuthUsers() {
        List<AuthUserInfo> Users = new ArrayList<>();
        String SQL = "SELECT UserID, Email, Role FROM Users";
        try (Connection conn = DatabaseUtil.GetAuthConnection(); PreparedStatement stmt = conn.prepareStatement(SQL); ResultSet Result = stmt.executeQuery()) {
            while (Result.next()) {
                Users.add(new AuthUserInfo(Result.getInt("UserID"), Result.getString("Email"), Result.getString("Role")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("The GetAllAuthUsers method in UserDAO was Failure");

        }
        System.out.println("The GetAllAuthUsers method in UserDAO was success");
        return Users;
    }
}