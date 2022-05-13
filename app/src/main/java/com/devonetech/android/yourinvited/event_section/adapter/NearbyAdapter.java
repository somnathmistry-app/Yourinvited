package com.devonetech.android.yourinvited.event_section.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.model.NearbyPlaceModel;

import java.util.ArrayList;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder> {

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView yacht_type,yacht_requested_by,yacht_date,yacht_person;
        public ImageView vv;

        public ViewHolder(View view) {
            super(view);


            yacht_type=(TextView)view.findViewById(R.id.yacht__name);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

    }
    private final Activity mActivity;

    OnItemClickListener mItemClickListener;
    String IMg;
    private ArrayList<NearbyPlaceModel> mUserDetails = new ArrayList<NearbyPlaceModel>();
    private String type;


    public NearbyAdapter(Activity mActivity,ArrayList<NearbyPlaceModel> mUserDetails,String type) {
        this.mActivity = mActivity;
        this.mUserDetails=mUserDetails;
        this.type=type;
        // createUserDetails();
    }



    @Override
    public int getItemCount() {
        return mUserDetails.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder , final int position) {

        holder.yacht_type.setText(mUserDetails.get(position).name);






    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.nearby_item, parent, false);
        return new ViewHolder(sView);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }





}
