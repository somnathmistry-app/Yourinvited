package com.devonetech.android.yourinvited.social_network.group;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.PlaceDetailsJSONParser;
import com.devonetech.android.yourinvited.network.PlaceJSONParser;
import com.devonetech.android.yourinvited.shared.Updatedlatlong;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.ShareLocationActivity;
import com.devonetech.android.yourinvited.social_network.SocialNetworkMain;
import com.devonetech.android.yourinvited.social_network.TagFriendsActivity;
import com.devonetech.android.yourinvited.social_network.group.adapter.AllAddressAdapter;
import com.devonetech.android.yourinvited.social_network.group.adapter.AllLocationGroupAdapter;
import com.devonetech.android.yourinvited.social_network.model.LocationModel;
import com.google.android.gms.maps.model.LatLng;

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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ShareLocationInGroup extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    AutoCompleteTextView atvPlaces;
    ConnectionDetector connection;

    private double lati,longi;

    DownloadTask placeDetailsDownloadTask;
    ParserTask placeDetailsParserTask;
    final int PLACES=0;

    final int PLACES_DETAILS=1;
    DownloadTask placesDownloadTask;
    ParserTask placesParserTask;
    LatLng search_latlong;
    private String Search_place="";
    UserShared psh;
    Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView nodata;
    RecyclerView recycler_save_lcoation;
    ArrayList<LocationModel> locationList;
    private ProgressDialog progressDialog;

    String responseString=null;
    private MultipartEntity reqEntity;
    private static String TAG;
    private CheckBox locationOption;
    private TextView share_current_location;
    private Updatedlatlong lat_psh;
    String place,address;
    public static  String currentaddress="";
    public static String currentlat="";
    public static String currentlong="";
    Intent mIntent;
    List<Address> addresse;
    private String LOCATION="",LAT="",LONGI="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_location_activity);
        psh=new UserShared(this);
        mIntent=getIntent();
        lat_psh=new Updatedlatlong(this);
        connection=new ConnectionDetector(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        xmlint();

        try {
            currentaddress=getstraddress(Double.parseDouble(lat_psh.getUserUpdatedLatitude()),Double.parseDouble(lat_psh.getUserUpdatedLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentlat=String.valueOf(lat_psh.getUserUpdatedLatitude());
        currentlong=String.valueOf(lat_psh.getUserUpdatedLongitude());
        //Toast.makeText(ShareLocationActivity.this,currentaddress,Toast.LENGTH_SHORT).show();


    }
    @Override
    public void onRefresh() {

        callListApi();

    }



    private void xmlint() {

        atvPlaces = (AutoCompleteTextView) findViewById(R.id.locationn);
        atvPlaces.setThreshold(1);

        // Adding textchange listener
        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }



            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching "s"

                Boolean isInternetPresent = connection.isConnectingToInternet();
                if (isInternetPresent) {

                    placesDownloadTask = new DownloadTask(PLACES);
                    String url="";
                    try {
                        // Getting url to the Google Places Autocomplete api
                        url = getAutoCompleteUrl(URLEncoder.encode(s.toString(),"utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // Start downloading Google Places
                    // This causes to execute doInBackground() of DownloadTask class

                    placesDownloadTask.execute(url);


                }
                else{
                    showToastLong(getString(R.string.no_internet_message));
                }

            }
        });

        // Setting an item click listener for the AutoCompleteTextView dropdown list
        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {
                Search_place=atvPlaces.getText().toString();
                Log.d("place ----", Search_place);

                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));

                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);



            }
        });

        recycler_save_lcoation = (RecyclerView)findViewById(R.id.my_recycler_view);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_save_lcoation.setLayoutManager(mLayoutManager);
        recycler_save_lcoation.setItemAnimator(new DefaultItemAnimator());

        nodata = findViewById(R.id.no_data);
        locationList = new ArrayList<LocationModel>();
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
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

        locationOption = findViewById(R.id.checkBox);

        locationOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    recycler_save_lcoation.setVisibility(View.VISIBLE);
                    atvPlaces.setVisibility(View.GONE);
                }
                else {
                    recycler_save_lcoation.setVisibility(View.GONE);
                    atvPlaces.setVisibility(View.VISIBLE);
                }
            }
        });

        share_current_location=findViewById(R.id.share_current_location);
        share_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareCurrentLocation();
            }
        });



    }

    private void shareCurrentLocation() {


        if (!currentlat.equals("")&&!currentlong.equals("")){


            LOCATION=currentaddress;
            LAT=currentlat;
            LONGI=currentlong;
            validation();

        }
    }

    private String getstraddress(double latitude, double longitude) throws IOException {
        // TODO Auto-generated method stub
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        addresse  = geocoder.getFromLocation(latitude,longitude, 1);

        place = addresse.get(0).getAddressLine(0);
        address = addresse.get(0).getAddressLine(1);

        //source_txt.setText(place);
        //set_flag="0";
        // addsource_marker();
        if (address!=null){
            return (place + "," + address);
        }
        else {
            return (place);
        }
        /*location_name_tv.setVisibility(View.VISIBLE);
        location_name_tv.setText(place + "," + address);
        source.setText(place + "," + address);*/
    }



    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try{

            strUrl=strUrl.replaceAll(" ", "%20");
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception ", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private String getAutoCompleteUrl(String place){

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyBpn_h1uk7GZo522LNlqy0aNbhyDXAY4Xw";

        // place to be be searched
        String input = "input="+place;

        // place type to be searched
        String types = "types=geocode";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = input+"&"+types+"&"+sensor+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        //for country base use "&types=(cities)&components=country:uk"

        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

        return url;
    }

    private String getPlaceDetailsUrl(String ref){

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyBpn_h1uk7GZo522LNlqy0aNbhyDXAY4Xw";

        // reference of place
        String reference = "reference="+ref;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference+"&"+sensor+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;

        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType=0;

        // Constructor
        public DownloadTask(int type){
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch(downloadType){
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);

                    break;

                case PLACES_DETAILS :
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
            }
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        int parserType = 0;

        public ParserTask(int type){
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try{
                jObject = new JSONObject(jsonData[0]);

                switch(parserType){
                    case PLACES :
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS :
                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                }

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch(parserType){
                case PLACES :
                    String[] from = new String[] { "description"};
                    int[] to = new int[] { android.R.id.text1 };

                    if (result!=null) {

                        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

                        // Setting the adapter
                        atvPlaces.setAdapter(adapter);
                        synchronized (adapter){
                            adapter.notifyDataSetChanged();
                        }

                    }

                    // Creating a SimpleAdapter for the AutoCompleteTextView


                    break;
                case PLACES_DETAILS :
                    HashMap<String, String> hm = result.get(0);

                    // Getting latitude from the parsed data
                    double latitude = Double.parseDouble(hm.get("lat"));

                    // Getting longitude from the parsed data
                    double longitude = Double.parseDouble(hm.get("lng"));

                    // Getting reference to the SupportMapFragment of the activity_main.xml
                    //SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                    // Getting GoogleMap from SupportMapFragment
                    //googleMap = fm.getMap();
                    InputMethodManager ipmm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    ipmm1.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);

                    LatLng point = new LatLng(latitude, longitude);
                    lati=latitude;
                    longi=longitude;

                    search_latlong=new LatLng(lati, longi);
                    Log.d("place  lat long---", String.valueOf(search_latlong));

                  /*  Intent intent=new Intent(ShareLocationInGroup.this,TagFriendsActivity.class);
                    intent.putExtra("search_location", Search_place);
                    intent.putExtra("search_lat", String.valueOf(lati));
                    intent.putExtra("search_laong", String.valueOf(longi));
                    startActivity(intent);*/
                    LOCATION=Search_place;
                    LAT=String.valueOf(lati);
                    LONGI=String.valueOf(longi);
                    validation();

                   /* Intent intent=new Intent(FabLocationActivity.this, SaveLocation.class);
                    intent.putExtra("search_location", Search_place);
                    intent.putExtra("search_lat", String.valueOf(lati));
                    intent.putExtra("search_laong", String.valueOf(longi));
                    intent.putExtra("fm", mIntent.getStringExtra("from"));
                    startActivity(intent);*/





                    break;
            }
        }
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
            HttpPost httppost = new HttpPost(Constants.URL_AddedPlace);

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
                    locationList=new ArrayList<LocationModel>();
                    if (Ack.equals("1")) {



                        JSONArray jj=jsonObject.getJSONArray("data");

                        for (int i = 0; i <jj.length() ; i++) {
                            JSONObject obj=jj.getJSONObject(i);



                            LocationModel item=new LocationModel(obj.getString("id"),obj.getString("location_name"),
                                    obj.getString("location_title"),
                                    obj.getString("lati"),
                                    obj.getString("longi"),
                                    obj.getString("user_id"),
                                    obj.getString("image")
                            );
                            locationList.add(item);

                        }
                        Collections.reverse(locationList);
                        setdata();
                        swipeRefreshLayout.setRefreshing(false);


                        // progressDialog.dismiss();
                        // progressDialog.cancel();
                        // showToastLong(msg);
                    }
                    else{
                        // progressDialog.dismiss();
                        // progressDialog.cancel();
                        showToastLong(msg);
                        // setdata();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                else{
                    // progressDialog.dismiss();
                    //   progressDialog.cancel();
                    showToastLong("Sorry! Problem cannot be recognized.");
                }
            } catch(Exception e){
                //  progressDialog.dismiss();
                //  progressDialog.cancel();
                e.printStackTrace();
            }
        }

    }




    private void setdata() {

        AllLocationGroupAdapter recentCallAdapter = new AllLocationGroupAdapter(this,locationList,mIntent.getStringExtra("group_id"));
        recycler_save_lcoation.setAdapter(recentCallAdapter);
    }



    private void validation() {




        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;




        if (LOCATION.equals("")){

            message = "Please Select Location.";
            cancel = true;
            tempCond = false;

        }



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

                reqEntity = null;
                reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);


                reqEntity.addPart("user_id", new StringBody(psh.getUserId()));
                reqEntity.addPart("group_id", new StringBody(mIntent.getStringExtra("group_id")));
                reqEntity.addPart("type", new StringBody("map"));
                reqEntity.addPart("location", new StringBody(LOCATION));
                reqEntity.addPart("lati", new StringBody(LAT));
                reqEntity.addPart("longi", new StringBody(LONGI));




            }

            catch (Exception e) {
                e.printStackTrace();
            }

            Boolean isInternetPresent = connection.isConnectingToInternet();

            if (isInternetPresent) {
                progressDialog = ProgressDialog.show(this,
                        "",getString(R.string.progress_bar_loading_message),
                        false);

                new UploadToServer().execute();
            } else {
                showToastLong(this.getString(R.string.no_internet_message));
            }
        }

    }
    private void showToastLong(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }



    private class UploadToServer extends AsyncTask<Void, Integer, String> {
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
            HttpPost httppost = new HttpPost(Constants.URL_FeedPost);

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

                        Intent i=new Intent(ShareLocationInGroup.this, SocialNetworkMain.class);
                        startActivity(i);



                        progressDialog.dismiss();
                        progressDialog.cancel();
                        showToastLong(msg);
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


}
