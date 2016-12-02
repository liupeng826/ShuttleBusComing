package com.liupeng.shuttleBusComing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.activities.MainActivity;
import com.liupeng.shuttleBusComing.activities.MapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;
    //private BusLineItem busLineItem;
    private LinearLayout mainMessage;
    private RelativeLayout hideBg;
    private TextView recentStation;
    private TextView recentLine;
    private TextView nextStation;
    private ListView stationListView;

    private LinearLayout nowLine;
    private LinearLayout nowStation;

    @BindView(R.id.imgBtn_favorite)
    ImageButton imgBtn_favorite;
    @BindView(R.id.imgBtn_map)
    ImageButton imgBtn_map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initView(view);

        return view;
    }

    public void initView(View view) {
//        myAddress = (TextView) view.findViewById(R.id.myAddress);
//        hideMyAddress = (RelativeLayout) view.findViewById(R.id.hide_myaddress);
//        nonBusLine = (RelativeLayout) view.findViewById(R.id.nonBusLine);
//        locatingWaitingProGrass = (RelativeLayout) view.findViewById(R.id.locatingWaitingStatus);
//        waitingBusLine = (RelativeLayout) view.findViewById(R.id.waiting_BusLine);
//        locationMessageLayout = (LinearLayout) view.findViewById(R.id.myLocationMessage);
//        busListForm = (LinearLayout) view.findViewById(R.id.busListForm);
//        busLine = (TextView) view.findViewById(R.id.busLine);
//        busList = (ListView) view.findViewById(R.id.bus_list);
        mainMessage = $(view, R.id.main_message);
        //hideBg = $(view, R.id.hide_bg);
        recentStation = $(view, R.id.recent_stations);
        recentLine = $(view, R.id.recent_line);
        nextStation = $(view, R.id.next_station);
        stationListView = $(view, R.id.stations_list);
        nowLine = $(view, R.id.now_line);
        nowStation = $(view, R.id.now_station);
        imgBtn_favorite.setOnClickListener(this);
        imgBtn_map.setOnClickListener(this);
//        mainActivity = (MainActivity) getActivity();
//        stationListView.setOnItemClickListener(this);
//        nowLine.setOnClickListener(this);
//        nowStation.setOnClickListener(this);

    }







    private <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public static HomeFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_favorite:
                break;
            case R.id.imgBtn_map:
                startActivity(new Intent(getActivity(), MapActivity.class));
                break;
        }
    }
}
