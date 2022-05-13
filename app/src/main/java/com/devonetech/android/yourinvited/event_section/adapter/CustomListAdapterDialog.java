package com.devonetech.android.yourinvited.event_section.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.model.EventModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class CustomListAdapterDialog extends BaseAdapter {

    private ArrayList<EventModel> listData;

    private LayoutInflater layoutInflater;

    public CustomListAdapterDialog(Context context, ArrayList<EventModel> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_of_people, null);
            holder = new ViewHolder();
            holder.unitView = (TextView) convertView.findViewById(R.id.name);
            holder.picture = convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.unitView.setText(listData.get(position).name);
        //holder.quantityView.setText(listData.get(position).getUnit().toString());
        Picasso.with(holder.picture.getContext()).load(listData.get(position).profile_image).into(holder.picture);
        return convertView;
    }

    static class ViewHolder {
        TextView unitView;
        ImageView picture;
    }

}