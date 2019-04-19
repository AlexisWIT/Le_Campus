package com.uol.yt120.lecampus.utility;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

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
            //long timeStamp = Long.valueOf(locationJSONObject.getString("timeStamp"));

            location.setLatitude(lat);
            location.setLongitude(lon);
            location.setAltitude(alt);
            location.setAccuracy(acc);
            location.setSpeed(spdE);
            //location.setTime(timeStamp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
    }
}
