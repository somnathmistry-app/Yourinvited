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
import com.devonetech.android.yourinvited.event_section.EventDetailsActivity;
import com.devonetech.android.yourinvited.event_section.model.EventModel;
import com.devonetech.android.yourinvited.social_network.ShareLocationActivity;
import com.devonetech.android.yourinvited.social_network.TagFriendsActivity;
import com.devonetech.android.yourinvited.social_network.model.LocationModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AllAddressAdapter extends  RecyclerView.Adapter<AllAddressAdapter.ViewHolder> {


    private Context context;
    private int loCount;
    ArrayList<LocationModel> itemms;

    public AllAddressAdapter(Context context, ArrayList<LocationModel> items) {
        this.itemms = items;
        this.context = context;
    }
   /* public AllAddressAdapter(Context context) {
        this.context = context;
    }*/


    @Override
    public AllAddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_location, parent, false);
        return new AllAddressAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllAddressAdapter.ViewHolder holder, final int position) {

        holder.event_title.setText(itemms.get(position).location_title);
        holder.event_location.setText(itemms.get(position).location_name);
        Picasso.with(holder.event_image.getContext()).load(itemms.get(position).image).into(holder.event_image);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,TagFriendsActivity.class);
                intent.putExtra("search_location", itemms.get(position).location_name);
                intent.putExtra("search_lat", itemms.get(position).lati);
                intent.putExtra("search_laong", itemms.get(position).longi);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemms.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView event_image;

        public TextView event_title,event_location;

        public ViewHolder(View itemView) {
            super(itemView);
            event_image = (ImageView) itemView.findViewById(R.id.image);
            event_title=itemView.findViewById(R.id.name);
            event_location=itemView.findViewById(R.id.email);


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
