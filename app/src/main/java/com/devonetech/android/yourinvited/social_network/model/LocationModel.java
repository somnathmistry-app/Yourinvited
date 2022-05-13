package com.devonetech.android.yourinvited.social_network.model;

public class LocationModel {

    public String id;
    public String location_name;
    public String location_title;
    public String lati;
    public String longi;
    public String user_id;
    public String image;

    public LocationModel(String id,String location_name,String location_title,String lati,String longi,String user_id,String image){
        this.id=id;
        this.location_name=location_name;
        this.location_title=location_title;
        this.lati=lati;
        this.longi=longi;
        this.user_id=user_id;
        this.image=image;

    }



}
