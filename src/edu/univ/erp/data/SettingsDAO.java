package edu.univ.erp.data;

import edu.univ.erp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


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
}
