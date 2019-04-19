package com.uol.yt120.lecampus.utility;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

public class LocationServiceController {

    private static final String KEY_LOCATION_SERVICE_STATUS = "updloc";

    public static boolean isAPPRequestingService(Context context) {
        boolean result;
        result = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_LOCATION_SERVICE_STATUS, false);
        return result;
    }

    public static void setLocationServiceStatus (Context context, boolean isRequestingService) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(KEY_LOCATION_SERVICE_STATUS, isRequestingService)
                .apply();
    }

    public String getNormalNotificationTitle(Context context) {
        return "Location Service Notification";
    }

    public String getSecurityNotificationTitle(Context context) {
        return "Security Warning";
    }

    public String getSecurityNotificationContent(Context context) {
        return "This is warning content";
    }

}
