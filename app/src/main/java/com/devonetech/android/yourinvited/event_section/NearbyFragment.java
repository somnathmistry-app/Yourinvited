package com.devonetech.android.yourinvited.event_section;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devonetech.android.yourinvited.R;
import com.devonetech.android.yourinvited.event_section.adapter.NearbyAdapter;
import com.devonetech.android.yourinvited.event_section.model.NearbyPlaceModel;

import java.util.ArrayList;

public class NearbyFragment extends Fragment {
    private TextView title;
    RecyclerView recyclerView;

    View rootView;
    public final static String ITEMS_COUNT_KEY = "PartThreeFragment$ItemsCount";


    private NearbyAdapter ListFragmentAdapter;

    private static ArrayList<NearbyPlaceModel> Dataitem1;

    static NearbyFragment partThreeFragment;
    private TextView no_data_found;
    public static int abc;

    public static NearbyFragment createInstance(int itemsCount,ArrayList<NearbyPlaceModel> Dataitem,String flag) {
        partThreeFragment = new NearbyFragment();

        Bundle bundle = new Bundle();
        // bundle.putInt(ITEMS_COUNT_KEY, itemsCount);
        bundle.putString(ITEMS_COUNT_KEY, flag);
        partThreeFragment.setArguments(bundle);
        Dataitem1=Dataitem;
        abc=itemsCount;

        return partThreeFragment;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recent_call, container, false);

        Bundle ll=partThreeFragment.getArguments();
        String ss=ll.getString(ITEMS_COUNT_KEY);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //  no_data_found=(TextView)rootView.findViewById(R.id.no_data_found);
        title=(TextView)rootView.findViewById(R.id.nodata);
        title.setVisibility(View.GONE);


        if (Dataitem1.size()==0){

            recyclerView.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);

            ListFragmentAdapter = new NearbyAdapter(getActivity(), Dataitem1,"all");

            recyclerView.setAdapter(ListFragmentAdapter);


            //title.setText(String.valueOf(abc));


        }

        return rootView;
    }

}
