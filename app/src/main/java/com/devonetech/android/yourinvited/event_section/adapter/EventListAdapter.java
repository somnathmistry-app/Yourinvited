package com.devonetech.android.yourinvited.event_section.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.EventDetailsActivity;
import com.devonetech.android.yourinvited.event_section.NearbyPlaceActivity;
import com.devonetech.android.yourinvited.event_section.model.EventModel;
import com.devonetech.android.yourinvited.shared.Updatedlatlong;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {


    private Context context;
    private int loCount;
    ArrayList<EventModel> itemms;

    Updatedlatlong lat_psh;

      public EventListAdapter(Context context, ArrayList<EventModel> items) {
          this.itemms = items;
          this.context = context;
      }
   /* public EventListAdapter(Context context) {
        this.context = context;
    }*/


    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_events, parent, false);
        return new EventListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventListAdapter.ViewHolder holder, final int position) {

        lat_psh=new Updatedlatlong(context);

        holder.event_title.setText(itemms.get(position).name);
        holder.event_location.setText(itemms.get(position).location);
        Picasso.with(holder.event_image.getContext()).load(itemms.get(position).image).into(holder.event_image);
        try {
            JSONArray jj=new JSONArray(itemms.get(position).member);
            for (int i = 0; i <jj.length() ; i++) {

                holder.no_of_people.setText("   "+String.valueOf(i+1)+" People Going");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        holder.viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, EventDetailsActivity.class);
                i.putExtra("id",itemms.get(position).event_id);
                context.startActivity(i);
            }
        });

        holder.eventTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?saddr=" + lat_psh.getUserUpdatedLatitude() + "," + lat_psh.getUserUpdatedLongitude() + "&daddr=" + itemms.get(position).lat + "," + itemms.get(position).longi;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });

        holder.nearByplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, NearbyPlaceActivity.class);
                i.putExtra("lat",itemms.get(position).lat);
                i.putExtra("long",itemms.get(position).longi);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemms.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView event_image;

        public TextView event_title,event_location,no_of_people;
        public TextView viewAll,eventTracking,nearByplace;

        public ViewHolder(View itemView) {
            super(itemView);
            event_image = (ImageView) itemView.findViewById(R.id.image1);
            event_title=itemView.findViewById(R.id.textView7);
            event_location=itemView.findViewById(R.id.pro_name);
            no_of_people=itemView.findViewById(R.id.pro_price);

            viewAll=itemView.findViewById(R.id.view_all_user);
            eventTracking=itemView.findViewById(R.id.event_tracking);
            nearByplace=itemView.findViewById(R.id.near_by_place);

        }
    }
    private void openActivityVdMultipleViews(Context context, View imageViewBg,
                                             View textViewTitle, String url
    ) {

       /* Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("Url",url);
        Pair<View, String> p1 = Pair.create(imageViewBg, "iv");
        Pair<View, String> p2 = Pair.create(textViewTitle, "title_animator");

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2);
        ActivityCompat.startActivity((Activity) context, intent, options.toBundle());*/

    }
}
