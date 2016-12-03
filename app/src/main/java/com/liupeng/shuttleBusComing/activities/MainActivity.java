package com.liupeng.shuttleBusComing.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;

import com.amap.api.services.busline.BusLineItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.liupeng.shuttleBusComing.bean.LocationMessage;
import com.liupeng.shuttleBusComing.fragment.HomeFragment;
import com.liupeng.shuttleBusComing.fragment.LineFragment;
import com.liupeng.shuttleBusComing.fragment.MeFragment;
import com.liupeng.shuttleBusComing.service.LocationService;
import com.liupeng.shuttleBusComing.utils.Initialize;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends CheckPermissionsActivity
        implements
            BottomNavigationBar.OnTabSelectedListener {
    private ArrayList<Fragment> fragments;

    final String URL = "http://180.76.169.196:8000/";
    final String mFileName = "BusLine";
    final String mUUIDKey = "UUID_KEY";
    private Intent startLocationServiceIntent;
//    private AMapLocationClient locationClient = null;
//    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Initialize.init(this);
        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_DEFAULT);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.home_homeb_ic, "首页").setActiveColorResource(R.color.blue_bg))
                .addItem(new BottomNavigationItem(R.drawable.navigation_bus, "路线").setActiveColorResource(R.color.blue_bg))
                .addItem(new BottomNavigationItem(R.drawable.navigation_me, "我").setActiveColorResource(R.color.blue_bg))
                .setFirstSelectedPosition(0)
                .initialise();

        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(this);

//        initLocation();
//        startLocation();
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, HomeFragment.newInstance("Home"));
        transaction.commit();
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(HomeFragment.newInstance("Home"));
        fragments.add(LineFragment.newInstance("line"));
        fragments.add(MeFragment.newInstance("me"));
        return fragments;
    }

    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                // 开启一个Fragment事务
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.replace(R.id.layFrame, fragment);
                } else {
                    ft.add(R.id.layFrame, fragment);
                }
                ft.commitAllowingStateLoss();
            }
        }

    }

    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 每次重新回到界面的时候注册广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.locationReceiver");
        registerReceiver(mainReceiver, filter);

        IntentFilter busLineFilter = new IntentFilter();
        busLineFilter.addAction("com.BusLineBroadcast");
        registerReceiver(busLineReceiver, busLineFilter);

        //先注册触发器再开启服务发送广播
        startLocationServiceIntent = new Intent(this,
                LocationService.class);
        startService(startLocationServiceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mainReceiver) {
            unregisterReceiver(mainReceiver);
        }
        if (null != busLineReceiver) {
            unregisterReceiver(busLineReceiver);
        }
    }

    /**
     //     * 初始化定位
     //     */
