package com.example.android.alzmate.EventDetail;


public class EventHolder {
    public String title;
    public String body;
    public String time;

    public EventHolder(String title, String body, String time)
    {
        this.title=title;
        this.body=body;
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
    public String getTime()
    {
        return time;
    }
}
