package com.devonetech.android.yourinvited.event_section.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.model.NearByMapModel;

import java.util.List;

/**
 * Created by Developer on 12/29/2017.
 */

public class NearByMapAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private String view_status;
    private List<NearByMapModel> contentArray;

    public NearByMapAdapter(Context context, List<NearByMapModel> contentArray, String view_status) {
        this.context = context;
        this.view_status = view_status;
        this.contentArray = contentArray;
    }

    @Override
    public int getCount() {
        return contentArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        NearByMapAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_nearby_map, parent, false);
            viewHolder = new NearByMapAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NearByMapAdapter.ViewHolder) convertView.getTag();
        }


        viewHolder.setValue( contentArray.get(position));

        return convertView;
    }

    class ViewHolder {
        private TextView name_tv,address_tv,status_tv;
        private RatingBar rating;

        public ViewHolder(View view) {
            name_tv = (TextView) view.findViewById(R.id.name_tv);
            address_tv = (TextView) view.findViewById(R.id.address_tv);
            status_tv = (TextView) view.findViewById(R.id.status_tv);
            rating= (RatingBar) view.findViewById(R.id.rating);
        }

        private void setValue(NearByMapModel nearByMapModel) {
            name_tv.setText(nearByMapModel.getName());
            address_tv.setText(nearByMapModel.getVicinity());
            rating.setRating(nearByMapModel.getRating());
            if (nearByMapModel.getIsopen()){
                status_tv.setText("Open");
                status_tv.setTextColor(context.getResources().getColor(R.color.green));
            }
            else{
                status_tv.setText("Closed");
                status_tv.setTextColor(context.getResources().getColor(R.color.red));
            }


        }


    }


}
