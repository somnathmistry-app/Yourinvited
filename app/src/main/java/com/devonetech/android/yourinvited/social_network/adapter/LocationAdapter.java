package com.devonetech.android.yourinvited.social_network.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom.Circle_Image;
import com.devonetech.android.yourinvited.social_network.FabLocationActivity;
import com.devonetech.android.yourinvited.social_network.model.LocationModel;
import com.devonetech.android.yourinvited.social_network.model.LocationModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    Activity context;
    ArrayList<LocationModel> datalist;
    private LayoutInflater inflater;


    public LocationAdapter(Activity context,ArrayList<LocationModel> datalist) {
        this.context = context;
        this.datalist=datalist;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.save_loc_footer, parent, false);
            return new FooterViewHolder (v);
        } else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_save_location, parent, false);
            return new GenericViewHolder (v);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;

            footerHolder.txtTitleFooter.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View view) {

                    Intent i=new Intent(context, FabLocationActivity.class);
                    context.startActivity(i);

                }
            });
        } else if(holder instanceof GenericViewHolder) {
            GenericViewHolder genericViewHolder = (GenericViewHolder) holder;
            genericViewHolder.txtName.setText (datalist.get(position).location_title);
            genericViewHolder.txtTitle.setText (datalist.get(position).location_name);

            if (!datalist.get(position).image.equals("")) {
                try {
                    String apiLink = datalist.get(position).image;

                    //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                    String encodedurl = "";
                    encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                            apiLink.lastIndexOf('/') + 1));
                    Log.d("UP Social List adapter", "encodedurl:"+encodedurl);
                    if (!apiLink.equals("") && apiLink != null) {
                    Picasso.with(context)
                            .load(encodedurl) // load: This path may be a remote URL,
                            .placeholder(R.drawable.no_data_found_1x)
                            .resize(130, 130)
                            .error(R.drawable.no_data_found_1x)
                            .centerCrop()
                            .into(genericViewHolder.locationPic);
                        // Into: ImageView into which the final image has to be passed


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            genericViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {




                    editloc(position);




                    return false;
                }
            });

            genericViewHolder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    delete_loc(position);
                }
            });

            genericViewHolder.shareImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String uri = "http://maps.google.com/maps?saddr=" +datalist.get(position).lati+","+datalist.get(position).longi;

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String ShareSub = datalist.get(position).location_title;
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });

        }

    }

    @Override
    public int getItemViewType (int position) {
        if(isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter (int position) {
        return position == datalist.size();
    }


    @Override
    public int getItemCount () {
        return datalist.size() + 1;
    }


    class FooterViewHolder extends RecyclerView.ViewHolder {
        FrameLayout txtTitleFooter;

        public FooterViewHolder (View itemView) {
            super (itemView);
            this.txtTitleFooter = (FrameLayout) itemView.findViewById (R.id.fl);
        }
    }

    class GenericViewHolder extends RecyclerView.ViewHolder {
        TextView txtName,txtTitle;
        ImageView deleteImg,shareImg,locationPic;


        public GenericViewHolder (View itemView) {
            super (itemView);
             this.txtName = (TextView) itemView.findViewById (R.id.customer_name_tv);
            this.txtTitle = (TextView) itemView.findViewById (R.id.company_name_tv);
            this.deleteImg=itemView.findViewById(R.id.delete_item);
            this.shareImg=itemView.findViewById(R.id.pic_iv);
            this.locationPic=itemView.findViewById(R.id.picture);
        }
    }


    private void editloc(final int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.edit_item, null);
        final EditText editTitle = (EditText) content.findViewById(R.id.title);
        final EditText editAuthor = (EditText) content.findViewById(R.id.author);
        final EditText editThumbnail = (EditText) content.findViewById(R.id.thumbnail);


        String title = datalist.get(position).location_title;

        editTitle.setText(title);
        // editAuthor.setText(book.getAuthor());
        // editThumbnail.setText(book.getImageUrl());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(content)
                .setTitle("Edit place name")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {





                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void delete_loc(int position) {

        datalist.remove(position);

        notifyDataSetChanged();
    }

}
