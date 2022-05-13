package com.devonetech.android.yourinvited.social_network;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devonetech.android.yourinvited.MainActivity;
import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.shared.Updatedlatlong;
import com.devonetech.android.yourinvited.social_network.fragment.EventsFragment;
import com.devonetech.android.yourinvited.social_network.fragment.FeedsFragment;
import com.devonetech.android.yourinvited.social_network.fragment.FeedsFragmentPagination;
import com.devonetech.android.yourinvited.social_network.fragment.FriendRequestFragment;
import com.devonetech.android.yourinvited.social_network.fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class SocialNetworkMain extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    private TabLayout tabLayout;
    Toolbar toolbar;
    private ViewPager viewPager;
    private ImageView shareLocation;
    Updatedlatlong lat_psh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getLayoutInflater().inflate(R.layout.social_network_main, container);
        setContentView(R.layout.social_network_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lat_psh=new Updatedlatlong(this);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        shareLocation=findViewById(R.id.imageView9);
        shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?saddr=" +lat_psh.getUserUpdatedLatitude()+","+lat_psh.getUserUpdatedLongitude();

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //String ShareSub = datalist.get(position).location_title;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Current Location");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

    }
    int k = 0;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        {
            k++;
            if(k == 1)
            {
                Snackbar.make(coordinatorLayout, "Please press again to exit .", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Toast.makeText(SocialNetworkMain.this, "", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_rss_feed_black_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_people_gray_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_open_in_new_gray_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_filter_list_black_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FeedsFragmentPagination(), "Feeds");
        adapter.addFragment(new FriendRequestFragment(), "Friend Request");
        adapter.addFragment(new EventsFragment(), "Events");
        adapter.addFragment(new ProfileFragment(), "Profile");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

      /*  @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }*/
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
