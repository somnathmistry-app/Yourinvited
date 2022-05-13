package com.devonetech.android.yourinvited.social_network.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devonetech.android.yourinvited.LoginActivity;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom.Circle_Image;
import com.devonetech.android.yourinvited.event_section.EventListActivity;
import com.devonetech.android.yourinvited.event_section.EventRequestActivity;
import com.devonetech.android.yourinvited.event_section.NearbyPlaceActivity;
import com.devonetech.android.yourinvited.friends_section.FriendListActivity;
import com.devonetech.android.yourinvited.friends_section.MyFriendListActivity;
import com.devonetech.android.yourinvited.shared.Updatedlatlong;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.devonetech.android.yourinvited.social_network.EditAccountActivity;
import com.devonetech.android.yourinvited.social_network.FeedPostActivity;
import com.devonetech.android.yourinvited.social_network.MyTimelineActivity;
import com.devonetech.android.yourinvited.social_network.SaveLocationActiviity;
import com.devonetech.android.yourinvited.social_network.group.CreateGroupActivity;
import com.devonetech.android.yourinvited.social_network.group.MyGroupsListActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    View mainView;
    private SharedPreferences prefs;
    UserShared psh;
    private TextView logout_user,userName,userEmail,userNumber,userAddress,editAddress,editProfile;
    LinearLayout my_event,event_request,myFriends,myTimeline,mygroup,saveLocations,nearby;

    Updatedlatlong lat_psh;
    private ImageView btnSOS;
    private Circle_Image profile_iv;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_profile, container, false);
        prefs=getActivity().getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        psh=new UserShared(getActivity());
        lat_psh=new Updatedlatlong(getActivity());
        iniTView(mainView);
        xmlonclick();
       // setData();
        return mainView;
    }



    private void iniTView(View v) {


        logout_user=v.findViewById(R.id.logout_user);
        userName=v.findViewById(R.id.user_name);
        userEmail=v.findViewById(R.id.user_email);
        userNumber=v.findViewById(R.id.user_number);
        editProfile=v.findViewById(R.id.edit_account);
        my_event=v.findViewById(R.id.myEvents);
        event_request=v.findViewById(R.id.eventRequest);
        myFriends=v.findViewById(R.id.my_friends);
        myTimeline=v.findViewById(R.id.my_timeline);
        mygroup=v.findViewById(R.id.my_group);
        saveLocations=v.findViewById(R.id.save_locations);
        nearby=v.findViewById(R.id.nearbyplace);
        profile_iv=v.findViewById(R.id.profile_iv);
        btnSOS=v.findViewById(R.id.imageView8);

        setdata();

    }

    private void setdata() {

        userName.setText(psh.getUserName());
        userEmail.setText(psh.getUserEmail());
        if (!psh.getUserPhone().equals("")){
            userNumber.setText(psh.getUserPhone());
        }
        else {
            userNumber.setText("No Number");
        }

        if (!psh.getUserPic().equals("")) {
            try {
                String apiLink = psh.getUserPic();

                //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
                String encodedurl = "";
                encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
                        apiLink.lastIndexOf('/') + 1));
                Log.d("UP Social List adapter", "encodedurl:"+encodedurl);
                if (!apiLink.equals("") && apiLink != null) {

                    Glide.with(getActivity())
                            .load(encodedurl)

                            .into(profile_iv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




    }


    private void xmlonclick() {
        logout_user.setOnClickListener(this);
        my_event.setOnClickListener(this);
        event_request.setOnClickListener(this);
        myFriends.setOnClickListener(this);
        myTimeline.setOnClickListener(this);
        mygroup.setOnClickListener(this);
        saveLocations.setOnClickListener(this);
        nearby.setOnClickListener(this);
        btnSOS.setOnClickListener(this);
        editProfile.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.logout_user:

                logout();
                break;

            case R.id.edit_account:

                Intent je=new Intent(getActivity(),EditAccountActivity.class);
                startActivityForResult(je, 200);
                break;

            case R.id.myEvents:


                Intent j=new Intent(getActivity(),EventListActivity.class);
                startActivityForResult(j, 200);

                break;
            case R.id.eventRequest:

                Intent k=new Intent(getActivity(), EventRequestActivity.class);
                startActivityForResult(k, 200);

                break;

            case R.id.my_friends:

                Intent ki=new Intent(getActivity(), MyFriendListActivity.class);
                startActivityForResult(ki, 200);
                break;

            case R.id.my_timeline:

                Intent kki=new Intent(getActivity(), MyTimelineActivity.class);
                startActivityForResult(kki, 200);
                break;

            case R.id.my_group:

                Intent kkk=new Intent(getActivity(), MyGroupsListActivity.class);
                startActivityForResult(kkk, 200);
                break;

            case R.id.save_locations:

                Intent sl=new Intent(getActivity(), SaveLocationActiviity.class);
                startActivityForResult(sl, 200);
                break;
            case R.id.nearbyplace:

                Intent i=new Intent(getActivity(), NearbyPlaceActivity.class);
                i.putExtra("lat",lat_psh.getUserUpdatedLatitude());
                i.putExtra("long",lat_psh.getUserUpdatedLongitude());
                startActivity(i);

                break;
            case R.id.imageView8:


                String uri = "http://maps.google.com/maps?saddr=" +lat_psh.getUserUpdatedLatitude()+","+lat_psh.getUserUpdatedLongitude();

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //String ShareSub = datalist.get(position).location_title;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Current Location");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;


        }
    }

    public  void onActivityResult(int arg0, int arg1, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, data);

        switch (arg0) {

            case 100:
                //Toast.makeText(this, "come back", Toast.LENGTH_SHORT).show();
                break;
            case 200:

                if (arg1!=RESULT_OK) {

                  //  Bundle puredata=data.getExtras();

                    //String postStatus=data.getStringExtra("post_status");

                   // setdata();

                }

                break;

            default:
                break;
        }
    }

    private void logout() {


        // TODO Auto-generated method stub
        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();


    }
}