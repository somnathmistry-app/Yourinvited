package com.devonetech.android.yourinvited.friends_section.model;


public class FriendModel {

    public String profile_image;
    public String user_id;
    public String name;
    public String email;
    public String is_friend;

    public FriendModel(String profile_image,String user_id,String name,String email,String is_friend){
        this.profile_image=profile_image;
        this.user_id=user_id;
        this.name=name;
        this.email=email;
        this.is_friend=is_friend;

    }
}
