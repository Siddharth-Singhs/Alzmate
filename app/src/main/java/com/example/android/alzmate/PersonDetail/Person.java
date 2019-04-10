package com.example.android.alzmate.PersonDetail;



public class Person {
    public String name;
    public String relationship;
    public String bio;
    public String imageUrl;
    public Person(String name,String relationship,String bio,String imageUrl)
    {
        this.name=name;
        this.relationship=relationship;
        this.bio=bio;
        this.imageUrl=imageUrl;
    }
    public String getName()
    {
        return  name;
    }
    public String getRelationship()
    {
        return relationship;
    }
    public String getBio()
    {
        return  bio;
    }
    public String getImageUrl()
    {
        return imageUrl;
    }
}
