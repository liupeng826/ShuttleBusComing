package com.liupeng.shuttleBusComing.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.liupeng.shuttleBusComing.bean.ErrorStatus;

public class RouteService extends Service implements RouteSearch.OnRouteSearchListener{
	public RouteService() {
	}

	private ErrorStatus errorStatus;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

			if(null != intent){
				errorStatus = intent.getParcelableExtra("ErrorStatus");
				if(errorStatus.getIsError()){
					Toast.makeText(RouteService.this, "驾车线路获取失败!!!---RouteService", Toast.LENGTH_SHORT).show();
				}else{
					//getBusLines();
				}
			}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

	}

	@Override
	public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

	}
}
