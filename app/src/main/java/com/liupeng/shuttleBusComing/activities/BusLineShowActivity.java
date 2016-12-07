package com.liupeng.shuttleBusComing.activities;

import android.content.Context;
import android.content.SharedPreferences;
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
import static com.liupeng.shuttleBusComing.utils.Initialize.FILENAME;
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
    private Button back;
    private RelativeLayout waiting;

    private LinearLayout busLineMainPage;
    private RelativeLayout hidePage;
    //private BusLineView busLineView;
    private int busLineNo;
    private String destinationText;
    private String timeText;

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
        back = $(R.id.back);
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
                        for (StationGson.DataBean dataBean : stationGson.getData()) {
                            Station station = new Station();
                            station.setline(dataBean.getLine());
                            station.setStationId(dataBean.getStationId());
                            station.setStationName(dataBean.getStationName());
                            station.setLat(dataBean.getLat());
                            station.setLng(dataBean.getLng());
                            station.setUpdateTime(dataBean.getUpdateTime());
                            stationList.add(station);
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

        if (stationList.size() > 0) {

            busStation = stationList;
            String startTime = "7:00";
            String endTime = "17:10";
            timeText = "首车:" +
                    startTime +
                    "·" +
                    "末车:" +
                    endTime;

            destinationText = stationList.get(0).getStationName()
                    + "→"
                    + stationList.get(stationList.size() - 1).getStationName();
        } else {
            timeText = "无该公交信息!!";
        }

        busLineView.setBusStation(busStation);
        busName.setText(busLineNo + "号线");
        destination.setText(destinationText);
        timeAndPrice.setText(timeText);
    }



    public String[] getSharedPreference(String key) {
        String regularEx = "#";
        String[] str = null;
        SharedPreferences sp = getSharedPreferences(FILENAME, MODE_PRIVATE);
        String values;
        values = sp.getString(key, "");
        if (!values.equals("")) {
            str = values.split(regularEx);
        }

        return str;
    }

    public void setSharedPreference(String key, String[] values) {
        String regularEx = "#";
        String str = "";
        SharedPreferences sp = getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        if (values != null && values.length > 0) {
            for (String value : values) {
                str += value;
                str += regularEx;
            }
            SharedPreferences.Editor et = sp.edit();
            et.putString(key, str);
            et.apply();
        }
    }

    public void updateSharedPreference(String key, String value) {

        List<String> list = new ArrayList<>();
        String[] keyValue = getSharedPreference(key);
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
        setSharedPreference(key, list.toArray(strings));

    }

    public void updateFavorite(String key, String value) {

        List<String> list = new ArrayList<>();
        String[] keyValue = getSharedPreference(key);
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
