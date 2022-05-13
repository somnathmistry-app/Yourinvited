package com.devonetech.android.yourinvited.social_network.friend_profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
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
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.shared.Updatedlatlong;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.ImagePagerActivity;
import com.devonetech.android.yourinvited.social_network.NewActivity;
import com.devonetech.android.yourinvited.social_network.adapter.PostCommentAdapter;
import com.devonetech.android.yourinvited.social_network.model.FeedPostModel;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.lopei.collageview.CollageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class FriendProofileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private static final int ITEM = 0;
    private static final int LOADING = 1;

    int sizes=0,newposition,like_count=0,cmt_count=0,ncp;
    //MovieVH holder1,holder2;
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
    ConnectionDetector connection;
    UserShared psh;
    Updatedlatlong lat_psh;
    String IMg="";
    private ArrayList<FeedPostModel> tagFriends;
    private ArrayList<FeedPostModel> UserComment;
    private List<FeedPostModel> mUserDetails;
    private Activity mActivity;

    private boolean isLoadingAdded = false;
    View popUpView;
    ProgressDialog progressDialog;

    public FriendProofileAdapter(Activity context) {
        this.mActivity = context;
        mUserDetails = new ArrayList<>();
    }

    public List<FeedPostModel> getMovies() {
        return mUserDetails;
    }

    public void setMovies(List<FeedPostModel> mUserDetails) {
        this.mUserDetails = mUserDetails;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new FriendProofileAdapter.LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.items_feeds, parent, false);
        viewHolder = new FriendProofileAdapter.MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // FeedPostModel result = mUserDetails.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final FriendProofileAdapter.MovieVH movieVH = (FriendProofileAdapter.MovieVH) holder;

                // movieVH.mMovieTitle.setText(result.getTitle());

                //  holder1=holder;
                connection=new ConnectionDetector(mActivity);
                psh=new UserShared(mActivity);
                lat_psh=new Updatedlatlong(mActivity);
                movieVH.bloger_name.setText(mUserDetails.get(position).usrname);


                movieVH.post_date.setText(getDateTime(mUserDetails.get(position).post_date));

                if (mUserDetails.get(position).usrname.equals(psh.getUserName())){

                    movieVH.editMenu.setVisibility(View.VISIBLE);

                }
                else {
                    movieVH.editMenu.setVisibility(View.GONE);
                }

                if (mUserDetails.get(position).ingroup.equals("1")){

                    movieVH.group_name.setVisibility(View.VISIBLE);
                    movieVH.group_name.setText(mUserDetails.get(position).group_name);

                }
                else {
                    movieVH.group_name.setVisibility(View.GONE);

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

                        movieVH.wasTXT.setText("was with");
                        movieVH.tagFriendLay.setVisibility(View.VISIBLE);
                        movieVH.tagFirstUser.setText(tagFriends.get(0).name);
                        if (tagFriends.size()==1){
                            movieVH.tagCount.setText("");
                            movieVH.andtxt.setText("");
                        }
                        else {
                            movieVH.tagCount.setText(tagFriends.size()-1+" others");
                        }


                        movieVH.tagCount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });



                    }
                    else {
                        movieVH.wasTXT.setText("");
                        movieVH.tagFriendLay.setVisibility(View.GONE);
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
                            movieVH.like_count.setText(String.valueOf(mUserDetails.get(position).like_count));
                            //JSONObject jobj=new JSONArray(mUserDetails.get(position).post_like_list).getJSONObject(i);

                            //String my_id=jobj.getString("user_id");
                            //String my_like_status=jobj.getString("l_status");
                            if ( mUserDetails.get(position).like_stat.equals("1")) {

                                movieVH.like_lay.setVisibility(View.GONE);
                                movieVH.unlike_lay.setVisibility(View.VISIBLE);
                            }
                            else if( mUserDetails.get(position).like_stat.equals("0")){
                                movieVH.like_lay.setVisibility(View.VISIBLE);
                                movieVH.unlike_lay.setVisibility(View.GONE);
                            }




                        }
                        else {
                            movieVH.like_count.setText(String.valueOf(mUserDetails.get(position).like_count));
                            //JSONObject jobj=new JSONArray(mUserDetails.get(position).post_like_list).getJSONObject(i);

                            //String my_id=jobj.getString("user_id");
                            //String my_like_status=jobj.getString("l_status");
                            if ( mUserDetails.get(position).like_stat.equals("1")) {

                                movieVH.like_lay.setVisibility(View.GONE);
                                movieVH.unlike_lay.setVisibility(View.VISIBLE);
                            }
                            else if( mUserDetails.get(position).like_stat.equals("0")){
                                movieVH.like_lay.setVisibility(View.VISIBLE);
                                movieVH.unlike_lay.setVisibility(View.GONE);
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
                            movieVH.comment_count.setText(String.valueOf(mUserDetails.get(position).comnt_count));
                        }
                        else {
                            movieVH.comment_count.setText("0");
                        }


                    }
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }


                if (mUserDetails.get(position).post_type.equals("image")) {

                    movieVH.play_btn.setVisibility(View.GONE);
                    movieVH._body_image.setVisibility(View.GONE);
                    movieVH.collageView.setVisibility(View.VISIBLE);
                    movieVH.youTubeThumbnailView.setVisibility(View.GONE);
                    movieVH.mapLayout.setVisibility(View.GONE);

                    movieVH.blog_title.setText(mUserDetails.get(position).post_description);
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

                    movieVH.collageView
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

                    movieVH.play_btn.setVisibility(View.VISIBLE);
                    movieVH.blog_title.setText(mUserDetails.get(position).post_description);
                    movieVH.collageView.setVisibility(View.GONE);
                    movieVH._body_image.setVisibility(View.VISIBLE);
                    movieVH.youTubeThumbnailView.setVisibility(View.GONE);
                    movieVH.mapLayout.setVisibility(View.GONE);

                    try {
                        resized = retriveVideoFrameFromVideo(mUserDetails.get(position).video_file);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    movieVH._body_image.setImageBitmap(resized);
                    //  holder._body_image.setImageResource(R.drawable.thumbnail);




                }

                else if (mUserDetails.get(position).post_type.equals("post")) {

                    movieVH.play_btn.setVisibility(View.GONE);
                    movieVH._body_image.setVisibility(View.GONE);
                    movieVH.blog_title.setText(mUserDetails.get(position).post_description);
                    movieVH.collageView.setVisibility(View.GONE);
                    movieVH.youTubeThumbnailView.setVisibility(View.GONE);
                    movieVH.mapLayout.setVisibility(View.GONE);
                }

                else if (mUserDetails.get(position).post_type.equals("url")) {

                    movieVH.play_btn.setVisibility(View.GONE);
                    movieVH._body_image.setVisibility(View.GONE);
                    movieVH.blog_title.setText(mUserDetails.get(position).url);
                    movieVH.blog_title.setPaintFlags(movieVH.blog_title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    movieVH.collageView.setVisibility(View.GONE);
                    movieVH.youTubeThumbnailView.setVisibility(View.GONE);
                    movieVH.mapLayout.setVisibility(View.GONE);
                }

                else if (mUserDetails.get(position).post_type.equals("video")) {
                    movieVH.play_btn.setVisibility(View.VISIBLE);
                    movieVH.play_btn.setImageResource(R.drawable.youtube_btn);
                    movieVH.collageView.setVisibility(View.GONE);
                    movieVH._body_image.setVisibility(View.GONE);
                    movieVH.blog_title.setText(mUserDetails.get(position).post_description);
                    movieVH.mapLayout.setVisibility(View.GONE);


                    final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
                        @Override
                        public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                        }

                        @Override
                        public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                            youTubeThumbnailView.setVisibility(View.VISIBLE);
                            movieVH.youTubeThumbnailView.setVisibility(View.VISIBLE);
                        }
                    };

                    movieVH.youTubeThumbnailView.initialize(Constants.KEY, new YouTubeThumbnailView.OnInitializedListener() {
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

                    movieVH.play_btn.setVisibility(View.GONE);
                    movieVH._body_image.setVisibility(View.GONE);
                    //holder.blog_title.setText(mUserDetails.get(position).post_description);
                    movieVH.collageView.setVisibility(View.GONE);
                    movieVH.youTubeThumbnailView.setVisibility(View.GONE);
                    movieVH.mapLayout.setVisibility(View.VISIBLE);
                    movieVH.blog_title.setText(mUserDetails.get(position).location);
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

                                        .into(movieVH.map_image);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    movieVH.mapLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = "http://maps.google.com/maps?saddr=" + lat_psh.getUserUpdatedLatitude() + "," + lat_psh.getUserUpdatedLongitude() + "&daddr=" + str_lati + "," + str_longi;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            mActivity.startActivity(intent);
                        }
                    });


                }

                movieVH.itemView.setOnClickListener(new View.OnClickListener() {
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

                movieVH.collageView.setOnPhotoClickListener(new CollageView.OnPhotoClickListener() {
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

                                    .into(movieVH.bloger_pic);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                movieVH.like_lay.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        String stattus="1";
                        calllikeApi(mUserDetails.get(position).post_id,stattus,movieVH.like_lay, mUserDetails.get(position).like_count,movieVH,position,mUserDetails.get(position));

                    }
                });
                movieVH.unlike_lay.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        String stattus="0";
                        calllikeApi(mUserDetails.get(position).post_id,stattus,movieVH.unlike_lay,mUserDetails.get(position).like_count,movieVH,position,mUserDetails.get(position));
                    }






                });

                movieVH.comment_lay.setOnClickListener(new View.OnClickListener() {

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

                movieVH.editMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showPopUp(movieVH.editMenu);
                    }
                });




                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mUserDetails == null ? 0 : mUserDetails.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mUserDetails.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(FeedPostModel r) {
        mUserDetails.add(r);
        notifyItemInserted(mUserDetails.size() - 1);
    }

    public void addAll(List<FeedPostModel> moveResults) {
        for (FeedPostModel result : moveResults) {
            add(result);
        }
    }

    public void remove(FeedPostModel r) {
        int position = mUserDetails.indexOf(r);
        if (position > -1) {
            mUserDetails.remove(position);
            notifyItemRemoved(position);
        }
    }



    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new FeedPostModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mUserDetails.size() - 1;
        FeedPostModel result = getItem(position);

        if (result != null) {
            mUserDetails.remove(position);
            notifyItemRemoved(position);
        }
    }

    public FeedPostModel getItem(int position) {
        return mUserDetails.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class MovieVH extends RecyclerView.ViewHolder {
        private TextView bloger_name,blog_title,like_count,comment_count,post_date;
        private ImageView _body_image,bloger_pic,play_btn,editMenu;
        private LinearLayout like_lay,unlike_lay,comment_lay;
        private VideoView video_viw;
        private CollageView collageView;
        private YouTubeThumbnailView youTubeThumbnailView;
        private FrameLayout mapLayout;
        private SupportMapFragment mapFragment;
        private TextView wasTXT,tagFirstUser,tagCount,andtxt;
        private LinearLayout tagFriendLay;
        private ImageView map_image;
        private TextView group_name;

        public MovieVH(View itemView) {
            super(itemView);
            group_name=itemView.findViewById(R.id.gruopName);
            wasTXT=itemView.findViewById(R.id.was_withtxt);
            tagFirstUser=itemView.findViewById(R.id.tag_first);
            tagCount=itemView.findViewById(R.id.tag_count);
            tagFriendLay=itemView.findViewById(R.id.tag_friend_lay);
            andtxt=itemView.findViewById(R.id.andtxt);
            bloger_name = (TextView) itemView.findViewById(R.id.bloger_name);
            blog_title = (TextView) itemView.findViewById(R.id.bloger_title);
            _body_image = (ImageView) itemView.findViewById(R.id.body_iv);
            bloger_pic = (ImageView) itemView.findViewById(R.id.bloger_pic);
            like_count=(TextView)itemView.findViewById(R.id.like_count);
            comment_count=(TextView)itemView.findViewById(R.id.comment_count);
            play_btn=(ImageView)itemView.findViewById(R.id.play_btn_img);
            like_lay=(LinearLayout)itemView.findViewById(R.id.like_layout);
            unlike_lay=(LinearLayout)itemView.findViewById(R.id.unlike_layout);
            comment_lay=(LinearLayout)itemView.findViewById(R.id.comment_layout);
            video_viw=(VideoView)itemView.findViewById(R.id.VideoView);
            post_date=(TextView)itemView.findViewById(R.id.textView4);
            collageView = (CollageView)itemView. findViewById(R.id.collageView);
            editMenu=itemView.findViewById(R.id.imageView3);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
            mapLayout = itemView.findViewById(R.id.map);
            map_image=itemView.findViewById(R.id.map_iv);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
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


    ////comment section

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



                                    FeedPostModel object=new FeedPostModel(obj2.post_id, obj2.usrname, obj2.profile_pic,obj2.cover_image, obj2.post_description,
                                            obj2.post_date, obj2.post_type, obj2.post_like_list, obj2.post_comment_list,
                                            obj2.like_count, obj2.like_stat, cmt_count,obj2.image,obj2.video_url,obj2.video_file,obj2.url,obj2.youtubecode,obj2.location,obj2.lati,obj2.longi,obj2.tag,obj2.posted_by,
                                            obj2.ingroup,obj2.group_id,obj2.group_name,obj2.group_image,obj2.group_member);
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

    ////end of comment section


    ///like section

    private void calllikeApi(final String post_id, String stattus, LinearLayout unlike_lay, int likesize, FriendProofileAdapter.MovieVH holder, int position, FeedPostModel feedPostModel) {

        obj=feedPostModel;
        like_count=likesize;
        // holder2=holder;
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
                                    FeedPostModel object=new FeedPostModel(obj.post_id, obj.usrname, obj.profile_pic,obj.cover_image, obj.post_description,
                                            obj.post_date, obj.post_type, obj.post_like_list, obj.post_comment_list,
                                            like_count, likes_updated_status, obj.comnt_count,obj.image,obj.video_url,obj.video_file,obj.url,obj.youtubecode,obj.location,obj.lati,obj.longi,obj.tag,obj.posted_by,
                                            obj.ingroup,obj.group_id,obj.group_name,obj.group_image,obj.group_member);
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


    ///end of like section

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
}
