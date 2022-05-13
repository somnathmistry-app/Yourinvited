package com.devonetech.android.yourinvited.event_section;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.MainActivity;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom_view.MultiSelctionSpinner;
import com.devonetech.android.yourinvited.event_section.adapter.AllPlaceAdapter;
import com.devonetech.android.yourinvited.event_section.adapter.ViewPagerAdapter;
import com.devonetech.android.yourinvited.event_section.model.NearbyPlaceModel;
import com.devonetech.android.yourinvited.event_section.model.PlaceModelList;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.social_network.group.adapter.AllUserAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NearbyPlaceActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog progressDialog;
    ArrayList<NearbyPlaceModel> tablist;
    ArrayList<NearbyPlaceModel> tabdata;
    ArrayList<ArrayList<NearbyPlaceModel>> tabdatalist;
    CoordinatorLayout coordinatorLayout;
    String currentLocation="",category_name;
    Intent mIntent;
    Toolbar toolbar;
    private ArrayList<PlaceModelList> placeList;
    private AllPlaceAdapter adapter;
    private ImageView allPlaces;
    View popUpView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_place);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setTitle("Near By Places");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }

        );
        mIntent=getIntent();

      //  viewPager = (ViewPager)findViewById(R.id.pager);
        tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        allPlaces=findViewById(R.id.imageView7);

        allPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllPlaces();
            }
        });

        //replaceFragment(new NearByMapFragment(tabLayout,mIntent.getStringExtra("lat"),mIntent.getStringExtra("long")));


        callAllPlacesApi();



       // setupTab();
       // callPlaceApi();


    }

    private void setAllPlaces() {

        final PopupWindow mpopup;
        popUpView = getLayoutInflater().inflate(R.layout.dialog_near_by_all, null); // inflating popup layout
        mpopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, true); //Creation of popup
        // mpopup.setAnimationStyle(android.R.style.Animation_Dialog);

        mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0);

        RecyclerView recycler_view = (RecyclerView)popUpView.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        TextView done=popUpView.findViewById(R.id.donetxt);

        adapter = new AllPlaceAdapter(this, placeList,tabLayout,mIntent.getStringExtra("lat"),mIntent.getStringExtra("long"),done,mpopup);
        recycler_view.setAdapter(adapter);


    }

    private void callAllPlacesApi() {


        if (AppController.getInstance().isConnected()) {
            progressDialog = Helper.setProgressDailog(NearbyPlaceActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.GET, Constants.URL_MapServices,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();
                                    placeList=new ArrayList<PlaceModelList>();
                                    JSONArray jj=jsonObject.getJSONArray("service");

                                    for (int i = 0; i <jj.length() ; i++) {
                                        JSONObject obj=jj.getJSONObject(i);
                                        PlaceModelList item=new PlaceModelList(obj.getString("id"),obj.getString("name"),obj.getString("value")
                                               );
                                        placeList.add(item);


                                    }

                                    setAllPlaces();



                                } else {

                                    Toast.makeText(NearbyPlaceActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                /*@Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id",psh.getUserId());
                    return params;
                }*/
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
            Toast.makeText(NearbyPlaceActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void setPlaces() {





    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String errors = data.getString("message");
            // showSnackBar(errors);
            Toast.makeText(getApplicationContext(),errors,Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit();
    }



}
