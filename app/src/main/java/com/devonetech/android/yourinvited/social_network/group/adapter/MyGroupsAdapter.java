package com.devonetech.android.yourinvited.social_network.group.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom.Circle_Image;
import com.devonetech.android.yourinvited.social_network.TagFriendsActivity;
import com.devonetech.android.yourinvited.social_network.group.ViewGroupActivity;
import com.devonetech.android.yourinvited.social_network.model.LocationModel;
import com.devonetech.android.yourinvited.social_network.model.MyGrops;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyGroupsAdapter extends  RecyclerView.Adapter<MyGroupsAdapter.ViewHolder> {


    private Context context;
    private ArrayList<MyGrops> groupList;


    public MyGroupsAdapter(Context context,ArrayList<MyGrops> groupList) {

        this.context = context;
        this.groupList=groupList;
    }



    @Override
    public MyGroupsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_groups, parent, false);
        return new MyGroupsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyGroupsAdapter.ViewHolder holder, final int position) {
        holder.event_title.setText(groupList.get(position).name);
        holder.count.setText(groupList.get(position).member);
        Picasso.with(holder.event_image.getContext()).load(groupList.get(position).image).into(holder.event_image);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ViewGroupActivity.class);
                intent.putExtra("group_id", groupList.get(position).group_id);
                intent.putExtra("group_name", groupList.get(position).name);
                intent.putExtra("group_image", groupList.get(position).image);
                intent.putExtra("group_member", groupList.get(position).member);
                intent.putExtra("group_member_list", groupList.get(position).member_list);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public Circle_Image event_image;

        public TextView event_title,count;

        public ViewHolder(View itemView) {
            super(itemView);
            event_image = itemView.findViewById(R.id.image1);
            event_title=itemView.findViewById(R.id.textView7);
            count=itemView.findViewById(R.id.textView14);


        }
    }

}
