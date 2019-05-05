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

    private static final String LE_CAMPUS_SERVER_URL = "";

    private static final String BASE_URL = "";

    /**
     * Send HTTP request to google to get Direction path
     * @param requestUrl
     * @return
     */
    public String processDirectionRequest(String requestUrl) {
        InputStream inS_GgleResult = null;
        String jsStr_GgleResult = "";

//        try {
//            HttpURLConnection urlConnection;
//            URL url = new URL(requestUrl);
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            inS_GgleResult = urlConnection.getInputStream();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // encapsulate JSON result from Google into String
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    inS_GgleResult, "iso-8859-1"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//
//            jsStr_GgleResult = sb.toString();
//            inS_GgleResult.close();
//        } catch (Exception e) {
//            Log.e("[HTTPHandler]", "Read Google Direction Result Error: " + e.toString());
//        }
        return jsStr_GgleResult;
    }


    public RestApiClient initRestApiClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            // Intercept the request and add header
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
