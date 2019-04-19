package com.uol.yt120.lecampus.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.skyhookwireless.wps.GeoFenceCallback;
import com.skyhookwireless.wps.TilingListener;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSGeoFence;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;
import com.skyhookwireless.wps.XPS;
import com.skyhookwireless.wps.WPSGeoFence.Handle;
import com.uol.yt120.lecampus.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * As a backup option, this service will be started
 * when the Google's service is temporarily unavailable
 *
 * Also this service will provide help on GeoFence functions
 */
public class SkyhookLocationService extends Service {

    public static final String TAG = SkyhookLocationService.class.getSimpleName();

    public static final String GET_LOCATION_UPDATE =
            "com.uol.yt120.lecampus.service.GET_LOCATION_UPDATE_INTENT";

    public static final String CLEAR_GEOFENCES =
            "com.uol.yt120.lecampus.service.CLEAR_GEOFENCE_INTENT";

    public static final String SET_GEOFENCE =
            "com.uol.yt120.lecampus.service.SET_GEOFENCE_TO_CLEAR_INTENT";

    public static final String GEOFENCE_TRGGERED_NOTE =
            "com.uol.yt120.lecampus.service.GEOFENCE_TRGGERED_NOTE_INTENT";

    public static final String LOCATION_UPDATED =
            "com.uol.yt120.lecampus.service.LOCATION_UPDATED";

    public static final String STOP_SERVICE =
            "com.uol.yt120.lecampus.service.STOP_SERVICE";

    // EXTRA
    public static final String EXTRA_XPS_STATUS = "extraXPSstatus";
    public static final String EXTRA_XPS_GEOFENCE = "extraXPSgeofence";
    public static final String EXTRA_XPS_LOCATION = "extraXPSlocation";

    private XPS xPositionService;
    private List<Handle> geofenceHandleList;
    private volatile boolean isLocationRequestPending = false;
    private String SKYHOOK_API_KEY;

    // Settings
    private final int MAX_DATASIZE_PER_SESSION = 200 * 1024;// 200KB - Tile data downloading
    private final int MAX_DATASIZE_IN_TOTAL = 2 * 1024 * 1024; // 2MB - Tile data downloading limit


    public static void clearGeoFences(final Context context) {
        context.startService(new Intent(CLEAR_GEOFENCES, null, context, SkyhookLocationService.class));
    }

    public static void requestLocationUpdate(final Context context) {
        context.startService(new Intent(GET_LOCATION_UPDATE, null, context, SkyhookLocationService.class));
    }

    public static void setNewGeofence(final Context context, WPSGeoFence newGeoFence) {
        final Intent intent = new Intent(SET_GEOFENCE, null, context, SkyhookLocationService.class);
        intent.putExtra(EXTRA_XPS_GEOFENCE, newGeoFence);
        context.startService(intent);
    }

    public static void stopService(final Context context) {
        context.stopService(new Intent(STOP_SERVICE, null, context, SkyhookLocationService.class));
    }

    private final WPSPeriodicLocationCallback periodicLocationCallback = new WPSPeriodicLocationCallback() {
        @Override
        public WPSContinuation handleWPSPeriodicLocation(WPSLocation wpsLocation) {
            Log.i("[Syhk Location Service]", "Processing periodic location: "+ wpsLocation);
            isLocationRequestPending = false;
            sendSyhkNewLocationBroadcast(wpsLocation);

            if (!geofenceHandleList.isEmpty()) {
                return WPSContinuation.WPS_CONTINUE;
            } else {
                return WPSContinuation.WPS_STOP;
            }
        }

        @Override
        public void done() {
            Log.i("[Syhk Location Service]", "Periodic location process completed");
        }

        @Override
        public WPSContinuation handleError(WPSReturnCode wpsReturnCode) {
            Log.w("[Syhk Location Service]", "Periodic location processing error: "+wpsReturnCode);
            sendSyhkLocationErrorBroadcast(wpsReturnCode);
            if (!geofenceHandleList.isEmpty() || isLocationRequestPending) {
                return WPSContinuation.WPS_CONTINUE;
            } else {
                return WPSContinuation.WPS_STOP;
            }
        }
    };

