package com.example.android.alzmate.LoginModel;



public class UserInformation {

    String nameAlz;
    String emailAlz;
    boolean Authority;

    public UserInformation(String nameAlz, String emailAlz)
    {
        this.nameAlz=nameAlz;
        this.emailAlz=emailAlz;
        Authority=false;
    }
}