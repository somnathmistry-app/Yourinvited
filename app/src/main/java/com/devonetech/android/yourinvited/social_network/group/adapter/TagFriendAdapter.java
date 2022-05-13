package com.devonetech.android.yourinvited.social_network.group.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom_view.CustomAutoCompleteTextView;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.SocialNetworkMain;
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

public class TagFriendAdapter extends RecyclerView.Adapter<TagFriendAdapter.ViewHolder> {

    private ArrayList<FriendModel> items = new ArrayList<>();
    Activity context;
    ArrayList<String> xyz=new ArrayList<>();
    ArrayList<String> abc=new ArrayList<>();

    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    TextView shareLocation;

    ConnectionDetector connection;
    UserShared psh;
    ProgressDialog progressDialog;
    String responseString=null;
    private MultipartEntity reqEntity;
    private static String TAG;
    CustomFilter filter;
    ArrayList<FriendModel> filterList;
    private TextView post_location;
    String LOCATION="",LAT="",LONGI="";

    public TagFriendAdapter(Activity context, ArrayList<FriendModel> items,TextView post_location,String LOCATION,String LAT,String LONGI) {
        this.context=context;
        this.items=items;
        this.post_location=post_location;
        this.LOCATION=LOCATION;
        this.LAT=LAT;
        this.LONGI=LONGI;
        this.filterList=items;


    }



    @Override
    public TagFriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // context = parent.getContext();
        int layoutForItem = R.layout.item_all_users;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForItem, parent, false);
        return new TagFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TagFriendAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void loadItems(ArrayList<FriendModel> tournaments) {
        this.items = tournaments;
        notifyDataSetChanged();
    }




    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckedTextView mCheckedTextView;
        ImageView profilePic;
        TextView name,email;

        ViewHolder(View itemView) {
            super(itemView);
            mCheckedTextView = (CheckedTextView) itemView.findViewById(R.id.checked_text_view);
            profilePic=itemView.findViewById(R.id.image);
            name=itemView.findViewById(R.id.name);
            email=itemView.findViewById(R.id.email);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {

            connection=new ConnectionDetector(context);
            psh=new UserShared(context);

            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                mCheckedTextView.setChecked(false);}
            else {
                mCheckedTextView.setChecked(true);
            }



            // mCheckedTextView.setText(String.valueOf(items.get(position).getPosition()));


            name.setText(items.get(position).name);
            email.setText(items.get(position).email);
            /*"Setting event image" Starts*/
            if (!items.get(position).profile_image.equals("")) {
                try {
                    String apiLink = items.get(position).profile_image;

                    //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                    String encodedurl = "";
                    encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                            apiLink.lastIndexOf('/') + 1));
                    Log.d("Profile", "encodedurl:"+encodedurl);
                    if (!apiLink.equals("") && apiLink != null) {
                        Picasso.with(context)
                                .load(encodedurl) // load: This path may be a remote URL,
                                .placeholder(R.drawable.no_data_found_1x)
                                //.resize(130, 130)
                                .error(R.drawable.no_data_found_1x)
                                .into(profilePic); // Into: ImageView into which the final image has to be passed
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



            post_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    validation();
                }
            });


        }

        @Override
        public void onClick(View v) {



                int adapterPosition = getAdapterPosition();
                if (!itemStateArray.get(adapterPosition, false)) {
                    mCheckedTextView.setChecked(true);
                    itemStateArray.put(adapterPosition, true);
                    xyz.add(String.valueOf(items.get(adapterPosition).user_id));
                    abc.add(String.valueOf(items.get(adapterPosition)));
                    //Toast.makeText(context,String.valueOf(xyz),Toast.LENGTH_SHORT).show();

                }
                else  {
                    mCheckedTextView.setChecked(false);
                    itemStateArray.put(adapterPosition, false);
                    xyz.remove(String.valueOf(items.get(adapterPosition).user_id));
                    abc.add(String.valueOf(items.get(adapterPosition)));
                   // Toast.makeText(context,String.valueOf(xyz),Toast.LENGTH_SHORT).show();
                }
            }






    }
    private void showToastLong(String s) {
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }


    private void validation() {




        boolean cancel = false;
        String message="";
        View focusView = null;
        boolean tempCond = false;




        if (xyz.size()==0){

            message = "Please Select Group Members.";
            focusView = post_location;
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
                reqEntity.addPart("group_id", new StringBody(""));
                reqEntity.addPart("type", new StringBody("map"));
                reqEntity.addPart("location", new StringBody(LOCATION));
                reqEntity.addPart("lati", new StringBody(LAT));
                reqEntity.addPart("longi", new StringBody(LONGI));

                String _fulstringid = "";
                for(int i=0;i<xyz.size();i++){
                    _fulstringid =String.valueOf(xyz.get(i));
                    reqEntity.addPart("tag_friend[]", new StringBody(_fulstringid));
                }




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

                new TagFriendAdapter.UploadFileToServer().execute();
            } else {
                showToastLong(context.getString(R.string.no_internet_message));
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

    public Filter getFilter() {

        if(filter == null)
        {
            filter=new CustomFilter();
        }

        return filter;
    }


    //INNER CLASS
    class CustomFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CONSTARINT TO UPPER
                constraint=constraint.toString().toUpperCase();

                ArrayList<FriendModel> filters=new ArrayList<FriendModel>();

                //get specific items
                for(int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).name.toUpperCase().contains(constraint))
                    {

                        FriendModel item = new FriendModel(filterList.get(i).profile_image, filterList.get(i).user_id, filterList.get(i).name,
                                filterList.get(i).email, filterList.get(i).is_friend);
                        filters.add(item);

                    }
                }

                results.count=filters.size();
                results.values=filters;

            }else
            {
                results.count=filterList.size();
                results.values=filterList;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub

            items=(ArrayList<FriendModel>) results.values;
            notifyDataSetChanged();
        }



    }


}
