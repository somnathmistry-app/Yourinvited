package com.devonetech.android.yourinvited.event_section.model;

public class NearbyPlaceModel {

    public String type;
    public String name;
    public String vicinity;

    public NearbyPlaceModel(String type){
        this.type=type;
    }

    public NearbyPlaceModel(String name,String vicinity){
        this.name=name;
        this.vicinity=vicinity;

    }
}
