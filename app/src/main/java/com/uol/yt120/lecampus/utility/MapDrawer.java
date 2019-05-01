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

    public static final String MODE_WALK = "direction_mode_walk";
    public static final String MODE_DRIVE = "direction_mode_drive";

    static InputStream inputStream = null;
    static JSONObject jsonObject = null;
    static String jsonString = "";


    // make requeset for direction
    public String makeDirectionRequestURL (double startLat, double startLng, double endLat, double endLng, String mode){
        String directionRequestUrl = "http://maps.googleapis.com/maps/api/directions/json" +
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
        return directionRequestUrl;
    }

    public String processDirectionRequest(String requestUrl) {

        try {

            HttpURLConnection urlConnection;
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            inputStream = urlConnection.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // encapsulate JSON result from Google into String
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            jsonString = sb.toString();
            inputStream.close();
        } catch (Exception e) {
            Log.e("[Map Drawer]", "Error converting result " + e.toString());
        }
        return jsonString;
    }

    public List<LatLng> createDirectionPath (String googleDirectionResult) {
        List<LatLng> pathList = null;
        try {
            final JSONObject jsonObject = new JSONObject(googleDirectionResult);

            JSONArray routeArray = jsonObject.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolyline = routes.getJSONObject("overview_polyline");

            String encodedPolyline = overviewPolyline.getString("points");
            pathList = PolyUtil.decode(encodedPolyline);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pathList;
    }

    /**
     *  Title: Decode Google direction result
     *    Author: Zeeshan Mirza
     *    Date: 5 Feb 2013
     *    Code version: 1.0
     *    Availability: https://stackoverflow.com/a/14702636/11338406
     *
     * @param encodedPolyline encodedPolyline result from Google
     * @return
     */
//    private List<LatLng> decodePolyline(String encodedPolyline) {
//
//        //PolyUtil.encode()
//        //PolyUtil.decode()
//
//        List<LatLng> polyList = new ArrayList<LatLng>();
//        int index = 0, len = encodedPolyline.length();
//        int lat = 0, lng = 0;
//
//        while (index < len) {
//            int b, shift = 0, result = 0;
//            do {
//                b = encodedPolyline.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lat += dlat;
//
//            shift = 0;
//            result = 0;
//            do {
//                b = encodedPolyline.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lng += dlng;
//
//            LatLng p = new LatLng( (((double) lat / 1E5)),
//                    (((double) lng / 1E5) ));
//            polyList.add(p);
//        }
//
//        return polyList;
//    }
}
