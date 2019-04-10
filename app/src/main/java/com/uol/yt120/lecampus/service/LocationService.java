//package com.uol.yt120.lecampus;
//
//import android.app.IntentService;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import timber.log.Timber;
//
//public class LocationService extends IntentService {
//
//    private LocationManager locationManager;
//    private String locationProvider;
//    private ArrayList<String> providerArray;
//    public static boolean endProcess;
//
//    private LocationListener listenerGPS = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };
//
//    private LocationListener listenerPassive = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };
//
//    private LocationListener listenerNetwork = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };
//
//    public LocationService() {
//        super("LS");
//        providerArray = new ArrayList<>();
//        providerArray.add(LocationManager.GPS_PROVIDER);
//        providerArray.add(LocationManager.NETWORK_PROVIDER);
//        providerArray.add(LocationManager.PASSIVE_PROVIDER);
//        endProcess = false;
//
//    }
//
//    /**
//     * Determine the best location service provider - GPS, Network or other
//     */
//    private synchronized void getBestLocationProvider() {
//        if (locationManager == null) {
//            locationProvider = null;
//            return;
//        }
//
//        List<String> providerList = locationManager.getAllProviders();
//        if (providerList == null || providerList.size() <= 0) {
//            locationProvider = null;
//            return;
//        }
//
//        Location bestLocation = null;
//        String bestProvider = null;
//        for (String provider : providerList) {
//            Timber.tag("[Location Service]").i("Provider Info - %s", provider);
//            if ((provider != null) && (providerArray.contains(provider))) {
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    Timber.tag("[Location Service]").i("Access denied 1");
//                    return;
//                }
//                Location location = locationManager.getLastKnownLocation(provider);
//
//                Timber.tag("[Location Service]").i("Location Info - %s", location);
//                if (location == null) {
//                    continue;
//                }
//
//                Timber.tag("[Location Service]").i("Best Location Info - %s", bestLocation);
//                if (bestLocation == null) {
//                    bestLocation = location;
//                    bestProvider = provider;
//                    continue;
//                }
//
//                Timber.tag("[Location Service]").i("Location Accuracy [" + location.getAccuracy() + "]  Best Location Accuracy [" + bestLocation.getAccuracy() + "]");
//                if (Float.compare(location.getAccuracy(), bestLocation.getAccuracy()) >= 0) {
//                    bestLocation = location;
//                    bestProvider = provider;
//                }
//            }
//        }
//
//        locationProvider = bestProvider;
//
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Timber.tag("[Location Service]").i("Intent start");
//        locationProvider = null;
//        locationManager = null;
//
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager == null) {
//            return;
//        }
//
//        List<String> allProviderList = locationManager.getAllProviders();
//        Timber.tag("[Location Service]").i("All providers detected [" + allProviderList + "]");
//        if (allProviderList != null) {
//            for (String provider : allProviderList) {
//                Timber.tag("[Location Service]").i("Current provider being tested [" + provider + "]");
//                if ((provider != null) && (providerArray.contains(provider))) {
//                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        Timber.tag("[Location Service]").i("Access denied 2");
//                        return;
//                    }
//
//                    if (LocationManager.GPS_PROVIDER.equals(provider)) {
//                        Timber.tag("[Location Service]").i("Provider [GPS]");
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LocationServiceManager.FAST_UPDATE_INTERVAL, 0, listenerGPS);
//
//                    } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
//                        Timber.tag("[Location Service]").i("Provider [Network]");
//                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LocationServiceManager.FAST_UPDATE_INTERVAL, 0, listenerNetwork);
//
//                    } else if (LocationManager.PASSIVE_PROVIDER.equals(provider)) {
//                        Timber.tag("[Location Service]").i("Provider [Passive]");
//                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, LocationServiceManager.FAST_UPDATE_INTERVAL, 0, listenerPassive);
//                    }
//                }
//            }
//        }
//
//        while (!endProcess) {
//            getBestLocationProvider();
//            Timber.tag("[Location Service]").i("Location Provider [" + locationProvider + "]");
//
//            updateLocation();
//            Timber.tag("[Location Service]").i("Process end [" + endProcess + "]");
//
//            if (endProcess) return; // For the purpose of saving power
//
//            if ((locationProvider != null) && (providerArray.contains(locationProvider))) {
//                try {
//                    if (!isWrongPosition(UserLocation.getInstance().lat, UserLocation.getInstance().lon)) {
//                        endProcess = true;
//                    } else {
//                        Thread.sleep(LocationServiceManager.FAST_UPDATE_INTERVAL);
//                    }
//                } catch (InterruptedException ex) {
//                    Log.i("[Location Service]", " onHandleIntent ", ex);
//                }
//            } else {
//                try {
//                    Thread.sleep(LocationServiceManager.FAST_UPDATE_INTERVAL);
//                } catch (Exception ex) {
//                    Log.i("AlexLocation", " onHandleIntent ", ex);
//                }
//            }
//        }
//    }
//
//    private void updateLocation() {
//        Log.i("[Location Service]", " ----> updateLocation <---- locationProvider => " + locationProvider);
//        if ((locationProvider != null) && (!locationProvider.equals("")) && (providerArray.contains(locationProvider))) {
//            try {
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    Timber.tag("[Location Service]").i("Access denied 3");
//                    return;
//                }
//                Location currentLocation = locationManager.getLastKnownLocation(locationProvider);
//                Log.i("[Location Service]","Obtained by Old version: Longitude="+currentLocation.getLongitude()+", Latitude="+currentLocation.getLatitude()+", Provider="+locationProvider);
//                if(LocationServiceManager.isDebugging)
//                    Toast.makeText(getApplicationContext(),"Obtained by Android: Longitude="+currentLocation.getLongitude()+", Latitude="+currentLocation.getLatitude(),Toast.LENGTH_LONG).show();
//                Log.i("[Location Service]", "locationProvider -> " + locationProvider + "  currentLocation -> " + currentLocation);
//                if (currentLocation != null) {
//                    final double newLatitude = currentLocation.getLatitude();
//                    final double newLongitude = currentLocation.getLongitude();
//                    final float accuracy = currentLocation.getAccuracy();
//                    Log.i("[Location Service]", "locationProvider (" + newLatitude + " : " + newLongitude + "), Accuracy="+accuracy);
//                    if(!isWrongPosition(newLatitude,newLongitude))LocationServiceManager.recordLocation(this,newLatitude,newLongitude,accuracy);
//                    if(LocationServiceManager.locationServiceManager !=null)LocationServiceManager.locationServiceManager.currentStatus = LocationServiceManager.STATUS.NOT_TRACK;
//
//                    if(!isWrongPosition(newLatitude,newLongitude))endProcess=true;
//                }
//            } catch (Exception ex) {
//                Timber.tag("[Location Service]").i(ex, " updateLocation ");
//            }
//        }
//    }
//
//    /**
//     * Determine if the coordinate is 0,0 (Invalid)
//     * @param latitude
//     * @param longitude
//     * @return
//     */
//    public static boolean isWrongPosition(double latitude,double longitude){
//        return Math.abs(latitude) < 0.01 && Math.abs(longitude) < 0.1;
//    }
//
//    @Override
//    public void onDestroy() {
//        Log.i("[Location Service]", " --> onDestroy");
//        super.onDestroy();
//        endProcess = true;
//
//        if ((locationManager != null) && (listenerGPS != null)) {
//            locationManager.removeUpdates(listenerGPS);
//        }
//
//        if ((locationManager != null) && (listenerNetwork != null)) {
//            locationManager.removeUpdates(listenerNetwork);
//        }
//
//        if ((locationManager != null) && (listenerPassive != null)) {
//            locationManager.removeUpdates(listenerPassive);
//        }
//    }
//
//}
