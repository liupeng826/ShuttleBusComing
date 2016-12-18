package com.liupeng.shuttleBusComing.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.liupeng.shuttleBusComing.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MapFragment extends SupportMapFragment implements LocationSource,
        AMapLocationListener {

    private MapView mMapView;
    private AMap mAMap;
    private AMapLocation mMyLocationPoint;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption = null;
    private OnLocationChangedListener mLocationChangeListener = null;

    private Handler handler;
    private Runnable runnable;
    private Marker mMarker;
    private Marker mLocationMarker;
    private List<LatLng> mLocations;
    private String mSelectedBusLine;
    private int mSelectedBusLineNumber;
    private boolean mSelectedBusLineChanged = false;
    private LatLng currentlocation;
    private CheckBox mDriver;
    private boolean mDriverChecked = false;
    private String mUUID;

    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private static final double DISTANCE = 0.0001;
    private static MapFragment fragment=null;
    public static final int POSITION=0;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (mapLayout == null) {
//            mapLayout = inflater.inflate(R.layout.fragment, container, false);
//            mMapView = (MapView) mapLayout.findViewById(R.id.map);
//            mMapView.onCreate(savedInstanceState);
//            if (mAMap == null) {
//                mAMap = mMapView.getMap();
//            }
//        } else {
//            if (mapLayout.getParent() != null) {
//                ((ViewGroup) mapLayout.getParent()).removeView(mapLayout);
//            }
//        }
//        return mapLayout;
//        if(view == null) {
            view = inflater.inflate(R.layout.fragment, container, false);
            mMapView = (MapView) view.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            initMap();
            //setUpMap();
//        }
//        else
//        {
//            if (view.getParent() != null) {
//                ((ViewGroup) view.getParent()).removeView(view);
//            }
//
//        }
        return view;
    }

    /**
     * 初始化AMap对象
     */
    private void initMap() {

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        Log.i("sys", "mf onResume");
        super.onResume();
        mMapView.onResume();
        if(mLocationClient!=null){
            mLocationClient.startLocation();
        }
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mMapView.onPause();
        if(mLocationClient!=null){
            mLocationClient.stopLocation();
        }
        deactivate();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        Log.i("sys", "mf onDestroy");
        super.onDestroy();
        //mMapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mLocationClient!=null){
            mLocationClient.onDestroy();
        }
        mMapView.onDestroy();
    }

    @Override
    public void deactivate() {
        mLocationChangeListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mLocationChangeListener = listener;
        if (mLocationClient == null) {
            startLocation();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setLocationSource(this);
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        mAMap.setMyLocationEnabled(true);
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAMap.getUiSettings().setLogoPosition(
                AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// logo位置
        mAMap.getUiSettings().setScaleControlsEnabled(true);// 标尺开关
        mAMap.getUiSettings().setCompassEnabled(true);// 指南针开关
    }

    public static MapFragment newInstance() {
        if(fragment==null){
            synchronized(MapFragment.class){
                if(fragment==null){
                    fragment=new MapFragment();
                }
            }
        }
        return fragment;
    }

    private void startLocation() {
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(getActivity());
            //设置定位回调监听
            mLocationClient.setLocationListener(this);

            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(true);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(3 * 1000);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //启动定位
            mLocationClient.startLocation();

        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                // 定位成功回调信息，设置相关消息
                amapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();// 获取经度
                amapLocation.getLongitude();// 获取纬度
                amapLocation.getAccuracy();// 获取精度信息
                SimpleDateFormat df = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);// 定位时间
                amapLocation.getAddress();// 地址，如果option中设置isNeedAddress为false，则没有此结果
                amapLocation.getCountry();// 国家信息
                amapLocation.getProvince();// 省信息
                amapLocation.getCity();// 城市信息
                amapLocation.getDistrict();// 城区信息
                amapLocation.getRoad();// 街道信息
                amapLocation.getCityCode();// 城市编码
                amapLocation.getAdCode();// 地区编码
                mMyLocationPoint = amapLocation;
                mLocationChangeListener.onLocationChanged(mMyLocationPoint);
            } else {
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError",
                        "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
            }
        }
    }
}
