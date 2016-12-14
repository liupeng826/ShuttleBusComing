package com.liupeng.shuttleBusComing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.liupeng.shuttleBusComing.R;
import com.liupeng.shuttleBusComing.activities.BusLineShowActivity;
import com.liupeng.shuttleBusComing.activities.MainActivity;
import com.liupeng.shuttleBusComing.activities.MainActivity.OnGetLocationMessage;
import com.liupeng.shuttleBusComing.activities.MapActivity;
import com.liupeng.shuttleBusComing.adapter.LineAdapter;
import com.liupeng.shuttleBusComing.bean.Coordinate;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;
import com.liupeng.shuttleBusComing.bean.LocationMessage;
import com.liupeng.shuttleBusComing.bean.Station;
import com.liupeng.shuttleBusComing.utils.ApiService;
import com.liupeng.shuttleBusComing.utils.CoordinateGson;
import com.liupeng.shuttleBusComing.utils.SPUtil;
import com.liupeng.shuttleBusComing.utils.StationGson;

import java.util.ArrayList;
import java.util.List;
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
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.liupeng.shuttleBusComing.utils.Initialize.FAVORITELINE_KEY;
import static com.liupeng.shuttleBusComing.utils.Initialize.FETCH_TIME_INTERVAL;
import static com.liupeng.shuttleBusComing.utils.Initialize.WebApiURL;

public class HomeFragment extends Fragment implements
		OnGetLocationMessage,
		OnRouteSearchListener,
		OnClickListener {

	private MainActivity mainActivity;
	private LinearLayout mainMessage;
	private RelativeLayout hideBg;
	private TextView favoriteStationTextView;
	private Integer favoriteStationId;
	private TextView recentLineTextView;
	private TextView nextStationTextView;
	private LinearLayout nowLineLinearLayout;
	private LinearLayout nowStationLinearLayout;
	private LineAdapter stationAdapter;
	private ArrayList<Map<String, BusLineItem>> busLineMessage;
	private boolean isLast = false;
	private Handler handler;
	private Runnable runnable;
	private int busLineNo;
	private Coordinate coordinate;
	private List<Station> stationList;
	private String nextStation;
	double distance;
	private RouteSearch routeSearch;

	@BindView(R.id.imgBtn_myFavorite)
	ImageButton imgBtn_favorite;
	@BindView(R.id.imgBtn_map)
	ImageButton imgBtn_map;
	@BindView(R.id.location)
	TextView locationText;
	@BindView(R.id.duration)
	TextView durationTextView;

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
		favoriteStationTextView = $(view, R.id.recent_stations);
		recentLineTextView = $(view, R.id.recent_line);
		nextStationTextView = $(view, R.id.next_station);
		nowLineLinearLayout = $(view, R.id.now_line);
		nowStationLinearLayout = $(view, R.id.now_station);
		imgBtn_favorite.setOnClickListener(this);
		imgBtn_map.setOnClickListener(this);
		mainActivity = (MainActivity) getActivity();
		mainActivity.setOnGetLocationMessage(this);
		nowLineLinearLayout.setOnClickListener(this);
		nowStationLinearLayout.setOnClickListener(this);

		routeSearch = new RouteSearch(getContext());
		routeSearch.setRouteSearchListener(this);
	}

	private ArrayList<Station> data = new ArrayList<Station>();

	public void initData() {

		//  显示收藏的站点
//	    List<SPMap> sPMapList = SPUtil.getAllFavoriteKey(getContext());
		String favoriteStation = SPUtil.getSharedPreference(FAVORITELINE_KEY, getContext());
		busLineNo = 0;
		favoriteStationId = 0;

		if (!"".equals(favoriteStation)) {
			String[] str = favoriteStation.split(SPUtil.regularEx);
			busLineNo = Integer.valueOf(str[0]);
			favoriteStationTextView.setText(str[2]);
			favoriteStationId = Integer.valueOf(str[1]);
			recentLineTextView.setText(busLineNo + "号线");
			getStationData();
			showMessage();
		} else {
			mainMessage.setVisibility(View.GONE);
			hideBg.setVisibility(View.GONE);
		}
	}

	private <T extends View> T $(View view, int resId) {
		return (T) view.findViewById(resId);
	}

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

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
			case R.id.imgBtn_myFavorite:
				//startActivity(new Intent(getActivity(), FavoriteActivity.class));
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
			if (name.equals(favoriteStationTextView.getText().toString())) {
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
		intentShowLine.putExtra("LineNumber", busLineNo);
		startActivity(intentShowLine);
//        }
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
					DisplayStationInformation(coordinate);
				}
			}

			@Override
			public void onFailure(Call<CoordinateGson> call, Throwable t) {
				//处理请求失败
				Toast.makeText(getActivity(), busLineNo + "号线班车位置获取错误", Toast.LENGTH_SHORT).show();
			}
		});
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

		service.getLineDetail(busLineNo)
				.subscribeOn(Schedulers.newThread())
				.map(new Func1<StationGson, List<Station>>() {
					@Override
					public List<Station> call(StationGson stationGson) { //
						stationList = new ArrayList<Station>();
						for (Station dataBean : stationGson.getData()) {
							stationList.add(dataBean);
						}
						return stationList; // 返回类型
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<List<Station>>() {
					@Override
					public void onNext(List<Station> stationList) {
						if (stationList.size() > 0) getLocationDataTask();
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

	private void DisplayStationInformation(Coordinate coordinate) {
		int recentStationId = coordinate.getStationId();
		LatLonPoint latLonFromPoint = new LatLonPoint(Double.valueOf(coordinate.getLat()), Double.valueOf(coordinate.getLng()));
		LatLonPoint latLonToPoint = null;
		List<LatLonPoint> passedByPoints = new ArrayList<>();
		for (int i = recentStationId; i < stationList.size(); i++) {
			if (favoriteStationTextView.getText().equals(stationList.get(i).getStationName())) {
				latLonToPoint = new LatLonPoint(Double.valueOf(stationList.get(i).getLat()), Double.valueOf(stationList.get(i).getLng()));
				break;
			} else {
				passedByPoints.add(new LatLonPoint(Double.valueOf(stationList.get(i).getLat()), Double.valueOf(stationList.get(i).getLng())));
			}
		}

		RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(latLonFromPoint, latLonToPoint);
		RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, passedByPoints, null, "");
		if (recentStationId < favoriteStationId) {
			routeSearch.calculateDriveRouteAsyn(query);
		} else {
			durationTextView.setText("到站/已过站");
		}

		if (recentStationId == stationList.size() - 1) {
			nextStation = String.valueOf(stationList.get(coordinate.getStationId()).getStationName());
			nextStationTextView.setText("终点站:" + nextStation);
		} else {
			nextStation = String.valueOf(stationList.get(coordinate.getStationId() + 1).getStationName());
			nextStationTextView.setText("下一站:" + nextStation);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		if (handler != null && runnable != null) {
			handler.removeCallbacks(runnable);
		}
	}

	@Override
	public void OnReceiveMessage(LocationMessage locationMessage, ErrorStatus errorStatus) {
//        locationText.setHint(locationMessage.getCity() + locationMessage.getDistrict() + locationMessage.getStreet());
		locationText.setText(locationMessage.getCity() + locationMessage.getDistrict() + locationMessage.getStreet());
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
}