//    private void initLocation(){
//        //初始化client
//        locationClient = new AMapLocationClient(this.getApplicationContext());
//        //设置定位参数
//        locationClient.setLocationOption(getDefaultOption());
//        // 设置定位监听
//        locationClient.setLocationListener(locationListener);
//    }
//
//    /**
//     * 默认的定位参数
//     */
//    private AMapLocationClientOption getDefaultOption(){
//        AMapLocationClientOption mOption = new AMapLocationClientOption();
//        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
//        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
//        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//        mOption.setInterval(3000);//可选，设置定位间隔。默认为2秒
//        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
//        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
//        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
//        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
//
//        mOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
//        mOption.setMockEnable(true);//设置是否允许模拟位置,默认为false，不允许模拟位置
//
//        return mOption;
//    }
//
//    /**
//     * 开始定位
//     */
//    private void startLocation(){
//        // 设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 启动定位
//        locationClient.startLocation();
//    }
//
//    /**
//     * 定位监听
//     */
//    AMapLocationListener locationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation loc) {
//            if (null != loc && loc.getErrorCode() == 0 && loc.getAccuracy() < 80.0) {
//                // 上传定位信息
//                //postLocation(loc);
//
//            }
////            else {
////                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_SHORT).show();
////            }
//        }
//    };
//

    private boolean poiFlag = false;

    private LocationMessage locationMessage;
    private ErrorStatus locationErrorStatus;

    private BroadcastReceiver mainReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            locationErrorStatus = intent.getParcelableExtra("ErrorStatus");
            if (locationErrorStatus.getIsError()) {

            } else {
                locationMessage = intent.getExtras().getParcelable("Location");
                if (null == mGetLocationMessage) {
                    mGetLocationMessage.OnReceiveMessage(locationMessage, locationErrorStatus);
                }
                poiFlag = true;
            }
        }
    };

    private ArrayList<Map<String, BusLineItem>> busLineItems;
    private ErrorStatus busLineErrorStatus;

    private BroadcastReceiver busLineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            busLineErrorStatus = intent.getParcelableExtra("ErrorStatus");
            if (busLineErrorStatus.getIsError()) {
                Log.i("ERRORR", "busline服务获取错误");
            } else {
                String jsonData = intent.getStringExtra("BusLine");
                busLineItems = new Gson()
                        .fromJson(jsonData, new TypeToken<ArrayList<Map<String, BusLineItem>>>() {
                        }.getType());

                if (null != mGetBusLineMessage) {
                    mGetBusLineMessage.OnReceiveBusLineMessage(busLineItems, busLineErrorStatus);
                    Initialize.RECEIVED = true;
                }

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (null != locationClient) {
//            /**
//             * 如果AMapLocationClient是在当前Activity实例化的，
//             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//             */
//            locationClient.onDestroy();
//            locationClient = null;
//            locationOption = null;
//        }
    }

//    public void postLocation(AMapLocation myLocation) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(URL)
//                //增加返回值为String的支持
//                .addConverterFactory(ScalarsConverterFactory.create())
//                //增加返回值为Gson的支持(以实体类返回)
//                .addConverterFactory(GsonConverterFactory.create())
//                //增加返回值为Oservable<T>的支持
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
//
//        ApiService apiManager = retrofit.create(ApiService.class);//这里采用的是Java的动态代理模式
//        Coordinate coordinate = new Coordinate();
//        coordinate.setUuid(getUUID(this));
//        coordinate.setLat(String.valueOf(myLocation.getLatitude()));
//        coordinate.setLng(String.valueOf(myLocation.getLongitude()));
//
//        Call<Coordinate> call = apiManager.updateCoordinate(coordinate);
//        call.enqueue(new Callback<Coordinate>() {
//            @Override
//            public void onResponse(Call<Coordinate> call, Response<Coordinate> response) {
//                //处理请求成功
//            }
//
//            @Override
//            public void onFailure(Call<Coordinate> call, Throwable t) {
//                //处理请求失败
//            }
//        });
//    }

    public String getUUID(Context context) {
        String mUUID = "";

        try {
            // 读取存储数据
            SharedPreferences settings = getSharedPreferences(mFileName, MODE_PRIVATE);
            mUUID = settings.getString(mUUIDKey, "");

            if (mUUID.equals("")) {

                // IMEI
                TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
                String m_szImei = tm.getDeviceId();

                // The WLAN MAC Address string
                WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
                String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();

                String m_szLongID = m_szImei + m_szWLANMAC;
                // compute md5
                MessageDigest m = null;
                try {
                    m = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
                // get md5 bytes
                byte p_md5Data[] = m.digest();
                // create a hex string
                String m_szUniqueID = new String();
                for (int i = 0; i < p_md5Data.length; i++) {
                    int b = (0xFF & p_md5Data[i]);
                    // if it is a single digit, make sure it have 0 in front (proper padding)
                    if (b <= 0xF)
                        m_szUniqueID += "0";
                    // add number to string
                    m_szUniqueID += Integer.toHexString(b);
                }   // hex string to uppercase
                mUUID = m_szUniqueID.toUpperCase();


                // 存储UUID
                //步骤1：获取输入值
                //步骤2-1：创建一个SharedPreferences.Editor接口对象，lock表示要写入的XML文件名
                SharedPreferences.Editor editor = getSharedPreferences(mFileName, MODE_PRIVATE).edit();
                //步骤2-2：将获取过来的值放入文件
                editor.putString(mUUIDKey, mUUID);
                //步骤3：提交
                editor.apply();
            }
        }catch(Exception ignored){}

        return mUUID;
    }


    private OnGetLocationMessage mGetLocationMessage;

    public void setOnGetLocationMessage(OnGetLocationMessage onGetLocationMessage) {
        mGetLocationMessage = onGetLocationMessage;
    }

    private OnGetBusLineMessage mGetBusLineMessage;

    public void setOnGetBusLineMessage(OnGetBusLineMessage onGetBusLineMessage) {
        mGetBusLineMessage = onGetBusLineMessage;
    }

    //接口便于其他fragment即使获取locationMessage
    public interface OnGetLocationMessage {
        public void OnReceiveMessage(LocationMessage locationMessage, ErrorStatus errorStatus);
    }

    //busline获取接口
    public interface OnGetBusLineMessage {
        public void OnReceiveBusLineMessage(ArrayList<Map<String, BusLineItem>> busLineItems, ErrorStatus errorStatus);
    }
}
