package com.devonetech.android.yourinvited.social_network.friend_profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom.Circle_Image;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.network.PaginationScrollListener;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.model.FeedPostModel;

import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendProfileActivity extends AppCompatActivity {

    UserShared psh;
    ConnectionDetector connection;
    RecyclerView recycler_view;
    //SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout post_status;

    String responseString=null;
    private MultipartEntity reqEntity;
    private static String TAG;
    ArrayList<FeedPostModel> allFeedsList;
    FriendProofileAdapter mAdapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    LinearLayoutManager linearLayoutManager;
    private Circle_Image profile_iv;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    Intent mIntent;
    private ImageView profilePic,coverPic;
    private TextView userName;
    private TextView about_user,user_friends,user_photos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_profile_activity);
        psh = new UserShared(this);
        connection = new ConnectionDetector(this);
        toolbar = (Toolbar) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // finish();
                        onBackPressed();
                    }
                }

        );
        mIntent=getIntent();

        xmlInit();
        setIntentData();
    }

    private void setIntentData() {

        userName.setText(mIntent.getStringExtra("name"));

        if (!mIntent.getStringExtra("userpic").equals("")) {
            try {
                String apiLink = mIntent.getStringExtra("userpic");

                //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                String encodedurl = "";
                encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                        apiLink.lastIndexOf('/') + 1));
                Log.d("UP Social List adapter", "encodedurl:"+encodedurl);
                if (!apiLink.equals("") && apiLink != null) {

                    Glide.with(this)
                            .load(encodedurl)

                            .into(profilePic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!mIntent.getStringExtra("coverpic").equals("")) {
            try {
                String apiLink = mIntent.getStringExtra("coverpic");

                //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                String encodedurl = "";
                encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                        apiLink.lastIndexOf('/') + 1));
                Log.d("UP Social List adapter", "encodedurl:"+encodedurl);
                if (!apiLink.equals("") && apiLink != null) {

                    Glide.with(this)
                            .load(encodedurl)
                            .into(coverPic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    private void xmlInit() {
        coverPic=findViewById(R.id.cover_picture);
        profilePic=findViewById(R.id.profile_picture);
        userName=findViewById(R.id.textView17);

        about_user=findViewById(R.id.textView18);
        user_friends=findViewById(R.id.textView19);
        user_photos=findViewById(R.id.textView20);

        about_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(FriendProfileActivity.this, AboutFriendDetails.class);
                startActivity(i);
            }
        });
        user_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(FriendProfileActivity.this, FriendofFriendActivity.class);
                startActivity(i);
            }
        });
        user_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(FriendProfileActivity.this, FriendPhotosActivity.class);
                startActivity(i);
            }
        });



        recycler_view =findViewById(R.id.my_recycler_view);
        mAdapter = new FriendProofileAdapter(FriendProfileActivity.this);

        linearLayoutManager = new LinearLayoutManager(this) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(FriendProfileActivity.this) {

                    private static final float SPEED = 50000f;

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }

                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayoutManager);

        recycler_view.setItemAnimator(new DefaultItemAnimator());

        recycler_view.setAdapter(mAdapter);
        recycler_view.setHasFixedSize(true);
        //recycler_view.setNestedScrollingEnabled(true);

       /* swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        // fetchMovies();


                                    }
                                }
        );*/
       /* swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



*/
        loadFirstPage();
        recycler_view.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }


    private void loadFirstPage() {

        if (AppController.getInstance().isConnected()) {
             progressDialog = Helper.setProgressDailog(FriendProfileActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_TimelineList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    TOTAL_PAGES = jsonObject.optInt("total_page");
                                    // alertDialog.cancel();
                                    allFeedsList=new ArrayList<FeedPostModel>();
                                    JSONArray jj=jsonObject.getJSONArray("data");



                                    for (int i = 0; i <jj.length() ; i++) {
                                        JSONObject obj=jj.getJSONObject(i);
                                        JSONArray jr_like=obj.getJSONArray("like");
                                        JSONArray jr_comment=obj.getJSONArray("comment");
                                        String like_status=likestatusdefine(jr_like);
                                        String comment_status=commentstatusdefine(jr_comment);




                                        FeedPostModel item=new FeedPostModel(obj.getString("post_id"), obj.getString("posted_by_name"),
                                                obj.getString("profile_image"),obj.getString("cover_image"),obj.getString("description"),
                                                obj.getString("posted_on"),obj.getString("post_type"),
                                                obj.getString("like"),obj.getString("comment"),
                                                obj.getJSONArray("like").length(),like_status,
                                                obj.getJSONArray("comment").length(),obj.getString("image"),
                                                obj.getString("video_url"),obj.getString("video_file"),obj.getString("url"),obj.getString("youtubecode"),
                                                obj.getString("location"),obj.getString("lati"),obj.getString("longi"),obj.getString("tag"),obj.getString("posted_by"),
                                                obj.getString("ingroup"),obj.getString("group_id"),obj.getString("group_name"),"","");
                                        allFeedsList.add(item);



                                    }

                                    mAdapter.addAll(allFeedsList);

                                    if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
                                    else isLastPage = true;


                                   // swipeRefreshLayout.setRefreshing(false);


                                } else {
                                   // mAdapter.removeLoadingFooter();
                                  //  swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(FriendProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                    params.put("user_id",psh.getUserId());
                    params.put("friend_id",mIntent.getStringExtra("friend_id"));
                    params.put("start",String.valueOf(currentPage));
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
            Toast.makeText(FriendProfileActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadNextPage() {
        if (AppController.getInstance().isConnected()) {
            // progressDialog = Helper.setProgressDailog(AddEventActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_TimelineList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    mAdapter.removeLoadingFooter();
                                    isLoading = false;
                                    TOTAL_PAGES = jsonObject.optInt("total_page");
                                    // alertDialog.cancel();
                                    allFeedsList=new ArrayList<FeedPostModel>();
                                    JSONArray jj=jsonObject.getJSONArray("data");



                                    for (int i = 0; i <jj.length() ; i++) {
                                        JSONObject obj=jj.getJSONObject(i);
                                        JSONArray jr_like=obj.getJSONArray("like");
                                        JSONArray jr_comment=obj.getJSONArray("comment");
                                        String like_status=likestatusdefine(jr_like);
                                        String comment_status=commentstatusdefine(jr_comment);




                                        FeedPostModel item=new FeedPostModel(obj.getString("post_id"), obj.getString("posted_by_name"),
                                                obj.getString("profile_image"),obj.getString("cover_image"),obj.getString("description"),
                                                obj.getString("posted_on"),obj.getString("post_type"),
                                                obj.getString("like"),obj.getString("comment"),
                                                obj.getJSONArray("like").length(),like_status,
                                                obj.getJSONArray("comment").length(),obj.getString("image"),
                                                obj.getString("video_url"),obj.getString("video_file"),obj.getString("url"),obj.getString("youtubecode"),
                                                obj.getString("location"),obj.getString("lati"),obj.getString("longi"),obj.getString("tag"),obj.getString("posted_by"),
                                                obj.getString("ingroup"),obj.getString("group_id"),obj.getString("group_name"),obj.getString("group_image"),obj.getString("group_member"));
                                        allFeedsList.add(item);



                                    }

                                    mAdapter.addAll(allFeedsList);

                                    if (currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter();
                                    else isLastPage = true;


                                   // swipeRefreshLayout.setRefreshing(false);


                                } else {
                                  //  swipeRefreshLayout.setRefreshing(false);
                                    mAdapter.removeLoadingFooter();
                                  //  Toast.makeText(FriendProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                //  progressDialog.hide();
                            }
                            //  progressDialog.hide();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String json="";
                            // NetworkResponse response = error.networkResponse;
                            parseVolleyError(error);


                            // progressDialog.hide();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id",psh.getUserId());
                    params.put("friend_id",mIntent.getStringExtra("friend_id"));
                    params.put("start",String.valueOf(currentPage));
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
            Toast.makeText(FriendProfileActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private String likestatusdefine(JSONArray jr) {
        String like_status="0";

        if(jr.length()!=0){
            for(int j=0;j<jr.length();j++){
                JSONObject childj;
                try {
                    childj = jr.getJSONObject(j);

                    if(childj.getString("user_id").equals(psh.getUserId())){
                        like_status="1";

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }

        return like_status;
    }

    private String commentstatusdefine(JSONArray jr) {
        String comment_status="0";

        if(jr.length()!=0){
            for(int j=0;j<jr.length();j++){
                JSONObject childj;
                try {
                    childj = jr.getJSONObject(j);

                    if(childj.getString("user_id").equals(psh.getUserId())){
                        comment_status="1";

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }

        return comment_status;
    }




    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String errors = data.getString("message");
            // showSnackBar(errors);
            Toast.makeText(this,errors,Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }



   /* @Override
    public void onRefresh() {

        currentPage=1;

        loadFirstPage();

    }*/

}
