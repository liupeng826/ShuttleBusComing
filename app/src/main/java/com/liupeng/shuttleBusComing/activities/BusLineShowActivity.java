package com.liupeng.shuttleBusComing.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liupeng.shuttleBusComing.Interfaces.OnBusStationClickListener;
import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.liupeng.shuttleBusComing.bean.LocationMessage;
import com.liupeng.shuttleBusComing.bean.Station;
import com.liupeng.shuttleBusComing.utils.ApiService;
import com.liupeng.shuttleBusComing.utils.SPUtil;
import com.liupeng.shuttleBusComing.utils.StationGson;
import com.liupeng.shuttleBusComing.views.BusLineView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.liupeng.shuttleBusComing.utils.Initialize.FAVORITELINE_KEY;
import static com.liupeng.shuttleBusComing.utils.Initialize.WebApiURL;

/**
 * Created by liupeng on 2016/12/7.
 * E-mail: liupeng826@hotmail.com
 */

public class BusLineShowActivity extends AppCompatActivity implements
        View.OnClickListener, OnBusStationClickListener {

    private TextView busName;
    private TextView destination;
    private TextView timeAndPrice;
    private TextView belowTitle;
    private RelativeLayout waiting;
    private LinearLayout busLineMainPage;
    private RelativeLayout hidePage;
    private int busLineNo;
    private String destinationText;
    private String driverText;

    private LocationMessage locationMessage;
    private List<Station> busStation;

    @BindView(R.id.imgView_favorite)
    ImageView imgView_favorite;
    @BindView(R.id.imgBtn_favorite)
    ImageButton imgBtn_favorite;
    @BindView(R.id.imgBtn_relocate)
    ImageButton imgBtn_relocate;
    @BindView(R.id.imgBtn_reminder)
    ImageButton imgBtn_reminder;
    @BindView(R.id.bus_station)
    BusLineView busLineView;
//    @BindView(R.id.back)
//    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line_show);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    public void initData(){
        busStation = new ArrayList<>();
        busLineNo = getIntent().getIntExtra("LineNumber", 1);
        getStationData();
    }

    public void initView(){
        busName = $(R.id.bus_name);
        destination = $(R.id.destination);
        belowTitle = $(R.id.below_title);
        timeAndPrice = $(R.id.time_price);
        waiting = $(R.id.waiting);

        busLineMainPage = $(R.id.busline_message);
        hidePage = $(R.id.hide_bus);

//        busLineView = $(R.id.bus_station);
        Button back = $(R.id.back);
        back.setOnClickListener(this);
        imgBtn_favorite.setOnClickListener(this);
        imgBtn_relocate.setOnClickListener(this);
        imgBtn_reminder.setOnClickListener(this);
        busLineView.setOnStationClickListener(this,"else");
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T $(int resId){
        return (T) super.findViewById(resId);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.imgBtn_favorite:
                updateSharedPreference(FAVORITELINE_KEY + busLineNo, busStation.get(position).getStationName());
                break;
            case R.id.imgBtn_relocate:
                break;
            case R.id.imgBtn_reminder:
                break;
            default:
                break;
        }
    }

    public void showMessage(ErrorStatus status){
        if(!status.getIsError()){
        }
        busLineView.setOnClickEnable(true);
        belowTitle.setVisibility(View.VISIBLE);
        waiting.setVisibility(View.GONE);
    }

    public void hideMessage(){
        busLineView.setOnClickEnable(false);
        belowTitle.setVisibility(View.GONE);
        waiting.setVisibility(View.VISIBLE);
    }


    private int position;
    @Override
    public void OnClickItem(int position) {

        this.position = position;
        // 标记收藏按钮
        updateFavorite(FAVORITELINE_KEY + busLineNo, busStation.get(position).getStationName());
    }

    public void getStationData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApiURL)
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ApiService service = retrofit.create(ApiService.class);//这里采用的是Java的动态代理模式

        service.getStations(busLineNo)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<StationGson, List<Station>>() {
                    @Override
                    public List<Station> call(StationGson stationGson) { //
                        List<Station> stationList = new ArrayList<Station>();
                        for (Station dataBean : stationGson.getData()) {
//                            Station station = new Station();
//                            station.setStationId(dataBean.getStationId());
//                            station.setLine(dataBean.getLine());
//                            station.setStationName(dataBean.getStationName());
//                            station.setLat(dataBean.getLat());
//                            station.setLng(dataBean.getLng());
//                            station.setReachTime(dataBean.getReachTime());
//                            station.setBusNo(dataBean.getBusNo());
//                            station.setDriverName(dataBean.getDriverName());
//                            station.setUpdateTime(dataBean.getUpdateTime());
                            stationList.add(dataBean);
                        }
                        return stationList; // 返回类型
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Station>>() {
                    @Override
                    public void onNext(List<Station> stationList) {
                        // show list view
                        displayListView(stationList);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i("onCompleted", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });

    }

    public void displayListView(List<Station> stationList){

        String timeText;
        if (stationList.size() > 0) {

            busStation = stationList;
            String startTime = busStation.get(0).getReachTime();
            String endTime = "17:10:00";
            timeText = "首车:" +
                    startTime +
                    " " +
                    "末车:" +
                    endTime;

            destinationText = stationList.get(0).getStationName()
                    + "→"
                    + stationList.get(stationList.size() - 1).getStationName();
            driverText = " " + busStation.get(0).getDriverName() + " " + busStation.get(0).getDriverTel();
        } else {
            timeText = "无该公交信息!!";
        }

        busLineView.setBusStation(busStation);
        busName.setText(busLineNo + "号线" + driverText);
        destination.setText(destinationText);
        timeAndPrice.setText(timeText);
    }

    public void updateSharedPreference(String key, String value) {

        List<String> list = new ArrayList<>();
        String[] keyValue = SPUtil.getSharedPreference(key, this);
        if(keyValue != null) {
            list = new ArrayList<>(Arrays.asList(keyValue));
        }

        if (list.contains(value)) {
            list.remove(value);
            imgView_favorite.setImageResource(R.drawable.ic_favorite);
        } else {
            list.add(value);
            imgView_favorite.setImageResource(R.drawable.ic_favoriteon);
        }

        String[] strings = new String[list.size()];
        SPUtil.setSharedPreference(key, list.toArray(strings), this);

    }

    public void updateFavorite(String key, String value) {

        List<String> list = new ArrayList<>();
        String[] keyValue = SPUtil.getSharedPreference(key, this);
        if(keyValue != null) {
            list = new ArrayList<>(Arrays.asList(keyValue));
        }

        if(list.contains(value))
        {
            imgView_favorite.setImageResource(R.drawable.ic_favoriteon);
        }
        else{
            imgView_favorite.setImageResource(R.drawable.ic_favorite);
        }
    }
}
