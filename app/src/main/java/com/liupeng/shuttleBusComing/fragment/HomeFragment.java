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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.activities.BusLineShowActivity;
import com.liupeng.shuttleBusComing.activities.MainActivity;
import com.liupeng.shuttleBusComing.activities.MapActivity;
import com.liupeng.shuttleBusComing.adapter.LineAdapter;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.liupeng.shuttleBusComing.utils.Initialize;
import com.liupeng.shuttleBusComing.utils.Station;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements MainActivity.OnGetBusLineMessage, View.OnClickListener {

    private MainActivity mainActivity;
    private BusLineItem busLineItem;
    private LinearLayout mainMessage;
    private RelativeLayout hideBg;
    private TextView recentStation;
    private TextView recentLine;
    private TextView nextStation;

    private LinearLayout nowLine;
    private LinearLayout nowStation;
    private boolean isFirst = true;
    private LineAdapter stationAdapter;
    private ArrayList<Map<String, BusLineItem>> busLineMessage;
    private boolean isLast = false;

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
        initData();
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
        hideBg = $(view, R.id.hide_bg);
        recentStation = $(view, R.id.recent_stations);
        recentLine = $(view, R.id.recent_line);
        nextStation = $(view, R.id.next_station);
        nowLine = $(view, R.id.now_line);
        nowStation = $(view, R.id.now_station);
        imgBtn_favorite.setOnClickListener(this);
        imgBtn_map.setOnClickListener(this);
        mainActivity = (MainActivity) getActivity();
        nowLine.setOnClickListener(this);
        nowStation.setOnClickListener(this);
    }

    private ArrayList<Station> data = new ArrayList<Station>();

    public void initData(){
        recentStation.setText("日报大厦");
        recentLine.setText("6号线");
//            String result = getNextStation(busLineItem.getBusStations());
        String result = "中山门";
        String forward = null;
        if (!Initialize.ERROR.equals(result)) {
            forward = isLast ? "终点站:" : "下一站:";
            nextStation.setText(forward + result);
        }
        showMessage();
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
            case R.id.now_line:
            case R.id.now_station:
                showLine();
                break;
            default:
                break;
        }
    }

    private ArrayList<Station> restList = new ArrayList<>();

    public void addDataToAdapter(ArrayList<Station> data, ArrayList<Station> poiMessage) {
        int flag = -1;
        String name = null;
        for (int count = 0; count < poiMessage.size(); count++) {
            name = poiMessage.get(count).getStationName();
            name = name.substring(0, name.indexOf("("));
            if (name.equals(recentStation.getText().toString())) {
                flag = count;
            }
        }
        data.addAll(poiMessage);
        if (-1 != flag) {
            data.remove(flag);
        }
        restList.addAll(data);
    }

    private void showMessage() {
        mainMessage.setVisibility(View.VISIBLE);
        hideBg.setVisibility(View.GONE);
    }

    public void showLine() {
//        if (null != busLineMessage) {
            Intent intentShowLine = new Intent(getActivity(), BusLineShowActivity.class);

//            intentShowLine.putParcelableArrayListExtra("GoLine",
//                    (ArrayList<? extends Parcelable>) busLineMessage.get(0)
//                            .get("GoBusLineMessage")
//                            .getBusStations());
//
//            intentShowLine.putParcelableArrayListExtra("BackLine",
//                    (ArrayList<? extends Parcelable>) busLineMessage.get(0)
//                            .get("BackBusLineMessage")
//                            .getBusStations());
//
//            intentShowLine.putExtra("BusName", busLineMessage.get(0)
//                    .get("GoBusLineMessage")
//                    .getBusLineName());
//
//            intentShowLine.putExtra("FirstBus", busLineMessage.get(0)
//                    .get("GoBusLineMessage").getFirstBusTime());
//
//            intentShowLine.putExtra("LastBus", busLineMessage.get(0)
//                    .get("GoBusLineMessage").getLastBusTime());
            startActivity(intentShowLine);
//        }
    }

    @Override
    public void OnReceiveBusLineMessage(ArrayList<Map<String, BusLineItem>> busLineItems, ErrorStatus errorStatus) {

        if (!errorStatus.getIsError()) {
            busLineMessage = busLineItems;
            busLineItem = busLineItems.get(0).get("GoBusLineMessage");
            recentLine.setText(busLineItem.getBusLineName());
            //String result = getNextStation(busLineItem.getBusStations());
            String result = "中山门";
            String forward = null;
            if (!Initialize.ERROR.equals(result)) {
                forward = isLast ? "终点站:" : "下一站:";
                nextStation.setText(forward + result);
            }
            showMessage();
        }
    }



//    public String getNextStation(List<BusStationItem> stationItems) {
//        int correctLocation = -1;
//        if (null != mPoiMessage) {
//            int count = 0;
//            String busStationName = null;
//            for (BusStationItem item : stationItems) {
//                busStationName = mPoiMessage.get(0).getTitle();
//                busStationName = busStationName.substring(0, busStationName.lastIndexOf("("));
//                if (item.getBusStationName().indexOf(busStationName) != -1) {
//                    correctLocation = count;
//                    break;
//                }
//                count++;
//            }
//        }
//        isLast = (correctLocation == (stationItems.size() - 1)) ? true : false;
//        return (correctLocation == -1) ?
//                Initialize.ERROR : (isLast ?
//                (stationItems.get(correctLocation).getBusStationName()) : (stationItems.get(correctLocation + 1).getBusStationName()));
//    }
}
