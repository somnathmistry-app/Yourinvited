package com.devonetech.android.yourinvited.friends_section.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFriendsAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {


    private Context context;
    private int loCount;
    ArrayList<FriendModel> items;
    ProgressDialog progressDialog;
    UserShared psh;

    public MyFriendsAdapter(Context context, ArrayList<FriendModel> items) {
        this.items = items;
        this.context = context;
    }
  /*  public FriendListAdapter(Context context) {
        this.context = context;
    }*/


    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);
        return new FriendListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FriendListAdapter.ViewHolder holder, final int position) {
        psh=new UserShared(context);

        Picasso.with(holder.image.getContext()).load(items.get(position).profile_image).into(holder.image);
        holder._name.setText(items.get(position).name);
        holder._email.setText(items.get(position).email);

        holder._notFriends.setVisibility(View.GONE);
        holder._friends.setVisibility(View.VISIBLE);
        holder.pending.setVisibility(View.GONE);


        holder._notFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // callAddFrind(position);
            }
        });
    }



    private void showmenu(TextView view, int position) {

        final PopupMenu menu = new PopupMenu (context, view);
        menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
        {
            @Override
            public boolean onMenuItemClick (MenuItem item)
            {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.item_settings:
                        // Log.i (Tag, "settings");
                        break;
                    case R.id.item_about:
                        // Log.i (Tag, "about");
                        menu.dismiss();
                        break;
                }
                return true;
            }
        });
        menu.inflate (R.menu.friend_option);
        menu.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public TextView _name,_email,_friends,_notFriends,pending,modifyCampaign;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            _name=itemView.findViewById(R.id.name);
            _email=itemView.findViewById(R.id.email);
            _friends=itemView.findViewById(R.id.friends);
            _notFriends=itemView.findViewById(R.id.not_friends);
            pending=itemView.findViewById(R.id.pending);

        }
    }


    private void callAddFrind(final int position) {
        if (AppController.getInstance().isConnected()) {
            progressDialog = Helper.setProgressDailog(context);

            StringRequest strRequest = new StringRequest(Request.Method.POST, Constants.URL_friendadd,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String msg = jsonObject.getString("message");
                                if (status.equals("1")) {
                                    // alertDialog.cancel();

                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

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
                    params.put("user_id", psh.getUserId());
                    params.put("receiver_id", items.get(position).user_id);
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
