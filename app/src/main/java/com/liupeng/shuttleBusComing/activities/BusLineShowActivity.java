package com.liupeng.shuttleBusComing.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.liupeng.shuttleBusComing.Interfaces.OnBusStationClickListener;
import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.liupeng.shuttleBusComing.bean.LocationMessage;
import com.liupeng.shuttleBusComing.utils.ApiService;
import com.liupeng.shuttleBusComing.utils.PoiAddressUtil;
import com.liupeng.shuttleBusComing.utils.Station;
import com.liupeng.shuttleBusComing.utils.StationGson;
import com.liupeng.shuttleBusComing.views.BusLineView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.liupeng.shuttleBusComing.utils.Initialize.WebApiURL;

public class BusLineShowActivity extends AppCompatActivity implements
        View.OnClickListener, OnBusStationClickListener {

    private TextView busName;
    private TextView destination;
    private TextView timeAndPrice;
    private TextView belowTitle;
    private Button back;
    private RelativeLayout waiting;

    private LinearLayout busLineMainPage;
    private LinearLayout switchBlank;
    private RelativeLayout switchLine;
    private RelativeLayout hidePage;
    private BusLineView busLineView;
    private String busLineName;
    private String destinationText;
    private String timeText;

    private LocationMessage locationMessage;
//    private Intent showSameStation;
    private PoiAddressUtil mPoiAddressUtil;
    private int mSelectedBusLineNumber;
    private List<BusStationItem> goStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line_show);
        initData();
        initView();
    }


    public void initData(){
        try {
            goStation = new ArrayList<>();

            mSelectedBusLineNumber = getIntent().getIntExtra("LineNumber", 0);
            busLineName = mSelectedBusLineNumber + "号线";
            getStationData();
        }catch(Exception e){
            Log.i("getSatation: ",e.getMessage());
        }
    }

    public void initView(){
        busName = $(R.id.bus_name);
        destination = $(R.id.destination);
        belowTitle = $(R.id.below_title);
        timeAndPrice = $(R.id.time_price);
        waiting = $(R.id.waiting);

        busLineMainPage = $(R.id.busline_message);
        hidePage = $(R.id.hide_bus);

        busLineView = $(R.id.bus_station);

        back = $(R.id.back);
        back.setOnClickListener(this);
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
            default:
                break;
        }
    }

    public void showMessage(ErrorStatus status){
        if(!status.getIsError()){
            switchLine.setEnabled(true);
        }
        busLineView.setOnClickEnable(true);
        belowTitle.setVisibility(View.VISIBLE);
        waiting.setVisibility(View.GONE);
        switchLine.setEnabled(true);
    }

    public void hideMessage(){
        busLineView.setOnClickEnable(false);
        belowTitle.setVisibility(View.GONE);
        waiting.setVisibility(View.VISIBLE);
        switchLine.setEnabled(false);
    }


    private int position;
    @Override
    public void OnClickItem(int position) {

        this.position = position;

//        hideMessage();

//        mPoiAddressUtil.setmLatLng(goStation.get(position).getLatLonPoint())
//                .setmCity(locationMessage.getCity())
//                .setKeyWord(goStation.get(position).getBusStationName());
//        mPoiAddressUtil.startPoiSearch();




//        showSameStation.putExtra("StationName",goStation.get(position).getBusStationName());
//        showSameStation.putExtra("Point",goStation.get(position).getLatLonPoint());
    }

    private String snippet;
    private String stationName;

    public void onGetPoiMessage(List<PoiItem> poiItems, String poiTitle, LatLonPoint addressPoint, ErrorStatus status) {
        if(status.getIsError()){
            belowTitle.setText("该站点暂无信息!");
            showMessage(status);
        }else{
            for(PoiItem item: poiItems){
                if(item.getTitle().equals(goStation.get(position).getBusStationName() + "(公交站)")){
                    this.snippet = item.getSnippet();
                    this.stationName = item.getTitle();
                }
            }
            if(null != snippet){
//                showSameStation.putExtra("Snippet",snippet);
                belowTitle.setText(stationName + "该站点公交线路:" + snippet);
                showMessage(status);
            }
        }
    }

    public void getStationData() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApiURL)
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ApiService service = retrofit.create(ApiService.class);//这里采用的是Java的动态代理模式

        service.getStations(mSelectedBusLineNumber)
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
            for (Station station : stationList) {
                BusStationItem bItem = new BusStationItem();
                bItem.setBusStationId(String.valueOf(station.getStationId()));
                bItem.setBusStationName(station.getStationName());
                goStation.add(bItem);
            }

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

        busLineView.setBusStation(goStation);
        busName.setText(busLineName);
        destination.setText(destinationText);
        timeAndPrice.setText(timeText);
    }
}
