package com.devonetech.android.yourinvited.social_network.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.friends_section.adapter.RequestListAdapter;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.shared.UserShared;

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

public class RequestedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public RequestedFragment() {
        // Required empty public constructor
    }

    View rootView;
    RecyclerView recycler_view;
    //ProgressDialog progressDialog;

    String responseString=null;
    private MultipartEntity reqEntity;
    private static String TAG;
    ConnectionDetector connection;
    UserShared psh;
    ArrayList<FriendModel> userlist;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView nodata;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_swipetorefresh, container, false);

        connection=new ConnectionDetector(getActivity());
        psh=new UserShared(getActivity());
        xmlini(rootView);



        return rootView;
    }

    private void xmlini(View rootView) {

        recycler_view = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        nodata=rootView.findViewById(R.id.no_data);


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        // fetchMovies();


                                        callListApi();
                                    }
                                }
        );
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub

        callListApi();
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
            InputMethodManager imm = (InputMethodManager) getActivity()
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
               /* progressDialog = ProgressDialog.show(getActivity(),
                        "",
                        getString(R.string.progress_bar_loading_message),
                        false);*/
                swipeRefreshLayout.setRefreshing(true);
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
            HttpPost httppost = new HttpPost(Constants.URL_FriendRequestList);

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
                    userlist=new ArrayList<FriendModel>();
                    if (Ack.equals("1")) {


                        JSONArray jj=jsonObject.getJSONArray("user");

                        for (int i = 0; i <jj.length() ; i++) {
                            JSONObject obj=jj.getJSONObject(i);
                            FriendModel item=new FriendModel(obj.getString("profile_image"),obj.getString("user_id"),obj.getString("name"),
                                    obj.getString("email"),obj.getString("is_friend"));
                            userlist.add(item);

                        }

                        setdata();

                        swipeRefreshLayout.setRefreshing(false);

                        // progressDialog.dismiss();
                        // progressDialog.cancel();
                        // showToastLong(msg);
                    }
                    else{
                        //  progressDialog.dismiss();
                        //   progressDialog.cancel();
                        // showToastLong(msg);
                        setdata();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                else{
                    //   progressDialog.dismiss();
                    //   progressDialog.cancel();
                    showToastLong("Sorry! Problem cannot be recognized.");
                    swipeRefreshLayout.setRefreshing(false);
                }
            } catch(Exception e){
                //  progressDialog.dismiss();
                //  progressDialog.cancel();
                e.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
            }
        }

    }


    private void showToastLong(String string) {
        Toast.makeText(getActivity(),string, Toast.LENGTH_SHORT).show();
    }

    private void setdata() {

        if (userlist.size()>0){

            nodata.setVisibility(View.GONE);
            recycler_view.setVisibility(View.VISIBLE);
            RequestListAdapter recentCallAdapter = new RequestListAdapter(getActivity(),userlist);
            recycler_view.setAdapter(recentCallAdapter);
        }
        else {
            nodata.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

        }


    }


}