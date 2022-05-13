package com.devonetech.android.yourinvited.shared;


import android.content.Context;
import android.content.SharedPreferences;
import com.devonetech.android.yourinvited.R;


public class UserShared {
	private final Context context;
	private final SharedPreferences prefs;

	public UserShared(Context context) {
		this.context = context;
		prefs = context.getSharedPreferences("MY_SHARED_PREF", context.MODE_PRIVATE);
	}




	public boolean getLoggedInStatusUser(){
		return prefs.getBoolean(context.getString(R.string.shared_loggedin_status_user), false);
	}

	public String getUserId(){
		return prefs.getString(context.getString(R.string.shared_user_id), context.getString(R.string.shared_no_data));

	}

	public String getUserName(){
		return prefs.getString(context.getString(R.string.shared_user_fullname), context.getString(R.string.shared_no_data));

	}

	public String getUserEmail(){
		return prefs.getString(context.getString(R.string.shared_user_email), context.getString(R.string.shared_no_data));

	}
	public String getUserPhone(){
		return prefs.getString(context.getString(R.string.shared_user_number), context.getString(R.string.shared_no_data));

	}

	public String getGuestAddress(){
		return prefs.getString(context.getString(R.string.shared_guest_address), context.getString(R.string.shared_no_data));

	}

	public String getUserPic(){
		return prefs.getString(context.getString(R.string.shared_user_picture), context.getString(R.string.shared_no_data));

	}

	public String getCoverPic(){
		return prefs.getString(context.getString(R.string.shared_user_cover), context.getString(R.string.shared_no_data));

	}



	public String getabout_me(){
		return prefs.getString(context.getString(R.string.shared_user_about_me), context.getString(R.string.shared_no_data));

	}
	public String getbirth_place(){
		return prefs.getString(context.getString(R.string.shared_user_birth_place), context.getString(R.string.shared_no_data));

	}
	public String getlives_in(){
		return prefs.getString(context.getString(R.string.shared_user_lives_in), context.getString(R.string.shared_no_data));

	}
	public String getmstatus(){
		return prefs.getString(context.getString(R.string.shared_user_status), context.getString(R.string.shared_no_data));

	}
	public String getwebsite(){
		return prefs.getString(context.getString(R.string.shared_user_website), context.getString(R.string.shared_no_data));

	}
	public String getpolitical_view(){
		return prefs.getString(context.getString(R.string.shared_user_political_view), context.getString(R.string.shared_no_data));

	}
	public String getreligious(){
		return prefs.getString(context.getString(R.string.shared_user_religious), context.getString(R.string.shared_no_data));

	}
	public String getdob(){
		return prefs.getString(context.getString(R.string.shared_user_date), context.getString(R.string.shared_no_data));

	}
	public String gethobbies(){
		return prefs.getString(context.getString(R.string.shared_user_hobbies), context.getString(R.string.shared_no_data));

	}
	public String gettv_shows(){
		return prefs.getString(context.getString(R.string.shared_user_tv_shows), context.getString(R.string.shared_no_data));

	}
	public String getmovies(){
		return prefs.getString(context.getString(R.string.shared_user_movies), context.getString(R.string.shared_no_data));

	}
	public String getgames(){
		return prefs.getString(context.getString(R.string.shared_user_games), context.getString(R.string.shared_no_data));

	}
	public String getmusic(){
		return prefs.getString(context.getString(R.string.shared_user_music), context.getString(R.string.shared_no_data));

	}
	public String getbooks(){
		return prefs.getString(context.getString(R.string.shared_user_books), context.getString(R.string.shared_no_data));

	}

	public String getwriters(){
		return prefs.getString(context.getString(R.string.shared_user_writers), context.getString(R.string.shared_no_data));

	}
	public String getothers(){
		return prefs.getString(context.getString(R.string.shared_user_others), context.getString(R.string.shared_no_data));

	}
	public String getskills(){
		return prefs.getString(context.getString(R.string.shared_user_skills), context.getString(R.string.shared_no_data));

	}
	public String getschool_start_year(){
		return prefs.getString(context.getString(R.string.shared_user_school_start_year), context.getString(R.string.shared_no_data));

	}

	public String getschool_end_year(){
		return prefs.getString(context.getString(R.string.shared_user_school_end_year), context.getString(R.string.shared_no_data));

	}
	public String getschool(){
		return prefs.getString(context.getString(R.string.shared_user_school), context.getString(R.string.shared_no_data));

	}
	public String getschool_id(){
		return prefs.getString(context.getString(R.string.shared_user_school_id), context.getString(R.string.shared_no_data));

	}

	public String getuniversity(){
		return prefs.getString(context.getString(R.string.shared_user_university), context.getString(R.string.shared_no_data));

	}
	public String getuniversitystart_year(){
		return prefs.getString(context.getString(R.string.shared_user_university_start_year), context.getString(R.string.shared_no_data));

	}
	public String getuniversityend_year(){
		return prefs.getString(context.getString(R.string.shared_user_university_end_year), context.getString(R.string.shared_no_data));

	}







}
