//package com.uol.yt120.lecampus;
//
//import android.app.Application;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.location.Location;
//import android.net.ConnectivityManager;
//import android.net.Network;
//import android.net.NetworkInfo;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.telephony.CellIdentityCdma;
//import android.telephony.CellIdentityGsm;
//import android.telephony.CellIdentityLte;
//import android.telephony.CellIdentityWcdma;
//import android.telephony.CellInfo;
//import android.telephony.CellInfoCdma;
//import android.telephony.CellInfoGsm;
//import android.telephony.CellInfoLte;
//import android.telephony.CellInfoWcdma;
//import android.telephony.CellLocation;
//import android.telephony.NeighboringCellInfo;
//import android.telephony.TelephonyManager;
//import android.telephony.cdma.CdmaCellLocation;
//import android.telephony.gsm.GsmCellLocation;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.Task;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
//import cn.finalteam.okhttpfinal.HttpRequest;
//import cn.finalteam.okhttpfinal.RequestParams;
//import timber.log.Timber;
//
//public class LocationServiceManager implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
//
//    private static String googleAPIKey;
//
//    private static final boolean PrecisionMode = true;
//
//    private static final int MAX_deviation = PrecisionMode ? 60 : 100; // true : false
//    public static final int FAST_UPDATE_INTERVAL = PrecisionMode ? 10000 : 20000;
//    private static final int FASTEST_INTERVAL = 5000;
//    public static final int FAST_DISPLACEMENT = PrecisionMode ? 1 : 10;
//
//
//    private static final int SLOW_UPDATE_INTERVAL = 60000;
//    private static final int SLOW_INTERVAL = 30000;
//    private static final int SLOW_DISPLACEMENT = 500;
//
//    private Application context;
//    private GoogleApiClient googleApiClient;
//    public static LocationServiceManager locationServiceManager;
//
//    public STATUS currentStatus = STATUS.NOT_CONNECT;
//    private Timer locationTimer;
//    public String dataJson;
//    private static LocationCallback locationCallback;
//
//    public enum STATUS {
//        NOT_CONNECT,
//        TRYING_FIRST,
//        LOW_POWER,
//        NOT_TRACK
//    }
//
//    final static boolean isDebugging = true;
//
//    public static LocationServiceManager getInstance() {
//        return locationServiceManager;
//    }
//
//    public static void setGoogleAPIKey (String googleApiKey) {
//        googleAPIKey = googleApiKey;
//    }
//
//    /**
//     * GPS Listener
//     * @param context
//     */
//    public static void onCreateGPS(final Application context) {
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                onNewLocation(locationResult.getLastLocation());
//            }
//        };
//
//        if (locationServiceManager != null && locationServiceManager.googleApiClient != null) return;
//        Timber.tag("[Location Service Mgr]").i("Ready to turn on GPS");
//        locationServiceManager = new LocationServiceManager();
//        locationServiceManager.context = context;
//        locationServiceManager.googleApiClient = new GoogleApiClient.Builder(context)
//                .addConnectionCallbacks(locationServiceManager)
//                .addOnConnectionFailedListener(locationServiceManager)
//                .addApi(LocationServices.API)
//                .build();
//        locationServiceManager.googleApiClient.connect();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (locationServiceManager == null || locationServiceManager.context == null || locationServiceManager.currentStatus != STATUS.NOT_CONNECT)
//                    return;
//                Timber.tag("[Location Service Mgr]").i("Please install Google Play service");
//                Toast.makeText(locationServiceManager.context, "Please install Google Play Framework", Toast.LENGTH_LONG).show();
//                context.startService(new Intent(locationServiceManager.context, LocationService.class));
//                if (locationServiceManager.locationTimer == null) locationServiceManager.locationTimer = new Timer();
//                try {
//                    locationServiceManager.locationTimer.scheduleAtFixedRate(new LocationTask(), 0, FAST_UPDATE_INTERVAL);
//                } catch (Exception e) {
//                    Timber.tag("[Location Service Mgr]").i(e, "Error occured when start locationtask ");
//                }
//                new AsyncTask<Void, Void, String>() {
//
//                    @Override
//                    protected String doInBackground(Void... params) {
//                        GeoLocationAPI geoLocationAPI = null;
//                        try {
//                            geoLocationAPI = getCellInfo(locationServiceManager.context);
//                        } catch (Exception e) {
//                            Timber.tag("[Location Service Mgr]").i(e, "Error occured when obtain nearby Cellular Tower info");
//                        }
//                        if (geoLocationAPI == null) {
//                            Timber.tag("[Location Service Mgr]").i("Failed to obtain Cellular Tower info");
//                            return "{}";
//                        }
//                        getWifiInfo(locationServiceManager.context, geoLocationAPI);
//                        String json = geoLocationAPI.toJson();
//                        Timber.tag("[Location Service Mgr]").i("JSON Data ready for Google: " + json);
//                        return json;
//                    }
//
//                    @Override
//                    protected void onPostExecute(String json) {
//                        super.onPostExecute(json);
//
//                        if (locationServiceManager != null && context != null)
//                            locationServiceManager.sendJsonByPost(json, "https://www.googleapis.com/geolocation/v1/geolocate?key=" + googleAPIKey);
//                        else return;
//                        Toast.makeText(context, "No Google Play framework installed. JSON Data sent to Google: " + json, Toast.LENGTH_LONG).show();
//                    }
//                };
//            }
//        }, 9000);
//    }
//
//    public static class LocationTask extends TimerTask {
//
//        @Override
//        public void run() {
//            Timber.tag("[Location Service Mgr]").i("Executing location task, locationServiceManager is [" + locationServiceManager + "], Process status is" + LocationService.endProcess);
//            if (locationServiceManager == null || !LocationService.endProcess) return;
//            locationServiceManager.context.startService(new Intent(locationServiceManager.context, LocationService.class));
//        }
//    }
//
//    /**
//     * Refresh GPS
//     * @param context
//     */
//    public void restartGPS(Application context) {
//        stopGPS(context);
//        onCreateGPS(context);
//    }
//
//
//    /**
//     * Stop GPS service to save power
//     */
//    public static void stopGPS(Application context) {
//        if (locationServiceManager == null) return;
//        pauseGPS(context);
//        locationServiceManager.googleApiClient = null;
//        locationServiceManager = null;
//    }
//
//    /**
//     * Suspend GPS service when app runs background
//     */
//    public static void pauseGPS(Application context) {
//        Timber.tag("[Location Service Mgr]").i("Ready to suspend GPS service");
//        if (locationServiceManager == null || locationServiceManager.googleApiClient == null || locationServiceManager.currentStatus == STATUS.NOT_CONNECT || locationServiceManager.currentStatus == STATUS.NOT_TRACK)
//            return;
//        try {
//            LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates();
//            locationServiceManager.currentStatus = STATUS.NOT_CONNECT;
//            if (locationServiceManager.googleApiClient.isConnected() || locationServiceManager.googleApiClient.isConnecting())
//                locationServiceManager.googleApiClient. disconnect();
//            locationServiceManager.googleApiClient = null;
//        } catch (Exception e) {
//            Timber.tag("[Location Service Mgr]").i(e, "Error occured when suspend GPS service");
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Timber.tag("[Location Service Mgr]").i("GPS signal connected.");
//        if (currentStatus != STATUS.NOT_CONNECT)
//            return;
//        currentStatus = STATUS.TRYING_FIRST;
//        if (!getCurrentLocation()) {
//            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(googleApiClient, createFastLocationRequest(), this);
//            new TowerAndWiFiTask().executeDependSDK();
//
//        } else if (PrecisionMode) {
//            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(googleApiClient, createFastLocationRequest(), this);
//            new TowerAndWiFiTask().executeDependSDK();
//            Toast.makeText(locationServiceManager.context, "Obtained the last coordinate and start tracking", Toast.LENGTH_LONG).show();
//            Timber.tag("[Location Service Mgr]").i("Obtained the last coordinate and start tracking");
//
//        } else {
//            Toast.makeText(locationServiceManager.context, "Obtained the last coordinate and stop tracking", Toast.LENGTH_LONG).show();
//            Timber.tag("[Location Service Mgr]").i("Obtained the last coordinate and stop tracking");
//            currentStatus = STATUS.NOT_TRACK;
//
//        }
//    }
//
//    class TowerAndWiFiTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... params) {
//            GeoLocationAPI geoLocationAPI = null;
//            try {
//                geoLocationAPI = getCellInfo(context);
//            } catch (Exception e) {
//                Timber.tag("[Location Service Mgr]").i(e, "Error occured when tring to obtain the nearby Cellular Tower info");
//            }
//            if (geoLocationAPI == null) {
//                Timber.tag("[Location Service Mgr]").i("Failed to obtain the Cellular Tower info");
//                return "{}";
//            }
//            getWifiInfo(context, geoLocationAPI);
//            String json = geoLocationAPI.toJson();
//            Timber.tag("[Location Service Mgr]").i("JSON Data ready for Google: %s", json);
//            return json;
//        }
//
//        @Override
//        protected void onPostExecute(String json) {
//            super.onPostExecute(json);
//
//            sendJsonByPost(json, "https://www.googleapis.com/geolocation/v1/geolocate?key=" + googleAPIKey);
//            Toast.makeText(context, "Google Play Framework installed, JSON Data sent to Google: " + json, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /**
//     * Coordinate from hardware
//     * @return
//     */
//    public boolean getCurrentLocation() {
//        Location mLastLocation = LocationServices.getFusedLocationProviderClient(this).getLastLocation(googleApiClient);
//        Timber.tag("[Location Service Mgr]").i("Last location = %s", mLastLocation);
//        if (mLastLocation == null) return false;
//        double latitude = mLastLocation.getLatitude();
//        double longitude = mLastLocation.getLongitude();
//        double altitude = mLastLocation.getAltitude();
//        float last_accuracy = mLastLocation.getAccuracy();
//        Timber.tag("[Location Service Mgr]").i("Accuracy of the last Location = %s", last_accuracy);
//        String provider = mLastLocation.getProvider();
//        float bearing = mLastLocation.getBearing();
//        float speed = mLastLocation.getSpeed();
//        if (isDebugging)
//            Toast.makeText(context, "Obtained the last location，Latitude=" + latitude + ", Longitude=" + longitude + ", Accuracy=" + last_accuracy, Toast.LENGTH_LONG).show();
//        Timber.tag("[Location Service Mgr]").i("Obtained the last location，Latitude=" + latitude + ", Longitude=" + longitude + ", Altitude=" + altitude + ", Provider=" + provider + ", Speed=" + speed + ", Accuracy=" + last_accuracy);
//        if (last_accuracy < MAX_deviation) {
//            recordLocation(context, latitude, longitude, last_accuracy);
//        } else {
//            Timber.tag("[Location Service Mgr]").i("Discard the last Location due to low accuracy");
//        }
//        return last_accuracy < MAX_deviation;
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    private void onNewLocation(Location location) {
//        Log.i(TAG, "New location: " + location);
//
//        mLocation = location;
//
//        // Notify anyone listening for broadcasts about the new location.
//        Intent intent = new Intent(ACTION_BROADCAST);
//        intent.putExtra(EXTRA_LOCATION, location);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//
//        // Update notification content if running as a foreground service.
//        if (serviceIsRunningInForeground(this)) {
//            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
//        }
//    }
//
//
//    @Override
//    public void onLocationChanged(Location location) {
//        if (currentStatus == STATUS.LOW_POWER)
//            Timber.tag("[Location Service Mgr]").i("Power-saver Locating Mode");
//            Timber.tag("[Location Service Mgr]").i("Position Changed: %s", location);
//
//        if (location == null)
//            return;
//
//        if (isDebugging)
//            Toast.makeText(context, "The newest GPS coordinate: " + location.toString() + " Accuracy=" + location.getAccuracy(), Toast.LENGTH_LONG).show();
//
//        if (location.getAccuracy() < MAX_deviation) {
//            recordLocation(context, location.getLatitude(), location.getLongitude(), location.getAccuracy());
//
//        } else {
//            Timber.tag("[Location Service Mgr]").i("Discard the newest coordinate due to the low accuracy.");
//        }
//
//        if (location.getAccuracy() > 50 || currentStatus != STATUS.TRYING_FIRST || PrecisionMode)
//            return;
//
//        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//        Timber.tag("[Location Service Mgr]").i("Ready to switch to Power-saver Mode");
//        currentStatus = STATUS.LOW_POWER;
//        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLowPowerLocationRequest(), this);
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//
//    private static LocationRequest createFastLocationRequest() {
//        LocationRequest mLocationRequest = LocationRequest.create();
//        mLocationRequest.setInterval(FAST_UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(FAST_DISPLACEMENT);
//        return mLocationRequest;
//    }
//
//
//    private static LocationRequest createLowPowerLocationRequest() {
//        LocationRequest mLocationRequest = LocationRequest.create();
//        mLocationRequest.setInterval(SLOW_UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(SLOW_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(SLOW_DISPLACEMENT);
//        return mLocationRequest;
//    }
//
//
//    public static void recordLocation(Context context, double latitude, double longitude, float accuracy) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("lastLocationRecord", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//
//        editor.putString("latitude", String.valueOf(latitude));
//        editor.putString("longitude", String.valueOf(longitude));
//        editor.putFloat("accuracy", accuracy);
//        editor.apply();
//        Timber.tag("[Location Service Mgr]").i("Latitude=" + latitude + "   Longitude=" + longitude + "   Accuracy=" + accuracy);
//        UserLocation myLocationStatic = UserLocation.getInstance();
//
//        if (myLocationStatic.updateTime == 0 || System.currentTimeMillis() - myLocationStatic.updateTime > SLOW_INTERVAL || accuracy <= myLocationStatic.accuracy) {
//            myLocationStatic.lat = latitude;
//            myLocationStatic.lon = longitude;
//            myLocationStatic.accuracy = accuracy;
//            myLocationStatic.updateTime = System.currentTimeMillis();
//            if (isDebugging)
//                Toast.makeText(context, "Latitude=" + latitude + "   Longitude=" + longitude + "   Accuracy=" + accuracy, Toast.LENGTH_LONG).show();
//        } else {
//            Timber.tag("[Location Service Mgr]").i("Discard the inaccuracy coordinate");
//        }
//    }
//
//
//    public static double[] getOldLocation(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("lastLocationRecord", Context.MODE_PRIVATE);
//        String latitudeStr = sharedPreferences.getString("latitude", "");
//        String longitudeStr = sharedPreferences.getString("longitude", "");
//        float accuracy = sharedPreferences.getFloat("accuracy", 9999);
//        Timber.tag("[Location Service Mgr]").i("Shared Pref Accuracy: %s", accuracy);
//        if (latitudeStr.length() == 0 || longitudeStr.length() == 0) return null;
//        double[] latlng = {-1, -1};
//        try {
//            latlng[0] = Double.valueOf(latitudeStr);
//            latlng[1] = Double.valueOf(longitudeStr);
//        } catch (Exception e) {
//            Timber.tag("[Location Service Mgr]").i(e, "Coordinate error");
//        }
//        return latlng;
//    }
//
//
//    public static GeoLocationAPI getCellInfo(Context context) {
//        //Get lac:mcc:mnc:cell-id by using TelephonyManager
//        GeoLocationAPI cellInfo = new GeoLocationAPI();
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (telephonyManager == null) return cellInfo;
//
//        // return MCC + MNC
//        /*# MCC，Mobile Country Code
//         * # MNC，Mobile Network Code
//         * # LAC，Location Area Code
//         * # CID，Cell Identity
//         * # BSSS，Base station signal strength
//         */
//        String operator = telephonyManager.getNetworkOperator();
//        Timber.tag("[Location Service Mgr]").i("Cellular Tower info: %s", operator);
//        if (operator == null || operator.length() < 5) {
//            Timber.tag("[Location Service Mgr]").i("Unable to obtain Cellular Tower info");
//            return cellInfo;
//        }
//        int mcc = Integer.parseInt(operator.substring(0, 3));
//        int mnc = Integer.parseInt(operator.substring(3));
//        int lac;
//        int cellId;
//
//
//        CellLocation cellLocation = telephonyManager.getCellLocation();
//        if(cellLocation==null){
//            Timber.tag("[Location Service Mgr]").i("No Simcard");
//            return cellInfo;
//        }
//        if(telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
//            Timber.tag("[Location Service Mgr]").i("GSM Cell Tower");
//            GsmCellLocation location = (GsmCellLocation)cellLocation;
//            lac = location.getLac();
//            cellId = location.getCid();
//
//            Timber.tag("[Location Service Mgr]").i(" MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);
//        }else if(telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
//
//            Timber.tag("[Location Service Mgr]").i("CDMA Cell Tower");
//            CdmaCellLocation locationCDMA = (CdmaCellLocation) telephonyManager.getCellLocation();
//            lac = locationCDMA.getNetworkId();
//            cellId = locationCDMA.getBaseStationId();
//            cellId /= 16;
//        }else {
//            Timber.tag("[Location Service Mgr]").i("Unknown Tower");
//            return cellInfo;
//        }
//        cellInfo.radioType = determineCellType(context);
//        cellInfo.homeMobileCountryCode = mcc;
//        cellInfo.homeMobileNetworkCode = mnc;
//        cellInfo.carrier = getCarrier(operator);
//        cellInfo.considerIp = considerIP(context);
//        ArrayList<GoogleCellTower> towers = new ArrayList<>(1);
//        GoogleCellTower bigTower = new GoogleCellTower();
//        bigTower.cellId = cellId;
//        bigTower.mobileCountryCode = mcc;
//        bigTower.mobileNetworkCode = mnc;
//        bigTower.locationAreaCode = lac;
//        bigTower.signalStrength = 0;
//        towers.add(bigTower);
//        cellInfo.cellTowers = towers;
//
//        if(Build.VERSION.SDK_INT<17) {
//            List<NeighboringCellInfo> infos = telephonyManager.getNeighboringCellInfo();
//            if(infos==null){
//                Timber.tag("[Location Service Mgr]").i("Locating by Cellular Tower is not supported by your device.");
//                return cellInfo;
//            }
//            if(infos.size()==0)
//                return cellInfo;
//
//            towers = new ArrayList<>(infos.size());
//            StringBuffer sb = new StringBuffer("Total towers nearby: " + infos.size() + "\n");
//            for (NeighboringCellInfo info1 : infos) {
//                GoogleCellTower tower = new GoogleCellTower();
//                sb.append(" LAC : " + info1.getLac());
//                tower.locationAreaCode = info1.getLac();
//                tower.mobileCountryCode = mcc;
//                tower.mobileNetworkCode = mnc;
//                tower.signalStrength = info1.getRssi();
//                sb.append(" CID : " + info1.getCid());
//                tower.cellId = info1.getCid();
//                sb.append(" BSSS : " + (-113 + 2 * info1.getRssi()) + "\n");
//                towers.add(tower);
//            }
//            Timber.tag("[Location Service Mgr]").i("Cellular Tower info: %s", sb);
//        }else {
//            List<CellInfo> infos = telephonyManager.getAllCellInfo();
//            if(infos!=null) {
//                if(infos.size()==0)return cellInfo;
//                towers = new ArrayList<>(infos.size());
//                for (CellInfo i : infos) {
//                    Timber.tag("[Location Service Mgr]").i("Cellular Tower nearby: %s", i.toString());
//                    GoogleCellTower tower = new GoogleCellTower();
//                    if(i instanceof CellInfoGsm){
//                        Timber.tag("[Location Service Mgr]").i("GSM Tower");
//                        CellIdentityGsm cellIdentityGsm = ((CellInfoGsm)i).getCellIdentity();
//                        if(cellIdentityGsm==null)continue;
//                        tower.locationAreaCode = cellIdentityGsm.getLac();
//                        tower.mobileCountryCode = cellIdentityGsm.getMcc();
//                        tower.mobileNetworkCode = cellIdentityGsm.getMnc();
//                        tower.signalStrength = 0;
//                        tower.cellId = cellIdentityGsm.getCid();
//                    }else if(i instanceof CellInfoCdma){
//                        Timber.tag("[Location Service Mgr]").i("CDMA Tower");
//                        CellIdentityCdma cellIdentityCdma = ((CellInfoCdma)i).getCellIdentity();
//                        if(cellIdentityCdma==null)continue;
//                        tower.locationAreaCode = lac;
//                        tower.mobileCountryCode = mcc;
//                        tower.mobileNetworkCode = cellIdentityCdma.getSystemId();
//                        tower.signalStrength = 0;
//                        cellIdentityCdma.getNetworkId();
//                        tower.cellId = cellIdentityCdma.getBasestationId();
//                    }else if(i instanceof CellInfoLte) {
//                        Timber.tag("[Location Service Mgr]").i("LTE Tower");
//                        CellIdentityLte cellIdentityLte = ((CellInfoLte) i).getCellIdentity();
//                        if(cellIdentityLte==null)continue;
//                        tower.locationAreaCode = lac;
//                        tower.mobileCountryCode = cellIdentityLte.getMcc();
//                        tower.mobileNetworkCode = cellIdentityLte.getMnc();
//                        tower.cellId = cellIdentityLte.getCi();
//                        tower.signalStrength = 0;
//                    }else if(i instanceof CellInfoWcdma && Build.VERSION.SDK_INT>=18){
//                        Timber.tag("[Location Service Mgr]").i("WCDMA Tower");
//                        CellIdentityWcdma cellIdentityWcdma = ((CellInfoWcdma)i).getCellIdentity();
//                        if(cellIdentityWcdma==null)continue;
//                        tower.locationAreaCode = cellIdentityWcdma.getLac();
//                        tower.mobileCountryCode = cellIdentityWcdma.getMcc();
//                        tower.mobileNetworkCode = cellIdentityWcdma.getMnc();
//                        tower.cellId = cellIdentityWcdma.getCid();
//                        tower.signalStrength = 0;
//                    }else {
//                        Timber.tag("[Location Service Mgr]").i("Unknown cellular tower");
//                    }
//                    towers.add(tower);
//                }
//            }else {
//                Timber.tag("[Location Service Mgr]").i("Try method for lower Android version");
//                List<NeighboringCellInfo> infos2 = telephonyManager.getNeighboringCellInfo();
//                if(infos2==null || infos2.size()==0){
//                    Timber.tag("[Location Service Mgr]").i("Cellular network is not supported on this device");
//                    return cellInfo;
//                }
//                towers = new ArrayList<>(infos2.size());
//                StringBuffer sb = new StringBuffer("Total towers nearby: " + infos2.size() + "\n");
//                for (NeighboringCellInfo i : infos2) {
//                    GoogleCellTower tower = new GoogleCellTower();
//                    sb.append(" LAC : " + i.getLac());
//                    tower.age = 0;
//                    tower.locationAreaCode = i.getLac();
//                    tower.mobileCountryCode = mcc;
//                    tower.mobileNetworkCode = mnc;
//                    sb.append(" CID : " + i.getCid());
//                    tower.cellId = i.getCid();
//                    sb.append(" BSSS : " + (-113 + 2 * i.getRssi()) + "\n");
//                    towers.add(tower);
//                }
//                Timber.tag("[Location Service Mgr]").i("Cell Tower Info: %s", sb);
//            }
//        }
//        cellInfo.cellTowers = towers;
//        return cellInfo;
//    }
//
//    /**
//     * Determine if it's Wi-Fi (true) or Mobile Data (false)
//     * @param context
//     * @return
//     */
//    public static boolean isWifiEnvironment(Context context){
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        if(networkInfo==null){
//            Timber.tag("[Location Service Mgr]").i("No network connection");
//            return false;
//        }
//        int netType = networkInfo.getType();
//        switch (netType){
//            case ConnectivityManager.TYPE_WIFI:
//                Timber.tag("[Location Service Mgr]").i("Status: Wi-Fi connected");
//                return true;
//            case ConnectivityManager.TYPE_VPN:
//                Timber.tag("[Location Service Mgr]").i("Status: Virtual Private Network (VPN) connected");
//                break;
//            case ConnectivityManager.TYPE_MOBILE:
//                Timber.tag("[Location Service Mgr]").i("Status: Mobile Data connected");
//                int subType = networkInfo.getSubtype();
//                Timber.tag("[Location Service Mgr]").i("Sub-type (2G/3G/4G): " + subType + "  " + networkInfo.getSubtypeName());
//                break;
//            default:
//                Timber.tag("[Location Service Mgr]").i("Status: Unknown");
//                break;
//        }
//        return false;
//    }
//
//    /**
//     * Determine if IP address is available for Locating
//     * @param context
//     */
//    public static boolean considerIP(Context context){
//        boolean considerIP = true;
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager==null)return true;
//        if(!isWifiEnvironment(context))return false;
//        if(Build.VERSION.SDK_INT<21) {
//            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
//            if(networkInfos==null)return true;
//            for(NetworkInfo i:networkInfos){
//                if(i==null)continue;
//                Timber.tag("[Location Service Mgr]").i("Current Network Status: " + i.getTypeName() + i.getType() + "   " + i.getSubtypeName());//WIFI, VPN, MOBILE/LTE
//                if(i.getType()== ConnectivityManager.TYPE_VPN){
//                    Timber.tag("[Location Service Mgr]").i("Virtual Private Network (VPN) connected, unable to locate by IP");
//                    considerIP = false;
//                    break;
//                }
//            }
//        }else {// After Android 4.0
//            Network[] networks = connectivityManager.getAllNetworks();
//            if(networks==null)return true;
//            for(Network n:networks){
//                if(n==null)continue;
//                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(n);
//                if(networkInfo==null)continue;
//                Timber.tag("[Location Service Mgr]").i("Current Network Status: " + networkInfo.getTypeName() + networkInfo.getType() + "   " + networkInfo.getSubtypeName());//WIFI, VPN, MOBILE/LTE
//                if(networkInfo.getType()== ConnectivityManager.TYPE_VPN){
//                    Timber.tag("[Location Service Mgr]").i("Virtual Private Network (VPN) connected, unable to locate by IP");
//                    considerIP = false;
//                    break;
//                }
//            }
//        }
//        return considerIP;
//    }
//
//    /**
//     * Determine Cellular network type 2G/3G/4G
//     */
//    public static String determineCellType(Context context){
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager==null)return null;
//        if(Build.VERSION.SDK_INT<21) {
//            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
//            if(networkInfos==null)return null;
//            for(NetworkInfo i: networkInfos){
//                if(i==null)
//                    continue;
//                Timber.tag("[Location Service Mgr]").i("Current Network Status: " + i.getTypeName() + i.getType() + "   " + i.getSubtypeName());//WIFI, VPN, MOBILE/LTE
//
//                if(i.getType()!= ConnectivityManager.TYPE_MOBILE)
//                    continue;
//                else
//                    Timber.tag("[Location Service Mgr]").i("Mobile Network");
//                return determineCellType(i);
//            }
//        }else {// After Android 4.0
//            Network[] networks = connectivityManager.getAllNetworks();
//            if(networks==null)return null;
//            for(Network n: networks){
//                if(n==null)
//                    continue;
//                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(n);
//
//                if(networkInfo==null)
//                    continue;
//                Timber.tag("[Location Service Mgr]").i("Current Network Status: " + networkInfo.getTypeName() + networkInfo.getType() + "   " + networkInfo.getSubtypeName());//WIFI, VPN, MOBILE/LTE
//                if(networkInfo.getType()!= ConnectivityManager.TYPE_MOBILE)
//                    continue;
//                return determineCellType(networkInfo);
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Determine Cellular network standards
//     * @param info
//     * @return
//     */
//    public static String determineCellType(NetworkInfo info){
//        if(info==null)return null;
//        switch (info.getSubtype()){
//            case TelephonyManager.NETWORK_TYPE_LTE:
//                return "LTE";
//            case TelephonyManager.NETWORK_TYPE_EDGE:
//                return "EDGE";
//            case TelephonyManager.NETWORK_TYPE_CDMA:
//                return "CDMA";
//            case TelephonyManager.NETWORK_TYPE_GPRS:
//                return "GPRS";
//            case TelephonyManager.NETWORK_TYPE_HSDPA:
//                return "HSDPA";
//            case TelephonyManager.NETWORK_TYPE_HSPA:
//                return "HSPA";
//            case TelephonyManager.NETWORK_TYPE_HSPAP:
//                return "HSPAP";
//            case TelephonyManager.NETWORK_TYPE_HSUPA:
//                return "HSUPA";
//            case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                return "EVDO_0";
//            case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                return "EVDO_A";
//            case TelephonyManager.NETWORK_TYPE_EVDO_B:
//                return "EVDO_B";
//            case TelephonyManager.NETWORK_TYPE_IDEN:
//                return "IDEN";
//            case TelephonyManager.NETWORK_TYPE_UMTS:
//                return "UMTS";
//            case TelephonyManager.NETWORK_TYPE_EHRPD:
//                return "EHRPD";
//            case TelephonyManager.NETWORK_TYPE_1xRTT:
//                return "RTT";
//            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
//                return "UNKNOWN";
//        }
//        return null;
//
//    }
//
//    /**
//     * Get Wi-Fi signal info nearby
//     * @param context
//     * @param geoLocationAPI
//     * @return
//     */
//    public static GeoLocationAPI getWifiInfo(Context context, GeoLocationAPI geoLocationAPI){
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        if(wifiManager == null)return geoLocationAPI;
//        Timber.tag("[Location Service Mgr]").i("Ready to scanning Wi-Fi signal nearby");
//        wifiManager.startScan();
//
//        ArrayList<ScannedWifi> lsAllWIFI = new ArrayList<ScannedWifi>();
//        List<ScanResult> lsScanResult = wifiManager.getScanResults();
//        if(lsScanResult == null){
//            Timber.tag("[Location Service Mgr]").i("Failed to scanning Wi-Fi signal nearby");
//            return geoLocationAPI;
//
//        }
//
//        for (ScanResult result : lsScanResult) {
//            Timber.tag("[Location Service Mgr]").i("Found Wi-Fi signal nearby: " + result.SSID + "  MAC Address: " + result.BSSID + "   Strength: " + result.level);
//            if(result == null)continue;
//            ScannedWifi scanWIFI = new ScannedWifi(result);
//            lsAllWIFI.add(scanWIFI);
//
//        }
//
//        ArrayList<GoogleWifiInfo> wifiInfos = new ArrayList<>(lsAllWIFI.size());
//        for (ScannedWifi w:lsAllWIFI){
//            if(w == null)continue;
//            GoogleWifiInfo wifiInfo = new GoogleWifiInfo();
//            wifiInfo.macAddress = w.mac.toUpperCase();
//            wifiInfo.signalStrength = w.dBm;
//            wifiInfo.channel = w.channel;
//            wifiInfos.add(wifiInfo);
//
//        }
//
//        geoLocationAPI.wifiAccessPoints = wifiInfos;
//        return geoLocationAPI;
//    }
//
//    /**
//     * Record scanned Wi-Fi info
//     */
//    public static class ScannedWifi implements Comparable<ScannedWifi> {
//        public final int dBm;
//        public final String ssid;
//        public final String mac;
//        public short channel;
//        public ScannedWifi(ScanResult scanresult) {
//            dBm = scanresult.level;
//            ssid = scanresult.SSID;
//            mac = scanresult.BSSID;//MAC Address
//            channel = getChannelByFrequency(scanresult.frequency);
//        }
//        public ScannedWifi(String s, int i, String s1, String imac) {
//            dBm = i;
//            ssid = s1;
//            mac = imac;
//        }
//
//        /**
//         * Sort by signal strength
//         * @param wifiinfo
//         * @return
//         */
//        public int compareTo(ScannedWifi wifiinfo) {
//            int i = wifiinfo.dBm;
//            int j = dBm;
//            return i - j;
//        }
//
//        /**
//         * To avoid duplicate list
//         * @param obj
//         * @return
//         */
//        public boolean equals(Object obj) {
//            boolean flag = false;
//            if (obj == this) {
//                flag = true;
//                return flag;
//            } else {
//                if (obj instanceof ScannedWifi) {
//                    ScannedWifi wifiinfo = (ScannedWifi) obj;
//                    int i = wifiinfo.dBm;
//                    int j = dBm;
//                    if (i == j) {
//                        String s = wifiinfo.mac;
//                        String s1 = this.mac;
//                        if (s.equals(s1)) {
//                            flag = true;
//                            return flag;
//                        }
//                    }
//                    flag = false;
//                } else {
//                    flag = false;
//                }
//            }
//            return flag;
//        }
//        public int hashCode() {
//            int i = dBm;
//            int j = mac.hashCode();
//            return i ^ j;
//        }
//
//    }
//
//    /**
//     * Get channel by frequency
//     * @param frequency
//     * @return
//     */
//    public static short getChannelByFrequency(int frequency) {
//        short channel = -1;
//        switch (frequency) {
//            case 2412:
//                channel = 1;
//                break;
//            case 2417:
//                channel = 2;
//                break;
//            case 2422:
//                channel = 3;
//                break;
//            case 2427:
//                channel = 4;
//                break;
//            case 2432:
//                channel = 5;
//                break;
//            case 2437:
//                channel = 6;
//                break;
//            case 2442:
//                channel = 7;
//                break;
//            case 2447:
//                channel = 8;
//                break;
//            case 2452:
//                channel = 9;
//                break;
//            case 2457:
//                channel = 10;
//                break;
//            case 2462:
//                channel = 11;
//                break;
//            case 2467:
//                channel = 12;
//                break;
//            case 2472:
//                channel = 13;
//                break;
//            case 2484:
//                channel = 14;
//                break;
//            case 5745:
//                channel = 149;
//                break;
//            case 5765:
//                channel = 153;
//                break;
//            case 5785:
//                channel = 157;
//                break;
//            case 5805:
//                channel = 161;
//                break;
//            case 5825:
//                channel = 165;
//                break;
//        }
//        Timber.tag("[Location Service Mgr]").i("Channel: %s", channel);
//        return channel;
//    }
//
//    /**
//     * Get carrier (operator) name by country code
//     * @param operatorString
//     * @return
//     */
//    public static String getCarrier(String operatorString){
//        if(operatorString == null)
//            return "0";
//
//        if(operatorString.equals("46000") || operatorString.equals("46002"))
//            return "China Mobile";
//
//        else
//            if(operatorString.equals("46001"))
//                return "China Unicom";
//
//        else
//            if(operatorString.equals("46003"))
//                return "China Telecom";
//
//        return "Unknown Carrier";
//    }
//
//    /**
//     * Info of Cellular sent to Google to get Geo Data
//     */
//    public static class GeoLocationAPI {
//
//        /**
//         * homeMobileCountryCode : 310
//         * homeMobileNetworkCode : 410
//         * radioType : gsm
//         * carrier : Vodafone
//         * considerIp : true
//         * cellTowers : []
//         * wifiAccessPoints : []
//         */
//
//        public int homeMobileCountryCode;//MCC
//        public int homeMobileNetworkCode;//MNC
//        public String radioType;//radioType
//        public String carrier;
//        public boolean considerIp;
//        public List<GoogleCellTower> cellTowers;
//        public List<GoogleWifiInfo> wifiAccessPoints;
//
//        public String toJson(){
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("homeMobileCountryCode",homeMobileCountryCode);
//                jsonObject.put("homeMobileNetworkCode",homeMobileNetworkCode);
//                jsonObject.put("radioType",radioType);
//                jsonObject.put("carrier",carrier);
//                jsonObject.put("considerIp",considerIp);
//                if(cellTowers!=null){
//                    JSONArray jsonArray = new JSONArray();
//                    for (GoogleCellTower t:cellTowers) jsonArray.put(t.toJson());
//                    jsonObject.put("cellTowers",jsonArray);
//                }
//                if(wifiAccessPoints!=null){
//                    JSONArray jsonArray = new JSONArray();
//                    for (GoogleWifiInfo w:wifiAccessPoints) jsonArray.put(w.toJson());
//                    jsonObject.put("wifiAccessPoints",jsonArray);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return jsonObject.toString();
//
//        }
//
//    }
//
//    /**
//     * Format Cellular Data to JSON
//     */
//    public static class GoogleCellTower {
//
//        //Compulsory Part
//        int cellId;
//        int locationAreaCode;
//        int mobileCountryCode;
//        int mobileNetworkCode;
//        int signalStrength;
//
//        //Optional Part
//        int age;
//        int timingAdvance;
//
//        JSONObject toJson(){
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("cellId",cellId);
//                jsonObject.put("locationAreaCode",locationAreaCode);
//                jsonObject.put("mobileCountryCode",mobileCountryCode);
//                jsonObject.put("mobileNetworkCode",mobileNetworkCode);
//                jsonObject.put("signalStrength",signalStrength);
//                jsonObject.put("age",age);
//                jsonObject.put("timingAdvance",timingAdvance);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return jsonObject;
//        }
//    }
//
//    /**
//     * Format Wi-Fi Data to JSON
//     */
//    public static class GoogleWifiInfo {
//
//        /**
//         * macAddress : 01:23:45:67:89:AB
//         * signalStrength : -65
//         * age : 0
//         * channel : 11
//         * signalToNoiseRatio : 40
//         */
//
//        public String macAddress;
//        public int signalStrength;
//        public int age;
//        public short channel;
//        public int signalToNoiseRatio;
//
//        public JSONObject toJson(){
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("signalStrength",signalStrength);
//                jsonObject.put("age",age);
//                jsonObject.put("macAddress",macAddress);
//                jsonObject.put("channel",channel);
//                jsonObject.put("signalToNoiseRatio",signalToNoiseRatio);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return  jsonObject;
//        }
//    }
//
//
//    /**
//     * A POST request of JSON sent by httpclient
//     * @param url
//     * @return
//     */
//    public void sendJsonByPost(String json, String url){
//        this.dataJson = json;
//        RequestParams params = new RequestParams();
//        params.applicationJson(JSON.parseObject(json));
//        HttpRequest.post(url, params, new BaseHttpRequestCallback<String>(){
//            @Override
//            protected void onSuccess(String result) {
//                super.onSuccess(result);
//
//                if(result==null || context==null)
//                    return;
//
//                Timber.tag("[Location Service Mgr]").i("Success: %s", result);
//                if(isDebugging)
//                    Toast.makeText(context,"Result received from Google: " + result, Toast.LENGTH_LONG).show();
//
//                if(result==null || result.length()<10 || !result.startsWith("{"))
//                    Timber.tag("[Location Service Mgr]").i("Invalid result format: %s", result);
//
//                JSONObject returnJson = null;
//
//                try {
//                    returnJson = new JSONObject(result);
//                    JSONObject location = returnJson.getJSONObject("location");
//                    if(location==null){
//                        Timber.tag("[Location Service Mgr]").i("Unable to locate due insufficient info");
//                        return;
//                    }
//
//                    double latitude = location.getDouble("lat");
//                    double longitute = location.getDouble("lng");
//                    double google_accuracy = returnJson.getDouble("accuracy");
//                    Timber.tag("[Location Service Mgr]").i("Coordinate received from Google: " + latitude + "  :  " + longitute + "  Accuracy [" + google_accuracy + "]");
//                    if(isDebugging)
//                        Toast.makeText(context,"Coordinate received from Google: "+latitude+"  :  "+longitute+"  Accuracy ["+google_accuracy+"]",Toast.LENGTH_LONG).show();
//
//                    recordLocation(context, latitude, longitute, (float)google_accuracy);
//                } catch (JSONException e) {
//                    Timber.tag("[Location Service Mgr]").i("Unable to locate: %s", e);
//                }
//
//            }
//
//            @Override
//            public void onFailure(int errorCode, String msg) {
//                super.onFailure(errorCode, msg);
//                if(msg==null)return;
//
//                Timber.tag("[Location Service Mgr]").i("Failed: " + msg + "   " + errorCode);
//                if(errorCode==0)
//                    Timber.tag("[Location Service Mgr]").i("Google can't found your coordinate");
//                if(isDebugging)
//                    Toast.makeText(context,"Failed to locate, no Internet/Cellular connection",Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//
//
//    /**
//     * Check Wi-Fi connection
//     */
//    public void checkNetCardState(WifiManager mWifiManager) {
//        if (mWifiManager.getWifiState() == 0) {
//            Timber.tag("[Location Service Mgr]").i("Turning off Wi-Fi...");
//        } else if (mWifiManager.getWifiState() == 1) {
//            Timber.tag("[Location Service Mgr]").i("Wi-Fi turned off");
//        } else if (mWifiManager.getWifiState() == 2) {
//            Timber.tag("[Location Service Mgr]").i("Turning on Wi-Fi...");
//        } else if (mWifiManager.getWifiState() == 3) {
//            Timber.tag("[Location Service Mgr]").i("Wi-Fi turned on");
//        } else {
//            Timber.tag("[Location Service Mgr]").i("Unable to get Wi-Fi status");
//        }
//    }
//
//    public static void  getConnectedWifiInfo(WifiManager wifiManager){
//        if(wifiManager==null)return;
//        WifiInfo wifiConnection = wifiManager.getConnectionInfo();
//        if (wifiConnection != null) {
//            String wifiMAC = wifiConnection.getBSSID();
//            int i = wifiConnection.getRssi();
//            String s1 = wifiConnection.getSSID();
//            String mac = wifiConnection.getMacAddress();
//            Timber.tag("[Location Service Mgr]").i("MAC Address (Your device): %s", mac);
//        }
//    }
//
//    /**
//     * Calculate distance between two coordinate
//     * @param lat_a
//     * @param lng_a
//     * @param lat_b
//     * @param lng_b
//     * @return
//     */
//    public static double getGPSDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
//        final double M_PI = 3.14159265358979323846264338327950288, EARTH_RADIUS = 6378138.0;
//        final double dd = M_PI / 180.0;
//
//        double lon2 = lng_b;
//        double lat2 = lat_b;
//
//        double x1 = lat_a * dd, x2 = lat2 * dd;
//        double y1 = lng_a * dd, y2 = lon2 * dd;
//        double distance = (2 * EARTH_RADIUS * Math.asin(Math.sqrt(2 - 2 * Math.cos(x1)
//                * Math.cos(x2) * Math.cos(y1 - y2) - 2 * Math.sin(x1)
//                * Math.sin(x2)) / 2));
//        Timber.tag("[Location Service Mgr]").i("Distance moved: " + distance + " meter(s)");
//        if(isDebugging && locationServiceManager !=null)Toast.makeText(locationServiceManager.context,"Distance moved: "+distance+" meter(s)",Toast.LENGTH_LONG).show();
//        return distance;
//    }
//
//}
