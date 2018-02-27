package com.stake.networkframework.net;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetroApiService {


    @GET("api/signinfo")
    Observable<String> getInfo(@Query("service_token")String token);
}
