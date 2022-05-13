package com.devonetech.android.yourinvited.social_network.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom.Circle_Image;
import com.devonetech.android.yourinvited.event_section.AddEventActivity;
import com.devonetech.android.yourinvited.friends_section.adapter.RequestListAdapter;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.FeedPostActivity;
import com.devonetech.android.yourinvited.social_network.adapter.AllFeedsAdapter;
import com.devonetech.android.yourinvited.social_network.adapter.CustomPaginationAdapter;
import com.devonetech.android.yourinvited.social_network.model.FeedPostModel;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class FeedsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public FeedsFragment() {
        // Required empty public constructor
    }
    View rootView;
    XRecyclerView recycler_view;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout post_status;

    String responseString=null;
    private MultipartEntity reqEntity;
    private static String TAG;
    ConnectionDetector connection;
    UserShared psh;
    ArrayList<FeedPostModel> allFeedsList;
    AllFeedsAdapter mAdapter;
    private int pagenumber=1;
    private int TOTAL_PAGES=1;
    LinearLayoutManager layoutManager1;
    private Circle_Image profile_iv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.all_feeds_new, container, false);


        psh=new UserShared(getActivity());
        connection=new ConnectionDetector(getActivity());

        xmlini(rootView);
        return rootView;
    }


    private void xmlini(View rootView) {

        profile_iv=rootView.findViewById(R.id.imageView4);

        if (!psh.getUserPic().equals("")) {
            try {
                String apiLink = psh.getUserPic();

                //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                String encodedurl = "";
                encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                        apiLink.lastIndexOf('/') + 1));
                Log.d("UP Social List adapter", "encodedurl:"+encodedurl);
                if (!apiLink.equals("") && apiLink != null) {

                    Glide.with(getActivity())
                            .load(encodedurl)

                            .into(profile_iv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

      /*  LinearLayoutManager layoutManager1
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);*/
        recycler_view = rootView.findViewById(R.id.my_recycler_view);
        recycler_view.setHasFixedSize(true);
        //recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutManager1 = new LinearLayoutManager(getContext()) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {

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
        recycler_view.setLayoutManager(layoutManager1);

        recycler_view.setItemAnimator(new DefaultItemAnimator());


        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        // fetchMovies();

                                        callListApiVolley(1);
                                    }
                                }
        );
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        post_status=rootView.findViewById(R.id.top_lay);
        post_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j=new Intent(getActivity(),FeedPostActivity.class);
                startActivityForResult(j, 200);
            }
        });



        recycler_view.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pagenumber = 1;
                        allFeedsList=new ArrayList<FeedPostModel>();
                        mAdapter.notifyDataSetChanged();
                        callListApiVolley(pagenumber);
                        recycler_view.refreshComplete();
                    }
                }, 1000);


            }

            @Override
            public void onLoadMore() {
                if (pagenumber < TOTAL_PAGES) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            pagenumber += 1;
                            callListApiVolley(pagenumber);
                            recycler_view.loadMoreComplete();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            recycler_view.setNoMore(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                }

            }

        });





    }
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        //pagestart=pageend;
        //int a=10;
        // int b=Integer.parseInt(pageend)+a;
        //  pageend = String.valueOf(b);

        callListApiVolley(1);

    }


    private void callListApiVolley(final int pagenumber) {
        swipeRefreshLayout.setRefreshing(true);

        if (AppController.getInstance().isConnected()) {
           // progressDialog = Helper.setProgressDailog(AddEventActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_FeedList,
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
                                                obj.getString("location"),obj.getString("lati"),obj.getString("longi"),obj.getString("tag"),obj.getString("posted_by"));
                                        allFeedsList.add(item);



                                    }

                                    setdata();


                                    swipeRefreshLayout.setRefreshing(false);


                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
                    params.put("start",String.valueOf(pagenumber));
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
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String errors = data.getString("message");
            // showSnackBar(errors);
            Toast.makeText(getActivity(),errors,Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }


    public  void onActivityResult(int arg0, int arg1, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, data);

        switch (arg0) {

            case 100:
                //Toast.makeText(this, "come back", Toast.LENGTH_SHORT).show();
                break;
            case 200:

                if (arg1!=RESULT_OK) {

                   // Bundle puredata=data.getExtras();

                 //   String postStatus=data.getStringExtra("post_status");


                    callListApiVolley(1);

                }

                break;

            default:
                break;
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



    private void showToastLong(String string) {
        Toast.makeText(getActivity(),string,Toast.LENGTH_SHORT).show();
    }



    private void setdata() {

        if (allFeedsList.size()>0){

            mAdapter = new AllFeedsAdapter(getActivity(),allFeedsList);
            recycler_view.setAdapter(mAdapter);
            swipeRefreshLayout.setRefreshing(false);

          
        }
        else {
            Intent j=new Intent(getActivity(),FeedPostActivity.class);
            startActivityForResult(j, 200);
            swipeRefreshLayout.setRefreshing(false);

        }



    }





}