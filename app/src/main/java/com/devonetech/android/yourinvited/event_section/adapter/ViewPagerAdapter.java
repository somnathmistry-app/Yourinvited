package com.devonetech.android.yourinvited.event_section.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.devonetech.android.yourinvited.event_section.NearbyFragment;
import com.devonetech.android.yourinvited.event_section.model.NearbyPlaceModel;
import java.util.ArrayList;

/**
 * Created by App-DEVELOPER on 3/16/2018.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Fragment fragment = null;
    ArrayList<NearbyPlaceModel> datalist;
    ArrayList<ArrayList<NearbyPlaceModel>> tabdata;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<NearbyPlaceModel> datalist, ArrayList<ArrayList<NearbyPlaceModel>> itemlist) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.datalist=datalist;
        this.tabdata=itemlist;
    }

    @Override
    public Fragment getItem(int position) {

        for (int i = 0; i < mNumOfTabs ; i++) {
            if (i == position) {
                fragment = NearbyFragment.createInstance(mNumOfTabs,tabdata.get(i),"");
                break;
                      }
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
