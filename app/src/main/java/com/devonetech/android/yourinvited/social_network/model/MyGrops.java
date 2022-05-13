package com.devonetech.android.yourinvited.social_network.model;

public class MyGrops {

    public String id;
    public String group_id;
    public String name;
    public String image;
    public String privacy;
    public String member;
    public String member_list;


    public MyGrops(String id,String group_id,String name,String image,String privacy,String member,String member_list){
        this.id=id;
        this.group_id=group_id;
        this.name=name;
        this.image=image;
        this.privacy=privacy;
        this.member=member;
        this.member_list=member_list;


    }
    public String user_id;
    public String picture;


    public MyGrops(String name,String user_id,String picture){

        this.name=name;
        this.user_id=user_id;
        this.picture=picture;
    }



}
