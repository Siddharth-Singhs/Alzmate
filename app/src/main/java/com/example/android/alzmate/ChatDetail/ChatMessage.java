package com.example.android.alzmate.ChatDetail;



public class ChatMessage {
    public String name;
    public String messageText;
    public String messageTime;
    public String careTaker;
    public String imageURL;
    public ChatMessage(String name,String messageText,String messageTime)
    {
        this.name=name;
        this.messageText=messageText;
        this.messageTime=messageTime;
    }
    public ChatMessage(String name,String messageText,String messageTime,String careTaker,String imageURL)
    {
        this.name=name;
        this.messageText=messageText;
        this.messageTime=messageTime;
        this.careTaker=careTaker;
        this.imageURL=imageURL;
    }
    public String getName()
    {
        return name;
    }
    public String getMessageText()
    {
        return messageText;
    }
    public String getMessageTime()
    {
        return messageTime;
    }

    public String getCareTaker()
    {
        return careTaker;
    }
    public String getImageURL()
    {
        return imageURL;
    }
}
