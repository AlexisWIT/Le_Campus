package com.uol.yt120.lecampus;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Objects;

import timber.log.Timber;


public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private String googleAPIkey;
    private GoogleMap gMap;
    private Location location;
    private boolean enableFootprintTrack;

    Marker prevMarker;
    Marker currentMarker;
    LatLng prevLatLng;
    LatLng currentLatLng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_campus_map));
        googleAPIkey = getString(R.string.google_maps_key);
        Log.i("GoogleAPIKey",googleAPIkey);

        View googleMapViewLayout = inflater.inflate(R.layout.fragment_google_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        mapFragment.getMapAsync(this);

        // Locating button - Google
        FloatingActionButton googleLocateButton = (FloatingActionButton) googleMapViewLayout.findViewById(R.id.button_locate_google);
        googleLocateButton.setOnClickListener(this::onClick);

        FloatingActionButton footprintTrackButton = googleMapViewLayout.findViewById(R.id.button_add_footprint);
        footprintTrackButton.setOnClickListener(this::onFootprintClick);
        enableFootprintTrack = false;

        return googleMapViewLayout;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        LatLng uniOfLeicester = new LatLng(52.6217, -1.1241);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uniOfLeicester,18));

        // Add marker to map location
//        gMap.addMarker(new MarkerOptions().position(uniOfLeicester).title("Charles Wilson Building")
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker)));
    }

    public void onFootprintClick(View view) {
        if (!enableFootprintTrack) {
            //If footprint tracking is disabled, enable it
            showStartTrackingDialog();

        } else {
            //Notify if user want to save footprint data
            showEndTrackingDialog();
        }
    }

    //@Override Click button "locating"
    public void onClick(View view) {

//        LocationServiceManager.setGoogleAPIKey(googleAPIkey);
//        LocationServiceManager.onCreateGPS(getActivity().getApplication());
        Snackbar.make(view, "Getting your current location...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        LocationManager locationManagerGPS = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        Log.i("[Google Map Fragmt]", "Location Manager for GPS defined: "+locationManagerGPS.getAllProviders().toString());

        Criteria criteria = new Criteria();

        criteria.setAltitudeRequired(true);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(true);

        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        double accuracy1 = 500.0;
        double accuracy2 = 500.0;

        try {
            Log.i("[Google Map Fragmt]", "Default Service Provider Found: "+locationManagerGPS.getBestProvider(criteria, true));
            String locationServiceProvider = locationManagerGPS.getBestProvider(criteria, true);

            location = locationManagerGPS.getLastKnownLocation(locationServiceProvider);
            Location location2 = locationManagerGPS.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location2 != null) {
                Log.i("[Google Map Fragmt]", "Network - LAT: "+location2.getLatitude()+", LNG: "+location2.getLongitude()+", ALT: "+location2.getAltitude()+", ACC: "+location2.getAccuracy());
                accuracy2 = location2.getAccuracy();
            }

            if (location != null) {
                Log.i("[Google Map Fragmt]", "Default - LAT: "+ location.getLatitude()+ ", LNG: "+ location.getLongitude() + ", ALT: " + location.getAltitude()+", ACC: "+location.getAccuracy());
                accuracy1 = location.getAccuracy();
            }

            if (location == null || (accuracy1 > accuracy2)) {
                location = location2;
            }

            if (location != null) {
                Log.i("[Google Map Fragmt]", "Final - LAT: "+location.getLatitude()+", LNG: "+location.getLongitude()+", ALT: "+location.getAltitude()+", ACC: "+location.getAccuracy());
            }

            //prevLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            updateLocation(location);

            // provider, minTime, minDistance, listener
            locationManagerGPS.requestLocationUpdates(locationServiceProvider, 1000, 10, this);

        } catch (Exception e) { // Location service permission error
            new AlertDialog.Builder(getActivity())
                    .setTitle("Location Service Unavailable")
                    .setMessage(e.getMessage())
                    .setPositiveButton("OK", (dialog, which) ->
                            Timber.d(this.getContext().toString()))
                    .show();
        }


    }

    public void updateLocation(Location locationForUpdate) {

        Location location1 = locationForUpdate;
        Location location2 = null;
        double accuracy1 = 500.0;
        double accuracy2 = 500.0;

        try {
            LocationManager locationManager2 = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
            location2 = locationManager2.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(getActivity())
                    .setTitle("Network Unavailable")
                    .setMessage(e.getMessage())
                    .setPositiveButton("OK", (dialog, which) ->
                            Timber.d(this.getContext().toString()))
                    .show();
        }

        if (location2 != null) {
            Log.i("[Google Map Fragmt]", "Network - LAT: "+location2.getLatitude()+", LNG: "+location2.getLongitude()+", ALT: "+location2.getAltitude()+", ACC: "+location2.getAccuracy());
            accuracy2 = location2.getAccuracy();
        }
        if (location1 != null) {
            Log.i("[Google Map Fragmt]", "Default - LAT: "+location1.getLatitude()+", LNG: "+location1.getLongitude()+", ALT: "+location1.getAltitude()+", ACC: "+location1.getAccuracy());
        }

        if (accuracy1 > accuracy2) {
            location1 = location2;
        }

        try {
            accuracy1 = location1.getAccuracy();
        } catch (Exception e) {

        }

        String statusMsg;
        if (location1 != null && accuracy1 <= 35) {
            try {
                double currentLatitude = location1.getLatitude();
                double currentLongitude = location1.getLongitude();
                double currentAltitude = location1.getAltitude();
                double currentAccuracy = location1.getAccuracy();
                Log.i("[Google Map Fragmt]", "Updated - LAT: "+currentLatitude+", LNG: "+currentLongitude+", ALT: "+currentAltitude+", ACC: "+currentAccuracy);

                if (currentLatLng != null) {
                    prevLatLng = currentLatLng;
                }

                currentLatLng = new LatLng(currentLatitude, currentLongitude);


                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f), 4000, null);// Updated coordinate

                if (currentMarker != null) {
                    prevMarker = currentMarker;
                }
                currentMarker = gMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude))
                        .title("LOCATION").snippet("LAT: "+currentLatitude+"\nLNG: "+currentLongitude+"\nALT: "+currentAltitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker)));

                if (prevMarker != null) {
                    prevMarker.remove();
                }

                if (prevLatLng != null && currentLatLng != null) {
                    gMap.addPolyline((new PolylineOptions()).add(prevLatLng, currentLatLng).width(9).color(Color.GRAY).visible(true));
                }

                // Enable marker text shown in multiple lines
                gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(getContext());
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(getContext());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(getContext());
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                new AlertDialog.Builder(getActivity())
                        .setTitle("Unable to update your location")
                        .setMessage(e.getMessage())
                        .setPositiveButton("OK", (dialog, which) ->
                                Timber.d(this.getContext().toString()))
                        .show();
            }


        } else {
            if (locationForUpdate != null) {
                Log.i("[Google Map Fragmt]", "Discarded - LAT: "+locationForUpdate.getLatitude()+", LNG: "+locationForUpdate.getLongitude()+", ALT: "+locationForUpdate.getAltitude()+", ACC: "+locationForUpdate.getAccuracy());
            }
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.button_locate_google),
                    "Unable to get your current location.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    @Override
    public void onLocationChanged(Location location)
    {
        updateLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        updateLocation(null);
    }

    @Override
    public void onProviderEnabled(String arg0)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }


    // Options specifically for Nearby fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_google_maps, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_switch_to_mapbox_map:
                Toast.makeText(getActivity(), "Switching to offline map", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new MapBoxMapsFragment()).commit();

                return true;
            case R.id.action_opt_2:
                Toast.makeText(getActivity(), "Option_2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showStartTrackingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Recording Footprint");
        builder.setMessage("You are about to start recording your footprint, continue?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the operation
                        enableFootprintTrack = false;
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User approved the operation
                        enableFootprintTrack = true;
                    }
                });
        builder.show();

    }

    private void showEndTrackingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Stop Recording Footprint");
        builder.setMessage("Do you want to stop recording and save your footprint?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the operation, continue tracking
                        enableFootprintTrack = true;
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User approved the operation, footprint tracking stopped
                        enableFootprintTrack = false;
                    }
                });
        builder.show();

    }

}