package com.stake.networkframework.net;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetroApiService {

    @FormUrlEncoded
    @POST("api/xxx")
    Observable<String> uploadInfo(@Field("data") String json);


}
