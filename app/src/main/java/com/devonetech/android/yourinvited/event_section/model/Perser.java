package com.devonetech.android.yourinvited.event_section.model;



import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Developer on 5/24/2017.
 */

public class Perser {



    public static List<NearByMapModel> getNearByData(JSONArray contentArrray,String type) {

        ArrayList<NearByMapModel> contentArray = new ArrayList<>();

        for (int i = 0; i < contentArrray.length(); i++) {

            JSONObject contentJSONObject = contentArrray.optJSONObject(i);
            NearByMapModel contestModel = new NearByMapModel();

            contestModel.setId(contentJSONObject.optString("id"));
            contestModel.setName(contentJSONObject.optString("name"));
            contestModel.setPlace_id(contentJSONObject.optString("place_id"));
            contestModel.setVicinity(contentJSONObject.optString("vicinity"));
            contestModel.setRating(contentJSONObject.optInt("rating"));
            contestModel.setLat(contentJSONObject.optJSONObject("geometry").optJSONObject("location").optDouble("lat"));
            contestModel.setLng(contentJSONObject.optJSONObject("geometry").optJSONObject("location").optDouble("lng"));
            if (contentJSONObject.has("opening_hours")) {
                contestModel.setIsopen(contentJSONObject.optJSONObject("opening_hours").optBoolean("open_now"));
            } else {
                contestModel.setIsopen(false);
            }

            if(type.equals("airport")){

                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/airport-71.png");
            }
            else if (type.equals("atm")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/atm-71.png");
            }


            else  if(type.equals("bank")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/bank_dollar-71.png");
            }
            else  if(type.equals("bar")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/bar-71.png");
            }
            else if(type.equals("bus_station")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/bus-71.png");
            }

            else if(type.equals("book_store")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/library-71.png");
            }
            else if(type.equals("cafe")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/cafe-71.png");
            }
            else if(type.equals("campground")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/camping-71.png");
            }
            else if(type.equals("car_rental")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/car_rental-71.png");
            }
            else if(type.equals("car_repair")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/car_repair-71.png");
            }
            else if(type.equals("church")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/worship_christian-71.png");
            }
            else if(type.equals("zoo")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/zoo-71.png");
            }
            else if(type.equals("train_station")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/train-71.png");
            }
            else if(type.equals("travel_agency")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png");
            }
            else if(type.equals("taxi_stand")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/taxi-71.png");
            }
            else if(type.equals("shopping_mall")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png");
            }
            else if(type.equals("school")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/school-71.png");
            }
            else if(type.equals("post_office")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/post_office-71.png");
            }
            else if(type.equals("local_government_office")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/government-71.png");
            }
            else if(type.equals("department_store")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/supermarket-71.png");
            }




            else if (type.equals("hospital")){
                contestModel.setMarkerIcon("https://www.shareicon.net/data/512x512/2016/08/04/806609_medical_512x512.png");
            }
            else if(type.equals("doctor")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/doctor-71.png");

            }
            else if(type.equals("pharmacy")){
                contestModel.setMarkerIcon("https://i.pinimg.com/originals/7e/88/cc/7e88cce2beec5f097aa60245d9d70fc0.png");

            }
            else if(type.equals("police")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/police-71.png");

            }
            else if(type.equals("fire_station")){
                contestModel.setMarkerIcon("http://www.myiconfinder.com/uploads/iconsets/256-256-1c50d2b6f380c5859f04513d94b2e6c6-station.png");

            }
            else if(type.equals("restaurant")){
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png");

            }

            else {
                contestModel.setMarkerIcon("https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png");
            }


            contentArray.add(contestModel);
        }
        return contentArray;

    }

    public static List<NearByMapModel> getNearByUserData(JSONArray contentArrray) {

        ArrayList<NearByMapModel> contentArray = new ArrayList<>();

        for (int i = 0; i < contentArrray.length(); i++) {

            JSONObject contentJSONObject = contentArrray.optJSONObject(i);
            NearByMapModel contestModel = new NearByMapModel();

            contestModel.setId(contentJSONObject.optString("UserId"));
            contestModel.setName(contentJSONObject.optString("UserName"));
            contestModel.setPlace_id(contentJSONObject.optString("Address"));
            contestModel.setVicinity(contentJSONObject.optString("Address"));
            contestModel.setRating(5);
            contestModel.setLat(Double.parseDouble(contentJSONObject.optString("Lat")));
            contestModel.setLng(Double.parseDouble(contentJSONObject.optString("Lng")));
            contestModel.setFormatted_phone_number(contentJSONObject.optString("Phone"));
            contestModel.setInternational_phone_number(contentJSONObject.optString("Phone"));
           /* if (contentJSONObject.has("opening_hours")) {
                contestModel.setIsopen(contentJSONObject.optJSONObject("opening_hours").optBoolean("open_now"));
            } else {
                contestModel.setIsopen(false);
            }*/
            contestModel.setIsopen(true);

            contentArray.add(contestModel);
        }
        return contentArray;

    }

    public static List<NearByMapModel> getNearByProviderData(JSONArray contentArrray) {

        ArrayList<NearByMapModel> contentArray = new ArrayList<>();

        for (int i = 0; i < contentArrray.length(); i++) {

            JSONObject contentJSONObject = contentArrray.optJSONObject(i);
            NearByMapModel contestModel = new NearByMapModel();

            contestModel.setId(contentJSONObject.optString("UserId"));
            contestModel.setName(contentJSONObject.optString("UserName"));
            contestModel.setPlace_id(contentJSONObject.optString("Address"));
            contestModel.setVicinity(contentJSONObject.optString("Address"));
            contestModel.setRating(5);
            contestModel.setLat(Double.parseDouble(contentJSONObject.optString("Lat")));
            contestModel.setLng(Double.parseDouble(contentJSONObject.optString("Lng")));
            contestModel.setFormatted_phone_number(contentJSONObject.optString("Phone"));
            contestModel.setInternational_phone_number(contentJSONObject.optString("Phone"));
            contestModel.setIsopen(true);

            contentArray.add(contestModel);
        }
        return contentArray;

    }



}
