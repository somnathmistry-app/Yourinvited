package com.devonetech.android.yourinvited.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.devonetech.android.yourinvited.R;

public class Updatedlatlong {
	private Context context;
	private SharedPreferences prefs;

	public Updatedlatlong(Context context) {
		this.context = context;
		prefs = context.getSharedPreferences("LATLONG_SHARED_PREF", context.MODE_PRIVATE);
	}
	

	
	public String getUserUpdatedLatitude(){
		return prefs.getString(context.getString(R.string.shared_updated_lat), context.getString(R.string.shared_no_data));

	}
	public String getUserUpdatedLongitude(){
		return prefs.getString(context.getString(R.string.shared_updated_long), context.getString(R.string.shared_no_data));

	}
	
	
	public String getSourcelat(){
		return prefs.getString(context.getString(R.string.shared_source_lat), context.getString(R.string.no_data_latlong));

	}
	public String getSourcelong(){
		return prefs.getString(context.getString(R.string.shared_source_long), context.getString(R.string.no_data_latlong));

	}
	
	
	public String getDestinationlat(){
		return prefs.getString(context.getString(R.string.shared_destination_lat), context.getString(R.string.no_data_latlong));

	}
	public String getDestinationlong(){
		return prefs.getString(context.getString(R.string.shared_destination_long), context.getString(R.string.no_data_latlong));

	}
	
	
	
	
	
}
