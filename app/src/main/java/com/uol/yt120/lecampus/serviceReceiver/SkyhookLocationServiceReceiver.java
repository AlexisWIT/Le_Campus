package com.uol.yt120.lecampus.serviceReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.skyhookwireless.wps.WPSGeoFence;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSReturnCode;
import com.uol.yt120.lecampus.service.SkyhookLocationService;
import com.uol.yt120.lecampus.utility.JsonDataProcessor;

public class SkyhookLocationServiceReceiver extends BroadcastReceiver {

    public static final String ACTION_SHK_SERVICE_BROADCAST_RELAY = "com.uol.yt120.lecampus.SkyhookLocationDataTransmission";
    public static final String ACTION_SHK_LOCATION_DATA = "com.uol.yt120.lecampus.SkyhookLocationData";
    public static final String ACTION_SHK_LOCATION_GEOFENCE = "com.uol.yt120.lecampus.SkyhookLocationGeofence";

    public static final int GEOFENCE_NULL = 0;
    public static final int GEOFENCE_IN = 1;
    public static final int GEOFENCE_OUT = 2;

    private final Handler handler = new Handler();
    Intent intent;
    Context context;

    public SkyhookLocationServiceReceiver() { }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = new Intent(ACTION_SHK_SERVICE_BROADCAST_RELAY);

        if(intent != null) {

            if(intent.getAction().equals(SkyhookLocationService.LOCATION_UPDATED)) {
                WPSReturnCode returnCode =
                        (WPSReturnCode) intent.getSerializableExtra(SkyhookLocationService.EXTRA_XPS_STATUS);

                if (returnCode != WPSReturnCode.WPS_OK) {
                    Log.w("[Glbal_Syhk_Rcivr]", "Received Error Message ["+returnCode+"]");
                    return;
                }

                WPSLocation location =
                        (WPSLocation) intent.getSerializableExtra(SkyhookLocationService.EXTRA_XPS_LOCATION);

                if (location != null) {
                    String locationJSON = getLocationInString(location);
                    Log.w("[Glbal_Syhk_Rcivr]", "Received Location Update ["+locationJSON+"]");
                    broadcastDataToActivity(locationJSON, GEOFENCE_NULL);
                }

            } else if (intent.getAction().equals(SkyhookLocationService.GEOFENCE_TRGGERED_NOTE)) {
                WPSGeoFence geoFence =
                        (WPSGeoFence) intent.getSerializableExtra(SkyhookLocationService.EXTRA_XPS_GEOFENCE);
                WPSLocation location =
                        (WPSLocation) intent.getSerializableExtra(SkyhookLocationService.EXTRA_XPS_LOCATION);

                if (geoFence == null || location == null) {
                    Log.w("[Glbal_Syhk_Rcivr]", "Received NULL GeoFence and Location Update");
                    return;
                }

                int triggerType; // 0 = no geofence, 1 = in/enter, 2 = out/leave
                if (geoFence.getType().equals(WPSGeoFence.Type.WPS_GEOFENCE_ENTER)
                        || geoFence.getType().equals(WPSGeoFence.Type.WPS_GEOFENCE_INSIDE)) {

                    triggerType = GEOFENCE_IN;

                } else {
                    triggerType = GEOFENCE_OUT;
                }

                String locationJSON = getLocationInString(location);
                Log.w("[Glbal_Syhk_Rcivr]", "Received Location Update ["+locationJSON+"] with GeoFence triggered ["+geoFence+"]");
                broadcastDataToActivity(locationJSON, triggerType);

            }

        }

    }

    // Broadcast(relay) data to Activity receiver
    private void broadcastDataToActivity(String data, int geoFenceTriggerType) {
        intent.putExtra(ACTION_SHK_LOCATION_DATA, data);
        intent.putExtra(ACTION_SHK_LOCATION_GEOFENCE, geoFenceTriggerType);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private String getLocationInString(WPSLocation location) {
        JsonDataProcessor dataProcessor = new JsonDataProcessor();
        String[] label = {"lat", "lon", "alt", "acc", "spdE", "localTimeStamp"};
        String localTimeStamp = String.valueOf(System.currentTimeMillis());

        String result;
        if (location != null) {

            String latitude = Double.toString(location.getLatitude());
            String longitude = Double.toString(location.getLongitude());
            String altitude = Double.toString(location.getAltitude());
            String accuracy = Double.toString(location.getHPE()); // Horizontal Positioning Error (metre)
            String speedEST = Double.toString(location.getSpeed());  // unit: metre/sec
            //String serverTimeStamp = String.valueOf(location.getServerTimestamp());

            String[] value = {""+latitude, ""+longitude, ""+altitude, ""+accuracy, ""+speedEST, ""+localTimeStamp};
            result = dataProcessor.encapDataToJSONString(label, value);

        } else {
            String[] value = {"", "", "", "", "", localTimeStamp};
            result = dataProcessor.encapDataToJSONString(label, value);
        }
        return result;
    }

}
