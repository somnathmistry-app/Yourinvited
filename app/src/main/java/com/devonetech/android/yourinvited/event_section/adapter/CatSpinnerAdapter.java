package com.devonetech.android.yourinvited.event_section.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.model.ServiceCatModel;

import java.util.ArrayList;

public class CatSpinnerAdapter extends ArrayAdapter<ServiceCatModel> {

	/*public ListAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}*/

	// Declare Variables
	private Context ctx;
	private int layoutId;
	private ArrayList<ServiceCatModel> DataList;
	
	
	String County_Name;
	String County_id;
	
	    
	public CatSpinnerAdapter(Context ctx, ArrayList<ServiceCatModel> DataList, int layoutId) {
		super(ctx,layoutId);
		this.ctx = ctx;
	
		this.DataList = DataList;
		
		this.layoutId = layoutId;
		
	
	
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return DataList.size() ;
	}

	@Override
	public ServiceCatModel getItem(int position) {
		// TODO Auto-generated method stub
		return DataList.get(position);
	}

	/*@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}*/
	
	 @Override
	    public View getDropDownView(int position, View convertView, ViewGroup parent) {
	        return getView(position, convertView, parent);
	    }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View result = convertView;
		
		if (result == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			result = inflater.inflate(layoutId, parent, false);
		}
		
		//u_id=psh.getUserId();
		

		TextView Name=(TextView)result.findViewById(R.id.company_name);
		TextView Id=(TextView)result.findViewById(R.id.company_id);
				
		
		 
		/* County_Name=DataList.get(position).county_name;
		 County_id=DataList.get(position).county_id;*/
		 
		 Name.setText(DataList.get(position).Service_catname);
		 Id.setText(DataList.get(position).Service_catId);
		
		 
		return result;
	
	}


	

	
  
	
}
