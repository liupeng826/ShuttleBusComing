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
    @GET("api/coordinate/")
    Call<CoordinateGson> getCoordinateData(@Query("roleId") long roleId);

    @GET("api/coordinate/getLines")
    Call<BusLineGson> getBusLines();

    //http://180.76.169.196:8000/api/coordinate/station?line=6
    @GET("api/coordinate/station")
    Observable<StationGson> getLineDetail(@Query("line") long roleId);

    //http://180.76.169.196:8000/api/coordinate/station?line=6
    @GET("api/coordinate/station")
    Call<StationGson> getStations(@Query("line") long roleId);

    @POST("api/coordinate")
    Call<Coordinate> updateCoordinate(@Body Coordinate coordinate);
}
