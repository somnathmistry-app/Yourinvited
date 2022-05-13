package com.devonetech.android.yourinvited;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;/*

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devonetech.android.startflic.network.ConnectionDetector;
import com.devonetech.android.startflic.network.Constants;
import com.devonetech.android.startflic.network.Helper;*/

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.devonetech.android.yourinvited.friends_section.FriendListActivity;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.social_network.SocialNetworkMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener{

    Toolbar toolbar;
    private LinearLayout signUptv;
    private EditText username,password;
    ProgressDialog progressDialog;
    String UserName ,Password;
    private Button signIntv;
    ConnectionDetector connection;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs=getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        connection=new ConnectionDetector(this);

        inti();
        xmlonclick();
    }

    private void xmlonclick() {
        signUptv.setOnClickListener(this);
        signIntv.setOnClickListener(this);
    }

    private void inti() {

        signUptv=findViewById(R.id.signup_lay);
        signIntv=findViewById(R.id.btn_signup_signup);
        username=findViewById(R.id._email);
        password=findViewById(R.id._password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_lay:

                Intent i=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(i);
                break;
            case R.id.btn_signup_signup:
                validation();

                break;

        }
    }

    private void validation() {
        UserName =username.getText().toString();
        Password = password.getText().toString();



        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;

        if(TextUtils.isEmpty(UserName)){
            message = "Please enter Your UserName Name.";
            focusView = username;
            cancel = true;
            tempCond = false;
        }
        if(TextUtils.isEmpty(Password)){
            message = "Please enter your password.";
            focusView = password;
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



            callv();
        }
    }
    private void showToastLong(String message) {
        Toast.makeText(LoginActivity.this,message,Toast.LENGTH_SHORT).show();
    }


    private void callv() {

        if (AppController.getInstance().isConnected()) {
            progressDialog = Helper.setProgressDailog(LoginActivity.this);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();
                                    JSONObject obj=jsonObject.getJSONObject("user");

                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean(getString(R.string.shared_loggedin_status_user),true);
                                    editor.putString(getString(R.string.shared_user_id), obj.getString("user_id"));
                                    editor.putString(getString(R.string.shared_user_fullname), obj.getString("name"));
                                    editor.putString(getString(R.string.shared_user_email), obj.getString("email"));
                                    editor.putString(getString(R.string.shared_user_number), obj.getString("mobile"));
                                    editor.putString(getString(R.string.shared_user_picture), obj.getString("profile_image"));
                                    editor.putString(getString(R.string.shared_user_cover), obj.getString("cover_image"));



                                    editor.commit();
                                    Intent j=new Intent(LoginActivity.this,SocialNetworkMain.class);
                                    startActivity(j);
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } else {

                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                    params.put("email", UserName);
                    params.put("password", Password);
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
            Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
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

}
