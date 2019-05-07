package com.uol.yt120.lecampus.utility;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class LocationDataProcessor {

    public Location encapStringToLocation(String locationInJSONString) {
        Location location = new Location("");
        try {
            JSONObject locationJSONObject = new JSONObject(locationInJSONString);

            double lat = Double.valueOf(locationJSONObject.getString("lat"));
            double lon = Double.valueOf(locationJSONObject.getString("lon"));
            double alt = Double.valueOf(locationJSONObject.getString("alt"));
            float acc = Float.valueOf(locationJSONObject.getString("acc"));
            float spdE = Float.valueOf(locationJSONObject.getString("spdE"));
            long timeStamp = Long.valueOf(locationJSONObject.getString("localTimeStamp"));

            location.setLatitude(lat);
            location.setLongitude(lon);
            location.setAltitude(alt);
            location.setAccuracy(acc);
            location.setSpeed(spdE);
            location.setTime(timeStamp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
    }

    public static LatLng getBetterLocation (Location googleLocation, Location skyhookLocation) {

        LatLng result = null;
        long timeDiff = Math.abs(googleLocation.getTime() - skyhookLocation.getTime());

        if (timeDiff <= 300) {

            if (googleLocation.getAccuracy() >= skyhookLocation.getAccuracy()) {
                result = new LatLng(googleLocation.getLatitude(), googleLocation.getLongitude());

            } else {
                result = new LatLng(skyhookLocation.getLatitude(), skyhookLocation.getLongitude());
            }

        }

        return result;

    }

    /**
     * Unit: metre
     * @param location1
     * @param location2
     * @return
     */
    public double getDistanceBetween (Location location1, Location location2) {
        float[] distance = new float[1];

        Location.distanceBetween(location1.getLatitude(), location1.getLongitude(),
                location2.getLatitude(), location2.getLongitude(), distance);

        double result = distance[0];

        return round(result, 2);

    }

    public static double getDistanceBetween (LatLng latLng1, LatLng latLng2) {
        float[] distance = new float[1];

        Location.distanceBetween(latLng1.latitude, latLng1.longitude,
                latLng2.latitude, latLng2.longitude, distance);

        double result = distance[0];

        return round(result, 2);

    }

    public static double round(double value, int scale) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, BigDecimal.ROUND_DOWN);
        double result = bd.doubleValue();
        bd = null;
        return result;
    }
}
