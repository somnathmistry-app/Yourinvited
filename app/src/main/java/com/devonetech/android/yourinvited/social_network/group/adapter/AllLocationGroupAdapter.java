package com.devonetech.android.yourinvited.social_network.group.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.SocialNetworkMain;
import com.devonetech.android.yourinvited.social_network.TagFriendsActivity;
import com.devonetech.android.yourinvited.social_network.model.LocationModel;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;

public class AllLocationGroupAdapter extends  RecyclerView.Adapter<AllLocationGroupAdapter.ViewHolder> {


    private Context context;
    private int loCount;
    ArrayList<LocationModel> itemms;
    private String LOCATION="",LAT="",LONGI="",group_id;
    ProgressDialog progressDialog;
    String responseString=null;
    private MultipartEntity reqEntity;
    private static String TAG;
    UserShared psh;
    ConnectionDetector connection;

    public AllLocationGroupAdapter(Context context, ArrayList<LocationModel> items,String group_id) {
        this.itemms = items;
        this.context = context;
        this.group_id=group_id;
    }
   /* public AllLocationGroupAdapter(Context context) {
        this.context = context;
    }*/


    @Override
    public AllLocationGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_location, parent, false);
        return new AllLocationGroupAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllLocationGroupAdapter.ViewHolder holder, final int position) {


        holder.event_title.setText(itemms.get(position).location_title);
        holder.event_location.setText(itemms.get(position).location_name);
        Picasso.with(holder.event_image.getContext()).load(itemms.get(position).image).into(holder.event_image);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent=new Intent(context,TagFriendsActivity.class);
                intent.putExtra("search_location", itemms.get(position).location_name);
                intent.putExtra("search_lat", itemms.get(position).lati);
                intent.putExtra("search_laong", itemms.get(position).longi);
                context.startActivity(intent);*/
                LOCATION=itemms.get(position).location_name;
                LAT=itemms.get(position).location_name;
                LONGI=itemms.get(position).location_name;

                validation();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemms.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView event_image;

        public TextView event_title,event_location;

        public ViewHolder(View itemView) {
            super(itemView);
            event_image = (ImageView) itemView.findViewById(R.id.image);
            event_title=itemView.findViewById(R.id.name);
            event_location=itemView.findViewById(R.id.email);


        }
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
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            try {

                reqEntity = null;
                reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);


                reqEntity.addPart("user_id", new StringBody(psh.getUserId()));
                reqEntity.addPart("group_id", new StringBody(group_id));
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
                progressDialog = ProgressDialog.show(context,
                        "",
                        context.getString(R.string.progress_bar_loading_message),
                        false);

                new UploadFileToServer().execute();
            } else {
                showToastLong(context.getString(R.string.no_internet_message));
            }
        }

    }
    private void showToastLong(String s) {
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
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

                        Intent i=new Intent(context, SocialNetworkMain.class);
                        context.startActivity(i);



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
