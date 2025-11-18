package edu.univ.erp.data;

import edu.univ.erp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//This class is basically used to access the SystemSetting table in the StudentDB
//That table just contains weather the maintenance mode is on or off
public class SettingsDAO {
    public boolean IsMaintenanceModeOn(){
        String SQL = "SELECT SettingValue FROM SystemSettings WHERE SettingKey = 'MaintenanceMode'";
        try(Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            try(ResultSet Result = Statement.executeQuery()){
                if(Result.next()){
                    return "true".equalsIgnoreCase(Result.getString("SettingValue"));
                }
            }
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    //This basically updates the maintenance mode in the database table
    public boolean SetMaintenanceMode(boolean is_on){
        String SQL = "UPDATE SystemSettings SET SettingValue = ? WHERE SettingKey = 'MaintenanceMode'";
        try(Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL)){
            Statement.setString(1, String.valueOf(is_on)); //true or false
            int RowsAffected = Statement.executeUpdate();
            return RowsAffected>0;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    //This method adds deadline to the table in database
    public String GetDeadline(){
        String SQL = "SELECT SettingValue FROM SystemSettings WHERE SettingKey = 'Deadline'";
        try(Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(SQL); ResultSet Result = Statement.executeQuery()){
            if(Result.next()){
                return Result.getString("SettingValue");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return "2099-01-1";
        //Default deadline
    }


    public boolean SetDeadline(String DateString){
        String UpdateSQL = "UPDATE SystemSettings SET SettingValue = ? WHERE SettingKey = 'Deadline'";
        try(Connection StudentDBConnection = DatabaseUtil.GetStudentConnection(); PreparedStatement Statement = StudentDBConnection.prepareStatement(UpdateSQL)){
            Statement.setString(1, DateString);
            int Rows = Statement.executeUpdate();
            if(Rows>0){
                return true;
            }
            String InsertSQL = "INSERT INTO SystemSettings (SettingKey, SettingValue) VALUES ('Deadline', ?)";
            try(PreparedStatement InsertStatement = StudentDBConnection.prepareStatement(InsertSQL)){
                InsertStatement.setString(1, DateString);
                return InsertStatement.executeUpdate()>0;
            }
        }
        catch (Exception e){
            e.printStackTrace();;
            return false;
        }
    }
}
