package edu.univ.erp.data;

import edu.univ.erp.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//This class handles all the data related to the notifications table in the Notifications table
public class NotificationDAO {
    //This method is used to add notification to the notification table in StudentDB
    public void AddNotification(String Message){
        String SQL = "INSERT INTO Notifications (Message) VALUES (?)";
        try(Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            Statement.setString(1, Message);
            Statement.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    //This method gets the 10 most recent notifications to display on the notifications panel
    //It adds the messages from the database to the String ArrayList in decreasing order of UserID
    //The higher the ID the more late is the entry and thus the later the message
    public List<String> GetRecentNotifications(){
        List<String> Messages = new ArrayList<>();
        String SQL = "SELECT Message, CreatedAt FROM Notifications ORDER BY NotificationID DESC LIMIT 10";
        try(Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL); ResultSet Result = Statement.executeQuery()){
            while(Result.next()){
                String Message = Result.getString("Message");
                String Time = Result.getTimestamp("CreatedAt").toString().substring(0,16); //Cut off the seconds
                Messages.add("["+Time+"]"+ Message);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Messages;
    }
}
