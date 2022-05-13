package com.devonetech.android.yourinvited.social_network;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.devonetech.android.yourinvited.R;


public class NewActivity extends AppCompatActivity {
 
    private ImageView btnonce, btncontinuously, btnstop, btnplay;
    private VideoView vv;
    private MediaController mediacontroller;
    private Uri uri;
    private boolean isContinuously = false;
    private ProgressBar progressBar;
    Intent mIntent;
    Toolbar toolbar;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_play_lay);
        
           toolbar = (Toolbar) findViewById(R.id.toolbar);
		   setSupportActionBar(toolbar);
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        getSupportActionBar().setTitle("Back");
	        toolbar.setNavigationOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                finish();
	            	
	            }
	        });
        mIntent=getIntent();
 
        progressBar = (ProgressBar) findViewById(R.id.progrss);
        btnonce = (ImageView) findViewById(R.id.btnonce);
        btncontinuously = (ImageView) findViewById(R.id.btnconti);
        btnstop = (ImageView) findViewById(R.id.btnstop);
        btnplay = (ImageView) findViewById(R.id.btnplay);
        vv = (VideoView) findViewById(R.id.vv);
 
        mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(vv);
       // String uriPath = "http://www.demonuts.com/Demonuts/smallvideo.mp4"; //update package name
      //  String uriPath = "http://dev.goigi.biz/memeyatcht/uploads/news/343528959.mp4";
        //String base_file=getResources().getString(R.string.uploads_userpost);
    	
        
        if (mIntent.getStringExtra("from").equals("diary")) {
			
        	 uri = Uri.parse(mIntent.getStringExtra("file"));
        	 
        	 isContinuously = false;
             progressBar.setVisibility(View.VISIBLE);
             vv.setMediaController(mediacontroller);
             vv.setVideoURI(uri);
             vv.requestFocus();
             vv.start();
		}
        else if(mIntent.getStringExtra("from").equals("news")) {
        	
        	uri = Uri.parse(mIntent.getStringExtra("file"));
		
        	 isContinuously = false;
             progressBar.setVisibility(View.VISIBLE);
             vv.setMediaController(mediacontroller);
             vv.setVideoURI(uri);
             vv.requestFocus();
             vv.start();
        }
       
 
       /* vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(isContinuously){
                    vv.start();
                }
            }
        });
 */
        vv.setOnCompletionListener(new OnCompletionListener() {
			
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if(isContinuously){
                    vv.start();
                }
			}
		});
        
       /* btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vv.pause();
            }
        });*/
        
        btnstop.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 vv.pause();
			}
		});
 
     /*   btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vv.start();
            }
        });*/
        
        
        btnplay.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 vv.start();
			}
		});
 
      /*  btnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isContinuously = false;
                progressBar.setVisibility(View.VISIBLE);
                vv.setMediaController(mediacontroller);
                vv.setVideoURI(uri);
                vv.requestFocus();
                vv.start();
            }
        });*/
        
        btnonce.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				    isContinuously = false;
	                progressBar.setVisibility(View.VISIBLE);
	                vv.setMediaController(mediacontroller);
	                vv.setVideoURI(uri);
	                vv.requestFocus();
	                vv.start();
			}
		});
 
      /*  btncontinuously.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isContinuously = true;
                progressBar.setVisibility(View.VISIBLE);
                vv.setMediaController(mediacontroller);
                vv.setVideoURI(uri);
                vv.requestFocus();
                vv.start();
            }
        });*/
        
        btncontinuously.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isContinuously = true;
                progressBar.setVisibility(View.VISIBLE);
                vv.setMediaController(mediacontroller);
                vv.setVideoURI(uri);
                vv.requestFocus();
                vv.start();
			}
		});
 
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
               progressBar.setVisibility(View.GONE);
            }
        });
 
    }
}