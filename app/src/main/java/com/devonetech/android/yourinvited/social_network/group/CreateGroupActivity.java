package com.devonetech.android.yourinvited.social_network.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.Utility;
import com.devonetech.android.yourinvited.friends_section.adapter.FriendListAdapter;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.fragment.SearchFragment;
import com.devonetech.android.yourinvited.social_network.group.adapter.AllUserAdapter;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateGroupActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recycler_view;
    String responseString = null;
    private MultipartEntity reqEntity;
    private static String TAG;
    ConnectionDetector connection;
    UserShared psh;
    ArrayList<FriendModel> userlist;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView nodata;
    private SearchView searchView;
    Toolbar toolbar;
    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    ArrayList<String> selecteduser=new ArrayList<>();
    AllUserAdapter adapter;
    private RadioGroup radioGroup;
    private EditText gruopTitle;
    private ImageView groupImage;
    private TextView createGroup;
    String PrivecyType="",profile_pic_string="";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri profile_pic_url;
    private File profile_upload_file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_activity);
        psh=new UserShared(this);
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
        callListApi();
    }



    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        //pagestart=pageend;
        //int a=10;
        // int b=Integer.parseInt(pageend)+a;
        //  pageend = String.valueOf(b);
        callListApi();

    }

    private void xmlint() {

        recycler_view = (RecyclerView)findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        nodata = findViewById(R.id.no_data);
        userlist = new ArrayList<FriendModel>();
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

        radioGroup = findViewById(R.id.radioGroup);
        groupImage=findViewById(R.id.imageView5);
        gruopTitle=findViewById(R.id._title);
        createGroup=findViewById(R.id.createGroup);

        searchView=findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // TODO Auto-generated method stub

                adapter.getFilter().filter(query);

                return false;
            }
        });

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 openImageDialog();
            }
        });



    }

       private void openImageDialog() {

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(CreateGroupActivity.this);
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


    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {

        // super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == SELECT_FILE || requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {


            if (data.getData() != null) {
                profile_pic_url = data.getData();
                setdata();
            } else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profile_pic_url = getImageUri(this, photo);
                setdata();
            }
            profile_pic_string = profile_pic_url.toString();


            if (requestCode == SELECT_FILE) {
                setdata();
                onSelectFromGalleryResult(data, groupImage);
            } else if (requestCode == REQUEST_CAMERA) {
                setdata();
                onCaptureImageResult(data, groupImage);
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
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
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




    private void callListApi() {
        boolean cancel = false;
        String message = "";
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


            } catch (Exception e) {
                e.printStackTrace();
            }

            Boolean isInternetPresent = connection.isConnectingToInternet();

            if (isInternetPresent) {
               /* progressDialog = ProgressDialog.show(this,
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
            HttpPost httppost = new HttpPost(Constants.URL_FriendList);

            try {


                /*to print in log*/

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                reqEntity.writeTo(bytes);

                String content = bytes.toString();

                Log.e("MultiPartEntityRequest:", content);

                /*to print in log*/
                httppost.addHeader(Constants.authKEY, Constants.authVALUE);
                String credentials = Constants.authuserID + ":" + Constants.authPassword;
                String auth = "Basic " +
                        Base64.encodeToString(credentials.getBytes(),
                                Base64.NO_WRAP);
                //headers.put("Authorization", auth);
                httppost.addHeader("Authorization", auth);
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

            try {
                if (!responseString.equals("")) {

                    JSONObject jsonObject = new JSONObject(responseString);
                    String Ack = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if (Ack.equals("1")) {


                        JSONArray jj=jsonObject.getJSONArray("user");

                        for (int i = 0; i <jj.length() ; i++) {
                            JSONObject obj=jj.getJSONObject(i);
                            FriendModel item=new FriendModel(obj.getString("profile_image"),obj.getString("user_id"),obj.getString("name"),
                                    obj.getString("email"),"");
                            userlist.add(item);

                        }

                        setdata();

                        swipeRefreshLayout.setRefreshing(false);

                        // progressDialog.dismiss();
                        // progressDialog.cancel();
                        // showToastLong(msg);
                    } else {
                        //  progressDialog.dismiss();
                        //   progressDialog.cancel();
                        // showToastLong(msg);
                        setdata();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    //   progressDialog.dismiss();
                    //   progressDialog.cancel();
                    showToastLong("Sorry! Problem cannot be recognized.");
                    swipeRefreshLayout.setRefreshing(false);
                }
            } catch (Exception e) {
                //  progressDialog.dismiss();
                //  progressDialog.cancel();
                e.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
            }
        }

    }


    private void showToastLong(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void setdata() {

        if (userlist.size() > 0) {

            String Name=gruopTitle.getText().toString();
            nodata.setVisibility(View.GONE);
            recycler_view.setVisibility(View.VISIBLE);
            adapter = new AllUserAdapter(this, userlist,createGroup,radioGroup,gruopTitle,profile_pic_url);
            recycler_view.setAdapter(adapter);



        } else {
            nodata.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);


        }


    }
}
