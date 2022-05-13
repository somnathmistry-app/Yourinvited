package com.devonetech.android.yourinvited.social_network.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom.Circle_Image;
import com.devonetech.android.yourinvited.social_network.model.FeedPostModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.ViewHolder> {

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}
	public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

		    public TextView bloger_name,blog_title,date_time;
	        public ImageView _body_image;
	        private Circle_Image bloger_pic;
	        private LinearLayout first_lay;
	        private ImageView share_now;

		public ViewHolder(View view) {
			super(view);
			bloger_name = (TextView) view.findViewById(R.id.textView);
			blog_title = (TextView) view.findViewById(R.id.cmt_txt);
			_body_image = (ImageView) view.findViewById(R.id.imageView);
		
            date_time=(TextView)view.findViewById(R.id.cmt_time);
        

			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, getPosition());
			}
		}

	}
	private final Activity mActivity;

	OnItemClickListener mItemClickListener;
    String IMg;
	private ArrayList<FeedPostModel> mUserDetails = new ArrayList<FeedPostModel>();

	public PostCommentAdapter(Activity mActivity, ArrayList<FeedPostModel> mUserDetails) {
		this.mActivity = mActivity;
		this.mUserDetails=mUserDetails;
		// createUserDetails();
	}

	@Override
	public int getItemCount() {
		return mUserDetails.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder , final int position) {
		holder.bloger_name.setText(mUserDetails.get(position).comment_by);
		holder.blog_title.setText(mUserDetails.get(position).comment_txt);
		
		//String myUTC=mUserDetails.get(position).comment_time;
		
		/*SimpleDateFormat existingUTCFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat requiredFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm aa");

		Date getDate = null;
		try {
			getDate = existingUTCFormat.parse(myUTC);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String mydate=requiredFormat.format(getDate);*/
	
		
		//holder.date_time.setText(mydate);
    
		IMg = mUserDetails.get(position).comment_by_pic;
		/*"Setting event image" Starts*/
		if (!IMg.equals("")) {		         
	         try {
       	   String apiLink = IMg;
       	   
		       //Log.d("User Profile Image Parsing", "apiLink:"+apiLink);
		       String encodedurl = "";
		       encodedurl = apiLink.substring(0,apiLink.lastIndexOf('/'))+ "/"+ Uri.encode(apiLink.substring(
		       apiLink.lastIndexOf('/') + 1));
			   Log.d("Profile", "encodedurl:"+encodedurl);
				if (!apiLink.equals("") && apiLink != null) {
				    Picasso.with(mActivity)
				   .load(encodedurl) // load: This path may be a remote URL,				  
				   .placeholder(R.drawable.no_data_found_1x)
				   //.resize(130, 130)
				   .error(R.drawable.no_data_found_1x)
				   .into(holder._body_image); // Into: ImageView into which the final image has to be passed
				}
			 } catch (Exception e) {
				e.printStackTrace();
			 }
		 }
		
		
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
		final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
		final View sView = mInflater.inflate(R.layout.comment_item, parent, false);
		return new ViewHolder(sView);
	}

	public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}

	

}
