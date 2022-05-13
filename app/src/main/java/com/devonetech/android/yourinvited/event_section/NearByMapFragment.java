package com.devonetech.android.yourinvited.event_section;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.devonetech.android.yourinvited.AppController;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.adapter.NearByMapAdapter;
import com.devonetech.android.yourinvited.event_section.model.NearByMapModel;
import com.devonetech.android.yourinvited.event_section.model.Perser;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.Helper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;



@SuppressLint("ValidFragment")
public class NearByMapFragment extends Fragment implements OnMapReadyCallback {
    private CoordinatorLayout coordinatorLayout;
    private GoogleMap mMap;
    private List<NearByMapModel> contentArray;
    private ListView lv;
    private String view_status = "map", currentLocation, category_name = "Airport";
    private boolean change_view_status = true;
    NearByMapAdapter adapter;
    SupportMapFragment mapFragment;
    private TabLayout tabLayout;
   // private Location location;
    PopupWindow pw;
    private LinearLayout ll;
    private FloatingActionButton userb,provider;
    private String flag="neutral";
    private String Latitude="",Longtitude="";
    private ArrayList<String> filterPlaces=new ArrayList<String>();

    @SuppressLint("ValidFragment")
    public NearByMapFragment(TabLayout tabLayout,String Latitude,String Longtitude,ArrayList<String> filter) {

        this.tabLayout = tabLayout;
        this.Latitude=Latitude;
        this.Longtitude=Longtitude;
        this.filterPlaces=filter;

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearbymap, container, false);
       // setHasOptionsMenu(true);
        setupTab();
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        ll= (LinearLayout) view.findViewById(R.id.ll);
        lv = (ListView) view.findViewById(R.id.lv);

        TedPermission.with(getActivity())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mapFragment = (SupportMapFragment) getChildFragmentManager()
                                .findFragmentById(R.id.map);

