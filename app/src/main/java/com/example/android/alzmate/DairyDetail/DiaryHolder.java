package com.example.android.alzmate.DairyDetail;



public class DiaryHolder {
    private String title;
    private String body;
    private String date;
    private String time;
    public DiaryHolder(String title, String body, String date,String time)
    {
        this.title=title;
        this.body=body;
        this.date=date;
        this.time=time;
    }
    public String getTitle()
    {
        return title;
    }
    public String getBody()
    {
        return body;
    }
    public String getDate()
    {
        return date;
    }
    public String getTime()
    {
        return time;
    }
}