    public SkyhookLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SKYHOOK_API_KEY = getString(R.string.skyhook_api_token);
        xPositionService = new XPS(getApplicationContext());

        xPositionService.setKey(SKYHOOK_API_KEY);
        enableTiling();

        geofenceHandleList = new ArrayList<Handle>();
        isLocationRequestPending = false;
    }

    private void enableTiling() {
        try {
            String pathToStoreTiles = getExternalFilesDir(null).getAbsolutePath();

            final TilingListener tilingListener = (tileNum, tilesInTotal) -> {
                Log.w("[Syhk Location Service]", "tilingCallback with tileNumber "+tileNum+" of "+tilesInTotal+"total tiles");
                if (tileNum < 4) {
                    return WPSContinuation.WPS_CONTINUE;
                } else {
                    return WPSContinuation.WPS_STOP;
                }

            };
            xPositionService.setTiling(pathToStoreTiles, MAX_DATASIZE_PER_SESSION, MAX_DATASIZE_IN_TOTAL, tilingListener);

        } catch (Exception e) {
            Log.w("[Syhk Location Service]", "Error: No path found or no permission for storing Tiles file");
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY; // Stick in background if no intent;
        }

        final String action = intent.getAction();
        if (SET_GEOFENCE.equals(action)) {
            final WPSGeoFence geofence = (WPSGeoFence) intent.getExtras().getSerializable(EXTRA_XPS_GEOFENCE);

            if (geofence != null) {
                setGeofence(geofence);
            }

        } else if (CLEAR_GEOFENCES.equals(action)) {
            geofenceHandleList.clear();
            xPositionService.cancelAllGeoFences();

        } else if (GET_LOCATION_UPDATE.equals(action)) {
            isLocationRequestPending = true;
            getLocationUpdate();
        }
        return START_STICKY;
    }

    private void setGeofence(WPSGeoFence newGeoFence) {
        GeoFenceCallback callback = (wpsGeoFence, wpsLocation) -> {
            Log.i("[Syhk Location Service]", "GeoFence ["+wpsGeoFence+"] triggered at ["+wpsLocation+"]");
            sendSyhkGeoFenceTrgBroadcast(wpsGeoFence, wpsLocation);
            return WPSContinuation.WPS_CONTINUE;
        };
    }

    private void getLocationUpdate() {
        // if only geofence is required to update, set 1 hour period as MAX
        final int geofenceUpdateInterval = 60 * 60 * 1000;
        final int updateIteration = 0;
        xPositionService
                .getPeriodicLocation(null,
                        WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP,
                        geofenceUpdateInterval,
                        updateIteration,
                        periodicLocationCallback);
    }

    @Override
    public void onDestroy() {
        xPositionService.abort();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Start Broadcast
    private void sendSyhkLocationErrorBroadcast(WPSReturnCode returnCode) {
        final Intent intent = new Intent(LOCATION_UPDATED);
        intent.putExtra(EXTRA_XPS_STATUS, returnCode);
        sendBroadcast(intent);
    }

    private void sendSyhkNewLocationBroadcast(WPSLocation location) {
        final Intent intent = new Intent(LOCATION_UPDATED);
        intent.putExtra(EXTRA_XPS_STATUS, WPSReturnCode.WPS_OK);
        intent.putExtra(EXTRA_XPS_LOCATION, location);
        sendBroadcast(intent);
    }

    private void sendSyhkGeoFenceTrgBroadcast(WPSGeoFence wpsGeoFence, WPSLocation wpsLocation) {
        Intent intent = new Intent(GEOFENCE_TRGGERED_NOTE);
        intent.putExtra(EXTRA_XPS_GEOFENCE, wpsGeoFence);
        intent.putExtra(EXTRA_XPS_LOCATION, wpsLocation);
        sendBroadcast(intent);
    }

}
