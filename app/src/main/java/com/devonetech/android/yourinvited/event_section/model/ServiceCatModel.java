package com.devonetech.android.yourinvited.event_section.model;

import java.io.Serializable;


public class ServiceCatModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6480600302203777468L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	
	//private static final long serialVersionUID = 3348392585369527775L;
	

	
	public String Service_catId;
	public String Service_catname;
	public String Service_pic;
	public String subarray;
	

	
	
	public ServiceCatModel(String Service_catId, String Service_catname,
                           String subarray){
		
		
		this.Service_catId=Service_catId;
		this.Service_catname=Service_catname;
		
		this.subarray=subarray;
		
		
		
		
		
		
		
	}
	
	    private int image;
	    private String title;
	
	    public ServiceCatModel(int image, String title) {
	        this.image = image;
	        this.title = title;
	       
	    }
	    
	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }
	    
	    public int getImage() {
	        return image;
	    }

	    public void setImage(int image) {
	        this.image = image;
	    }

	
	
}