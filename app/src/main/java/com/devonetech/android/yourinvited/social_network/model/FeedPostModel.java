package com.devonetech.android.yourinvited.social_network.model;

public class FeedPostModel {

    public String post_id;
    public String usrname;
    public String profile_pic;
    public String post_description;
    public String post_date;
    public String post_type;
    public  String post_like_list;
    public String post_comment_list;
    public  int like_count;
    public String like_stat;
    public int comnt_count;

    public String image;
    public  String video_url;
    public String video_file;
    public String url;
    public String youtubecode;
    public String location;
    public String lati;
    public String longi;
    public String tag;
    public String cover_image;
    public String posted_by;



    public FeedPostModel(String post_id,String usrname,String profile_pic,String cover_image,
                        String post_description,String post_date,String post_type,String post_like_list,
                        String post_comment_list,int likecount,String like_stat,int comnt_count,
                         String image,String video_url,String video_file,String url,String youtubecode,
                         String location,String lati,String longi,String tag,String posted_by){

        this.post_id=post_id;
        this.usrname=usrname;
        this.profile_pic=profile_pic;
        this.cover_image=cover_image;
        this.post_description=post_description;
        this.post_date=post_date;
        this.post_type=post_type;
        this.post_like_list=post_like_list;
        this.post_comment_list=post_comment_list;
        this.like_count=likecount;
        this.like_stat=like_stat;
        this.comnt_count=comnt_count;

        this.image=image;
        this.video_url=video_url;
        this.video_file=video_file;
        this.url=url;
        this.youtubecode=youtubecode;

        this.location=location;
        this.lati=lati;
        this.longi=longi;
        this.tag=tag;
        this.posted_by=posted_by;




    }

    public String ingroup;
    public String group_id;
    public String group_name;
    public String group_image;
    public String group_member;


    public FeedPostModel(String post_id,String usrname,String profile_pic,String cover_image,
                         String post_description,String post_date,String post_type,String post_like_list,
                         String post_comment_list,int likecount,String like_stat,int comnt_count,
                         String image,String video_url,String video_file,String url,String youtubecode,
                         String location,String lati,String longi,String tag,String posted_by,
                         String ingroup,String group_id,String group_name,String group_image,String group_member){

        this.post_id=post_id;
        this.usrname=usrname;
        this.profile_pic=profile_pic;
        this.cover_image=cover_image;
        this.post_description=post_description;
        this.post_date=post_date;
        this.post_type=post_type;
        this.post_like_list=post_like_list;
        this.post_comment_list=post_comment_list;
        this.like_count=likecount;
        this.like_stat=like_stat;
        this.comnt_count=comnt_count;

        this.image=image;
        this.video_url=video_url;
        this.video_file=video_file;
        this.url=url;
        this.youtubecode=youtubecode;

        this.location=location;
        this.lati=lati;
        this.longi=longi;
        this.tag=tag;
        this.posted_by=posted_by;
        this.ingroup=ingroup;
        this.group_id=group_id;
        this.group_name=group_name;
        this.group_image=group_image;
        this.group_member=group_member;




    }


    public String name;
    public String user_id;

    public FeedPostModel(String name,String user_id){
        this.name=name;
        this.user_id=user_id;

    }



    public String comment_stat;
    public String comment_by;
    public String comment_by_pic;
    public String comment_txt;
    public String comment_time;




    public FeedPostModel(String comment_stat,String comment_by,String comment_by_pic,String comment_txt,String comment_time){

        this.comment_stat=comment_stat;
        this.comment_by=comment_by;
        this.comment_by_pic=comment_by_pic;
        this.comment_txt=comment_txt;
        this.comment_time=comment_time;
    }

    public String isFriend;


    public FeedPostModel(String name,String user_id,String image,String isFriend){
        this.name=name;
        this.user_id=user_id;
        this.image=image;
        this.isFriend=isFriend;

    }


    public FeedPostModel() {

    }
}
