package com.uol.yt120.lecampus.utility;

import android.util.Log;

import com.uol.yt120.lecampus.model.restAPI.RestApiClient;
import com.uol.yt120.lecampus.model.restDomain.CrimeGeoFence;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpHandler {

    private static final String LE_CAMPUS_SERVER_URL = " http://192.168.137.1:8090";
    private static final String BASE_URL = "";


    public RestApiClient initRestApiClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            // Intercept the request and add header
                            // TODO Security feature
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request initialRequest = chain.request();
                                Request newRequest =
                                        initialRequest.newBuilder()
                                                .header("Timestamp", String.valueOf(System.currentTimeMillis()))
                                                .build();

                                return chain.proceed(newRequest);
                            }
                        })
                        .addInterceptor(loggingInterceptor)
                        .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(LE_CAMPUS_SERVER_URL+"/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

        RestApiClient restApiClient = retrofit.create(RestApiClient.class);
        return restApiClient;
    }

}
