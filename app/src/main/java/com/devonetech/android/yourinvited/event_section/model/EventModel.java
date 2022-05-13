package com.devonetech.android.yourinvited.event_section.model;

public class EventModel {

    public String event_id;
    public String name;
    public String description;
    public String location;
    public String start_date;
    public String end_date;
    public String image;
    public String lat;
    public String longi;
    public String member;

    public EventModel(String event_id,String name,String description,String location,String start_date,String end_date,
                      String image,String lat,String longi,String member){
        this.event_id=event_id;
        this.name=name;
        this.description=description;
        this.location=location;
        this.start_date=start_date;
        this.end_date=end_date;
        this.image=image;
        this.lat=lat;
        this.longi=longi;
        this.member=member;

    }
    public String posted_by_user;
    public String category_name;
    public String request_status;
    public EventModel(String event_id,String name,String description,String location,String start_date,String end_date,
                      String image,String lat,String longi,String member,String posted_by_user,String category_name,String request_status){
        this.event_id=event_id;
        this.name=name;
        this.description=description;
        this.location=location;
        this.start_date=start_date;
        this.end_date=end_date;
        this.image=image;
        this.lat=lat;
        this.longi=longi;
        this.member=member;
        this.posted_by_user=posted_by_user;
        this.category_name=category_name;
        this.request_status=request_status;


    }

    public String user_id;
    public String profile_image;
    public double user_lat;
    public double user_long;
    public String status;

    public EventModel(String user_id,String name,String profile_image,double user_lat,double user_long,String status){
        this.user_id=user_id;
        this.name=name;
        this.profile_image=profile_image;
        this.user_lat=user_lat;
        this.user_long=user_long;
        this.status=status;


    }

    public EventModel(String user_id,String name,String profile_image){
        this.user_id=user_id;
        this.name=name;
        this.profile_image=profile_image;


    }




}