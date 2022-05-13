package com.devonetech.android.yourinvited.social_network.friend_profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.social_network.group.adapter.FriendPhotoAdapter;
import com.devonetech.android.yourinvited.social_network.group.adapter.MyGroupsAdapter;

public class FriendPhotosActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    private RelativeLayout topLay;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView nodata;
    RecyclerView recycler_view;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_of_friend_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Photos");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();

                    }
                }

        );


        xmlini();

    }
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        //callListApi();
        setdata();

    }

    private void xmlini() {
        topLay=findViewById(R.id.topLay);
        topLay.setVisibility(View.GONE);
        recycler_view = (RecyclerView)findViewById(R.id.my_recycler_view);
        recycler_view.setLayoutManager(new GridLayoutManager(this, 3));
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        nodata = findViewById(R.id.no_data);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        // fetchMovies();
                                       // callListApi();
                                        setdata();

                                    }
                                }
        );
    }

    private void setdata() {
        FriendPhotoAdapter adapter = new FriendPhotoAdapter(this);
        recycler_view.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }
}
