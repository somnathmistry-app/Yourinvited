package com.devonetech.android.yourinvited.event_section;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.adapter.PagerAdapter;
import com.devonetech.android.yourinvited.event_section.model.EventModel;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private ProgressDialog progressDialog;
    Toolbar toolbar;
    Intent mIntent;
    ArrayList<EventModel> eventlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        xmlini();
        adduserApi();
      /*  ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new FragmentOne(), "Info");
        adapter.addFragment(new FragmentTwo(), "Map");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);*/

    }





    private void adduserApi() {


        if (AppController.getInstance().isConnected()) {
            progressDialog = Helper.setProgressDailog(EventDetailsActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_EventList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();

                                    JSONArray jj=jsonObject.getJSONArray("product");
                                    eventlist=new ArrayList<EventModel>();

                                    for (int i = 0; i <jj.length() ; i++) {
                                        JSONObject obj=jj.getJSONObject(i);
                                        EventModel item=new EventModel(obj.getString("event_id"),obj.getString("name"),obj.getString("description"),
                                                obj.getString("location"),obj.getString("start_date"),obj.getString("end_date"),
                                                obj.getString("image"),obj.getString("lat"),obj.getString("long"),obj.getString("member"));
                                        eventlist.add(item);

                                    }
                                    getSupportActionBar().setTitle(eventlist.get(0).name);



                                    setData(eventlist);



                                } else {
                                    Toast.makeText(EventDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                    params.put("event_id",mIntent.getStringExtra("id"));
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
            Toast.makeText(EventDetailsActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }




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

    private void xmlini() {

        viewPager = (ViewPager)findViewById(R.id.pager);
        tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    /*private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_tab_favourite,
                R.drawable.ic_tab_call,
                R.drawable.ic_tab_contacts
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }*/

    private void setupViewPager(ViewPager viewPager) {


    }

    private void setData(ArrayList<EventModel> eventlist) {

        PagerAdapter adapter = new PagerAdapter(EventDetailsActivity.this.getSupportFragmentManager());
        //adapter.addFragment(FragmentOne.createInstance(eventlist.size(),eventlist,"Info"), getString(R.string.info));
        adapter.addFragment(FragmentTwo.createInstance(eventlist.size(),eventlist,"Map"), getString(R.string.map));
        viewPager.setAdapter(adapter);
    }


   // Adapter for the viewpager using FragmentPagerAdapter
   class ViewPagerAdapter extends FragmentPagerAdapter {
       private final List<Fragment> mFragmentList = new ArrayList<>();
       private final List<String> mFragmentTitleList = new ArrayList<>();

       public ViewPagerAdapter(FragmentManager manager) {
           super(manager);
       }

       @Override
       public Fragment getItem(int position) {
           return mFragmentList.get(position);
       }

       @Override
       public int getCount() {
           return mFragmentList.size();
       }

       public void addFragment(Fragment fragment, String title) {
           mFragmentList.add(fragment);
           mFragmentTitleList.add(title);
       }

       @Override
       public CharSequence getPageTitle(int position) {
           return mFragmentTitleList.get(position);
       }
   }
}
