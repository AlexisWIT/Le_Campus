package com.uol.yt120.lecampus.utility;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapDrawer {

    public static final String MODE_WALK = "walking";
    public static final String MODE_DRIVE = "driving";
    public static final String MODE_BICYCLE = "bicycling";

    // make requeset for direction
    public String makeDirectionRequestURL (double startLat, double startLng, double endLat, double endLng, String mode){
        String directionRequestUrl = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" +
                Double.toString(startLat) +
                "," +
                Double.toString(startLng) +
                "&destination=" +
                Double.toString(endLat) +
                "," +
                Double.toString(endLng) +
                "&sensor=false&mode="+
                mode +
                "&alternatives=true" +
                "&key=AIzaSyBbHqZklkMWDr7ZxbHywJssjyBKpHaAV00";

        Log.i("[Map Drawer]", "directionRequestUrl: "+directionRequestUrl);
        return directionRequestUrl;
    }

    public List<LatLng> createDirectionPath (String googleDirectionResult) {
        List<LatLng> pathList = null;
        try {
            final JSONObject jsonObject = new JSONObject(googleDirectionResult);

            JSONArray routeArray = jsonObject.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolyline = routes.getJSONObject("overview_polyline");

            String encodedPolyline = overviewPolyline.getString("points");
            //Log.i("[MapDrawer]","Got decodedDirection: "+encodedPolyline);
            pathList = PolyUtil.decode(encodedPolyline);
            Log.i("[MapDrawer]","Got decodedDirection: "+pathList.toString());

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pathList;
    }

}
