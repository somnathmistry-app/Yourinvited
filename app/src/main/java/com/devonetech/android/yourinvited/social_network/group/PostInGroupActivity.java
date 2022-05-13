package com.devonetech.android.yourinvited.social_network.group;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.FeedPostActivity;
import com.devonetech.android.yourinvited.social_network.ShareLocationActivity;
import com.devonetech.android.yourinvited.social_network.SocialNetworkMain;
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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PostInGroupActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    EditText edittext;
    private final long DELAY = 1000;

    private Timer timer = new Timer();

    private ImageView imgPreview;
    private VideoView vidPreview;
    private ImageView cam_pic,attachUrl,cam_video,shareLocation;

    private MultipartEntity reqEntity;
    //////take pic\\\\\\
    final int GALLARY_CAPTURE_IMAGE = 1;
    final int GALLARY_CAPTURE_VIDEO = 2;
    final int CAMERA_CAPTURE = 100;
    final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    Uri fileUri;
    private Bitmap photo = null;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static String TAG;
    Boolean cameraboolean = false,cropboolean = false;
    String picturePath=null;
    String imagePath="";
    String responseString=null;
    //////end take pic\\\\\\
    private TextView add_post_public,add_post_private;
    private String Post_comment="",Post_type="",img_s;
    UserShared psh;
    ConnectionDetector connection;
    private ProgressDialog progressDialog;

    private Bitmap resized;
    private File image_thumbnail;
    private File video_thumbnail;

    final CharSequence[] options_image = {"Camera Image", "Gallery Images", "Cancel"};
    final CharSequence[] options_video = {"Camera Video", "Gallery Video", "Youtube URl","Cancel"};

    private LinearLayout fl_selectedPhoto;
    public static ArrayList<Uri> photoList = new ArrayList<>();
    ArrayList<String> filepathstr;
    private LinearLayout.LayoutParams lp;
    private String imageType="";
    Intent mIntent;

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    private String Privacy_Type="";

    private File Final_Thumbnail;
    File profile_upload_file;
    private AlertDialog alertDialog;
    private String PostUrl="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_post_activity);
        psh=new UserShared(this);
        mIntent=getIntent();
        connection=new ConnectionDetector(this);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        ((AppController)getApplicationContext()).setImageUri(fileUri);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Share Moments");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent();
                intent.putExtra("post_status", "");
                setResult(200,intent);
                finish();

            }
        });



        xmlinti();
        xmlonclick();



    }///end of oncreate
    private void xmlinti() {
        // TODO Auto-generated method stub
        edittext=(EditText)findViewById(R.id.share_txt);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);
        cam_pic=(ImageView)findViewById(R.id.take_pic_with_cam);
        attachUrl=(ImageView)findViewById(R.id.attach_url);
        cam_video=(ImageView)findViewById(R.id.make_video);
        add_post_public=(TextView)findViewById(R.id.public_post);
        fl_selectedPhoto=findViewById(R.id.fl_selectedPhoto);
        shareLocation=findViewById(R.id.share_location);



    }
    private void xmlonclick() {
        // TODO Auto-generated method stub
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                //avoid triggering event when text is too short
                if (s.length() >= 3) {

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                        }

                    }, DELAY);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
                if(timer != null)
                    timer.cancel();
            }

        });

        cam_pic.setOnClickListener(this);
        attachUrl.setOnClickListener(this);
        cam_video.setOnClickListener(this);
        add_post_public.setOnClickListener(this);
        shareLocation.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.take_pic_with_cam:


                openImageDialog();

                break;
            case R.id.attach_url:


                openUrlDialog();


                break;
            case R.id.make_video:


                openVideoDialog();

                break;
            case R.id.public_post:

                validation();


                break;
            case R.id.share_location:

                Intent i=new Intent(PostInGroupActivity.this,ShareLocationInGroup.class);
                i.putExtra("group_id",mIntent.getStringExtra("group_id"));
                startActivity(i);

                break;



            default:
                break;
        }
    }

    private void openUrlDialog() {

        LayoutInflater inflater = LayoutInflater.from(PostInGroupActivity.this);
        View subView = inflater.inflate(R.layout.dialog_attachurl, null);
        final EditText url_edt = (EditText) subView.findViewById(R.id.email_edt);
        final Button reset_btn = (Button) subView.findViewById(R.id.reset_btn);
        final TextInputLayout email_text_input_layout = (TextInputLayout) subView.findViewById(R.id.email_text_input_layout);

        AlertDialog.Builder builder = new AlertDialog.Builder(PostInGroupActivity.this);
        builder.setTitle(" ");
        builder.setView(subView);
        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (button != null) {
                    button.setEnabled(false);
                }
            }
        });

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_text_input_layout.setErrorEnabled(false);
                if (url_edt.getText().toString().isEmpty() || !Patterns.WEB_URL.matcher(url_edt.getText().toString()).matches()) {

                    email_text_input_layout.setError("enter a valid url address");
                } else {

                    Post_type="url";
                    PostUrl=url_edt.getText().toString();
                    alertDialog.dismiss();
                    validation();
                    //callasyntask(alertDialog, url_edt.getText().toString());

                }
            }
        });


        builder.setPositiveButton(" ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton(" ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();

    }


    private void openImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostInGroupActivity.this);
        builder.setTitle("Attach From...");
        builder.setItems(options_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options_image[item].equals("Camera Image")) {
                    imageType="single";

                    try {
                        // use standard intent to capture an image
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                        cameraboolean = true;
                        cropboolean = true;


                    } catch (ActivityNotFoundException anfe) {
                        // display an error message
                        String errorMessage = "oops! your device doesn't support capturing images!";

                        Toast.makeText(getApplicationContext(), errorMessage,
                                Toast.LENGTH_SHORT).show();

                    }

                } else if (options_image[item].equals("Gallery Images")) {

                    imageType="multiple";

                    Intent chooseIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    chooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(chooseIntent, GALLARY_CAPTURE_IMAGE);


                } else if (options_image[item].equals("Cancel")) {
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void openVideoDialog() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(PostInGroupActivity.this);
        builder.setTitle("Attach From...");
        builder.setItems(options_video, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options_video[item].equals("Camera Video")) {

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
                    startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);


                } else if (options_video[item].equals("Gallery Video")) {


                    Intent i = new Intent(Intent.ACTION_PICK,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, GALLARY_CAPTURE_VIDEO);

                }else if(options_video[item].equals("Youtube URl")){

                    openYoutubeUri();

                }


                else if (options_video[item].equals("Cancel")) {
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void openYoutubeUri() {

        LayoutInflater inflater = LayoutInflater.from(PostInGroupActivity.this);
        View subView = inflater.inflate(R.layout.dialog_attachurl, null);
        final EditText url_edt = (EditText) subView.findViewById(R.id.email_edt);
        final Button reset_btn = (Button) subView.findViewById(R.id.reset_btn);
        final TextView postTxt=subView.findViewById(R.id.postTXt);
        postTxt.setText("Youtube URL");
        final TextInputLayout email_text_input_layout = (TextInputLayout) subView.findViewById(R.id.email_text_input_layout);

        AlertDialog.Builder builder = new AlertDialog.Builder(PostInGroupActivity.this);
        builder.setTitle(" ");
        builder.setView(subView);
        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (button != null) {
                    button.setEnabled(false);
                }
            }
        });

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_text_input_layout.setErrorEnabled(false);
                if (url_edt.getText().toString().isEmpty() || !Patterns.WEB_URL.matcher(url_edt.getText().toString()).matches()) {

                    email_text_input_layout.setError("enter a valid url address");
                } else {

                    Post_type="video";
                    PostUrl=url_edt.getText().toString();
                    alertDialog.dismiss();
                    validation();
                    //callasyntask(alertDialog, url_edt.getText().toString());

                }
            }
        });


        builder.setPositiveButton(" ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton(" ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }


    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        // TODO Auto-generated method stub
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                com.devonetech.android.yourinvited.Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + com.devonetech.android.yourinvited.Config.IMAGE_DIRECTORY_NAME + " directory");


                return null;
            }

        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".png");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                //launchUploadActivity(true);

                picturePath=fileUri.getPath();

                BitmapFactory.Options options = new BitmapFactory.Options();

                // down sizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;

                photo = BitmapFactory.decodeFile(picturePath, options);
                imgPreview.setVisibility(View.VISIBLE);
                vidPreview.setVisibility(View.GONE);
                fl_selectedPhoto.setVisibility(View.GONE);
                imgPreview.setImageBitmap(photo);
                imagePath=picturePath;
                Post_type="image";


            }
            else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            }


            else{

                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();

            }






        }
        else if (requestCode == GALLARY_CAPTURE_IMAGE) {

            if (resultCode == RESULT_OK) {
                Post_type="image";
                imgPreview.setVisibility(View.GONE);
                vidPreview.setVisibility(View.GONE);
                fl_selectedPhoto.setVisibility(View.VISIBLE);

                // Multi Image result
                photoList.clear();
                fl_selectedPhoto.removeAllViews();

                if (data.getClipData() != null) {

                    int count=data.getClipData().getItemCount();
                    // Toast.makeText(this, count+" Image Selected", Toast.LENGTH_SHORT).show();

                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        fileUri = item.getUri();
                        // profile_upload_file = new File(getRealPathFromURI_API19(AddProductActivity.this, profile_pic_url));
                        photoList.add(fileUri);
                    }
                } else if (data.getClipData() == null) {
                    Toast.makeText(this, "1 Image Selected", Toast.LENGTH_SHORT).show();
                    fileUri = data.getData();
                    photoList.add(fileUri);
                }
                launchUploadActivity();

              /*  if (photoList.size()<=4) {



                }
                else {

                    imgPreview.setVisibility(View.VISIBLE);
                    fl_selectedPhoto.setVisibility(View.GONE);
                    Toast.makeText(FeedPostActivity.this, "Max Limit 4", Toast.LENGTH_SHORT).show();
                }*/

                // AddPhotoOnLayout();


            }
            else {

                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();

            }
        }

        else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                //launchUploadActivity(false);



                picturePath=fileUri.getPath();
                imgPreview.setVisibility(View.GONE);
                vidPreview.setVisibility(View.VISIBLE);
                fl_selectedPhoto.setVisibility(View.GONE);
                vidPreview.setVideoPath(picturePath);
                // start playing
                vidPreview.start();
                imagePath=picturePath;
                Post_type="video_file";

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        else if (requestCode == GALLARY_CAPTURE_VIDEO) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                //launchUploadActivity(false);
                Log.v("data", data.toString());

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Video.Media.DATA };
                Log.v("selectedImage", selectedImage.toString());
                Log.v("filePathColumn", filePathColumn.toString());

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                if(cursor!=null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                }else{
                    picturePath = selectedImage.toString();
                }

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(picturePath);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                Log.v("----exifOrion------", exifOrientation);
                Log.d("filepath", picturePath);
                photo = BitmapFactory.decodeFile(picturePath);

                cameraboolean = false;

                //picturePath=fileUri.getPath();
                imgPreview.setVisibility(View.GONE);
                vidPreview.setVisibility(View.VISIBLE);
                vidPreview.setVideoPath(picturePath);
                // start playing
                vidPreview.start();
                imagePath=picturePath;
                Post_type="video_file";

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }



    }//End of activity result


    private void launchUploadActivity() {
        // TODO Auto-generated method stub
        filepathstr=new ArrayList<String>();
        for (int i=0;i<=photoList.size()-1;i++) {
            ImageView image=new ImageView(this);
            image.setId(i);
            filepathstr.add(photoList.get(i).getPath());
            Log.d("start", filepathstr.toString());
            lp = new LinearLayout.LayoutParams(350, 350);
            lp.setMargins(10,10,10,10);
            /* lp.addRule(RelativeLayout.CENTER_IN_PARENT);*/
            image.setLayoutParams(lp);
            Picasso.with(this)
                    .load(photoList.get(i))
                    .fit()
                    .into(image);

            fl_selectedPhoto.addView(image);
            //showToastLong(filepathstr.toString());
        }


    }

    private void validation() {
        // TODO Auto-generated method stub

        Post_comment=edittext.getText().toString();




        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;

        if (!Post_type.equals("url")){

            if(TextUtils.isEmpty(Post_comment)){
                message = "Please write about your moment.";
                focusView = edittext;
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

                //File sourceFile = new File(picturePath);



                reqEntity = null;
                reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                reqEntity.addPart("user_id", new StringBody(psh.getUserId()));
                reqEntity.addPart("group_id", new StringBody(mIntent.getStringExtra("group_id")));

                reqEntity.addPart("description", new StringBody(Post_comment));



                if (Post_type.equals("image")){

                    if (imageType.equals("single")&&!imagePath.equals("")) {

                        File sourceFile1 = new File(imagePath);

                        reqEntity.addPart("userFiles[]", new FileBody(sourceFile1));
                        reqEntity.addPart("type", new StringBody(Post_type));



                    }
                    else if(imageType.equals("multiple")&&imagePath.equals("")){
                        if (photoList.size()>0){

                            for (int i = 0; i < photoList.size(); i++) {


                                profile_upload_file = new File(getRealPathFromURI_API19(PostInGroupActivity.this, photoList.get(i)));
                                reqEntity.addPart("userFiles[]", new FileBody(profile_upload_file));
                                reqEntity.addPart("type", new StringBody(Post_type));


                            }
                        }

                    }
                    else {

                        reqEntity.addPart("userFiles[]", new StringBody(""));

                    }
                }
                else if(Post_type.equals("video_file")){
                    if (!imagePath.equals("")) {

                        File sourceFile1 = new File(imagePath);

                        reqEntity.addPart("videofile", new FileBody(sourceFile1));
                        reqEntity.addPart("type", new StringBody(Post_type));



                    }
                    else {

                        reqEntity.addPart("videofile", new StringBody(""));

                    }
                }

                else if(Post_type.equals("video")){

                    reqEntity.addPart("url", new StringBody(PostUrl));
                    reqEntity.addPart("type", new StringBody(Post_type));

                }
                else  if(Post_type.equals("url")){
                    reqEntity.addPart("url", new StringBody(PostUrl));
                    reqEntity.addPart("type", new StringBody(Post_type));
                }
                else if(Post_type.equals("")){
                    reqEntity.addPart("type", new StringBody("post"));
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
    }///end of validation
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
            HttpPost httppost = new HttpPost(Constants.URL_FeedPost);

            try {


                /*to print in log*/

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                reqEntity.writeTo(bytes);

                String content = bytes.toString();

                Log.e("MultiPartEntityRequest:",content);

                /*to print in log*/
                httppost.setEntity(reqEntity);
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
                    int Ack = jsonObject.getInt("status");
                    String msg = jsonObject.getString("message");
                    if (Ack==1) {

                        Intent i=new Intent(PostInGroupActivity.this, SocialNetworkMain.class);
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



    private File bitmaptofile(Bitmap bmp) throws FileNotFoundException {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/MeMeYachtThumbnil";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, "sketchpad" + String.valueOf(bmp.getGenerationId()) + ".png");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return file;

    }


    public Bitmap createThumbnailFromPath(String filePath, int type){
        return ThumbnailUtils.createVideoThumbnail(filePath, type);
    }



    private void showToastLong(String message) {
        // TODO Auto-generated method stub
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



}
