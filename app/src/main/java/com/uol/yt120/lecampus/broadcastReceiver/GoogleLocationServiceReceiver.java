package com.uol.yt120.lecampus.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.uol.yt120.lecampus.service.GoogleLocationService;
import com.uol.yt120.lecampus.utility.JsonDataProcessor;

public class GoogleLocationServiceReceiver extends BroadcastReceiver {

    public static final String ACTION_GLE_SERVICE_BROADCAST_RELAY = "com.uol.yt120.lecampus.GoogleLocationDataTransmission";
    public static final String ACTION_GLE_LOCATION_DATA = "com.uol.yt120.lecampus.GoogleLocationData";
    private final Handler handler = new Handler();
    Intent intent;
    Context context;
    String locationDataJSON;

//    private Runnable sendLocationUpdate = new Runnable() {
//        @Override
//        public void run() {
//            broadcastData();
//        }
//    };

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = new Intent(ACTION_GLE_SERVICE_BROADCAST_RELAY);

        if (intent != null) {
            Location location = intent.getParcelableExtra(GoogleLocationService.EXTRA_LOCATION);
            if (location != null) {
                String locationJSON = getLocationInString(location);
                broadcastDataToActivity(locationJSON);

            }
        }
    }

    // Broadcast(relay) data to Activity receiver
    private void broadcastDataToActivity(String data) {
        intent.putExtra(ACTION_GLE_LOCATION_DATA, data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public String getLocationInString(Location location) {

        JsonDataProcessor dataProcessor = new JsonDataProcessor();
        String[] label = {"lat", "lon", "alt", "acc", "spdE", "localTimeStamp"};
        String localTimeStamp = String.valueOf(System.currentTimeMillis());

        String result;
        if (location != null) {

            String latitude = Double.toString(location.getLatitude());
            String longitude = Double.toString(location.getLongitude());
            String altitude = Double.toString(location.getAltitude());
            String accuracy = Double.toString(location.getAccuracy());
            String speedEST = Double.toString(location.getSpeed());  // unit: metre/sec
            //String serverTimeStamp = String.valueOf(location.getTime());

            // Only available on API 26 or higher, current app is developed for API 23 or higher
            // unit: (+-)metre/sec
            //String speedACC = Double.toString(location.getSpeedAccuracyMetersPerSecond());

            String[] value = {""+latitude, ""+longitude, ""+altitude, ""+accuracy, ""+speedEST, ""+localTimeStamp};
            result = dataProcessor.encapDataToJSON(label, value).toString();

        } else {
            String[] value = {"", "", "", "", "", localTimeStamp};
            result = dataProcessor.encapDataToJSON(label, value).toString();
        }
        return result;
    }
}
