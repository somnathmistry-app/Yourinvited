package com.devonetech.android.yourinvited.event_section;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom_view.PicassoCircleTransformation;
import com.devonetech.android.yourinvited.event_section.model.EventModel;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentTwo extends Fragment {

    public FragmentTwo() {
        // Required empty public constructor
    }
    View rootView;

    MapView mMapView;
    private static GoogleMap googleMap;
    ArrayList<LatLng> markerPoints;
    static FragmentTwo partThreeFragment;
    private static ArrayList<EventModel> Dataitem1;
    public final static String ITEMS_COUNT_KEY = "PartThreeFragment$ItemsCount";
    private ImageView nodata;
    private static ArrayList<EventModel> markers;
    double event_lat,event_long;

    public static FragmentTwo createInstance(int itemsCount, ArrayList<EventModel> Dataitem, String flag) {
        partThreeFragment = new FragmentTwo();
        Bundle bundle = new Bundle();
        bundle.putString(ITEMS_COUNT_KEY, flag);
        partThreeFragment.setArguments(bundle);
        Dataitem1=Dataitem;


        return partThreeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_two, container, false);
        rootView = inflater.inflate(R.layout.fragment_two, container, false);

        mMapView= (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                if (checkLocationPermission()) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        googleMap.setMyLocationEnabled(true);
                    }
                }


                markerPoints = new ArrayList<LatLng>();

                event_lat=Double.parseDouble(Dataitem1.get(0).lat);
                event_long=Double.parseDouble(Dataitem1.get(0).longi);

                LatLng sydney = new LatLng(event_lat,event_long);
                googleMap.addMarker(new MarkerOptions().position(sydney).
                        title(Dataitem1.get(0).name).snippet(Dataitem1.get(0).location));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition =  new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                        (cameraPosition ));

                try {
                    JSONArray jj=new JSONArray(Dataitem1.get(0).member);

                    markers=new ArrayList<>();
                    for (int i = 0; i <jj.length() ; i++) {


                        JSONObject obj=jj.getJSONObject(i);

                        EventModel items=new EventModel(obj.getString("user_id"),obj.getString("name"),obj.getString("profile_image"),
                                Double.parseDouble(obj.getString("user_lat")),Double.parseDouble(obj.getString("user_long")) ,obj.getString("status"));
                        markers.add(items);


                    }
                      addEventusers(markers);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });





        return rootView;
    }

    private void addEventusers(final ArrayList<EventModel> dataitem1) {




        for(int i=0;i<dataitem1.size();i++){


            final int finalI = i;
            Picasso.with(getContext()).
                    load(dataitem1.get(finalI).profile_image).
                    transform(new PicassoCircleTransformation()).
                    resize(100,100).
                    into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            Marker mk1 = googleMap.addMarker(new MarkerOptions().
                                    title(dataitem1.get(finalI).name).
                                    icon(BitmapDescriptorFactory.fromBitmap(bitmap)).
                                    position(new LatLng(Double.valueOf(dataitem1.get(finalI).user_lat), Double.valueOf(dataitem1.get(finalI).user_long))));
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),new String[]
                                        {Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        googleMap.setMyLocationEnabled(true);
                    }

                } else {



                }
                return;
            }

        }
    }


}