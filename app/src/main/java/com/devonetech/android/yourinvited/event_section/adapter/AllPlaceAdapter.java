package com.devonetech.android.yourinvited.event_section.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.NearByMapFragment;
import com.devonetech.android.yourinvited.event_section.model.PlaceModelList;
import com.devonetech.android.yourinvited.friends_section.model.FriendModel;
import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.shared.UserShared;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AllPlaceAdapter extends RecyclerView.Adapter<AllPlaceAdapter.ViewHolder> {

    private ArrayList<PlaceModelList> items = new ArrayList<>();
    Activity context;
    ArrayList<String> xyz=new ArrayList<>();
    ArrayList<String> abc=new ArrayList<>();

    SparseBooleanArray itemStateArray= new SparseBooleanArray();
    ConnectionDetector connection;
    UserShared psh;
    ProgressDialog progressDialog;
    String responseString=null;
    private MultipartEntity reqEntity;
    private static String TAG;
    private TabLayout tabLayout;
    private String Latitude="",Longtitude="";
    private TextView doneTxt;
    private PopupWindow popupWindow;



    public AllPlaceAdapter(Activity context, ArrayList<PlaceModelList> items,TabLayout tabLayout,
                           String Latitude,String Longtitude,TextView  doneTxt,PopupWindow popupWindow) {
        this.context=context;
        this.items=items;
        this.tabLayout=tabLayout;
        this.Latitude=Latitude;
        this.Longtitude=Longtitude;
        this.doneTxt=doneTxt;
        this.popupWindow=popupWindow;



    }



    @Override
    public AllPlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // context = parent.getContext();
        int layoutForItem = R.layout.all_places;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForItem, parent, false);
        return new AllPlaceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllPlaceAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void loadItems(ArrayList<PlaceModelList> tournaments) {
        this.items = tournaments;
        notifyDataSetChanged();
    }




    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckedTextView mCheckedTextView;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            mCheckedTextView = (CheckedTextView) itemView.findViewById(R.id.checked_text_view);
            name=itemView.findViewById(R.id.name);


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
            doneTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (xyz.size()!=0){
                        replaceFragment(new NearByMapFragment(tabLayout,Latitude,Longtitude,xyz));
                        popupWindow.dismiss();


                    }
                    else{
                        showToastLong("Please Select Atlist One Place");
                    }
                }
            });





        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                mCheckedTextView.setChecked(true);
                itemStateArray.put(adapterPosition, true);
                xyz.add(String.valueOf(items.get(adapterPosition).value));
                abc.add(String.valueOf(items.get(adapterPosition)));
                Toast.makeText(context,String.valueOf(xyz),Toast.LENGTH_SHORT).show();

            }
            else  {
                mCheckedTextView.setChecked(false);
                itemStateArray.put(adapterPosition, false);
                xyz.remove(String.valueOf(items.get(adapterPosition).name));
                abc.add(String.valueOf(items.get(adapterPosition)));
                Toast.makeText(context,String.valueOf(xyz),Toast.LENGTH_SHORT).show();
            }


        }

    }
    private void showToastLong(String s) {
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit();
    }





}