                            mapFragment.getMapAsync(NearByMapFragment.this);

                    }


                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               if(flag.equals("neutral"))
                placeDetailsApi(contentArray.get(i), contentArray.get(i).getPlace_id());
               else if(flag.equals("user"))
                   popUpdailogUser_Provider(contentArray.get(i),"User" );
               else
                   popUpdailogUser_Provider(contentArray.get(i),"ServiceProvider" );

            }
        });


        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (mMap != null) {
            if (SmartLocation.with(getActivity()).location().state().locationServicesEnabled()) {
                SmartLocation.with(getActivity()).location()
                        .start(new OnLocationUpdatedListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onLocationUpdated(Location location) {
                                location = location;
                                currentLocation = String.valueOf(Latitude) + "," + String.valueOf(Longtitude);
                                placeApi(currentLocation, filterPlaces.get(0).toString());
                                tabLayout.setVisibility(View.VISIBLE);
                                tabLayout.getTabAt(0).select();
                                ll.setVisibility(View.VISIBLE);
                                mMap.setMyLocationEnabled(true);
                                //Marker mk = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longtitude))), 12));
                            }
                        });


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int position = (int) (marker.getTag());
                       // placeDetailsApi(contentArray.get(position), contentArray.get(position).getPlace_id());
                        if(flag.equals("neutral"))
                            placeDetailsApi(contentArray.get(position), contentArray.get(position).getPlace_id());
                        else if(flag.equals("user"))
                            popUpdailogUser_Provider(contentArray.get(position),"User" );
                        else
                            popUpdailogUser_Provider(contentArray.get(position),"ServiceProvider" );


                        return false;
                    }
                });


            } else {

                Helper.toast(getActivity(), coordinatorLayout, "Kindly turn on your location option form phone setting.");
            }
        }


    }
    public void placeApi(String currentLocation, final String type) {

        if (AppController.getInstance().isConnected()&&getActivity()!=null) {
            final ProgressDialog progressDialog = Helper.setProgressDailog(getActivity());
            SimpleMultiPartRequest request = new SimpleMultiPartRequest(Constants.URL_NEAR_BUY + "location=" + currentLocation + "&radius=" + Constants.RADIOUS + "&types=" + type + "&key=" + Constants.MAP_KEY,

                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");

                                if (status.equals("OK")) {
                                    contentArray = new ArrayList<>();
                                    contentArray = Perser.getNearByData(jsonObject.optJSONArray("results"),type);
                                    mMap.clear();
                                    LatLng locationS=new LatLng(Double.parseDouble(Latitude),Double.parseDouble(Longtitude));
                                    if (locationS != null)
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longtitude))), 12));
                                    for (int i = 0; i < contentArray.size(); i++) {

                                      /*  Marker mk1 = mMap.addMarker(new MarkerOptions().
                                                title(contentArray.get(i).getName()).
                                                position(new LatLng(Double.valueOf(contentArray.get(i).getLat()), Double.valueOf(contentArray.get(i).getLng()))));
                                        mk1.setTag(i);*/

                                        final int finalI = i;
                                        Picasso.with(getContext()).
                                                load(contentArray.get(finalI).getMarkerIcon()).
                                                resize(50,50).
                                                into(new Target() {
                                                    @Override
                                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                                        Marker mk1 = mMap.addMarker(new MarkerOptions().
                                                                title(contentArray.get(finalI).getName()).
                                                                icon(BitmapDescriptorFactory.fromBitmap(bitmap)).
                                                                position(new LatLng(Double.valueOf(contentArray.get(finalI).getLat()), Double.valueOf(contentArray.get(finalI).getLng()))));
                                                        mk1.setTag(finalI);
                                                    }

                                                    @Override
                                                    public void onBitmapFailed(Drawable errorDrawable) {

                                                    }

                                                    @Override
                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                    }
                                                });


                                    }

                                    adapter = new NearByMapAdapter(getActivity(), contentArray, view_status);
                                    lv.setAdapter(adapter);
                                } else {
                                    Helper.toast(getActivity(), coordinatorLayout, "No "+type+" found in this area");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();


                        }
                    },

                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                        }
                    });


            AppController.getInstance().addToRequestque(request);


        } else {
            Helper.toast(getActivity(), coordinatorLayout, "No internet connection");
        }
    }


    public void placeDetailsApi(final NearByMapModel nearByMapModel, String place_id) {

        if (AppController.getInstance().isConnected()) {
            final ProgressDialog progressDialog = Helper.setProgressDailog(getActivity());
            SimpleMultiPartRequest request = new SimpleMultiPartRequest(Constants.URL_PLACE_DETAILS + "placeid=" + place_id + "&key=" + Constants.MAP_KEY,

                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");

                                if (status.equals("OK")) {
                                    nearByMapModel.setFormatted_phone_number(jsonObject.optJSONObject("result").optString("formatted_phone_number"));
                                    nearByMapModel.setInternational_phone_number(jsonObject.optJSONObject("result").optString("international_phone_number"));
                                    popUpdailog(nearByMapModel, category_name);
                                } else {
                                    Helper.toast(getActivity(), coordinatorLayout, "Something went wrong");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();


                        }
                    },

                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                        }
                    });


            AppController.getInstance().addToRequestque(request);


        } else {
            Helper.toast(getActivity(), coordinatorLayout, "No internet connection");
        }
    }


    private void setupTab() {
       /* tabLayout.addTab(tabLayout.newTab().setText("Airport"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Atm"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Hospital"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Doctor"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Pharmacy"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Police"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Fire Station"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Restaurant"), true);
*/
       tabLayout.removeAllTabs();

        for (int i = 0; i <filterPlaces.size() ; i++) {

            tabLayout.addTab(tabLayout.newTab().setText(filterPlaces.get(i).toString()), true);

        }

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(getActivity(), R.color.tab_text_color));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.primary_text_color));
        bindWidgetsWithAnEvent();
    }


    private void bindWidgetsWithAnEvent() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                for (int i = 0; i <filterPlaces.size() ; i++) {

                    if (tab.getPosition()==i){
                        category_name=filterPlaces.get(i).toString();
                        placeApi(currentLocation, category_name);
                    }

                }


              /*  if (tab.getPosition() == 0) {
                    category_name = "Airport";
                    placeApi(currentLocation, "airport");
                }
                else if (tab.getPosition() == 1) {
                    category_name = "Atm";
                    placeApi(currentLocation, "atm");
                }
                else if (tab.getPosition() == 2) {
                    category_name = "Hospital";
                    placeApi(currentLocation, "hospital");
                } else if (tab.getPosition() == 3) {
                    category_name = "Doctor";
                    placeApi(currentLocation, "doctor");

                } else if (tab.getPosition() == 4) {
                    category_name = "Pharmacy";
                    placeApi(currentLocation, "pharmacy");

                } else if (tab.getPosition() == 5) {
                    category_name = "Police Station";
                    placeApi(currentLocation, "police");

                } else if (tab.getPosition() == 6) {
                    category_name = "Fire Station";
                    placeApi(currentLocation, "fire_station");
                }
                else if (tab.getPosition() == 7) {
                    category_name = "Restaurant";
                    placeApi(currentLocation, "restaurant");
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void popUpdailog(final NearByMapModel nearByMapModel, String category_name) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_near_by);
        dialog.show();
        TextView name_tv = (TextView) dialog.findViewById(R.id.name_tv);
        TextView address_tv = (TextView) dialog.findViewById(R.id.address_tv);
        TextView category_tv = (TextView) dialog.findViewById(R.id.category_tv);
        TextView ph_no_tv = (TextView) dialog.findViewById(R.id.ph_no_tv);
        Button call_btn = (Button) dialog.findViewById(R.id.call_btn);
        RatingBar rating = (RatingBar) dialog.findViewById(R.id.rating);
        name_tv.setText(nearByMapModel.getName());
        address_tv.setText(nearByMapModel.getVicinity());
        category_tv.setText(category_name);
        ph_no_tv.setText(nearByMapModel.getFormatted_phone_number());
        rating.setRating(nearByMapModel.getRating());
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TedPermission.with(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + nearByMapModel.getFormatted_phone_number()));
                                startActivity(intent);


                            }


                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                            }
                        })
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.CALL_PHONE)
                        .check();


            }
        });

    }



    public void popUpdailogUser_Provider(final NearByMapModel nearByMapModel, String category_name) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_near_by);
        dialog.show();
        TextView name_tv = (TextView) dialog.findViewById(R.id.name_tv);
        TextView address_tv = (TextView) dialog.findViewById(R.id.address_tv);
        TextView category_tv = (TextView) dialog.findViewById(R.id.category_tv);
        TextView ph_no_tv = (TextView) dialog.findViewById(R.id.ph_no_tv);
        Button call_btn = (Button) dialog.findViewById(R.id.call_btn);
        RatingBar rating = (RatingBar) dialog.findViewById(R.id.rating);
        name_tv.setText(nearByMapModel.getName());
        address_tv.setText(nearByMapModel.getVicinity());
        category_tv.setText(category_name);
        ph_no_tv.setText(nearByMapModel.getFormatted_phone_number());
        rating.setRating(nearByMapModel.getRating());
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TedPermission.with(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + nearByMapModel.getFormatted_phone_number()));
                                startActivity(intent);


                            }


                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                            }
                        })
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.CALL_PHONE)
                        .check();


            }
        });

    }

  /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.getItem(1);
            item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_grid) {

            if (adapter != null) {
                if (change_view_status) {
                    item.setIcon(R.drawable.map);
                    view_status = "map";
                    lv.setVisibility(View.VISIBLE);
                } else {

                    item.setIcon(R.drawable.list);
                    view_status = "list";

                    lv.setVisibility(View.GONE);
                }
                change_view_status = !change_view_status;
            }

        }


        return super.onOptionsItemSelected(item);
    }*/
}
