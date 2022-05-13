package com.devonetech.android.yourinvited;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
/*
import com.devonetech.android.startflic.network.ConnectionDetector;
import com.devonetech.android.startflic.network.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;*/
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;

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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private EditText name,email,number,password,schoolAddress,gur_name,gur_email;
    private Spinner usertype;
    private LinearLayout studentLay,schoolLay;
    private Button signUp;
    private String Name,Number,Email,Password,USERType,SchoolAddress,GName,GEmail;
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );
    private ProgressDialog progressDialog;
   private MultipartEntity reqEntity;
    ConnectionDetector connection;
    String responseString=null;
    private static String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_user);

        connection=new ConnectionDetector(this);

        init();

    }

    private void init() {


        usertype=findViewById(R.id.user_type);
        schoolLay=findViewById(R.id.school_lay);
        studentLay=findViewById(R.id.student_lay);
        name=findViewById(R.id._name);
        email=findViewById(R.id._email);
        number=findViewById(R.id._mobile);
        password=findViewById(R.id._password);
        schoolAddress=findViewById(R.id._school_address);
        gur_name=findViewById(R.id._guardian_name);
        gur_email=findViewById(R.id._guardian_email);

        signUp=findViewById(R.id.btn_signup_signup);



        usertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                USERType = parent.getItemAtPosition(pos).toString();

                if (USERType.equals("Guest")){

                    schoolLay.setVisibility(View.GONE);
                    studentLay.setVisibility(View.GONE);

                }
                else if(USERType.equals("School")){

                    schoolLay.setVisibility(View.VISIBLE);
                    studentLay.setVisibility(View.GONE);
                }
                else if(USERType.equals("Student")){

                    schoolLay.setVisibility(View.GONE);
                    studentLay.setVisibility(View.VISIBLE);
                }



            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validation();

            }
        });





    }




    private void validation() {
        Name =name.getText().toString();
        Number =number.getText().toString();
        Email = email.getText().toString();
        Password = password.getText().toString();
        GName =gur_name.getText().toString();
        GEmail =gur_email.getText().toString();
        SchoolAddress =schoolAddress.getText().toString();


        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;

        if(TextUtils.isEmpty(Name)){
            message = "Please enter Your Full Name.";
            focusView = name;
            cancel = true;
            tempCond = false;
        }
        if(TextUtils.isEmpty(Email)){
            message = "Please enter your email.";
            focusView = email;
            cancel = true;
            tempCond = false;
        }


        if(TextUtils.isEmpty(Number)){
            message = "Please enter mobile number.";
            focusView = number;
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



                if (USERType.equals("Guest")){
                    reqEntity.addPart("user_type", new StringBody("1"));
                    reqEntity.addPart("name", new StringBody(Name));
                    reqEntity.addPart("mobile", new StringBody(Number));
                    reqEntity.addPart("email", new StringBody(Email));
                    reqEntity.addPart("password", new StringBody(Password));
                    reqEntity.addPart("terms", new StringBody("yes"));
                    reqEntity.addPart("lat", new StringBody("23.8556745"));
                    reqEntity.addPart("long", new StringBody("87.56842565"));


                }
                else if(USERType.equals("School")){
                    reqEntity.addPart("user_type", new StringBody("3"));
                    reqEntity.addPart("name", new StringBody(Name));
                    reqEntity.addPart("mobile", new StringBody(Number));
                    reqEntity.addPart("email", new StringBody(Email));
                    reqEntity.addPart("password", new StringBody(Password));
                    reqEntity.addPart("terms", new StringBody("yes"));
                    reqEntity.addPart("lat", new StringBody("23.8556745"));
                    reqEntity.addPart("long", new StringBody("87.56842565"));
                    reqEntity.addPart("address", new StringBody(SchoolAddress));

                }
                else if(USERType.equals("Student")){
                    reqEntity.addPart("user_type", new StringBody("2"));
                    reqEntity.addPart("name", new StringBody(Name));
                    reqEntity.addPart("mobile", new StringBody(Number));
                    reqEntity.addPart("email", new StringBody(Email));
                    reqEntity.addPart("password", new StringBody(Password));
                    reqEntity.addPart("terms", new StringBody("yes"));
                    reqEntity.addPart("lat", new StringBody("23.8556745"));
                    reqEntity.addPart("long", new StringBody("87.56842565"));
                    reqEntity.addPart("gurdian_name", new StringBody(GName));
                    reqEntity.addPart("gurdian_email", new StringBody(GEmail));

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
        protected String doInBackground(Void... params) {
            return uploadFile();
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


                        Intent i=new Intent(RegistrationActivity.this,LoginActivity.class);
                        startActivity(i);

                        progressDialog.dismiss();
                        progressDialog.cancel();
                        //showToastLong("Profile created successfully.");
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

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
			progressDialog = ProgressDialog.show(RegistrationActivity.this,
					"",
					getString(R.string.progress_bar_loading_message),
					false);
            super.onPreExecute();
        }


        @SuppressWarnings("deprecation")
        private String uploadFile() {



            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL_REGISTRATION);

            try {


                /*to print in log*/

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                reqEntity.writeTo(bytes);

                String content = bytes.toString();

                Log.e("MultiPartEntityRequest:",content);

               /* to print in log*/

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

    }


    private void showToastLong(String string) {
        Toast.makeText(RegistrationActivity.this,string,Toast.LENGTH_SHORT).show();
    }
}
