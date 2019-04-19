package com.uol.yt120.lecampus.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.uol.yt120.lecampus.NavigationActivity;
import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.utility.LocationServiceController;

public class GoogleLocationService extends Service {

    private static final String TAG = GoogleLocationService.class.getSimpleName();

    private static final String PACKAGE_NAME =
            "com.uol.yt120.lecampus.GoogleLocationService";

    public static final String ACTION_BROADCAST =
            PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private static final String GOOGLE_LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID =
            "channel_google_location";

    private static final int GOOGLE_LOCATION_SERVICE_NOTIFICATION_IDENTIFIER = 112233;

    private static final long UPDATE_INTERVAL_FOREGROUND = 3500; // 3 sec when app in foreground
    private static final long UPDATE_INTERVAL_BACKGROUND = 10000; // 10 sec when app in background
    private static final long FASTEST_UPDATE_INTERVAL = 2000;   // 2 sec minimal interval

    private NotificationManager notificationManager;

    private boolean configChanged = false;


    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location location;
    private LocationCallback locationCallback;
    private Handler serviceHandler;


    private final IBinder googleLocationServiceBinder = new LocalBinder();

    public GoogleLocationService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w("[Ggle Location Service]", "====== Service bound to a Client ======");
        stopForeground(true);
        configChanged = false;
        return googleLocationServiceBinder;
    }

    @Override
    public void onCreate(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        initLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());

        /**
         * Notification part, to be moved to other place.
         */
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Device with Android API 26+ requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence displayedName = getString(R.string.app_name);

            NotificationChannel mChannel =
                    new NotificationChannel(GOOGLE_LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID,
                            displayedName, NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("[Ggle Location Service]", "====== Service started ======");
        boolean serviceStartedFromNotification =
                intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);

        if(serviceStartedFromNotification) {
            removeGoogleLocationUpdates();
            stopSelf();
        }

        return START_NOT_STICKY;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        configChanged = true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.w("[Ggle Location Service]", "====== Service bound to a Client again ======");
        stopForeground(true);
        configChanged = false;
        super.onRebind(intent);
    }

    /**
     * When activity is no longer available to receive data, service will be promoted to foreground
     *
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.w("[Ggle Location Service]", "====== A client unbound from service ======");

        if(!configChanged && LocationServiceController.isAPPRequestingService(this)){
            Log.w("[Ggle Location Service]", "====== Switch to foreground service ======");

            //startForeground(GOOGLE_LOCATION_SERVICE_NOTIFICATION_IDENTIFIER, getGoogleLocationNotification());
        }
        return true;
    }

    @Override
    public void onDestroy() {
        serviceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Setup location request and ready to locating
     */
    private void initLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_FOREGROUND);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            location = task.getResult();

                        } else {
                            Log.w("[Ggle Location Service]", "Failed to get last location");
                        }
                    }
                });

        } catch (SecurityException e) {
            Log.e("[Ggle Location Service]", "No Permission Granted ("+e+")");
        }
    }

    private Notification getGoogleLocationNotification() {
        Intent intent = new Intent(this, GoogleLocationService.class);

        CharSequence content = "Location Service is running";

        // Indicate onStartCommand is reached from notification
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // PendingIntent to launch activity
        PendingIntent activityPendingIntent = PendingIntent.getService(
                this, 0, new Intent(this, NavigationActivity.class), 0);

        // The PendingIntent leading to a call to onStartCommand in this service
        PendingIntent servicePendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, GOOGLE_LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID)
                    .addAction(R.drawable.ic_action_done, "Launch Le Campus", activityPendingIntent)
                    .addAction(R.drawable.ic_action_close, "Turn off", servicePendingIntent)
                    .setContentText("Text - Google Location Notification")
                    .setContentTitle("Title - Google Location Notification")
                    //.setBadgeIconType()
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setTicker("Ticker - Google Location Notification")
                    .setWhen(System.currentTimeMillis());

        // Set Channel ID for Andriod Device with API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(GOOGLE_LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID); // Channel ID
        }
        return notificationBuilder.build();

    }

    public class LocalBinder extends Binder {
        public GoogleLocationService getService() {
            return GoogleLocationService.this;
        }
    }

    /**
     * If running at foreground, return true
     * @param context
     * @return
     */
    public boolean isRunningForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (getClass().getName().equals(runningService.service.getClassName())) {
                if (runningService.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Call this method to start service
     */
    public void requestGoogleLocationUpdates() {
        Log.w("[Ggle Location Service]", "Requesting Google Location Data");

        LocationServiceController.setLocationServiceStatus(this, true);
        startService(new Intent(getApplicationContext(), GoogleLocationService.class));

        try {
            fusedLocationProviderClient
                    .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        } catch (SecurityException e) {
            Log.e("[Ggle Location Service]", "No Permission Granted ("+e+")");
            LocationServiceController.setLocationServiceStatus(this, false);
        }
    }

    /**
     * Call this method to stop service
     */
    public void removeGoogleLocationUpdates() {
        Log.i("[Ggle Location Service]", "Removing Google Location Data");
        try {
            fusedLocationProviderClient
                    .removeLocationUpdates(locationCallback);
            LocationServiceController.setLocationServiceStatus(this, false);
            stopSelf();

        } catch (SecurityException e) {
            Log.e("[Ggle Location Service]", "No Permission Granted ("+e+")");
            LocationServiceController.setLocationServiceStatus(this, true);

        }

    }

    // Start broadcast
    private void onNewLocation(Location newLocation) {
        //Log.w("[Ggle Location Service]", "New Location Data - Start Broadcast");

        location = newLocation;

        // Notify all listeners(receivers) who are binding to this service
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as foreground service
//        if (isRunningForeground(this)) {
//            notificationManager.notify(GOOGLE_LOCATION_SERVICE_NOTIFICATION_IDENTIFIER, getGoogleLocationNotification());
//        }
    }

}
