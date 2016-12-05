package com.liupeng.shuttleBusComing.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.liupeng.shuttleBusComing.bean.PoiMessage;
import com.liupeng.shuttleBusComing.utils.ApiService;
import com.liupeng.shuttleBusComing.utils.BusLineGson;
import com.liupeng.shuttleBusComing.utils.CoordinateGson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.liupeng.shuttleBusComing.utils.Initialize.FETCH_TIME_INTERVAL;

/**
 * Created by LiuPeng on 2016/12/4.
 */
public class BusLineService extends Service {

    private PoiMessage poiMessage;
    private String search;
    private Handler handler;
    private Runnable runnable;
    final String WebApiURL = "http://180.76.169.196:8000/";

    //存放线路信息的list
    private ArrayList<Map<String,Object>> busLineItems = new ArrayList<>();
    private Map<String,Object> busMap = null;

    private int MESSAGE_FLAG = 0;
    private ErrorStatus errorStatus;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(startId == 1){
            if(null != intent){
                errorStatus = intent.getParcelableExtra("ErrorStatus");
                if(errorStatus.getIsError()){
                    Toast.makeText(BusLineService.this, "班车线路获取失败!!!---BusLineService", Toast.LENGTH_SHORT).show();
                }else{
                    getBusLines();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private Intent busLineBroadcast;

    public void startSearch(){
        if(0 != MESSAGE_FLAG){
            searchLine(busLineItems.get(busLineItems.size()-MESSAGE_FLAG).get("LineNumber").toString());
            MESSAGE_FLAG --;
        }else{
            //发广播

            ArrayList<Map<String,BusLineItem>> utilList = new ArrayList<>();
            Map<String,BusLineItem> map = null;

            for(int count =0;count <busLineItems.size();count ++){
                map = new HashMap<>();
                for(int num = 0;num <2;num ++){
                    map.put((num == 0)?("GoBusLineMessage"):("BackBusLineMessage"),
                            (BusLineItem) busLineItems.get(count).get((num == 0)?("GoBusLineMessage"):("BackBusLineMessage")));
                }
                utilList.add(map);
            }




            String jsonData = new Gson().toJson(utilList, new TypeToken<ArrayList<Map<String,BusLineItem>>>(){}.getType());
            busLineBroadcast = new Intent("com.BusLineBroadcast");
            busLineBroadcast.putExtra("ErrorStatus",thisErrorStatus);
            busLineBroadcast.putExtra("test","接收到了");
            busLineBroadcast.putExtra("BusLine",jsonData);
            sendBroadcast(busLineBroadcast);
        }
    }




    private String cityCode = "";// 城市区号
    private int currentpage = 0;// 公交搜索当前页，第一页从0开始
    private BusLineResult busLineResult;// 公交线路搜索返回的结果
    private List<BusLineItem> lineItems = null;// 公交线路搜索返回的busline
    private BusLineQuery busLineQuery;// 公交线路查询的查询类

    private BusStationResult busStationResult;// 公交站点搜索返回的结果
    private List<BusStationItem> stationItems;// 公交站点搜索返回的busStation
    private BusStationQuery busStationQuery;// 公交站点查询的查询类

    private BusLineSearch busLineSearch;// 公交线路列表查询


    /**
     * 公交线路搜索
     */
    public void searchLine(String lineNumber) {
        cityCode = poiMessage.getCityCode();
        currentpage = 0;// 第一页默认从0开始
        busLineQuery = new BusLineQuery(lineNumber, BusLineQuery.SearchType.BY_LINE_NAME,
                cityCode);// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
        busLineQuery.setPageSize(10);// 设置每页返回多少条数据
        busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
        busLineSearch = new BusLineSearch(this, busLineQuery);// 设置条件
//        busLineSearch.setOnBusLineSearchListener(this);// 设置查询结果的监听
        busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
        if(null != busLineQuery){
//            Log.i("testAki49494949","no-nullllll");
        }else{
//            Log.i("testAki9393394","nullllllllllllll");
        }
    }

//    /**
//     * 公交站点查询结果回调
//     */
//
//    @Override
//    public void onBusStationSearched(BusStationResult result, int rCode) {
//        if (rCode == 1000) {
//            if (result != null && result.getPageCount() > 0
//                    && result.getBusStations() != null
//                    && result.getBusStations().size() > 0) {
//                busStationResult = result;
//                stationItems = result.getBusStations();
//            } else {
////                ToastUtil.show(BuslineActivity.this, R.string.no_result);
//            }
//        }  else {
////            ToastUtil.showerror(BuslineActivity.this, rCode);
//        }
////        busLineItems.notify();
//    }
//
//
//
//
//
//    private BusLineMessage busLineMessage;
    private ErrorStatus thisErrorStatus;
//    private boolean isFirst = true;
//    /**
//     * 公交线路查询结果回调
//     */
//    @Override
//    public void onBusLineSearched(BusLineResult result, int rCode) {
//        if(isFirst){
//            thisErrorStatus = new ErrorStatus();
//        }
//        if (rCode == 1000) {
//            if(isFirst){
//                thisErrorStatus.setReturnCode(rCode);
//            }
////            Log.i("testAki999","busLineSearch*******" + result.getBusLines().get(0).getTotalPrice());
//            if (result != null && result.getQuery() != null
//                    && result.getQuery().equals(busLineQuery)) {
//                if (result.getQuery().getCategory() == BusLineQuery.SearchType.BY_LINE_NAME) {
//                    if (result.getPageCount() > 0
//                            && result.getBusLines() != null
//                            && result.getBusLines().size() > 0) {
//                        busLineResult = result;
//                        lineItems = result.getBusLines();
////                        Map<String ,Object> map  = busLineItems.get(busLineItems.size()-(MESSAGE_FLAG+1)).getMap();
////                        map.put("BusLineMessage",lineItems);
////                        BusLineItem mBusLineItems = lineItems.get(busLineItems.size()-(MESSAGE_FLAG+1));
////                        busLineMessage = new BusLineMessage()
////                                .setBusLineId()
////                                .setBusLineName()
////                                .setBusLineType()
////                                .setBusLineType()
////                                .setBusCompany()
////                                .setFirstBusTime()
////                                .setLastBusTime()
////                                .setBasicPrice()
////                                .setBounds()
////                                .setDirectionsCoordinates()
////                                .setDistance()
////                                .setOriginatingStation()
////                                .setTerminalStation()
////                                .setTotalPrice();
//
//
////                        ParcelableMap mPmap = busLineItems.get(busLineItems.size()-(MESSAGE_FLAG+1));
////                        mPmap.put("BusLineMessage",lineItems);
//                        Map<String,Object> mPmap = busLineItems.get(busLineItems.size()-(MESSAGE_FLAG+1));
//                        mPmap.put("GoBusLineMessage",lineItems.get(0));
//                        mPmap.put("BackBusLineMessage",lineItems.get(1));
//
////                        Log.i("testAki48475757",MESSAGE_FLAG+1 + "*/*/*/*/" + busLineItems.get(busLineItems.size()-(MESSAGE_FLAG+1)).get("GoBusLineMessage").toString());
//
//                        for (BusLineItem line: lineItems) {
//                            Log.i("lineSearch", busLineItems.size()
//                                    + "\nBusLineName" +line.getBusLineName()
//                                    + "\nBusCompany" + line.getBusCompany()
//                                    + "\nBusLineId" + line.getBusLineId()
//                                    + "\nBusLineType" + line.getBusLineType()
//                                    + "\nTerminalStation" + line.getTerminalStation()
//                                    + "\nOriginatingStation"+  line.getOriginatingStation()
//                                    + "\nBusLineName" + line.getBusLineName()
//                                    + "\nBasicPrice" + line.getBasicPrice()
//                                    + "\nBounds" + line.getBounds()
////                                    + "\nDirectionsCoordinates" + line.getDirectionsCoordinates()
//                                    + "\nDistance" + line.getDistance()
//                                    + "\nFirstBusTime" + line.getFirstBusTime()
//                                    + "\nLastBusTime" + line.getLastBusTime()
//                                    + "\nTotalPrice" + line.getTotalPrice()
//                                    + "\nBusStations" + line.getBusStations());
//                        }
//
//                    }
//                } else if (result.getQuery().getCategory() == BusLineQuery.SearchType.BY_LINE_ID) {
//                    busLineResult = result;
//                    lineItems = busLineResult.getBusLines();
//                }
//                thisErrorStatus.setIsError(false).setReturnCode(rCode);
//            } else {
////                ToastUtil.show(BuslineActivity.this, R.string.no_result);
//                if(isFirst){
//                    thisErrorStatus.setIsError(true).setReturnCode(Initialize.NO_RESULT_CODE);
//                }
//            }
//        } else {
//            if(isFirst){
//                thisErrorStatus.setIsError(true).setReturnCode(Initialize.NO_RESULT_CODE);
//            }
////            ToastUtil.showerror(BuslineActivity.this, rCode);
//        }
//        Log.i("testAki89347","**********");
//        isFirst = (isFirst?false:false);
//        startSearch();
//
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

        Call<CoordinateGson> call = apiManager.getCoordinateData(1);
        call.enqueue(new Callback<CoordinateGson>() {
            @Override
            public void onResponse(Call<CoordinateGson> call, Response<CoordinateGson> response) {
                //处理请求成功

                if (response.body().getData() == null) {

                }else {

                    CoordinateGson.DataBean dataBean;
                    dataBean = response.body().getData();

                }
            }

            @Override
            public void onFailure(Call<CoordinateGson> call, Throwable t) {
                //处理请求失败
                Toast.makeText(getApplicationContext(), "获取班车位置出现错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getBusLines() {
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

        Call<BusLineGson> call = apiManager.getBusLines();
        call.enqueue(new Callback<BusLineGson>() {
            @Override
            public void onResponse(Call<BusLineGson> call, Response<BusLineGson> response) {
                //处理请求成功

                if (response.body().getData() != null) {
                    List<Integer> lineNumbers;
                    lineNumbers = response.body().getData();
                    for(Integer a: lineNumbers){
                        busMap = new HashMap<>();
                        busMap.put("LineNumber",a);
                        busLineItems.add(busMap);
                    }

                    if(null != busLineItems){
                        MESSAGE_FLAG = busLineItems.size();
                        startSearch();
                    }
                }
            }

            @Override
            public void onFailure(Call<BusLineGson> call, Throwable t) {
                //处理请求失败
                Toast.makeText(getApplicationContext(), "获取班车线路出现错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
