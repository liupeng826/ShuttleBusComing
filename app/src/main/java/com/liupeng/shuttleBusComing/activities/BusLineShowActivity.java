package com.liupeng.shuttleBusComing.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.liupeng.shuttleBusComing.BuildConfig;
import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.bean.BusLineItem;
import com.liupeng.shuttleBusComing.bean.Coordinate;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.liupeng.shuttleBusComing.bean.LocationMessage;
import com.liupeng.shuttleBusComing.bean.Station;
import com.liupeng.shuttleBusComing.utils.ApiService;
import com.liupeng.shuttleBusComing.utils.CoordinateGson;
import com.liupeng.shuttleBusComing.utils.SPUtil;
import com.liupeng.shuttleBusComing.utils.StationGson;
import com.liupeng.shuttleBusComing.views.BusLineView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.liupeng.shuttleBusComing.utils.Initialize.FAVORITELINE_KEY;
import static com.liupeng.shuttleBusComing.utils.Initialize.FETCH_TIME_INTERVAL;
import static com.liupeng.shuttleBusComing.utils.Initialize.WebApiURL;

/**
 * Created by liupeng on 2016/12/7.
 * E-mail: liupeng826@hotmail.com
 */

public class BusLineShowActivity extends AppCompatActivity implements
		View.OnClickListener, BusLineView.OnBusStationClickListener, OnRouteSearchListener {

	private TextView busName;
	private TextView destination;
	private TextView timeAndPrice;
	private LinearLayout busLineMainPage;
	private RelativeLayout hidePage;
	private BusLineView mBusLineView;
	private int busLineNo;
	private String destinationText;
	private String driverText;
	private Handler handler;
	private Runnable runnable;
	private LocationMessage locationMessage;
	private List<Station> busStation;
	private List<BusLineItem> mBusLineItems;
	private Coordinate coordinate;
	private RouteSearch routeSearch;
	private int position;
	private boolean isFirst = true;

	@BindView(R.id.imgView_favorite)
	ImageView imgView_favorite;
	@BindView(R.id.imgBtn_favorite)
	ImageButton imgBtn_favorite;
	@BindView(R.id.imgBtn_relocate)
	ImageButton imgBtn_relocate;
	@BindView(R.id.imgBtn_reminder)
	ImageButton imgBtn_reminder;
	@BindView(R.id.duration)
	TextView durationTextView;
//    @BindView(R.id.back)
//    Button back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_line_show);
		ButterKnife.bind(this);
		initView();
		initData();
	}

	public void initData() {
		busStation = new ArrayList<>();
		busLineNo = getIntent().getIntExtra("LineNumber", 1);
		getStationData();
		getLocationData();
	}

	public void initView() {
		busName = $(R.id.bus_name);
		destination = $(R.id.destination);
		timeAndPrice = $(R.id.time_price);
		busLineMainPage = $(R.id.busline_message);
		hidePage = $(R.id.hide_bus);
		mBusLineView = $(R.id.bus_station);
		Button back = $(R.id.back);
		back.setOnClickListener(this);
		imgBtn_favorite.setOnClickListener(this);
		imgBtn_relocate.setOnClickListener(this);
		imgBtn_reminder.setOnClickListener(this);
		mBusLineView.setOnBusStationClickListener(this);
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
	}

	@SuppressWarnings("unchecked")
	private <T extends View> T $(int resId) {
		return (T) super.findViewById(resId);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.imgBtn_favorite:
				//updateSharedPreference(FAVORITELINE_KEY + busLineNo, busStation.get(position).getStationName());
				updateSharedPreference(FAVORITELINE_KEY, busLineNo + "#" + busStation.get(position).getStationId() + "#" + busStation.get(position).getStationName());
				break;
			case R.id.imgBtn_relocate:
				break;
			case R.id.imgBtn_reminder:
				break;
			default:
				break;
		}
	}

	public void showMessage(ErrorStatus status) {
		if (!status.getIsError()) {
		}
		//mBusLineView.setOnClickEnable(true);
	}

	public void hideMessage() {
		//mBusLineView.setOnClickEnable(false);
	}


