package com.devonetech.android.yourinvited.event_section.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.AddEventActivity;
import com.devonetech.android.yourinvited.event_section.EventDetailsActivity;
import com.devonetech.android.yourinvited.event_section.model.EventModel;
import com.devonetech.android.yourinvited.event_section.model.ServiceCatModel;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class EventRequestAdapter extends RecyclerView.Adapter<EventRequestAdapter.ViewHolder> {


    private Activity context;
    private int loCount;
    ArrayList<EventModel> itemms;
    ArrayList<EventModel> memberList;
    UserShared psh;
    ProgressDialog progressDialog;


    public EventRequestAdapter(Activity context, ArrayList<EventModel> items) {
        this.itemms = items;
        this.context = context;
    }
   /* public EventRequestAdapter(Context context) {
        this.context = context;
    }*/


    @Override
    public EventRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_request_item, parent, false);
        return new EventRequestAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventRequestAdapter.ViewHolder holder, final int position) {

        psh=new UserShared(context);


        holder.event_title.setText(itemms.get(position).name);
        holder.event_location.setText(itemms.get(position).location);
        Picasso.with(holder.event_image.getContext()).load(itemms.get(position).image).into(holder.event_image);
        try {
            JSONArray jj=new JSONArray(itemms.get(position).member);
            for (int i = 0; i <jj.length() ; i++) {

                holder.no_of_people.setText("   "+String.valueOf(i+1)+" People Going");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.no_of_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(itemms.get(position).member);
            }
        });

        holder.dateTime.setText(getDateTime(itemms.get(position).start_date)+" to "+getDateTime(itemms.get(position).end_date));

        holder.organiser.setText("ORGANIZED BY "+ itemms.get(position).posted_by_user);

        if (itemms.get(position).request_status.equals("Pending")){

             holder.acceptTxt.setVisibility(View.VISIBLE);
             holder.declineTxt.setVisibility(View.VISIBLE);
        }
        else if(itemms.get(position).request_status.equals("Accepted")){
            holder.acceptTxt.setVisibility(View.GONE);
            holder.declineTxt.setVisibility(View.VISIBLE);
        }
        else if(itemms.get(position).request_status.equals("Declined")){
            holder.acceptTxt.setVisibility(View.VISIBLE);
            holder.declineTxt.setVisibility(View.GONE);
        }


        holder.acceptTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateStatus("Accepted",itemms.get(position).event_id);
            }
        });

        holder.acceptTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateStatus("Declined",itemms.get(position).event_id);
            }
        });



    }



    @Override
    public int getItemCount() {
        return itemms.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView event_image;

        public TextView event_title,event_location,no_of_people,dateTime,organiser,acceptTxt,declineTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            event_image = (ImageView) itemView.findViewById(R.id.image1);
            event_title=itemView.findViewById(R.id.textView7);
            event_location=itemView.findViewById(R.id.pro_name);
            no_of_people=itemView.findViewById(R.id.pro_people);
            dateTime=itemView.findViewById(R.id.textView9);
            organiser=itemView.findViewById(R.id.textView8);
            acceptTxt=itemView.findViewById(R.id.txt_accept);
            declineTxt=itemView.findViewById(R.id.txt_decline);


        }
    }

    private void showDialog(String datalist){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_list);

        ListView lv = (ListView) dialog.findViewById(R.id.custom_list);
        memberList=new ArrayList<>();
        try {
            JSONArray jj=new JSONArray(datalist);
            for (int i = 0; i <jj.length() ; i++) {
                JSONObject obj=jj.getJSONObject(i);

                EventModel item=new EventModel(obj.getString("member_id"),obj.getString("name"),obj.getString("profile_image"));
                memberList.add(item);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Change MyActivity.this and myListOfItems to your own values
        CustomListAdapterDialog clad = new CustomListAdapterDialog(context, memberList);

        lv.setAdapter(clad);

        //lv.setOnItemClickListener(........);




        dialog.show();
    }



    private String getDateTime(String abc) {

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.setTimeInMillis(Long.parseLong(abc) * 1000);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currenTimeZone = (Date) calendar.getTime();
        String axbc=String.valueOf(currenTimeZone);
        // Toast.makeText(AddEventActivity.this,String.valueOf(currenTimeZone),Toast.LENGTH_SHORT).show();

        return axbc;
    }

    private void updateStatus(final String status, final String event_id) {

        if (AppController.getInstance().isConnected()) {
            progressDialog = Helper.setProgressDailog(context);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_EventRequestUpdate,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();
                                   // JSONArray jj=jsonObject.getJSONArray("category");

                                    context.finish();


                                } else {

                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
                    params.put("event_id",event_id);
                    params.put("status",status);
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
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }



    }
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String errors = data.getString("message");
            // showSnackBar(errors);
            Toast.makeText(context,errors,Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

   
}
