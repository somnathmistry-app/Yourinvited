package com.devonetech.android.yourinvited.social_network.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.LoginActivity;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.AddEventActivity;
import com.devonetech.android.yourinvited.friends_section.adapter.RequestListAdapter;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.shared.Updatedlatlong;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.ImagePagerActivity;
import com.devonetech.android.yourinvited.social_network.NewActivity;
import com.devonetech.android.yourinvited.social_network.SocialNetworkMain;
import com.devonetech.android.yourinvited.social_network.model.FeedPostModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.lopei.collageview.CollageView;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AllFeedsAdapter extends RecyclerView.Adapter<AllFeedsAdapter.ViewHolder> {


    int sizes=0,newposition,like_count=0,cmt_count=0,ncp;
    ViewHolder holder1,holder2;
    FeedPostModel obj;
    FeedPostModel obj2;
    RecyclerView comment_list;
    PostCommentAdapter pst_cmt_adapter;
    EditText commment_edt;
    ImageView send_comment;
    String Comment_Str="";
    ProgressDialog pDialog;
    Date date1;
    String str1;
    ArrayList<String> filepathstr;
    private Bitmap resized;



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView bloger_name,blog_title,like_count,comment_count,post_date;
        public ImageView _body_image,bloger_pic,play_btn,editMenu;
        public LinearLayout like_lay,unlike_lay,comment_lay;
        public VideoView video_viw;
        public CollageView collageView;
        public YouTubeThumbnailView youTubeThumbnailView;
        public FrameLayout mapLayout;
        private SupportMapFragment mapFragment;
        private TextView wasTXT,tagFirstUser,tagCount,andtxt;
        private LinearLayout tagFriendLay;
        private ImageView map_image;





        public ViewHolder(View view) {
            super(view);
            wasTXT=itemView.findViewById(R.id.was_withtxt);
            tagFirstUser=itemView.findViewById(R.id.tag_first);
            tagCount=itemView.findViewById(R.id.tag_count);
            tagFriendLay=itemView.findViewById(R.id.tag_friend_lay);
            andtxt=itemView.findViewById(R.id.andtxt);
            bloger_name = (TextView) view.findViewById(R.id.bloger_name);
            blog_title = (TextView) view.findViewById(R.id.bloger_title);
            _body_image = (ImageView) view.findViewById(R.id.body_iv);
            bloger_pic = (ImageView) view.findViewById(R.id.bloger_pic);
            like_count=(TextView)view.findViewById(R.id.like_count);
            comment_count=(TextView)view.findViewById(R.id.comment_count);
            play_btn=(ImageView)view.findViewById(R.id.play_btn_img);
            like_lay=(LinearLayout)view.findViewById(R.id.like_layout);
            unlike_lay=(LinearLayout)view.findViewById(R.id.unlike_layout);
            comment_lay=(LinearLayout)view.findViewById(R.id.comment_layout);
            video_viw=(VideoView)view.findViewById(R.id.VideoView);
            post_date=(TextView)view.findViewById(R.id.textView4);
            collageView = (CollageView)view. findViewById(R.id.collageView);
            editMenu=view.findViewById(R.id.imageView3);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
            mapLayout = itemView.findViewById(R.id.map);
            map_image=itemView.findViewById(R.id.map_iv);



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
    private ArrayList<FeedPostModel> mUserDetails;

    private ArrayList<FeedPostModel> UserComment;

    //private String imagelink="http://dev.goigi.biz/memeyatcht/uploads/userpost/";
    //private String userimagelink="http://dev.goigi.biz/memeyatcht/uploads/member/";

    private ArrayList<String> likesize;
    ConnectionDetector connection;
    private MultipartEntity reqEntity;
    UserShared psh;
    String responseString=null;
    private ProgressDialog progressDialog;
    private static String TAG;
    View popUpView;

    private MediaController mediacontroller;
    private Uri uri;
    int imagePosition=0;
    Marker destination_marker = null;
    private ArrayList<FeedPostModel> tagFriends;

    Updatedlatlong lat_psh;
    SharedPreferences prefs;

    public AllFeedsAdapter(Activity mActivity,ArrayList<FeedPostModel> mUserDetail) {
        this.mActivity = mActivity;
        this.mUserDetails=mUserDetail;
        // createUserDetails();
    }

    @Override
    public int getItemCount() {
        return mUserDetails.size();
    }

    @SuppressWarnings("unused")
    @Override
    public void onBindViewHolder(final ViewHolder holder , final int position) {

        holder1=holder;
        connection=new ConnectionDetector(mActivity);
        psh=new UserShared(mActivity);
        lat_psh=new Updatedlatlong(mActivity);
        holder.bloger_name.setText(mUserDetails.get(position).usrname);

        holder.setIsRecyclable(false);

        holder.post_date.setText(getDateTime(mUserDetails.get(position).post_date));

        if (mUserDetails.get(position).usrname.equals(psh.getUserName())){

            holder.editMenu.setVisibility(View.VISIBLE);

        }
        else {
            holder.editMenu.setVisibility(View.GONE);
        }


        try {
            JSONArray jj=new JSONArray(mUserDetails.get(position).tag);

            if (jj.length()>0){
                tagFriends=new ArrayList<>();
                for (int i = 0; i <jj.length() ; i++) {

                    JSONObject obj_tag=jj.getJSONObject(i);

                    FeedPostModel itemTag=new FeedPostModel(obj_tag.getString("name"),obj_tag.getString("user_id"));
                    tagFriends.add(itemTag);

                }

                holder.wasTXT.setText("was with");
                holder.tagFriendLay.setVisibility(View.VISIBLE);
                holder.tagFirstUser.setText(tagFriends.get(0).name);
                if (tagFriends.size()==1){
                    holder.tagCount.setText("");
                    holder.andtxt.setText("");
                }
                else {
                    holder.tagCount.setText(tagFriends.size()-1+" others");
                }


                holder.tagCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });



            }
            else {
                holder.wasTXT.setText("");
                holder.tagFriendLay.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            //JSONArray jj=new JSONArray(mUserDetails.get(position).post_like_list);
            for (int i = 0; i < mUserDetails.get(position).post_like_list.length(); i++) {

                //JSONObject ij=new JSONArray(mUserDetails.get(position).post_like_list).getJSONObject(i);

                if (new JSONArray(mUserDetails.get(position).post_like_list).length()!=0) {

                    //sizes=jj.length();
                    holder.like_count.setText(String.valueOf(mUserDetails.get(position).like_count));
                    //JSONObject jobj=new JSONArray(mUserDetails.get(position).post_like_list).getJSONObject(i);

                    //String my_id=jobj.getString("user_id");
                    //String my_like_status=jobj.getString("l_status");
                    if ( mUserDetails.get(position).like_stat.equals("1")) {

                        holder.like_lay.setVisibility(View.GONE);
                        holder.unlike_lay.setVisibility(View.VISIBLE);
                    }
                    else if( mUserDetails.get(position).like_stat.equals("0")){
                        holder.like_lay.setVisibility(View.VISIBLE);
                        holder.unlike_lay.setVisibility(View.GONE);
                    }
					
					


                }
                else {
                    holder.like_count.setText(String.valueOf(mUserDetails.get(position).like_count));
                    //JSONObject jobj=new JSONArray(mUserDetails.get(position).post_like_list).getJSONObject(i);

                    //String my_id=jobj.getString("user_id");
                    //String my_like_status=jobj.getString("l_status");
                    if ( mUserDetails.get(position).like_stat.equals("1")) {

                        holder.like_lay.setVisibility(View.GONE);
                        holder.unlike_lay.setVisibility(View.VISIBLE);
                    }
                    else if( mUserDetails.get(position).like_stat.equals("0")){
                        holder.like_lay.setVisibility(View.VISIBLE);
                        holder.unlike_lay.setVisibility(View.GONE);
                    }
                }


            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //	String ss1=mUserDetails.get(position).post_comment_list;

        try {
            //JSONArray jj=new JSONArray(mUserDetails.get(position).post_comment_list);
            for (int i = 0; i < new JSONArray(mUserDetails.get(position).post_comment_list).length(); i++) {

                //JSONObject ij=new JSONArray(mUserDetails.get(position).post_comment_list).getJSONObject(i);

                if (!(new JSONArray(mUserDetails.get(position).post_comment_list).getJSONObject(i)==null)) {

                    //int size=new JSONArray(mUserDetails.get(position).post_comment_list).length();
                    //cmt_count=Integer.valueOf(String.valueOf(new JSONArray(mUserDetails.get(position).post_comment_list).length()));
                    holder.comment_count.setText(String.valueOf(mUserDetails.get(position).comnt_count));
                }
                else {
                    holder.comment_count.setText("0");
                }


            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        if (mUserDetails.get(position).post_type.equals("image")) {

            holder.play_btn.setVisibility(View.GONE);
            holder._body_image.setVisibility(View.GONE);
            holder.collageView.setVisibility(View.VISIBLE);
            holder.youTubeThumbnailView.setVisibility(View.GONE);
            holder.mapLayout.setVisibility(View.GONE);

            holder.blog_title.setText(mUserDetails.get(position).post_description);
            filepathstr=new ArrayList<String>();
            try {
                JSONArray jj=new JSONArray(mUserDetails.get(position).image);

                for (int i = 0; i <jj.length() ; i++) {
                    JSONObject obj=jj.getJSONObject(i);
                    IMg = obj.getString("image");
                    filepathstr.add(IMg);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.collageView
                    .photoMargin(1)
                    .photoPadding(3)
                    .backgroundColor(Color.WHITE)
                    .photoFrameColor(Color.WHITE)
                    .useFirstAsHeader(true) // makes first photo fit device widtdh and use full line
                    .defaultPhotosForLine(5) // sets default photos number for line of photos (can be changed by program at runtime)

                    .useCards(true) // adds cardview backgrounds to all photos
                    .maxWidth(500) // will resize images if their side is bigger than 100
                    .placeHolder(R.drawable.no_data_found_1x ) //adds placeholder resource
                    .headerForm(CollageView.ImageForm.IMAGE_FORM_SQUARE) // sets form of image for header (if useFirstAsHeader == true)
                    .photosForm(CollageView.ImageForm.IMAGE_FORM_HALF_HEIGHT) //sets form of image for other photos
                    .loadPhotos(filepathstr);



        }
        else if (mUserDetails.get(position).post_type.equals("video_file")) {

            holder.play_btn.setVisibility(View.VISIBLE);
            holder.blog_title.setText(mUserDetails.get(position).post_description);
            holder.collageView.setVisibility(View.GONE);
            holder._body_image.setVisibility(View.VISIBLE);
            holder.youTubeThumbnailView.setVisibility(View.GONE);
            holder.mapLayout.setVisibility(View.GONE);

            try {
                resized = retriveVideoFrameFromVideo(mUserDetails.get(position).video_file);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

               holder._body_image.setImageBitmap(resized);
          //  holder._body_image.setImageResource(R.drawable.thumbnail);




        }

        else if (mUserDetails.get(position).post_type.equals("post")) {

            holder.play_btn.setVisibility(View.GONE);
            holder._body_image.setVisibility(View.GONE);
            holder.blog_title.setText(mUserDetails.get(position).post_description);
            holder.collageView.setVisibility(View.GONE);
            holder.youTubeThumbnailView.setVisibility(View.GONE);
            holder.mapLayout.setVisibility(View.GONE);
        }

        else if (mUserDetails.get(position).post_type.equals("url")) {

            holder.play_btn.setVisibility(View.GONE);
            holder._body_image.setVisibility(View.GONE);
            holder.blog_title.setText(mUserDetails.get(position).url);
            holder.blog_title.setPaintFlags(holder.blog_title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.collageView.setVisibility(View.GONE);
            holder.youTubeThumbnailView.setVisibility(View.GONE);
            holder.mapLayout.setVisibility(View.GONE);
        }

        else if (mUserDetails.get(position).post_type.equals("video")) {
            holder.play_btn.setVisibility(View.VISIBLE);
            holder.play_btn.setImageResource(R.drawable.youtube_btn);
            holder.collageView.setVisibility(View.GONE);
            holder._body_image.setVisibility(View.GONE);
            holder.blog_title.setText(mUserDetails.get(position).post_description);
            holder.mapLayout.setVisibility(View.GONE);


            final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                }

                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    youTubeThumbnailView.setVisibility(View.VISIBLE);
                    holder.youTubeThumbnailView.setVisibility(View.VISIBLE);
                }
            };

            holder.youTubeThumbnailView.initialize(Constants.KEY, new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                    youTubeThumbnailLoader.setVideo(mUserDetails.get(position).youtubecode);
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    //write something for failure
                }
            });

        }

        else if (mUserDetails.get(position).post_type.equals("map")) {

            holder.play_btn.setVisibility(View.GONE);
            holder._body_image.setVisibility(View.GONE);
            //holder.blog_title.setText(mUserDetails.get(position).post_description);
            holder.collageView.setVisibility(View.GONE);
            holder.youTubeThumbnailView.setVisibility(View.GONE);
            holder.mapLayout.setVisibility(View.VISIBLE);
            holder.blog_title.setText(mUserDetails.get(position).location);
            final String str_lati= mUserDetails.get(position).lati;
            final String str_longi=mUserDetails.get(position).longi;

            if (!mUserDetails.get(position).lati.equals("")) {
                try {

                    //String linkiv="http://maps.google.com/maps/api/staticmap?center=23.6290575,87.09239060000004,&zoom=15&markers=23.6290575,87.09239060000004|23.6290575,87.09239060000004&path=color:0x0000FF80|weight:5|23.6290575,87.09239060000004&size=600x330&sensor=TRUE";
                    String apiLink = "http://maps.google.com/maps/api/staticmap?center="+str_lati+","+str_longi+",&zoom=20&markers="+str_lati+","+str_longi+"|"+str_lati+","+str_longi+"&path=color:0x0000FF80|weight:5|"+str_lati+","+str_longi+"&size=600x330&sensor=TRUE&key="+"AIzaSyBBCrb9jMgk334tpDcleP0O-OXJ1iwkC0A";


                    //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                    /*String encodedurl = "";
                    encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                            apiLink.lastIndexOf('/') + 1));*/
                    Log.d("UP Social List adapter", "encodedurl:"+apiLink);
                    if (!apiLink.equals("") && apiLink != null) {
                   /* Picasso.with(mActivity)
                            .load(encodedurl) // load: This path may be a remote URL,
                            .placeholder(R.drawable.no_data_found_1x)
                            .resize(130, 130)
                            .error(R.drawable.no_data_found_1x)
                            .centerCrop()
                            .into(holder.bloger_pic); */
                        // Into: ImageView into which the final image has to be passed

                        Glide.with(mActivity)
                                .load(apiLink)

                                .into(holder.map_image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.mapLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "http://maps.google.com/maps?saddr=" + lat_psh.getUserUpdatedLatitude() + "," + lat_psh.getUserUpdatedLongitude() + "&daddr=" + str_lati + "," + str_longi;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    mActivity.startActivity(intent);
                }
            });

          /*  holder.mapFragment = SupportMapFragment.newInstance();
            ((FragmentActivity)mActivity).getSupportFragmentManager().beginTransaction().replace(R.id.map, holder.mapFragment).commit();

            holder.mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                  //  LatLng latLng = new LatLng(Double.parseDouble(mUserDetails.get(position).post_id),Double.parseDouble(mUserDetails.get(position).usrname) );
                   // googleMap.addMarker(new MarkerOptions().position(latLng));
                    googleMap.getUiSettings().setMapToolbarEnabled(true);
                    double de_lat=Double.parseDouble(mUserDetails.get(position).lati);
                    double de_long=Double.parseDouble(mUserDetails.get(position).longi);
                    LatLng latLng=new LatLng(de_lat,de_long);

                    destination_marker = googleMap.addMarker(new MarkerOptions().position(
                           latLng)
                            .title("Destination")
                            .snippet("")
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                    ;
                    //.icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
                    //mMap.addMarker(new MarkerOptions().position(bord).title("Marker on board"));
                   // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bord, 13.0f));


                }
            });*/
            //holder.mapFragment.postponeEnterTransition();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserDetails.get(position).post_type.equals("url")){
                    String url = mUserDetails.get(position).url;

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    mActivity.startActivity(i);
                }

                else if(mUserDetails.get(position).post_type.equals("video")){
                   // mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mUserDetails.get(position).video_url)));
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) mActivity, Constants.KEY, mUserDetails.get(position).youtubecode,0, true, false);
                    mActivity.startActivity(intent);

                }
                else if(mUserDetails.get(position).post_type.equals("video_file")){

                    Intent i=new Intent(mActivity,NewActivity.class);
                    i.putExtra("from", "diary");
                    i.putExtra("file",mUserDetails.get(position).video_file);
                    mActivity.startActivity(i);
                }


            }
        });

        holder.collageView.setOnPhotoClickListener(new CollageView.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(int i) {
                if(mUserDetails.get(position).post_type.equals("image")){

                    Intent intent = new Intent(mActivity, ImagePagerActivity.class);
                    intent.putExtra("position", i);
                    intent.putExtra("images",mUserDetails.get(position).image);
                    mActivity.startActivity(intent);

                }
            }
        });






        //String IMg2 = mUserDetails.get(position).profile_pic;
        //"Setting event image" Starts
        if (!mUserDetails.get(position).profile_pic.equals("")) {
            try {
                String apiLink = mUserDetails.get(position).profile_pic;

                //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                String encodedurl = "";
                encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                        apiLink.lastIndexOf('/') + 1));
                Log.d("UP Social List adapter", "encodedurl:"+encodedurl);
                if (!apiLink.equals("") && apiLink != null) {
                   /* Picasso.with(mActivity)
                            .load(encodedurl) // load: This path may be a remote URL,				  
                            .placeholder(R.drawable.no_data_found_1x)
                            .resize(130, 130)
                            .error(R.drawable.no_data_found_1x)
                            .centerCrop()
                            .into(holder.bloger_pic); */
                   // Into: ImageView into which the final image has to be passed

                    Glide.with(mActivity)
                            .load(encodedurl)

                            .into(holder.bloger_pic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.like_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String stattus="1";
                calllikeApi(mUserDetails.get(position).post_id,stattus,holder.like_lay, mUserDetails.get(position).like_count,holder1,position,mUserDetails.get(position));

            }
        });
        holder.unlike_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String stattus="0";
                calllikeApi(mUserDetails.get(position).post_id,stattus,holder.unlike_lay,mUserDetails.get(position).like_count,holder1,position,mUserDetails.get(position));
            }






        });

        holder.comment_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    comment_dialog(mUserDetails.get(position).post_id,mUserDetails.get(position).post_comment_list,position,mUserDetails.get(position).comnt_count,mUserDetails.get(position));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }






        });

        holder.editMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                showPopUp(holder.editMenu);
            }
        });

    }

    private void calllikeApi(final String post_id, String stattus, LinearLayout unlike_lay, int likesize, ViewHolder holder, int position, FeedPostModel feedPostModel) {

        obj=feedPostModel;
        like_count=likesize;
        holder2=holder;
        newposition=position;

        if (AppController.getInstance().isConnected()) {
            progressDialog = Helper.setProgressDailog(mActivity);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_LikePost,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();


                                    String likes_updated_status="0";
                                    //mUserDetails=new ArrayList<MyDiaryModel>();
                                    if(obj.like_stat.equals("1")){
                                        likes_updated_status="0";
                                        like_count=like_count-1;
                                    }
                                    else if(obj.like_stat.equals("0")){
                                        likes_updated_status="1";
                                        like_count=like_count+1;
                                    }
                                    FeedPostModel object=new FeedPostModel(obj.post_id, obj.usrname, obj.profile_pic,obj2.cover_image, obj.post_description,
                                            obj.post_date, obj.post_type, obj.post_like_list, obj.post_comment_list,
                                            like_count, likes_updated_status, obj.comnt_count,obj.image,obj.video_url,obj.video_file,obj.url,obj.youtubecode,obj.location,obj.lati,obj.longi,obj.tag,obj.posted_by);
                                    mUserDetails.add(ncp, object);
                                    notifyItemChanged(ncp,mUserDetails);

                                } else {

                                    Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.hide();
                            }
                            progressDialog.hide();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String json="";
                            // NetworkResponse response = error.networkResponse;
                            parseVolleyError(error);


                            progressDialog.hide();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", psh.getUserId());
                    params.put("post_id", post_id);
                    return params;



                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put(Constants.authKEY, Constants.authVALUE);
                    // add headers <key,value>
                    String credentials = Constants.authuserID+":"+Constants.authPassword;
                    String auth ="Basic "+
                            Base64.encodeToString(credentials.getBytes(),
                                    Base64.NO_WRAP);
                    headers.put("Authorization", auth);
                    return headers;
                }
            };
            AppController.getInstance().addToRequestque(strRequest);
        } else {
            Toast.makeText(mActivity, "No internet connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void showPopUp(ImageView editMenu) {
        final PopupMenu popup = new PopupMenu(mActivity, editMenu);
        popup.getMenuInflater().inflate(R.menu.feeds_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.item_edit) {
                    //do something
                    popup.dismiss();
                    return true;
                }
                else if (i == R.id.item_delete){
                    //do something
                    popup.dismiss();
                    return true;
                }

                else {
                    return onMenuItemClick(item);
                }
            }
        });

        popup.show();
    }

    private Bitmap retriveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    private String getDateTime(String abc) {

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.setTimeInMillis(Long.parseLong(abc) * 1000);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currenTimeZone = (Date) calendar.getTime();
        String axbc=String.valueOf(currenTimeZone);

        return axbc;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.items_feeds, parent, false);
        return new ViewHolder(sView);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }




    private void comment_dialog(final String post_id,
                                String post_comment_list, int position, final int comnt_count,
                                final FeedPostModel FeedPostModel) throws JSONException{
        // TODO Auto-generated method stub

        final int pp=position;


        /*private void comment_dialog(final String post_id, String post_comment_list,final int pos) throws JSONException {
         */// TODO Auto-generated method stub



        // TODO Auto-generated method stub
        final PopupWindow mpopup;
        popUpView = mActivity.getLayoutInflater().inflate(R.layout.comment_dialog, null); // inflating popup layout
        mpopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, true); //Creation of popup
        // mpopup.setAnimationStyle(android.R.style.Animation_Dialog);

        mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0);
        RelativeLayout main=(RelativeLayout)popUpView.findViewById(R.id.main_lay);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        comment_list=(RecyclerView)popUpView.findViewById(R.id.my_recycler_view_comment);
        comment_list.setLayoutManager(layoutManager);

        commment_edt=(EditText)popUpView.findViewById(R.id.editText1);
        send_comment=(ImageView)popUpView.findViewById(R.id.imageView1);

        String cmj=mUserDetails.get(pp).post_comment_list;

        UserComment=new ArrayList<FeedPostModel>();
        JSONArray jo=new JSONArray(cmj);


        for (int i = 0; i < jo.length(); i++) {

            JSONObject jjio=new JSONArray(mUserDetails.get(pp).post_comment_list).getJSONObject(i);

            FeedPostModel item=new FeedPostModel("", jjio.getString("comented_by"), jjio.getString("profile_image"), jjio.getString("comment"), "");
            UserComment.add(item);
        }

        setcommentdata();

        send_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                validation(post_id,pp,comnt_count,FeedPostModel);
            }







        });



    }

    private void validation(String post_id, int pp, int comnt_count,
                            FeedPostModel FeedPostModel) {
        // TODO Auto-generated method stub




        Comment_Str=commment_edt.getText().toString();

        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;


        if(TextUtils.isEmpty(Comment_Str)){
            message = "Please Add Your Comment!";
            focusView = commment_edt;
            cancel = true;
            tempCond = false;
        }


        if (cancel) {
            // focusView.requestFocus();
            if (!tempCond) {
                focusView.requestFocus();
            }
            showAlertMessage(message,"Yourinvited");
        }else{
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(commment_edt.getWindowToken(), 0);
            try {
                CommentAsyncHandler(post_id,pp,FeedPostModel,comnt_count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }




    private void showAlertMessage(String message, String string) {
        // TODO Auto-generated method stub
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    private void setcommentdata() {


        pst_cmt_adapter = new PostCommentAdapter(mActivity, UserComment);
        comment_list.setAdapter(pst_cmt_adapter);
        comment_list.setItemAnimator(new DefaultItemAnimator());





    }


    private void CommentAsyncHandler(final String post_id, int pp,
                                     FeedPostModel myDiaryModel, int comnt_count) {
        // TODO Auto-generated method stub

        ncp=pp;
        cmt_count=comnt_count;
        obj2=myDiaryModel;


        if (AppController.getInstance().isConnected()) {
            progressDialog = Helper.setProgressDailog(mActivity);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_CommentPost,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();

                                    commment_edt.setText("");
                                    cmt_count=cmt_count+1;
                                    //notifyDataSetChanged();
                                    //gg



                                    FeedPostModel object=new FeedPostModel(obj2.post_id, obj2.usrname, obj2.profile_pic, obj2.cover_image,obj2.post_description,
                                            obj2.post_date, obj2.post_type, obj2.post_like_list, obj2.post_comment_list,
                                            obj2.like_count, obj2.like_stat, cmt_count,obj2.image,obj2.video_url,obj2.video_file,obj2.url,obj2.youtubecode,obj2.location,obj2.lati,obj2.longi,obj2.tag,obj2.posted_by);
                                    mUserDetails.add(ncp, object);
                                    notifyItemChanged(ncp,mUserDetails);

                                    FeedPostModel inc=new FeedPostModel("", psh.getUserName(), psh.getUserPic(), Comment_Str, "");
                                    UserComment.add(inc);
                                    pst_cmt_adapter.notifyDataSetChanged();

                                } else {

                                    Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.hide();
                            }
                            progressDialog.hide();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String json="";
                            // NetworkResponse response = error.networkResponse;
                            parseVolleyError(error);


                            progressDialog.hide();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", psh.getUserId());
                    params.put("post_id", post_id);
                    params.put("comment", Comment_Str);
                    return params;



                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put(Constants.authKEY, Constants.authVALUE);
                    // add headers <key,value>
                    String credentials = Constants.authuserID+":"+Constants.authPassword;
                    String auth ="Basic "+
                            Base64.encodeToString(credentials.getBytes(),
                                    Base64.NO_WRAP);
                    headers.put("Authorization", auth);
                    return headers;
                }
            };
            AppController.getInstance().addToRequestque(strRequest);
        } else {
            Toast.makeText(mActivity, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }


    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String errors = data.getString("message");
            // showSnackBar(errors);
            Toast.makeText(mActivity,errors,Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }



    private void showToastLong(String message) {
        // TODO Auto-generated method stub
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }





}
