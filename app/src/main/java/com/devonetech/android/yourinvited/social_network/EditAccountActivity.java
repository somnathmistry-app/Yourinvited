package com.devonetech.android.yourinvited.social_network;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.Utility;
import com.devonetech.android.yourinvited.event_section.AddEventActivity;
import com.devonetech.android.yourinvited.event_section.EventListActivity;
import com.devonetech.android.yourinvited.event_section.adapter.CatSpinnerAdapter;
import com.devonetech.android.yourinvited.event_section.model.ServiceCatModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAccountActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView back,coverPic,profilePic;
    private EditText name,number,aboutInfo,dob;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri profile_pic_url,cover_pic_url;
    private File profile_upload_file,cover_upload_file;
    private int flag;
    UserShared psh;
    private Button updateBtn;
    private  String Name,Number,About;
    String responseString=null;
    private MultipartEntity reqEntity;
    ConnectionDetector connection;
    private static String TAG;
    ProgressDialog progressDialog;
    private SharedPreferences prefs;
    private int mYear, mMonth, mDay, mHour, mMinute;
    final Calendar c = Calendar.getInstance();
    private ArrayList<ServiceCatModel> datalist;
    private ArrayList<String> yearlist;



    private CatSpinnerAdapter adapter;
    private String school_ID="",UniFrom="",UniTo="",SchFrom="",SchTo="",Gender,MSatus;
    private Spinner spinner_school,uni_from,uni_to,sch_from,sch_to,spinner_gender,spinner_status;
    private EditText birth_place,lives_in,occuapation,religious,political_incline,website;
    private EditText profession_skill,university;
    private EditText hobbies,fav_music_bands,fav_tv_show,fav_books,fav_movies,fav_writer,fav_games,other_interest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_activity);
        psh=new UserShared(this);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        connection=new ConnectionDetector(this);
        prefs=getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        xmlint();
        xmlOnclick();

        callTypeApi();

        createyearlist();
        setdata();
    }

    private void createyearlist() {

        yearlist=new ArrayList<>();

        for (int i = 1950; i < 2029; i++) {
            //List<Integer> list = new ArrayList<>();
            yearlist.add(String.valueOf(i));
            // Use the list further...
        }

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                yearlist);
        uni_from.setAdapter(spinnerArrayAdapter);
        uni_to.setAdapter(spinnerArrayAdapter);
        sch_from.setAdapter(spinnerArrayAdapter);
        sch_to.setAdapter(spinnerArrayAdapter);
    }

    private void setdata() {
        name.setText(psh.getUserName());
        if (!psh.getUserPhone().equals("")){
            number.setText(psh.getUserPhone());
        }
        else {
            number.setHint("");
        }

        if (!psh.getUserPic().equals("")) {
            try {
                String apiLink = psh.getUserPic();

                //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                String encodedurl = "";
                encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                        apiLink.lastIndexOf('/') + 1));
                Log.d("UP Social List adapter", "encodedurl:"+encodedurl);
                if (!apiLink.equals("") && apiLink != null) {
                    Picasso.with(this)
                            .load(encodedurl) // load: This path may be a remote URL,
                            .placeholder(R.drawable.no_data_found_1x)
                            //.resize(130, 130)
                            .error(R.drawable.no_data_found_1x)
                            .into(profilePic);
                    // Into: ImageView into which the final image has to be passed


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!psh.getCoverPic().equals("")) {
            try {
                String apiLink = psh.getCoverPic();

                //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                String encodedurl = "";
                encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                        apiLink.lastIndexOf('/') + 1));
                Log.d("UP Social List adapter", "encodedurl:"+encodedurl);
                if (!apiLink.equals("") && apiLink != null) {
                    Picasso.with(this)
                            .load(encodedurl) // load: This path may be a remote URL,
                            .placeholder(R.drawable.no_data_found_1x)
                            //.resize(130, 130)
                            .error(R.drawable.no_data_found_1x)
                            .into(coverPic);
                    // Into: ImageView into which the final image has to be passed


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        aboutInfo.setText(psh.getabout_me());
        dob.setText(psh.getdob());
        birth_place.setText(psh.getbirth_place());
        lives_in.setText(psh.getlives_in());
        //occuapation.setText(psh.geto);
        religious.setText(psh.getreligious());
        political_incline.setText(psh.getpolitical_view());
        website.setText(psh.getwebsite());
      //  spinner_gender=findViewById(R.id.spinner_gender);

        if (psh.getmstatus().equals("Married")){
            spinner_status.setSelection(0);
        }
        else {
            spinner_status.setSelection(1);
        }



        ////education

        profession_skill.setText(psh.getskills());
        university.setText(psh.getuniversity());





        for (int i = 0; i <yearlist.size() ; i++) {

            if (psh.getuniversitystart_year().equals(String.valueOf(yearlist.get(i)))){

                uni_from.setSelection(i);
            }
            else if(psh.getuniversityend_year().equals(String.valueOf(yearlist.get(i)))){

                uni_to.setSelection(i);
            }
            else if(psh.getschool_start_year().equals(String.valueOf(yearlist.get(i)))){

                sch_from.setSelection(i);
            }
            else if(psh.getschool_end_year().equals(String.valueOf(yearlist.get(i)))){

                sch_from.setSelection(i);
            }

        }







        ///hobbies & interest
        hobbies.setText(psh.gethobbies());
        fav_music_bands.setText(psh.getmusic());
        fav_tv_show.setText(psh.gettv_shows());
        fav_books.setText(psh.getbooks());
        fav_movies.setText(psh.getmovies());
        fav_writer.setText(psh.getwriters());
        fav_games.setText(psh.getgames());
        other_interest.setText(psh.getothers());


    }

    private void xmlOnclick() {

        back.setOnClickListener(this);
        coverPic.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        dob.setOnClickListener(this);

        uni_from.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                UniFrom = yearlist.get(position).toString();
                //typeName=datalist.get(position).Service_catname;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        uni_to.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                UniTo = yearlist.get(position).toString();
                //typeName=datalist.get(position).Service_catname;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        sch_from.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                SchFrom = yearlist.get(position).toString();
                //typeName=datalist.get(position).Service_catname;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        sch_to.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                SchTo= yearlist.get(position).toString();
                //typeName=datalist.get(position).Service_catname;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spinner_status.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                MSatus = spinner_status.getItemAtPosition(position).toString();
                //typeName=datalist.get(position).Service_catname;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        spinner_gender.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Gender = spinner_gender.getItemAtPosition(position).toString();
                //typeName=datalist.get(position).Service_catname;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void xmlint() {

        back=findViewById(R.id.imageView3);

        updateBtn=findViewById(R.id.btn_update);

        spinner_school=findViewById(R.id.spinner_school);
        uni_from=findViewById(R.id.u_from);
        uni_to=findViewById(R.id.u_to);
        sch_from=findViewById(R.id.s_from);
        sch_to=findViewById(R.id.s_to);

        ///basic
        name=findViewById(R.id._name);
        number=findViewById(R.id._mobile);
        aboutInfo=findViewById(R.id.about_info);
        coverPic=findViewById(R.id.cover_picture);
        profilePic=findViewById(R.id.profile_picture);
        dob=findViewById(R.id.dob);
        birth_place=findViewById(R.id.birth_place);
        lives_in=findViewById(R.id.lives_in);
        occuapation=findViewById(R.id.occuapation);
        religious=findViewById(R.id.religious);
        political_incline=findViewById(R.id.political_incline);
        website=findViewById(R.id.website);
        spinner_gender=findViewById(R.id.spinner_gender);
        spinner_status=findViewById(R.id.spinner_m_status);


        ////education

        profession_skill=findViewById(R.id.profession_skill);
        university=findViewById(R.id.university);


        ///hobbies & interest
        hobbies=findViewById(R.id.hobbies);
        fav_music_bands=findViewById(R.id.fav_music_bands);
        fav_tv_show=findViewById(R.id.fav_tv_show);
        fav_books=findViewById(R.id.fav_books);
        fav_movies=findViewById(R.id.fav_movies);
        fav_writer=findViewById(R.id.fav_writer);
        fav_games=findViewById(R.id.fav_games);
        other_interest=findViewById(R.id.other_interest);


    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent=new Intent();
        setResult(200,intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imageView3:
                onBackPressed();

                break;
            case R.id.cover_picture:

                flag=1;
                camoption();
                break;
            case R.id.profile_picture:

                flag=2;
                camoption();
                break;
            case R.id.btn_update:

                validation();
                break;
            case R.id.dob:

                chooseDob();

                break;

        }

    }

    private void callTypeApi() {

        if (AppController.getInstance().isConnected()) {
            //progressDialog = Helper.setProgressDailog(AddCampaignActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.GET, Constants.URL_SchoolList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();
                                    JSONArray jj=jsonObject.getJSONArray("school");
                                    datalist=new ArrayList<ServiceCatModel>();


                                    for (int i = 0; i <jj.length() ; i++) {

                                        JSONObject obj=jj.getJSONObject(i);
                                        ServiceCatModel item=new ServiceCatModel(obj.getString("id"), obj.getString("school_name"), "");
                                        datalist.add(item);


                                    }



                                    setschool();


                                } else {

                                    Toast.makeText(EditAccountActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(EditAccountActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
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

    private void setschool() {
        adapter = new CatSpinnerAdapter(this, datalist, R.layout.spinner_rows);
        spinner_school.setAdapter(adapter);
       // Toast.makeText(getApplicationContext(),psh.getschool_id(),Toast.LENGTH_SHORT).show();

        for (int i = 0; i <datalist.size() ; i++) {

            if (psh.getschool_id().equals(datalist.get(i).Service_catId)){

                spinner_school.setSelection(i);
            }
            else{
                spinner_school.setSelection(0);
            }
        }


        spinner_school.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                school_ID = datalist.get(position).Service_catId;
                //typeName=datalist.get(position).Service_catname;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


    }

    private void chooseDob() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        //String SetDate=dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        String SetDate=dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        dob.setText(SetDate);



                    }





                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void camoption() {

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(EditAccountActivity.this);
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
      if (requestCode == SELECT_FILE || requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {


            if (data.getData() != null) {

                if (flag==1){
                    cover_pic_url = data.getData();
                }
                else if(flag==2){
                    profile_pic_url = data.getData();
                }

            } else {

                if (flag==1){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    cover_pic_url = getImageUri(EditAccountActivity.this, photo);
                }
                else if(flag==2){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    profile_pic_url = getImageUri(EditAccountActivity.this, photo);
                }

            }
          //  profile_pic_string = profile_pic_url.toString();

            if (requestCode == SELECT_FILE) {

                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
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
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(EditAccountActivity.this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (flag==1){
            coverPic.setImageBitmap(bm);
        }
        else if(flag==2){
            profilePic.setImageBitmap(bm);
        }



    }

    private void onCaptureImageResult(Intent data) {
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

        if (flag==1){
            coverPic.setImageBitmap(thumbnail);

        }
        else if(flag==2){
            coverPic.setImageBitmap(thumbnail);
        }



    }


    //////end of image


    private void validation() {

       // Name=name.getText().toString();
       // Number=number.getText().toString();
      //  About=aboutInfo.getText().toString();



        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;


/*


        if(TextUtils.isEmpty(Name)){
            message = "Please enter your fullname.";
            focusView = name;
            cancel = true;
            tempCond = false;
        }
        if(TextUtils.isEmpty(Number)){
            message = "Please enter your mobile number..";
            focusView = number;
            cancel = true;
            tempCond = false;
        }*/

        if (profile_pic_url != null) {

            profile_upload_file = new File(getRealPathFromURI_API19(this, profile_pic_url));
        }
        if (cover_pic_url!=null){
            cover_upload_file = new File(getRealPathFromURI_API19(this, cover_pic_url));
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


                /////basic


                reqEntity.addPart("user_id", new StringBody(psh.getUserId()));
                reqEntity.addPart("name", new StringBody(name.getText().toString()));
                reqEntity.addPart("mobile", new StringBody(number.getText().toString()));
                reqEntity.addPart("about_me", new StringBody(aboutInfo.getText().toString()));
                reqEntity.addPart("dob", new StringBody(dob.getText().toString()));
                reqEntity.addPart("birth_place", new StringBody(birth_place.getText().toString()));
                reqEntity.addPart("lives_in", new StringBody(lives_in.getText().toString()));
                reqEntity.addPart("gender", new StringBody(Gender));
                reqEntity.addPart("status", new StringBody(MSatus));
                reqEntity.addPart("website", new StringBody(website.getText().toString()));
                reqEntity.addPart("religious", new StringBody(religious.getText().toString()));
                reqEntity.addPart("political", new StringBody(political_incline.getText().toString()));




                if (profile_pic_url != null) {
                    reqEntity.addPart("profile_image", new FileBody(profile_upload_file));
                } else {
                    reqEntity.addPart("profile_image", new StringBody(""));
                }


                if (cover_pic_url != null) {
                    reqEntity.addPart("cover_image", new FileBody(cover_upload_file));
                } else {
                    reqEntity.addPart("cover_image", new StringBody(""));
                }

                ///basic end


                ///education

                reqEntity.addPart("skill", new StringBody(profession_skill.getText().toString()));
                reqEntity.addPart("university", new StringBody(university.getText().toString()));
                reqEntity.addPart("university_start_year", new StringBody(UniFrom));
                reqEntity.addPart("university_end_year", new StringBody(UniTo));
                reqEntity.addPart("school", new StringBody(school_ID));
                reqEntity.addPart("school_start_year", new StringBody(SchFrom));
                reqEntity.addPart("school_end_year", new StringBody(SchTo));



                ///end education

                ///hobbies

                reqEntity.addPart("hobbies", new StringBody(hobbies.getText().toString()));
                reqEntity.addPart("tv_show", new StringBody(fav_tv_show.getText().toString()));
                reqEntity.addPart("movies", new StringBody(fav_movies.getText().toString()));
                reqEntity.addPart("games", new StringBody(fav_games.getText().toString()));
                reqEntity.addPart("music", new StringBody(fav_music_bands.getText().toString()));
                reqEntity.addPart("books", new StringBody(fav_books.getText().toString()));
                reqEntity.addPart("writers", new StringBody(fav_writer.getText().toString()));
                reqEntity.addPart("others", new StringBody(other_interest.getText().toString()));




                ///end hobbies


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
            HttpPost httppost = new HttpPost(Constants.URL_EditProfile);

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

                        // alertDialog.cancel();
                        JSONObject obj=jsonObject.getJSONObject("data");

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.shared_user_fullname), obj.getString("name"));
                        editor.putString(getString(R.string.shared_user_email), obj.getString("email"));
                        editor.putString(getString(R.string.shared_user_number), obj.getString("mobile"));
                        editor.putString(getString(R.string.shared_user_picture), obj.getString("profile_image"));
                        editor.putString(getString(R.string.shared_user_cover), obj.getString("cover_image"));

                        editor.putString(getString(R.string.shared_user_about_me), obj.getString("about_me"));
                        editor.putString(getString(R.string.shared_user_birth_place), obj.getString("birth_place"));
                        editor.putString(getString(R.string.shared_user_lives_in), obj.getString("lives_in"));
                        editor.putString(getString(R.string.shared_user_status), obj.getString("status"));
                        editor.putString(getString(R.string.shared_user_website), obj.getString("website"));
                        editor.putString(getString(R.string.shared_user_political_view), obj.getString("political_view"));
                        editor.putString(getString(R.string.shared_user_religious), obj.getString("religious"));
                        editor.putString(getString(R.string.shared_user_date), obj.getString("date"));
                        editor.putString(getString(R.string.shared_user_hobbies), obj.getString("hobbies"));
                        editor.putString(getString(R.string.shared_user_tv_shows), obj.getString("tv_shows"));
                        editor.putString(getString(R.string.shared_user_movies), obj.getString("movies"));
                        editor.putString(getString(R.string.shared_user_games), obj.getString("games"));
                        editor.putString(getString(R.string.shared_user_music), obj.getString("music"));
                        editor.putString(getString(R.string.shared_user_books), obj.getString("books"));
                        editor.putString(getString(R.string.shared_user_writers), obj.getString("writers"));
                        editor.putString(getString(R.string.shared_user_others), obj.getString("others"));
                        editor.putString(getString(R.string.shared_user_skills), obj.getString("skills"));
                        editor.putString(getString(R.string.shared_user_school_start_year), obj.getString("school_start_year"));
                        editor.putString(getString(R.string.shared_user_school_end_year), obj.getString("school_end_year"));
                        editor.putString(getString(R.string.shared_user_school), obj.getString("school"));
                        editor.putString(getString(R.string.shared_user_school_id), obj.getString("school_id"));
                        editor.putString(getString(R.string.shared_user_university), obj.getString("university"));
                        editor.putString(getString(R.string.shared_user_university_start_year), obj.getString("university_start_year"));
                        editor.putString(getString(R.string.shared_user_university_end_year), obj.getString("university_end_year"));

                        editor.commit();


                        onBackPressed();



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
        Toast.makeText(EditAccountActivity.this,s,Toast.LENGTH_SHORT).show();
    }


}
