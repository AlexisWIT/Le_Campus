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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> createDirectionPath (String googleDirectionResult) {
        List<LatLng> pathList = null;
        Map<String, Object> result = new HashMap<>();
        try {
            final JSONObject jsonObject = new JSONObject(googleDirectionResult);

            JSONArray routeArray = jsonObject.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolyline = routes.getJSONObject("overview_polyline");

            JSONArray infoArray = routes.getJSONArray("legs");
            JSONObject distance = infoArray.getJSONObject(0);
            String distanceString = distance.getString("text");
            int distanceInMetre = distance.getInt("value");

            JSONObject duration = infoArray.getJSONObject(1);
            String durationString = distance.getString("text");
            int durationInSec = distance.getInt("value");

            String encodedPolyline = overviewPolyline.getString("points");
            pathList = PolyUtil.decode(encodedPolyline);
            Log.i("[MapDrawer]","Got decoded Direction: "+pathList.toString());

            result.put("pathList", pathList);
            result.put("distanceString", distanceString);
            result.put("distanceInMetre", distanceInMetre);
            result.put("durationString", durationString);
            result.put("durationInSec", durationInSec);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        //return pathList;
        return result;
    }

}
