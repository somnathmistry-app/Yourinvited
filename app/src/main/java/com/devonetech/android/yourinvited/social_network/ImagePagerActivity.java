package com.devonetech.android.yourinvited.social_network;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.custom.HackyViewPager;
import com.devonetech.android.yourinvited.photoview.view.PhotoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImagePagerActivity extends AppCompatActivity {

    private static final String ISLOCKED_ARG = "isLocked";
    private ViewPager mViewPager;
    private int position;
    Intent mIntent;
    SamplePagerAdapter adapter;
    ArrayList<String> imagelist=new ArrayList<>();
    String img;
    Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Create User");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }

        );

        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        //setContentView(mViewPager);

        mIntent=getIntent();

        try {
            JSONArray jj=new JSONArray(mIntent.getStringExtra("images"));

            for (int i = 0; i < jj.length(); i++) {

                JSONObject imgobj=jj.getJSONObject(i);

                img=imgobj.getString("image");
                imagelist.add(img);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //mViewPager.setAdapter(new ImagePagerActivity.SamplePagerAdapter());
        adapter = new SamplePagerAdapter(ImagePagerActivity.this,imagelist);
        mViewPager.setAdapter(adapter);
        if (getIntent() != null) {
            position = getIntent().getIntExtra("position", 0);
            mViewPager.setCurrentItem(position);
        }

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }

       /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/
    }

    static class SamplePagerAdapter extends PagerAdapter {
        /* Here I'm adding the demo pics, but you can add your Item related pics , just get your pics based on itemID (use asynctask) and
         fill the urls in arraylist*/
       // private static final String[] sDrawables = ImageUrlUtils.getImageUrls();
       // private static final String[] sDrawables = new String[0];

        String images;
        Context context;
        ArrayList<String> imagelist;

        public SamplePagerAdapter(Context context, ArrayList<String> stringExtra) {
            this.context=context;
            this.imagelist=stringExtra;
        }


        @Override
        public int getCount() {
            return imagelist.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
           // photoView.setImageUri(sDrawables[position]);





            photoView.setImageUri(imagelist.get(position));

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

}
