package com.stake.networkframework.net;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetroAdapter {
    private final static int CONNECT_TIMEOUT_MILLIS = 10000;
    private final static int READ_TIMEOUT_MILLIS = 10000;


    private static final Object lock = new Object();
    private static final HashMap<Integer, HashMap<Class, Object>> serviceMap = new HashMap<>();
    private static final HashMap<Integer, ServiceInfo> serviceInfoMap = new HashMap<>();


    @IntDef({ServiceType.GENERAL, ServiceType.OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceType {
        int GENERAL = 0;
        int OTHER = 1;
    }

    static {
        registerServiceInfo(ServiceType.GENERAL, new GeneralServiceInfo());
        registerServiceInfo(ServiceType.OTHER, new GeneralServiceInfo());
    }


    public static void registerServiceInfo(@ServiceType int serviceType, ServiceInfo serviceInfo) {
        synchronized (lock) {
            serviceInfoMap.put(serviceType, serviceInfo);
        }
    }


    private static <T> T createService(@ServiceType int type, Class<T> apiClass) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.SECONDS);
        clientBuilder.readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.SECONDS);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(httpLoggingInterceptor);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .client(clientBuilder.build())
                .baseUrl(getRequestUrl(type))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return retrofitBuilder.build().create(apiClass);

    }

    private static String getRequestUrl(@ServiceType int type) {
        return getServiceInfo(type).getBaseUrl();
    }

    public static ServiceInfo getServiceInfo(@ServiceType int serviceType) {
        synchronized (lock) {
            return serviceInfoMap.get(serviceType);
        }
    }

    public static <T> T getService(Class<T> apiClass) {
        return getService(ServiceType.GENERAL, apiClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(@ServiceType int type, Class<T> apiClass) {
        synchronized (lock) {
            putIfAbsent(serviceMap, type, HashMap::new);
            putIfAbsent(serviceMap.get(type), apiClass, () -> RetroAdapter.createService(type, apiClass));
            return (T) serviceMap.get(type).get(apiClass);
        }
    }


    private static <K, V> void putIfAbsent(HashMap<K, V> map, K key, Callable<V> create) {
        V v = map.get(key);
        if (v == null) {
            try {
                map.put(key, create.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
