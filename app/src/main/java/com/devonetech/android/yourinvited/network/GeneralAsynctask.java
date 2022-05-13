package com.devonetech.android.yourinvited.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

public class GeneralAsynctask extends AsyncTask<String, Integer, String> {
	
	private Context context;
	public ProgressDialog pdspinnerGeneral;

	public JSONObject jObj;
	private int noProgressDialog = 1;
	
	public GeneralAsynctask(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public GeneralAsynctask(Context context, int noProgressDialog) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.noProgressDialog = noProgressDialog;
	}
	
	
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if(noProgressDialog == 1) {
			pdspinnerGeneral = new ProgressDialog(context);
			//pdspinnerGeneral1 =new CustomPB();
			//pdspinnerGeneral.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			//pdspinnerGeneral.setMessage("Please Wait..!!");
			//pdspinnerGeneral.setCancelable(true);
			pdspinnerGeneral.show();
			
		}
	}



	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		Log.v("GeneralUrl url ------ >", params[0]);
		HttpHandler json = new HttpHandler();   	   
        String result = json.getJSONStringFromUrl(params[0]);
        int i =  0;
       
        
    	if(this.isCancelled())
    	{
    		return null;
    	}
						
		return result;
	}
	
	

	/*protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
					
		//return result;
		
	}*/
	

}
