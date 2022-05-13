package com.devonetech.android.yourinvited.social_network.group.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom.Circle_Image;
import com.devonetech.android.yourinvited.social_network.friend_profile.FriendProfileActivity;
import com.devonetech.android.yourinvited.social_network.group.ViewGroupActivity;
import com.devonetech.android.yourinvited.social_network.model.MyGrops;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MembersAdapter extends  RecyclerView.Adapter<MembersAdapter.ViewHolder> {


    private Context context;
    private ArrayList<MyGrops> groupList;


    public MembersAdapter(Context context,ArrayList<MyGrops> groupList) {

        this.context = context;
        this.groupList=groupList;
    }



    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_members, parent, false);
        return new MembersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MembersAdapter.ViewHolder holder, final int position) {
        holder.event_title.setText(groupList.get(position).name);
        Picasso.with(holder.event_image.getContext()).load(groupList.get(position).picture).into(holder.event_image);



        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,FriendProfileActivity.class);
                i.putExtra("userpic", groupList.get(position).profile_pic);
                i.putExtra("coverpic", groupList.get(position).cover_image);
                i.putExtra("name", groupList.get(position).usrname);
                i.putExtra("friend_id", groupList.get(position).posted_by);
                //i.putExtra("file",mUserDetails.get(position).video_file);
                context.startActivity(i);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public Circle_Image event_image;

        public TextView event_title;

        public ViewHolder(View itemView) {
            super(itemView);
            event_image = itemView.findViewById(R.id.image1);
            event_title=itemView.findViewById(R.id.textView7);


        }
    }

}
