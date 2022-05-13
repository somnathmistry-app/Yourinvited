package com.devonetech.android.yourinvited.event_section;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.model.EventModel;

import java.util.ArrayList;

public class FragmentOne extends Fragment {

    public FragmentOne() {
        // Required empty public constructor
    }
    static FragmentOne partThreeFragment;
    private static ArrayList<EventModel> Dataitem1;
    public final static String ITEMS_COUNT_KEY = "PartThreeFragment$ItemsCount";
    private ImageView nodata;

    public static FragmentOne createInstance(int itemsCount, ArrayList<EventModel> Dataitem, String flag) {
        partThreeFragment = new FragmentOne();
        Bundle bundle = new Bundle();
        bundle.putString(ITEMS_COUNT_KEY, flag);
        partThreeFragment.setArguments(bundle);
        Dataitem1=Dataitem;


        return partThreeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

}