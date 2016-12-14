package com.liupeng.shuttleBusComing.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.view.WindowManager;

import com.amap.api.services.core.LatLonPoint;
import com.liupeng.shuttleBusComing.bean.LocationMessage;

/**
 * Created by liupeng on 2016/11/29.
 *
 * 初始化静态类,加载主布局前调用init()初始化
 * 1.获取屏幕宽高
 *
 */

public class Initialize {

    public static final int FETCH_TIME_INTERVAL = 7000;
    public static final String WebApiURL = "http://180.76.169.196:8000/";
    public static boolean IS_LOGGING = false;

    public static final String FILENAME = "ShuttleBusComing";
    public static final String LINE_KEY = "LINE_KEY";
    public static final String LINES_KEY = "LINES_KEY";
    public static final String UUID_KEY = "UUID_KEY";
    public static final String FAVORITELINE_KEY = "FAVORITELINE_KEY";
    public static final String FAVORITESTATION_KEY = "FAVORITESTATION_KEY";

    public static final int BUTTON = 1;
    public static final int CURSOR = 2;
    public static final String INDEX_PAGE = "首页";
    public static final String SECOND_PAGE = "路线";
    public static final String THIRD_PAGE = "个人中心";
    public static final int LOGIN_SUCCESS = 0;
    public static final int ERROR_USER = 1;
    public static final int ERROR_PASSWORD = 2;
    public static final int REGIST_REQUEST = 33654;
    public static final int REGIST_RESULT = 55469;
    //private static UserOpenHelper TEST;

    private static String ID = "";
    private static String PASSWORD = "";
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static int DRAW_BACK;
    public static int POINT_BACK;
    public static int LINE_GREEN;
    public static int POINT_RED;
    public static int BLUE_TEXT;
    public static int REQUEST_CODE_UP;
    public static int RESULT_CODE_UP;
    public static int REQUEST_CODE_BELOW;
    public static int RESULT_CODE_BELOW;
    public static int GET_POINT;
    public static int SEND_POINT;
    public static int NO_RESULT_CODE;
    public static String PLACE_NAME;
    public static String PLACE_POINT;
    public static String LOCATION;
    public static String PLACE;
    public static String UP;
    public static String BELOW;
    public static String POSITION;
    public static String ERROR_ADDRESS;
    public static String CANT_RESOLVE;
    public static String ORIGIN_POINT;
    public static String TARGET_POINT;
    public static String POI_TYPE_ADDRESS;
    public static String POI_TYPE_SCIENCE;
    public static String POI_TYPE_PUBLIC;
    public static String POI_TYPE_GOV;
    public static String POI_TYPE_TRANSPORT;
    public static String ERROR;
    public static String LOADING;
    public static boolean RECEIVED;
    public static LatLonPoint ERROR_POINT;

    public static LocationMessage LOCAL_MESSAGE;



    public static String BASE_LOCATION_RECEIVER;

    public static void init(Context context){
        //TEST = new UserOpenHelper(context);
        WindowManager mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point mPoint = new Point();
        mWindowManager.getDefaultDisplay().getSize(mPoint);

        SCREEN_WIDTH = mPoint.x;
        SCREEN_HEIGHT = mPoint.y;

        DRAW_BACK = Color.rgb(238,238,238);
        POINT_BACK = Color.rgb(170,170,170);
        LINE_GREEN = Color.rgb(79,184,13);
        POINT_RED = Color.RED;
        BLUE_TEXT = Color.parseColor("#3498db");

        LOCATION = "Location";
        PLACE = "Place";
        UP = "UP";
        BELOW = "BELOW";
        PLACE_NAME = "PlaceName";
        PLACE_POINT = "PlacePoint";
        POSITION = "Position";
        ERROR_ADDRESS = "北极";
        CANT_RESOLVE = "无法确定位置";
        ORIGIN_POINT = "设置起点";
        TARGET_POINT = "设置终点";
        POI_TYPE_ADDRESS = "地名地址信息";
        POI_TYPE_SCIENCE = "科教文化服务";
        POI_TYPE_PUBLIC = "公共设施";
        POI_TYPE_GOV = "政府机构及社会团体";
        POI_TYPE_TRANSPORT = "交通设施服务";
        ERROR = "暂无信息";
        LOADING = "正在获取信息...";


        REQUEST_CODE_UP = 1;
        RESULT_CODE_UP = 11;
        REQUEST_CODE_BELOW = 2;
        RESULT_CODE_BELOW = 22;

        GET_POINT = 3;
        SEND_POINT = 33;

        NO_RESULT_CODE = 66666;


        BASE_LOCATION_RECEIVER = "com.liupeng.shuttleBusComing.BaseLocationReceiver";


        RECEIVED = false;

        ERROR_POINT = new LatLonPoint(180,0);

        IcoUtil.init(context);
        StyleUtil.init(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.liupeng.shuttleBusComing.locationReceiver");
//        context.registerReceiver(localReceiver,filter);
    }

//    private static ErrorStatus mErrorStatus;
//    public static BroadcastReceiver localReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            mErrorStatus = intent.getParcelableExtra("ErrorStatus");
//            if(!mErrorStatus.getIsError()){
//                if(null != intent.getParcelableExtra("Location")){
//                    LOCAL_MESSAGE = intent.getParcelableExtra("Location");
//                    context.unregisterReceiver(localReceiver);
//                }else{
//                    //错误处理
//                }
//            }
//        }
//    };
}
