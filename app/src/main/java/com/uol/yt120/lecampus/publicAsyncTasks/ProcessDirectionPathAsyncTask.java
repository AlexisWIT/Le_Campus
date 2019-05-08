package com.uol.yt120.lecampus.publicAsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.uol.yt120.lecampus.utility.HttpHandler;
import com.uol.yt120.lecampus.utility.MapDrawer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ProcessDirectionPathAsyncTask extends AsyncTask<Void, Integer, Map<String, Object>> {

    private String requestUrl;
    private LatLng startLatlng;
    private LatLng endLatlng;
    MapDrawer mapDrawer = new MapDrawer();
    HttpHandler httpHandler = new HttpHandler();

    public interface Response {
        void startProcessPath();
        void showPath(Map<String, Object> path, LatLng startLatlng, LatLng endLatlng);
    }

    public ProcessDirectionPathAsyncTask(String requestUrl, LatLng startLatlng, LatLng endLatlng, Response response) {
        this.requestUrl = requestUrl;
        this.response = response;
        this.startLatlng = startLatlng;
        this.endLatlng = endLatlng;
    }

    @Override
    protected void onPreExecute() {

        response.startProcessPath();
        super.onPreExecute();
    }

    public Response response = null;

    @Override
    protected Map<String, Object> doInBackground(Void... voids) {
//        String resultRawData = httpHandler.processDirectionRequest(requestUrl);

        InputStream inS_GgleResult = null;
        String jsStr_GgleResult = "";

        try {
            HttpURLConnection urlConnection;
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            inS_GgleResult = urlConnection.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // encapsulate JSON result from Google into String
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inS_GgleResult, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            jsStr_GgleResult = sb.toString();
            inS_GgleResult.close();
        } catch (Exception e) {
            Log.e("[HTTPHandler]", "Read Google Direction Result Error: " + e.toString());
        }

//        if(jsStr_GgleResult.length() > 3000) {
//            for(int i=0;i<jsStr_GgleResult.length();i+=3000){
//                if(i+3000<jsStr_GgleResult.length())
//                    Log.i("[DirectionAsync] -"+i,"Got direction: "+jsStr_GgleResult.substring(i, i+3000));
//                else
//                    Log.i("[DirectionAsync] -"+i,jsStr_GgleResult.substring(i, jsStr_GgleResult.length()));
//            }
//        } else {
//            Log.i("[DirectionAsync]","Got direction: "+jsStr_GgleResult);
//        }

        //List<LatLng> result = mapDrawer.createDirectionPath(jsStr_GgleResult);
        Map<String, Object> result = mapDrawer.createDirectionPath(jsStr_GgleResult);
        return result;
    }

//    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        super.onProgressUpdate(progress);
//        response.startProcessPath();
//    }

    @Override
    protected void onPostExecute(Map<String, Object> pathResult) {
        //showDialog("Downloaded " + result + " items");
        response.showPath(pathResult, startLatlng, endLatlng);
    }
}