//    @Override
//    public void OnClickItem(int position) {
//
//        this.position = position;
//	    durationTextView.setText(R.string.calculating);
//        // 标记收藏按钮
//        //updateFavorite(FAVORITELINE_KEY + busLineNo, busStation.get(position).getStationName());
//	    updateFavorite(FAVORITELINE_KEY, busLineNo + "#" + busStation.get(position).getStationId() + "#" + busStation.get(position).getStationName());
//
//	    getLocationDataTask();
//    }

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

//		service.getLineDetail(busLineNo)
//				.subscribeOn(Schedulers.newThread())
//				.map(new Func1<StationGson, List<Station>>() {
//					@Override
//					public List<Station> call(StationGson stationGson) { //
//						List<Station> stationList = new ArrayList<Station>();
//						mBusLineItems = new ArrayList<BusLineItem>();
//						for (Station dataBean : stationGson.getData()) {
//							BusLineItem item = new BusLineItem();
//							item.name = dataBean.getStationName();
//							item.stationId = dataBean.getStationId();
//							mBusLineItems.add(item);
//							stationList.add(dataBean);
//						}
//						return stationList; // 返回类型
//					}
//				})
//				.observeOn(AndroidSchedulers.mainThread())
//				.subscribe(new Subscriber<List<Station>>() {
//					@Override
//					public void onNext(List<Station> stationList) {
//						// show list view
//						displayListView(stationList);
//					}
//
//					@Override
//					public void onCompleted() {
//						Log.i("onCompleted", "onCompleted");
//					}
//
//					@Override
//					public void onError(Throwable e) {
//					}
//				});
//
//	}

		Call<StationGson> call = service.getStations(busLineNo);
		call.enqueue(new Callback<StationGson>() {
			@Override
			public void onResponse(Call<StationGson> call, Response<StationGson> response) {
				//处理请求成功
				if (response.body().getData() != null) {
					List<Station> stationList = new ArrayList<Station>();
					mBusLineItems = new ArrayList<BusLineItem>();
					for (Station dataBean : response.body().getData()) {
						BusLineItem item = new BusLineItem();
						item.name = dataBean.getStationName();
						item.stationId = dataBean.getStationId();
						mBusLineItems.add(item);
						stationList.add(dataBean);
					}
					// show list view
					displayListView(stationList);
				}
			}

			@Override
			public void onFailure(Call<StationGson> call, Throwable t) {
				//处理请求失败
			}
		});
	}

	public void displayListView(List<Station> stationList) {

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

		mBusLineView.setBusLineData(mBusLineItems);
		mBusLineView.requestLayout();

		busName.setText(busLineNo + "号线" + driverText);
		destination.setText(destinationText);
		timeAndPrice.setText(timeText);
	}

	public void updateSharedPreference(String key, String value) {
		if (SPUtil.getSharedPreference(key, this).equals(value)) {
			imgView_favorite.setImageResource(R.drawable.ic_favorite);
			SPUtil.removeSharedPreference(key, this);
		} else {
			imgView_favorite.setImageResource(R.drawable.ic_favoriteon);
			SPUtil.setSharedPreference(key, value, this);
		}
	}

	public void updateFavorite(String key, String value) {

		String keyValue = SPUtil.getSharedPreference(key, this);

		if (keyValue.equals(value)) {
			imgView_favorite.setImageResource(R.drawable.ic_favoriteon);
		} else {
			imgView_favorite.setImageResource(R.drawable.ic_favorite);
		}
	}

	private void getLocationDataTask() {

		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				getLocationData();
				handler.postDelayed(this, FETCH_TIME_INTERVAL);
			}
		};

		handler.post(runnable);
	}

	public void getLocationData() {
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

		Call<CoordinateGson> call = apiManager.getCoordinateData(busLineNo);
		call.enqueue(new Callback<CoordinateGson>() {
			@Override
			public void onResponse(Call<CoordinateGson> call, Response<CoordinateGson> response) {
				//处理请求成功
				if (response.body().getData() != null) {
					coordinate = response.body().getData();
					if(mBusLineItems != null && mBusLineItems.size() > coordinate.getStationId()) {
						mBusLineItems.get(coordinate.getStationId()).busPosition = 0;
						if (!isFirst) {
							DisplayStationInformation(coordinate);
						}
						mBusLineView.setBusLineData(mBusLineItems);
						mBusLineView.requestLayout();
						isFirst = false;
					}
				}
			}

			@Override
			public void onFailure(Call<CoordinateGson> call, Throwable t) {
				//处理请求失败
				Toast.makeText(getApplicationContext(), busLineNo + "号线班车位置获取错误", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void DisplayStationInformation(Coordinate coordinate) {
		Integer recentStationId = coordinate.getStationId();
		LatLonPoint latLonFromPoint = new LatLonPoint(Double.valueOf(coordinate.getLat()), Double.valueOf(coordinate.getLng()));
		LatLonPoint latLonToPoint = new LatLonPoint(Double.valueOf(busStation.get(position).getLat()), Double.valueOf(busStation.get(position).getLng()));
		List<LatLonPoint> passedByPoints = new ArrayList<>();
		for (int i = recentStationId + 1; i < position; i++) {
			passedByPoints.add(new LatLonPoint(Double.valueOf(busStation.get(i).getLat()), Double.valueOf(busStation.get(i).getLng())));
		}

		RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(latLonFromPoint, latLonToPoint);
		RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, passedByPoints, null, "");
		if (recentStationId < position) {
			routeSearch.calculateDriveRouteAsyn(query);
		} else {
			durationTextView.setText("到站/已过站");
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		if (rCode == 1000 && result != null && result.getPaths() != null) {
			if (result.getPaths().size() > 0) {
				Long duration = result.getPaths().get(0).getDuration();
				durationTextView.setText((int) (duration / 60) + "分");
			}
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

	}

	@Override
	public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handler != null && runnable != null) {
			handler.removeCallbacks(runnable);
		}
	}

	@Override
	public void onBusStationClick(View view, BusLineItem item) {
		if (BuildConfig.DEBUG) {
			Log.d("may", "name: " + item.name);
		}

		this.position = item.stationId;
		durationTextView.setText(R.string.calculating);
		// 标记收藏按钮
		updateFavorite(FAVORITELINE_KEY, busLineNo + "#" + busStation.get(position).getStationId() + "#" + busStation.get(position).getStationName());

		getLocationDataTask();
	}
//    public void updateSharedPreference(String key, String value) {
//
//        List<String> list = new ArrayList<>();
//        String[] keyValue = SPUtil.getSharedPreference(key, this);
//        if(keyValue != null) {
//            list = new ArrayList<>(Arrays.asList(keyValue));
//        }
//
//        if (list.contains(value)) {
//            list.remove(value);
//            imgView_favorite.setImageResource(R.drawable.ic_favorite);
//        } else {
//            list.add(value);
//            imgView_favorite.setImageResource(R.drawable.ic_favoriteon);
//        }
//
//        String[] strings = new String[list.size()];
//        SPUtil.setSharedPreference(key, list.toArray(strings), this);
//
//    }
//
//    public void updateFavorite(String key, String value) {
//
//        List<String> list = new ArrayList<>();
//        String[] keyValue = SPUtil.getSharedPreference(key, this);
//        if(keyValue != null) {
//            list = new ArrayList<>(Arrays.asList(keyValue));
//        }
//
//        if(list.contains(value))
//        {
//            imgView_favorite.setImageResource(R.drawable.ic_favoriteon);
//        }
//        else{
//            imgView_favorite.setImageResource(R.drawable.ic_favorite);
//        }
//    }
}
