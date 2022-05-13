package com.devonetech.android.yourinvited.event_section;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.adapter.EventListAdapter;
import com.devonetech.android.yourinvited.event_section.adapter.EventRequestAdapter;
import com.devonetech.android.yourinvited.event_section.model.EventModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.SocialNetworkMain;

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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class EventRequestActivity extends AppCompatActivity {

    RecyclerView recycler_view;
    ProgressDialog progressDialog;

    String responseString=null;
    private MultipartEntity reqEntity;
    private static String TAG;
    ConnectionDetector connection;
    UserShared psh;
    ArrayList<EventModel> eventlist;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate(R.layout.activity_recylerview, container);

        setContentView(R.layout.eventlist_activty);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        connection=new ConnectionDetector(this);
        psh=new UserShared(this);
        xmlini();
        callListApi();

    }

    private void xmlini() {

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        //recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_view.setItemAnimator(new DefaultItemAnimator());


       /* CampaignAdapter recentCallAdapter = new CampaignAdapter(this);
        recycler_view.setAdapter(recentCallAdapter);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);




    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
      // finish();

        Intent intent=new Intent();
        setResult(200,intent);
        finish();
    }

    private void callListApi() {
        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;


        if (cancel) {
            // focusView.requestFocus();
            if (!tempCond) {
                focusView.requestFocus();
            }
            showToastLong(message);
        } else {
            InputMethodManager imm = (InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            try {

                //File sourceFile = new File(picturePath);



                reqEntity = null;
                reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);


                reqEntity.addPart("user_id", new StringBody(psh.getUserId()));


            }

            catch (Exception e) {
                e.printStackTrace();
            }

            Boolean isInternetPresent = connection.isConnectingToInternet();

            if (isInternetPresent) {
                progressDialog = ProgressDialog.show(this,
                        "",
                        getString(R.string.progress_bar_loading_message),
                        false);

                new UploadFileToServer().execute();
            } else {
                showToastLong(getString(R.string.no_internet_message));
            }
        }
    }


    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
			/*progressDialog = ProgressDialog.show(PersonalRegistrationActivity.this,
					"",
					getString(R.string.progress_bar_loading_message),
					false);*/
            super.onPreExecute();
        }



        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {



            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL_EventRequestList);

            try {


                /*to print in log*/

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                reqEntity.writeTo(bytes);

                String content = bytes.toString();

                Log.e("MultiPartEntityRequest:",content);

                /*to print in log*/
                httppost.addHeader(Constants.authKEY,Constants.authVALUE);
                String credentials = Constants.authuserID+":"+Constants.authPassword;
                String auth ="Basic "+
                        Base64.encodeToString(credentials.getBytes(),
                                Base64.NO_WRAP);
                //headers.put("Authorization", auth);
                httppost.addHeader("Authorization",auth);
                httppost.setEntity(reqEntity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;





        }


        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            try{
                if (!responseString.equals("")) {

                    JSONObject jsonObject = new JSONObject(responseString);
                    String Ack = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if (Ack.equals("1")) {


                        eventlist=new ArrayList<EventModel>();
                        JSONArray jj=jsonObject.getJSONArray("event");

                        for (int i = 0; i <jj.length() ; i++) {
                            JSONObject obj=jj.getJSONObject(i);



                            EventModel item=new EventModel(obj.getString("event_id"),obj.getString("name"),
                                    obj.getString("description"),
                                    obj.getString("location"),
                                    obj.getString("start_on"),
                                    obj.getString("end_on"),
                                    obj.getString("image"),
                                    obj.getString("event_lat"),
                                    obj.getString("event_long"),obj.getString("member"),
                                    obj.getString("posted_by_user"),obj.getString("category_name"),obj.getString("request_status"));
                            eventlist.add(item);

                        }
                        Collections.reverse(eventlist);
                        setdata();


                        progressDialog.dismiss();
                        progressDialog.cancel();
                        // showToastLong(msg);
                    }
                    else{
                        progressDialog.dismiss();
                        progressDialog.cancel();
                        showToastLong(msg);
                    }
                }
                else{
                    progressDialog.dismiss();
                    progressDialog.cancel();
                    showToastLong("Sorry! Problem cannot be recognized.");
                }
            } catch(Exception e){
                progressDialog.dismiss();
                progressDialog.cancel();
                e.printStackTrace();
            }
        }

    }


    private void showToastLong(String string) {
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
    }

    private void setdata() {

        EventRequestAdapter recentCallAdapter = new EventRequestAdapter(this,eventlist);
        recycler_view.setAdapter(recentCallAdapter);
    }



}


