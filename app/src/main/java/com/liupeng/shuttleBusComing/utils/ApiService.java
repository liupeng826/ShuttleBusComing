package com.liupeng.shuttleBusComing.utils;


import com.liupeng.shuttleBusComing.bean.Coordinate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by LiuPeng on 2016/11/12.
 */

public interface ApiService {
    //http://60.205.182.57/api/coordinate?roleId=6
    @GET("api/coordinate/")
    Call<CoordinateGson> getCoordinateData(@Query("roleId") long roleId);

	//http://60.205.182.57/api/coordinate/getLines
    @GET("api/coordinate/getLines")
    Call<BusLineGson> getBusLines();

    //http://60.205.182.57/api/coordinate/station?line=6
    @GET("api/coordinate/station")
    Observable<StationGson> getLineDetail(@Query("line") long roleId);

    //http://60.205.182.57/api/coordinate/station?line=6
    @GET("api/coordinate/station")
    Call<StationGson> getStations(@Query("line") long roleId);

    //http://60.205.182.57/api/coordinate
    @POST("api/coordinate")
    Call<Coordinate> updateCoordinate(@Body Coordinate coordinate);
}
