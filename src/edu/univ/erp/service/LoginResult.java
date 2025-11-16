package edu.univ.erp.service;

public class LoginResult {
    public final int userId;
    public final String Role;
    public final String Message;
    public final boolean isSuccess;

    public LoginResult(int userId, String Role, String Message){
        this.userId = userId;
        this.Role = Role;
        this.Message = Message;
        if(Role!=null){
            this.isSuccess = true;
        }
        else{
            this.isSuccess = false;
        }
    }
}
