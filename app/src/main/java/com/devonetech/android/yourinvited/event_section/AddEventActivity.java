package com.devonetech.android.yourinvited.event_section;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.Utility;
import com.devonetech.android.yourinvited.custom_view.MultiSelctionSpinner;
import com.devonetech.android.yourinvited.event_section.adapter.CatSpinnerAdapter;
import com.devonetech.android.yourinvited.event_section.model.ServiceCatModel;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.SocialNetworkMain;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class AddEventActivity  extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    MultiSelctionSpinner friendspinner;

    ProgressDialog progressDialog;
    private ArrayList<ServiceCatModel> datalist;
    private ArrayList<ServiceCatModel> datalist1;
    UserShared psh;
    CatSpinnerAdapter adapter;
    Spinner typeSpinner,spinner_codinator;
    private String Type_Id,Type_Name,profile_pic_string = "";
    ArrayList<FriendModel> friendlist;
    ArrayList<String> categoriesid;
    ArrayList<String> coordinatorid;

    ImageView campaignPic;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri profile_pic_url;
    private File profile_upload_file;

    private EditText event_title,event_start,event_end,event_description,event_location;



    String Event_StartTime="",Event_EndTime="",Lat="",Longti="",EventTitle="",EventDescription="",EventLocation="",friend_id_fulstring="";


    private int mYear, mMonth, mDay, mHour, mMinute;
    final Calendar c = Calendar.getInstance();

    private RelativeLayout createCampaign;
    String responseString=null;
    private MultipartEntity reqEntity;
    ConnectionDetector connection;
    private static String TAG;
    private ArrayList<FriendModel> userlist;
    private String typeID,typeName,TimeTag,typeID1;
    String timeStamp_start,timeStamp_end;
    private EditText customCategory;
    private TextInputLayout cus_cat_lay;
    private String CustomCat="";
    int flag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_add_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Event");
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
        psh=new UserShared(this);
        connection=new ConnectionDetector(this);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        datalist=new ArrayList<ServiceCatModel>();

        xmlinti();
        xmlonClick();
        callTypeApi();
        callLocationApi();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
       finish();
    }

    private void xmlinti() {
        friendspinner=findViewById(R.id._spinner);
        typeSpinner=findViewById(R.id.products);
        campaignPic=findViewById(R.id.campaignPic);
        event_title=findViewById(R.id._title);
        event_description=findViewById(R.id._description);
        event_start=findViewById(R.id.c_start);
        event_end=findViewById(R.id.c_end);
        event_location=findViewById(R.id._location);
        createCampaign=findViewById(R.id.gonext_page);
        cus_cat_lay=findViewById(R.id.cuscat_lay);
        customCategory=findViewById(R.id._custom_cat);
        spinner_codinator=findViewById(R.id._spinner_codinator);
    }
    private void xmlonClick() {

        campaignPic.setOnClickListener(this);
        event_start.setOnClickListener(this);
        event_end.setOnClickListener(this);
        event_location.setOnClickListener(this);

        createCampaign.setOnClickListener(this);

    }


    private void callTypeApi() {

        if (AppController.getInstance().isConnected()) {
            //progressDialog = Helper.setProgressDailog(AddCampaignActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_CatEvent,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();
                                    JSONArray jj=jsonObject.getJSONArray("category");



                                    for (int i = 0; i <jj.length() ; i++) {

                                        JSONObject obj=jj.getJSONObject(i);
                                        ServiceCatModel item=new ServiceCatModel(obj.getString("id"), obj.getString("name"), "");
                                        datalist.add(item);


                                    }

                                    ServiceCatModel item=new ServiceCatModel("", "Other", "");
                                    datalist.add(item);

                                    setdata();


                                } else {

                                    Toast.makeText(AddEventActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                //  progressDialog.hide();
                            }
                            // progressDialog.hide();


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
            Toast.makeText(AddEventActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void callLocationApi() {

        if (AppController.getInstance().isConnected()) {
            progressDialog = Helper.setProgressDailog(AddEventActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_userlist,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();
                                    userlist=new ArrayList<FriendModel>();
                                    JSONArray jj=jsonObject.getJSONArray("user");

                                    for (int i = 0; i <jj.length() ; i++) {
                                        JSONObject obj=jj.getJSONObject(i);
                                        if (obj.getString("is_friend").equals("2")){

                                            FriendModel item=new FriendModel(obj.getString("profile_image"),obj.getString("user_id"),obj.getString("name"),
                                                    obj.getString("email"),obj.getString("is_friend"));
                                            userlist.add(item);

                                        }


                                    }

                                    setfriends();

                                    setCorrdinator();



                                } else {

                                    Toast.makeText(AddEventActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AddEventActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void setCorrdinator() {
        datalist1=new ArrayList<>();

        ServiceCatModel item0=new ServiceCatModel(psh.getUserId(), psh.getUserName(), "");
        datalist1.add(item0);

        for (int i = 0; i <userlist.size() ; i++) {

            ServiceCatModel item1=new ServiceCatModel(userlist.get(i).user_id, userlist.get(i).name, "");
            datalist1.add(item1);

        }

        adapter = new CatSpinnerAdapter(this, datalist1, R.layout.spinner_rows);
        spinner_codinator.setAdapter(adapter);

        spinner_codinator.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                //city_et.setText();
                typeID1 = datalist1.get(position).Service_catId;


            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

    }

    protected void setfriends() {
        // TODO Auto-generated method stub
        ArrayList<String> categories = new ArrayList<String>();
        categoriesid=new ArrayList<String>();

        for (int i = 0; i < userlist.size(); i++) {

            categories.add(userlist.get(i).name);
            categoriesid.add(userlist.get(i).user_id);
        }
        friendspinner.setItems(categories);

        friendspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position,
                                       long arg3) {
                // TODO Auto-generated method stub
                //item_sp = parent.getItemAtPosition(position).toString();
                //Log.d("tag", item_sp);


                String item = parent.getItemAtPosition(position).toString();
                //Toast.makeText(PersonalRegistrationActivity.this, item, Toast.LENGTH_SHORT).show();
                friendspinner.setSelection(position);

                //Passanger_Type=item_sp;
                //callmainsearchAsyn();


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });


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
    private void setdata() {
        adapter = new CatSpinnerAdapter(this, datalist, R.layout.spinner_rows);
        typeSpinner.setAdapter(adapter);


        typeSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                //city_et.setText();
                typeID = datalist.get(position).Service_catId;
                typeName=datalist.get(position).Service_catname;

                if (typeName.equals("Other")){
                    flag=1;
                    cus_cat_lay.setVisibility(View.VISIBLE);

                }
                else {
                    flag=2;
                    cus_cat_lay.setVisibility(View.GONE);
                }



            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.campaignPic:
                final CharSequence[] items = {"Take Photo", "Choose from Library",
                        "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEventActivity.this);
                builder.setTitle("");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(AddEventActivity.this);
                        if (items[item].equals("Take Photo")) {
                            if (result)
                                cameraIntent();

                        } else if (items[item].equals("Choose from Library")) {
                            if (result)
                                galleryIntent();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

                break;
            case R.id.c_start:

               openDatetime();
               // getTimeStamp();
                TimeTag="start";

                break;
            case R.id.c_end:

                openDatetime();
                TimeTag="end";

                break;
            case R.id._location:


                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(this);
                    startActivityForResult(intent, 200);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                break;

            case R.id.gonext_page:

                validation();
                break;



        }

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(AddEventActivity.this, data);
                Log.i("tag", "Place: " + place.getName()+place.getLatLng());
                double source_lat = place.getLatLng().latitude;
                double source_lng = place.getLatLng().longitude;
                Log.d("lat", source_lat+" "+source_lng);
                 Lat=String.valueOf(source_lat);
                Longti=String.valueOf(source_lng);
                event_location.setText(place.getAddress());
                EventLocation=String.valueOf(place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


       else if (requestCode == SELECT_FILE || requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {


            if (data.getData() != null) {
                profile_pic_url = data.getData();
            } else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profile_pic_url = getImageUri(AddEventActivity.this, photo);
            }
            profile_pic_string = profile_pic_url.toString();

            if (requestCode == SELECT_FILE) {

                onSelectFromGalleryResult(data, campaignPic);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data, campaignPic);
            }


        } else if (resultCode == RESULT_CANCELED) {

            // user cancelled Image capture
            Toast.makeText(this,
                    "User cancelled image capture", Toast.LENGTH_SHORT)
                    .show();
        } else {

            // failed to capture image
            Toast.makeText(this,
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                    .show();

        }


    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data, ImageView imageView) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(AddEventActivity.this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        imageView.setImageBitmap(bm);

    }

    private void onCaptureImageResult(Intent data, ImageView imageView) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        imageView.setImageBitmap(thumbnail);

    }


    //////end of image


    private void openDatetime() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        //String SetDate=dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        String SetDate=year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        opentime(SetDate);



                    }





                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void opentime(final String setDate) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                if(TimeTag.equals("start")){
                    Event_StartTime=setDate+" "+selectedHour + ":" + selectedMinute;
                    event_start.setText(Event_StartTime);
                    getTimeStamp(Event_StartTime);
                }
                else {
                    Event_EndTime=setDate+" "+selectedHour + ":" + selectedMinute;
                    event_end.setText(Event_EndTime);
                    getTimeStamp(Event_EndTime);
                }



            }


        }, mHour, mMinute, false);//Yes 24 hour time
        //mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    private void getTimeStamp(String str_date) {


        DateFormat formatter ;
        Date date = null;
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            date = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (TimeTag.equals("start")){
            timeStamp_start=String.valueOf(date.getTime());
        }
        else {
            timeStamp_end=String.valueOf(date.getTime());
        }

       // System.out.println("Today is " + date.getTime());
       // Toast.makeText(AddEventActivity.this,String.valueOf(date.getTime()),Toast.LENGTH_SHORT).show();

       //getDateTime(abc);
    }

    private void getDateTime(String abc) {

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.setTimeInMillis(Long.parseLong(abc) * 1000);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currenTimeZone = (Date) calendar.getTime();
        String axbc=String.valueOf(currenTimeZone);
        Toast.makeText(AddEventActivity.this,String.valueOf(currenTimeZone),Toast.LENGTH_SHORT).show();

    }


    /////end of date time

    private void validation() {

        EventTitle=event_title.getText().toString();
        EventDescription=event_description.getText().toString();
        EventLocation=event_location.getText().toString();
        CustomCat=customCategory.getText().toString();



        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;


        if (profile_pic_url == null) {
            message = "Please Select Event Picture.";
            focusView = campaignPic;
            cancel = true;
            tempCond = false;
        }



        if(TextUtils.isEmpty(EventDescription)){
            message = "Please enter Event description.";
            focusView = event_description;
            cancel = true;
            tempCond = false;
        }
        if(TextUtils.isEmpty(Event_EndTime)){
            message = "Please choose Event end time.";
            focusView = event_end;
            cancel = true;
            tempCond = false;
        }

        if(TextUtils.isEmpty(Event_StartTime)){
            message = "Please choose Event start time.";
            focusView = event_start;
            cancel = true;
            tempCond = false;
        }
        if(TextUtils.isEmpty(EventLocation)){
            message = "Please enter Event location.";
            focusView = event_location;
            cancel = true;
            tempCond = false;
        }

        if(TextUtils.isEmpty(EventTitle)){
            message = "Please enter Event title.";
            focusView = event_title;
            cancel = true;
            tempCond = false;
        }
        if (profile_pic_url != null) {

            profile_upload_file = new File(getRealPathFromURI_API19(this, profile_pic_url));
        }

        if (flag==1){
            if(TextUtils.isEmpty(CustomCat)){
                message = "Please enter event type.";
                focusView = customCategory;
                cancel = true;
                tempCond = false;
            }
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
                reqEntity.addPart("name", new StringBody(EventTitle));
                reqEntity.addPart("location", new StringBody(EventLocation));
                reqEntity.addPart("description", new StringBody(EventDescription));
                reqEntity.addPart("start_datetimes", new StringBody(timeStamp_start));
                reqEntity.addPart("end_datetimes", new StringBody(timeStamp_end));
                reqEntity.addPart("latitude", new StringBody(Lat));
                reqEntity.addPart("longitude", new StringBody(Longti));

                if (flag==1){
                    reqEntity.addPart("category", new StringBody(""));
                    reqEntity.addPart("custom_category", new StringBody(CustomCat));
                }
                else {
                    reqEntity.addPart("category", new StringBody(typeID));
                    reqEntity.addPart("custom_category", new StringBody(""));
                }

                reqEntity.addPart("coordinator", new StringBody(typeID1));

                List<Integer> count=friendspinner.getSelectedIndicies();
                String _fulstringid = "";
                for(int i=0;i<count.size();i++){
                    _fulstringid =String.valueOf(categoriesid.get(count.get(i)));
                    reqEntity.addPart("friends[]", new StringBody(_fulstringid));
                }


                if (profile_pic_url != null) {
                    reqEntity.addPart("image", new FileBody(profile_upload_file));
                } else {
                    reqEntity.addPart("image", new StringBody(""));
                }


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
            HttpPost httppost = new HttpPost(Constants.URL_EventAdd);

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

                        Intent i=new Intent(AddEventActivity.this,EventListActivity.class);
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


    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        try { // FIXME NPE error when select image from QuickPic, Dropbox etc
            @SuppressLint({"NewApi", "LocalSuppress"})
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();

            return filePath;
        } catch (Exception e) { // this is the fix lol
            String result;
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = uri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }
    }

    private void showToastLong(String s) {
        Toast.makeText(AddEventActivity.this,s,Toast.LENGTH_SHORT).show();
    }


}
