package com.devonetech.android.yourinvited;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.devonetech.android.yourinvited.network.ConnectionDetector;
import com.devonetech.android.yourinvited.network.Constants;
import com.devonetech.android.yourinvited.network.GeneralAsynctask;
import com.devonetech.android.yourinvited.shared.Updatedlatlong;
import com.devonetech.android.yourinvited.shared.UserShared;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class LocationUpdate extends Service implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {


	private class LocationListener implements
			com.google.android.gms.location.LocationListener {

		public LocationListener() {
		}

		@Override
		public void onLocationChanged(Location location) {
			prefs = getSharedPreferences("LATLONG_SHARED_PREF", MODE_PRIVATE);
			Log.e(TAG, "onLocationChanged: " + location);
			currentLat = location.getLatitude();
			currentLng = location.getLongitude();

			System.out.println(currentLat);
			System.out.println(currentLng);
			Log.e(TAG, "USEID " + pshuser.getUserId());
			Log.e(TAG, "LATITUDE " + currentLat);
			Log.e(TAG, "LONGITUDE " + currentLng);



			/*Intent i = new Intent();
            i.setAction("Refresh");
            Bundle b = new Bundle();
            b.putString("mlat", String.valueOf(currentLat));
            b.putString("mlng", String.valueOf(currentLng));
            i.putExtra("mBundle", b);
            sendBroadcast(i);*/

			if (currentLat != 0.0 && currentLng != 0.0) {
				try {
					Log.d("Latitude", String.valueOf(currentLat));
					Log.d("Longitude", String.valueOf(currentLng));

					//	Toast.makeText(getApplicationContext(), String.valueOf(currentLat), Toast.LENGTH_SHORT).show();

					Editor editor = prefs.edit();
					editor.putString(getString(R.string.shared_updated_lat), String.valueOf(currentLat));
					editor.putString(getString(R.string.shared_updated_long), String.valueOf(currentLng));
					editor.commit();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//Toast.makeText(context, "currentLat: " + currentLat + "  //  currentLng: " + currentLng, Toast.LENGTH_SHORT).show();

			/**SUMANA STARTS**/
			if (firsttimeUpdate) {
				try {
					if (!userid.equals("")) {
						firsttimeUpdate = false;
						//checkLogoutAsyncToServer();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/**SUMANA ENDS**/
		}
	}

	private class UpdateLatLogToServer extends AsyncTask<Void, Integer, String> {
		@Override
		protected String doInBackground(Void... params) {
			return uploadFile();
		}


		@Override
		protected void onCancelled() {

		}

		protected void onPostExecute(final Boolean success) {
			try {

				if (!responseString.equals("")) {
					String msage = "latitude: " + lat + "  longitude: " + lon + "   Userid: " + userid + " // " + responseString;
					//Toast.makeText(LocationUpdate.this, responseString, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		@Override
		protected void onPreExecute() {
			// setting progress bar to zero
			/*progressDialog = ProgressDialog.show(PersonalRegistrationActivity.this,
					"",
					getString(R.string.progress_bar_loading_message),
					false);*/
			super.onPreExecute();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {


			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL_Location_update);

			try {


				/*to print in log*/

				ByteArrayOutputStream bytes = new ByteArrayOutputStream();

				reqEntity.writeTo(bytes);

				String content = bytes.toString();

				Log.e("MultiPartEntityRequest:", content);

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
	}

	private static final float LOCATION_DISTANCE = 0;
	private static final int LOCATION_INTERVAL = 1000 * 8;
	public static final String LOCATION_RECEIVED = "com.goigi.android.efarenew";
	public static boolean runGpsUpdate = true;
	private static final String TAG = "DRIVER";

	private Context context;
	private double currentLat, currentLng;
	private String driverId;
	//private LocationManager lm;
	private boolean firsttimeUpdate = false;
	private String lat = "";

	//private Context context;
	private LocationListener locationListener;
	boolean loginStatus;
	private String lon = "";
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	private final LocationManager mLocationManager = null;
	// A request to connect to Location Services
	private LocationRequest mLocationRequest;
	private ConnectionDetector networkInfo;
	/**SUMANA STARTS**/
	private SharedPreferences prefs;
	private SharedPreferences prefs1;
	private Updatedlatlong psh;
	private UserShared pshuser;
	private MultipartEntity reqEntity;
	String responseString = null;
	private final HashMap<String, String> userdetailMap = new HashMap<String, String>();


	private String userid = "";

	private void checkLogoutAsyncToServer() {
		GeneralAsynctask submitAsync = new GeneralAsynctask(LocationUpdate.this) {

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				try {
					String msage = "latitude: " + String.valueOf(currentLat) + "  longitude: " + String.valueOf(currentLng) + "   Userid: " + userid + " // " + result;
					Toast.makeText(LocationUpdate.this, result, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		try {
			if (networkInfo.isConnectingToInternet()) {
				// 22.5782108, 88.4133091
				// 22.5782032
				//88.4133049
				//22.5782104,88.4132771
				//http://108.179.225.244/~nationalit/team5/goloko/webservice/service.php?
				//auth=fcea920f7412b5da7be0cf42b8c93759&action=updateLatLong&latitude=22.5782121&longitude=88.4132766&user_id=172
				/*http://team1.nationalitsolution.co.in/thelipstickbubble/webservice/service.php?
    			auth=fcea920f7412b5da7be0cf42b8c93759&action=editgeolocation
    					&user_id=27&latitude=22.573329&longitude=88.365022*/
				Log.d("LocationUpdate(377)", "Action: updateLatLong");
				Log.d("uid", userid);
			//	String params = getResources().getString(R.string.base_url) + "AccountApi/UpdateLocationAPI?userid=" + userid + "&lat=" + String.valueOf(currentLat) + "&longg=" + String.valueOf(currentLng);
				String params = "LocationUpdateApi?userid="+userid+"&latitude="+23.5371895+"&longitude="+87.2877356;

				submitAsync.execute(params);
			} else {
				showToastLong(getString(R.string.no_internet_message));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void myThread() {
		prefs1 = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
		prefs = getSharedPreferences("LATLONG_SHARED_PREF", MODE_PRIVATE);
		Thread th = new Thread() {

			@Override
			public void run() {
				try {
					Log.i("LocationService", "runGpsUpdate:" + runGpsUpdate);
					while (runGpsUpdate) {
						//Thread.sleep(18000);

						//	boolean loginStatus = prefs.getBoolean(getString(R.string.shared_loggedin_status_user), false);

						Log.i("LocationService", String.valueOf(loginStatus));
						if (!loginStatus) {
							pshuser = new UserShared(getApplicationContext());
							psh = new Updatedlatlong(getApplicationContext());
							runGpsUpdate = false;
							lat = "0.0";
							lon = "0.0";
							reqEntity = null;

							reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
							reqEntity.addPart("user_id", new StringBody(pshuser.getUserId()));
							reqEntity.addPart("lat", new StringBody(lat));
							reqEntity.addPart("long", new StringBody(lon));
							UpdateLatLogToServer aTask = new UpdateLatLogToServer();
							aTask.execute((Void) null);
							break;
						}
						Thread.sleep(10000);
						//Thread.sleep(12*60*1000);
						//Thread.sleep(10000);
						try {
							if (!userid.equals("")) {
								pshuser = new UserShared(getApplicationContext());
								psh = new Updatedlatlong(getApplicationContext());
								reqEntity = null;
								reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
								reqEntity.addPart("user_id", new StringBody(pshuser.getUserId()));
								reqEntity.addPart("lat", new StringBody(psh.getUserUpdatedLatitude()));
								reqEntity.addPart("long", new StringBody(psh.getUserUpdatedLongitude()));

								Log.v("Hey still running..!!!", "Agree now??");
								UpdateLatLogToServer aTask = new UpdateLatLogToServer();
								aTask.execute((Void) null);

								//checkLogoutAsyncToServer();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}; //End of thread class

		th.start();
	}//End of myThread()


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationRequest.setInterval(LOCATION_INTERVAL);
		mLocationRequest.setFastestInterval(LOCATION_INTERVAL);
		startLocationUpates();
	}


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		context = this.getApplicationContext();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();

		/**SUMANA STARTS**/
		networkInfo = new ConnectionDetector(this.getApplicationContext());
		prefs1 = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
		String jsonUserData = prefs1.getString(getString(R.string.user_info), getString(R.string.no_shared_data));
		Log.d("HomeActivity", "Sending to app class: " + jsonUserData);
		psh = new Updatedlatlong(this);
		pshuser = new UserShared(this);
		userid = pshuser.getUserId();
		//String visibilityStatus = psh.getUserVisibilityStatus();
		boolean bl = pshuser.getLoggedInStatusUser();
		String visibilityStatus = String.valueOf(bl);
		Log.d("LocationSS", "visibilityStatus:" + visibilityStatus);
		/*if(visibilityStatus.equals("1")){*/
		if (bl) {
			runGpsUpdate = true;
		} else {
			runGpsUpdate = false;
		}

		firsttimeUpdate = true;
		/**SUMANA ENDS**/
		loginStatus = pshuser.getLoggedInStatusUser();
		myThread();
	}


	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
	}


	/////////////////////////////////////////LOCATION UPDATE/////////////////////////////////////////////////

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		boolean stopService = false;
		if (intent != null)
			stopService = intent.getBooleanExtra("stopservice", false);

		System.out.println("stopservice " + stopService);

		locationListener = new LocationListener();
		if (stopService)
			stopLocationUpdates();
		else {
			if (!mGoogleApiClient.isConnected())
				mGoogleApiClient.connect();
		}

		return START_STICKY;
	}


	private void showToastLong(String string) {


	}


	private void startLocationUpates() {
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, locationListener);
		// displayLocation();
	}

	public void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, locationListener);

		if (mGoogleApiClient.isConnected())
			mGoogleApiClient.disconnect();
	}


}