package com.uol.yt120.lecampus.publicAsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.uol.yt120.lecampus.utility.MapDrawer;

import java.util.List;

public class ProcessDirectionPathAsyncTask extends AsyncTask<Void, Integer, List<LatLng>> {

    private String requestUrl;
    MapDrawer mapDrawer = new MapDrawer();

    public interface Response {
        void startProcessPath();
        void showPath(List<LatLng> path);
    }

    public ProcessDirectionPathAsyncTask(String requestUrl, Response response) {
        this.requestUrl = requestUrl;
        this.response = response;
    }

    @Override
    protected void onPreExecute() {
        Log.i("DirectionAsync", "Start getting direction");
        response.startProcessPath();
        super.onPreExecute();
    }

    public Response response = null;

    @Override
    protected List<LatLng> doInBackground(Void... voids) {

        String resultRawData = mapDrawer.processDirectionRequest(requestUrl);
        List<LatLng> result = mapDrawer.createDirectionPath(resultRawData);
        return result;
    }

//    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        super.onProgressUpdate(progress);
//        response.startProcessPath();
//    }

    @Override
    protected void onPostExecute(List<LatLng> pathResult) {
        //showDialog("Downloaded " + result + " items");
        response.showPath(pathResult);
    }
}
