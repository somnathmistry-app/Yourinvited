package com.devonetech.android.yourinvited.friends_section.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.devonetech.android.yourinvited.LoginActivity;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.friends_section.FriendListActivity;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> implements Filterable {


    private Context context;
    private int loCount;
    ArrayList<FriendModel> items;
    ProgressDialog progressDialog;
    UserShared psh;
    CustomFilter filter;
    ArrayList<FriendModel> filterList;

    public FriendListAdapter(Context context, ArrayList<FriendModel> items) {
        this.items = items;
        this.context = context;
        this.filterList=items;
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

        if (items.get(position).is_friend.equals("0")){

            holder._notFriends.setVisibility(View.VISIBLE);
            holder._friends.setVisibility(View.GONE);
            holder.pending.setVisibility(View.GONE);
        }
        else if(items.get(position).is_friend.equals("1")){

            holder._notFriends.setVisibility(View.GONE);
            holder._friends.setVisibility(View.GONE);
            holder.pending.setVisibility(View.VISIBLE);
        }
        else if(items.get(position).is_friend.equals("2")){

            holder._notFriends.setVisibility(View.GONE);
            holder._friends.setVisibility(View.VISIBLE);
            holder.pending.setVisibility(View.GONE);
        }

        holder._friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showmenu(holder._friends,position);
            }
        });
        holder._notFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callAddFrind(position);
            }
        });
    }



    private void showmenu(TextView view,int position) {

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



    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
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
