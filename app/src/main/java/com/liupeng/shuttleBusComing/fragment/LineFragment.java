package com.liupeng.shuttleBusComing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.activities.BusLineShowActivity;
import com.liupeng.shuttleBusComing.activities.MainActivity;
import com.liupeng.shuttleBusComing.activities.MapActivity;
import com.liupeng.shuttleBusComing.adapter.LineAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LineFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private MainActivity mainActivity;
    private LinearLayout mainMessage;
    private RelativeLayout hideBg;
    private LineAdapter stationAdapter;
    private ArrayList<String> data = new ArrayList<>();

    @BindView(R.id.imgBtn_map)
    ImageButton imgBtn_map;
    @BindView(R.id.lines_list)
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    public void initView(View view) {
        mainMessage = $(view, R.id.main_message);
        hideBg = $(view, R.id.hide_bg);
        //listView = $(view, R.id.lines_list);
        mainActivity = (MainActivity) getActivity();
        imgBtn_map.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    public void initData() {
        data = new ArrayList<String>(Arrays.asList("1号线","2号线","3号线","4号线","5号线","6号线","7号线","8号线","9号线"));

        stationAdapter = new LineAdapter(mainActivity, data);
        listView.setAdapter(stationAdapter);

        showMessage();
    }

    private <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static LineFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        LineFragment fragment = new LineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        showLine();
    }

    private void showMessage() {
        mainMessage.setVisibility(View.VISIBLE);
        hideBg.setVisibility(View.GONE);
    }
}
