package com.devonetech.android.yourinvited;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.devonetech.android.yourinvited.cache.ImagePipelineConfigFactory;
import com.devonetech.android.yourinvited.shared.Updatedlatlong;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Developer on 5/2/2017.
 */

public class AppController extends Application {

    private static AppController ourInstance;
    public static final String TAG = AppController.class
            .getSimpleName();
    private RequestQueue requestQueue;
    MediaPlayer mMediaPlayer;
    public static synchronized AppController getInstance() {
        return ourInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
      /*  OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                //.setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .autoPromptLocation(true)
                .init();*/

       startLocationService();

    }
    Updatedlatlong psh;
    SharedPreferences prefs;

    public void startLocationService(){
        psh=new Updatedlatlong(getApplicationContext());
        Log.i("GPSTRACKER", "called startLocationService from application");
        startService(new Intent(this, LocationUpdate.class));

        prefs = getSharedPreferences("LATLONG_SHARED_PREF", MODE_PRIVATE);
    }


    Uri selectedImage;

    public Uri getImageUri(){
        return selectedImage;
    }
    public void setImageUri(Uri selectedImage_){
        selectedImage = selectedImage_;
    }


  /*  private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {

        @Override
        public void notificationReceived(OSNotification notification) {
            Log.d("OneSignalExample", "notificationReceived!!!!!!");


          *//*  if (new UserShared(getApplicationContext()).getLoggedInStatusUser()) {


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);



            }
            else if(new UserShared(getApplicationContext()).getLoggedInStatusEngineer()){

                Intent intent = new Intent(getApplicationContext(), MainActivityEngineer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(new UserShared(getApplicationContext()).getLoggedInStatusAdmin()){

                Intent intent = new Intent(getApplicationContext(), MainActivityAdmin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }*//*



        }
    }




    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey,booking_id = "",booking_sts = "";
            //booking_id=data.getString("BookingId");
            // booking_sts=data.getString("BookingType");

			*//*if (data != null) {
				customKey = data.optString("customkey", null);
				if (customKey != null)
					Log.i("OneSignalExample", "customkey set with value: " + customKey);
			}*//*

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);



            // The following can be used to open an Activity of your choice.

            if (new UserShared(getApplicationContext()).getLoggedInStatusUser()) {


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);



            }
            else if(new UserShared(getApplicationContext()).getLoggedInStatusEngineer()){

                Intent intent = new Intent(getApplicationContext(), MainActivityEngineer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(new UserShared(getApplicationContext()).getLoggedInStatusAdmin()){

                Intent intent = new Intent(getApplicationContext(), MainActivityAdmin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }


            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //  if you are calling startActivity above.

			*//* <application ...>
	              <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
	            </application>*//*

        }
    }

*/


    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext().getApplicationContext());

        }
        return requestQueue;
    }


    public <T> void addToRequestque(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }


    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;
        }

        return false;
    }


}
