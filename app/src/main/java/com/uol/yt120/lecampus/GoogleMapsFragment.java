/*
 * Copyright 2019 yt120@student.le.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uol.yt120.lecampus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;


public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private String googleAPIkey;
    private GoogleMap gMap;
    private Location location;
    private boolean enableFootprintTrack;
    private FootprintViewModel footprintViewModel;

    private Activity mActivity;
    private Context mContext;

    //Calendar calendar = Calendar.getInstance();
    DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();

    Marker prevMarker;
    Marker currentMarker;
    LatLng prevLatLng;
    LatLng currentLatLng;

    List<Polyline> polylineList = new ArrayList<Polyline>();

    Integer trackpointIndex = 0;
    ArrayList<HashMap<String, Object>> trackpointList = new ArrayList<HashMap<String, Object>>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getActivity()!= null) { updateContext(getContext()); }

        getActivity().setTitle(getString(R.string.title_fragment_campus_map));
        googleAPIkey = getString(R.string.google_maps_key);
        Log.w("[DEBUG INFO]", "Google API Key: ["+googleAPIkey+"]");

        View googleMapViewLayout = inflater.inflate(R.layout.fragment_google_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        mapFragment.getMapAsync(this);

        // Locating button - Google
        FloatingActionButton googleLocateButton = (FloatingActionButton) googleMapViewLayout.findViewById(R.id.button_locate_google);
        googleLocateButton.setOnClickListener(this::onClick);

        FloatingActionButton footprintTrackButton = (FloatingActionButton) googleMapViewLayout.findViewById(R.id.button_tracking_google);
        footprintTrackButton.setOnClickListener(this::onFootprintClick);
        enableFootprintTrack = false;

        return googleMapViewLayout;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        LatLng uniOfLeicester = new LatLng(52.6217, -1.1241);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uniOfLeicester,18));

        gMap.getUiSettings().setMapToolbarEnabled(false);

        // Add marker to map location
//        gMap.addMarker(new MarkerOptions().position(uniOfLeicester).title("Charles Wilson Building")
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker)));
    }

    private void onFootprintClick(View view) {
        if (!enableFootprintTrack) {
            //If footprint tracking is disabled, enable it
            showStartTrackingDialog();

        } else {
            //Notify if user want to save footprint data
            showEndTrackingDialog();
        }
    }

    //@Override Click button "locating"
    @SuppressLint("MissingPermission")
    private void onClick(View view) {

 //       LocationServiceManager.setGoogleAPIKey(googleAPIkey);
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

            // provider, minTime(ms), minDistance(metre), listener
            locationManagerGPS.requestLocationUpdates(locationServiceProvider, 1000, 5, this);

        } catch (Exception e) { // Location service permission error
            e.printStackTrace();
            if (getActivity() != null) {
                AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity())
                        .setTitle("Location Service Unavailable")
                        .setMessage(e.getMessage())
                        .setPositiveButton("OK", (dialog, which) ->
                                Timber.d(this.getContext().toString()))
                        .show();
            }

        }


    }

    @SuppressLint("MissingPermission")
    public void updateLocation(Location locationForUpdate) {

        if (getActivity()!= null) { updateContext(getContext()); }

        Location location1 = locationForUpdate;
        Location location2 = null;
        double ACCURACY1 = 500.0;
        double accuracy2 = 500.0;

        try {
            LocationManager locationManager2 = (LocationManager) Objects.requireNonNull(mContext).getSystemService(Context.LOCATION_SERVICE);
            location2 = locationManager2.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            if (getActivity() != null) {
                AlertDialog alertDialog2 = new AlertDialog.Builder(getActivity())
                        .setTitle("Network Unavailable")
                        .setMessage(e.getMessage())
                        .setPositiveButton("OK", (dialog, which) ->
                                Timber.d(this.getContext().toString()))
                        .show();
            }

        }

        if (location1 != null) {
            Log.i("[Google Map Fragmt]", "Default [1] - LAT: "+location1.getLatitude()+", LNG: "+location1.getLongitude()+", ALT: "+location1.getAltitude()+", ACC: "+location1.getAccuracy());
        }

        if (location2 != null) {
            Log.i("[Google Map Fragmt]", "Network [2] - LAT: "+location2.getLatitude()+", LNG: "+location2.getLongitude()+", ALT: "+location2.getAltitude()+", ACC: "+location2.getAccuracy());
            accuracy2 = location2.getAccuracy();
        }

        if (ACCURACY1 > accuracy2) {
            location1 = location2;
        } else {
            location1 = locationForUpdate;
        }

        try {
            ACCURACY1 = location1.getAccuracy();
        } catch (Exception e) {

        }

        String statusMsg;
        if (location1 != null && ACCURACY1 <= 40) {
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
                    currentMarker.remove();
                }
                currentMarker = gMap.addMarker(new MarkerOptions().position(currentLatLng).flat(true)
                        .title("LOCATION").snippet("LAT: "+currentLatitude+"\nLNG: "+currentLongitude+"\nALT: "+currentAltitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker)));

                if (enableFootprintTrack) {
                    if (prevLatLng != null && currentLatLng != null) {
                        Polyline polyline = gMap.addPolyline((new PolylineOptions()).add(prevLatLng, currentLatLng).width(9).color(Color.GRAY).visible(true));
                        int index = trackpointIndex+1;

                        HashMap<String, Object> trackpoint = new HashMap<>();
                        trackpoint.put("index", index);
                        Date trackpointDate = new Date(System.currentTimeMillis());
                        trackpoint.put("time", dateTimeFormatter.formatDateToString(trackpointDate, "default"));
                        trackpoint.put("lat", location1.getLatitude());
                        trackpoint.put("lon", location1.getLongitude());
                        trackpoint.put("allInfo", location1.toString());

                        trackpointList.add(trackpoint);
                        Log.w("[DEBUG INFO]", "Current List: ["+trackpointList.toString()+"]");
                        trackpointIndex = index;
                        Log.w("[DEBUG INFO]", "Global Index: ["+trackpointIndex+"]");
                        polylineList.add(polyline);
                    }
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
                if (getActivity() != null) {
                    AlertDialog alertDialog3 = new AlertDialog.Builder(getActivity())
                            .setTitle("Unable to update your location")
                            .setMessage(e.getMessage())
                            .setPositiveButton("OK", (dialog, which) ->
                                    Timber.d(this.getContext().toString()))
                            .show();
                }

            }


        } else {
            if (locationForUpdate != null) {
                Log.i("[Google Map Fragmt]", "Discarded - LAT: "+locationForUpdate.getLatitude()+", LNG: "+locationForUpdate.getLongitude()+", ALT: "+locationForUpdate.getAltitude()+", ACC: "+locationForUpdate.getAccuracy());
            }
            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.button_locate_google),
                    "Unable to get your current location due to low data accuracy.", Snackbar.LENGTH_LONG)
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
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new MapBoxMapsFragment())
                        .commit();

                return true;
            case R.id.action_opt_2:
                Toast.makeText(getActivity(), "Option_2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showStartTrackingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Recording Footprint");
        builder.setMessage("You are about to start recording your footprint, continue?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the operation
                        enableFootprintTrack = false;
                        trackpointIndex = 0;
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User approved the operation
                        enableFootprintTrack = true;
                        trackpointIndex = 0;
                    }
                });
        builder.show();

    }

    private void showEndTrackingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Stop Recording Footprint");
        builder.setMessage("Do you want to stop recording and save your footprint?");
        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the operation, stop tracking, clear existing footprint
                    enableFootprintTrack = false;
                    trackpointIndex = 0;
                    clearAllTrackpoints();
                    clearAllPolylines();
                }
            })
            .setNegativeButton("Keep Recording", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the operation, continue tracking
                    enableFootprintTrack = true;
                }
            })
            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User approved the operation, footprint tracking stopped, save footprint
                    enableFootprintTrack = false;
                    //Date saveTime = calendar.getTime();
                    Date saveTime = new Date(System.currentTimeMillis());
                    Log.w("DEBUG", "SAVE DATE: "+saveTime.toString());


                    String title = "New Footprint";
                    String desc = "No description.";
                    String timeCreated = dateTimeFormatter.formatDateToString(saveTime, "default");

                    saveFootprint(title, desc, timeCreated, trackpointList);

                    trackpointIndex = 0;
                    clearAllTrackpoints();
                    clearAllPolylines();
                }
            });
        builder.show();

    }

    private void saveFootprint(String title, String desc, String timeCreated, ArrayList<HashMap<String, Object>> nodeList){
        footprintViewModel = ViewModelProviders.of(getActivity()).get(FootprintViewModel.class);

        JSONArray nodeJSONArray = new JSONArray();
        for (HashMap<String, Object> node : nodeList) {
            JSONObject nodeJSONElement = new JSONObject(node);
            nodeJSONArray.put(nodeJSONElement);
        }
        String nodeListJSON = nodeJSONArray.toString();

        Footprint footprint = new Footprint(title, desc, nodeListJSON, timeCreated);
        footprintViewModel.insert(footprint);
        Toast.makeText(getActivity(), "Footprint saved - "+timeCreated, Toast.LENGTH_SHORT).show();
    }

    private void clearAllPolylines() {
        if (!polylineList.isEmpty()) {
            for (Polyline line : polylineList) {
                line.remove();
            }
            polylineList.clear();
        }
    }

    private void clearAllTrackpoints() {
        if (!trackpointList.isEmpty()) {
            trackpointList.clear();
        }
    }

    /**
     * Due to the different lifecycle, Activity may be recycled
     * by the system with the Fragment still existed. To prevent the
     * return value 'null' from getActivity() when the Activity is
     * recycled...
     *
     * @param context
     */
    private void updateContext(Context context) {
        this.mContext = context;
        this.mActivity = getActivity();
    }

    private void clearContext() {
        mActivity = null;
        mContext = null;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearContext();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateContext(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clearContext();
    }

}