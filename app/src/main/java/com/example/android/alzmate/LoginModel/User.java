package com.example.android.alzmate.LoginModel;


public class User {
    private String nameAlz;
    private String emailAlz;
    private String AlzImageURL;

    public User(String nameAlz,String emailAlz,String AlzImageURL)
    {
        this.nameAlz=nameAlz;
        this.emailAlz=emailAlz;
        this.AlzImageURL=AlzImageURL;
    }
    public String getNameAlz()
    {
        return nameAlz;
    }
    public String getEmailAlz()
    {
        return emailAlz;
    }
    public String getAlzImageURL()
    {
        return AlzImageURL;
    }
}
