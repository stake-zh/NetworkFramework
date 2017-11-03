package com.stake.networkframework.net;

import android.support.annotation.IntDef;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroAdapter {
    private final static int CONNECT_TIMEOUT_MILLIS = 10000;
    private final static int READ_TIMEOUT_MILLIS = 10000;


    private static final Object lock = new Object();
    private static final SparseArray<RetroApiService> serviceMap = new SparseArray<>();
    private static final SparseArray<BaseServiceInfo> serviceInfoMap = new SparseArray<>();

    @IntDef({ServiceType.GENERAL, ServiceType.OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceType {
        int GENERAL = 0;
        int OTHER = 1;
    }

    @IntDef({ServerEnvType.FORMAL, ServerEnvType.TEST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServerEnvType {
        int FORMAL = 0;
        int TEST = 1;
    }

    static {
        registerServiceInfo(ServiceType.GENERAL, new GeneralServiceInfo());
    }


    public static RetroApiService getService() {
        return getService(ServiceType.GENERAL);
    }

    public static void registerServiceInfo(@ServiceType int serviceType, BaseServiceInfo serviceInfo) {
        synchronized (lock) {
            serviceInfoMap.put(serviceType, serviceInfo);
        }
    }

    public static RetroApiService getService(@ServiceType int type) {
        synchronized (lock) {
            if (serviceMap.get(type) == null) {
                serviceMap.put(type, createService(type));
            }
            return serviceMap.get(type);
        }
    }

    private static RetroApiService createService(@ServiceType int type) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.SECONDS);
        clientBuilder.readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.SECONDS);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(httpLoggingInterceptor);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .client(clientBuilder.build())
                .baseUrl(getRequestUrl(type))
                .addConverterFactory(new StringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return retrofitBuilder.build().create(RetroApiService.class);

    }

    private static String getRequestUrl(@ServiceType int type) {
        return getServiceInfo(type).getBaseUrl(ServerEnvType.FORMAL);
    }

    public static BaseServiceInfo getServiceInfo(@ServiceType int serviceType) {
        synchronized (lock) {
            return serviceInfoMap.get(serviceType);
        }
    }


}
