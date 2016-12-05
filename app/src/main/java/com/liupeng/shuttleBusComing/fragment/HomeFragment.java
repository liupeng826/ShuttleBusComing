package com.liupeng.shuttleBusComing.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.busline.BusLineItem;
import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.activities.BusLineShowActivity;
import com.liupeng.shuttleBusComing.activities.MainActivity;
import com.liupeng.shuttleBusComing.activities.MapActivity;
import com.liupeng.shuttleBusComing.adapter.LineAdapter;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.liupeng.shuttleBusComing.utils.ApiService;
import com.liupeng.shuttleBusComing.utils.CoordinateGson;
import com.liupeng.shuttleBusComing.utils.Initialize;
import com.liupeng.shuttleBusComing.utils.Station;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.liupeng.shuttleBusComing.utils.Initialize.FETCH_TIME_INTERVAL;
import static com.liupeng.shuttleBusComing.utils.Initialize.FILENAME;
import static com.liupeng.shuttleBusComing.utils.Initialize.LINE_KEY;
import static com.liupeng.shuttleBusComing.utils.Initialize.WebApiURL;

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
    private Handler handler;
    private Runnable runnable;
    private int mSelectedBusLineNumber;

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

        // 读取存储数据
        SharedPreferences settings = getActivity().getSharedPreferences(FILENAME, MODE_PRIVATE);
        mSelectedBusLineNumber = settings.getInt(LINE_KEY, 1);

        recentStation.setText("日报大厦");
        recentLine.setText(mSelectedBusLineNumber + "号线");

        getDataTask();

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
            intentShowLine.putExtra("LineNumber", mSelectedBusLineNumber);
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

    private void getDataTask() {

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                getData();
                handler.postDelayed(this, FETCH_TIME_INTERVAL);
            }
        };

        handler.postDelayed(runnable, FETCH_TIME_INTERVAL);
    }

    public void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApiURL)
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Oservable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ApiService apiManager = retrofit.create(ApiService.class);//这里采用的是Java的动态代理模式

        Call<CoordinateGson> call = apiManager.getCoordinateData(mSelectedBusLineNumber);
        call.enqueue(new Callback<CoordinateGson>() {
            @Override
            public void onResponse(Call<CoordinateGson> call, Response<CoordinateGson> response) {
                //处理请求成功

                if (response.body().getData() != null) {

                    CoordinateGson.DataBean dataBean;
                    dataBean = response.body().getData();

                }
            }

            @Override
            public void onFailure(Call<CoordinateGson> call, Throwable t) {
                //处理请求失败
                Toast.makeText(getActivity(), mSelectedBusLineNumber + "号线班车位置获取错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
