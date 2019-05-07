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

package com.uol.yt120.lecampus.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uol.yt120.lecampus.publicAsyncTasks.ProcessDirectionPathAsyncTask;
import com.uol.yt120.lecampus.utility.MapDrawer;
import com.uol.yt120.lecampus.view.NavigationActivity;
import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.model.domain.Footprint;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;
import com.uol.yt120.lecampus.viewModel.LocationDataCacheViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback, ProcessDirectionPathAsyncTask.Response {

    public static final String TAG = GoogleMapsFragment.class.getSimpleName();
    public static final String KEY_GOOGLE_MAPS_DATA = "com.uol.yt120.lecampus.googlemapsfragment";
    public static final String START_RECORDING_CODE = "rec_fprint";
    public static final String VIEW_FOOTPRINT_CODE = "view_fprint";
    public static final String VIEW_CRIME_CODE = "view_crime";

    private DataPassListener mCallback;
    private LocationDataCacheViewModel locationDataCacheViewModel;
    private ProgressDialog progressDialog;

    private GoogleMap gMap;
    private boolean footprintTrackEnabled;
    private FootprintViewModel footprintViewModel;

    private Activity mActivity;
    private Context mContext;
    private boolean locationEnabled = false;
    private boolean directionEnabled = false;

    private FloatingActionButton googleLocateButton;
    private static Polyline directionPolyline;
    private boolean cameraMovingEnabled = true;

    DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();

    Marker currentMarker = null;
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
        locationDataCacheViewModel = ViewModelProviders.of(getActivity()).get(LocationDataCacheViewModel.class);

        View googleMapViewLayout = inflater.inflate(R.layout.fragment_google_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        mapFragment.getMapAsync(this);

        // Locating button - Google
        googleLocateButton = (FloatingActionButton) googleMapViewLayout.findViewById(R.id.button_locate_google);
        googleLocateButton.setOnClickListener(this::onClick);

        FloatingActionButton footprintTrackButton = (FloatingActionButton) googleMapViewLayout.findViewById(R.id.button_tracking_google);
        footprintTrackButton.setOnClickListener(this::onFootprintClick);
        footprintTrackEnabled = false;

        locationDataCacheViewModel.getMutableCurrentLocationLiveData().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                if (location != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (directionEnabled) {
                        ((NavigationActivity)getActivity()).updateGoogleLocationService(3,2);
                    }
                    if (locationEnabled) {
                        updateLocation(location);
                    }
                }
            }
        });

        return googleMapViewLayout;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });

        LatLng uniOfLeicester = new LatLng(52.6217, -1.1241);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uniOfLeicester,18));
        Log.w("[GoogleMapsFragment]", "Map loaded");

    }

    private void onFootprintClick(View view) {
        if (!footprintTrackEnabled) {
            //If footprint tracking is disabled, enable it
            showStartTrackingDialog();

        } else {
            //Notify if user want to save footprint data
            showEndTrackingDialog();
        }
    }

    //@Override Click button "locating"
    private void onClick(View view) {

        if (locationEnabled) {
            locationEnabled = false;
            cameraMovingEnabled = false;
            googleLocateButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_map_locate));
            Snackbar.make(view, "Stop showing your location.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            if (footprintTrackEnabled) {
                showEndTrackingDialog();
            }
            ((NavigationActivity)getActivity()).updateGoogleLocationService(10,5);
        } else {
            locationEnabled = true;
            cameraMovingEnabled = true;
            googleLocateButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_map_location_disabled));
            Snackbar.make(view, "Getting your current location...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            ((NavigationActivity)getActivity()).updateGoogleLocationService(3,2);
        }

    }

    public void updateLocation(Location locationForUpdate) {

        if (getActivity()!= null) { updateContext(getContext()); }

        try {
            double currentLatitude = locationForUpdate.getLatitude();
            double currentLongitude = locationForUpdate.getLongitude();
            double currentAltitude = locationForUpdate.getAltitude();
            double currentAccuracy = locationForUpdate.getAccuracy();

            //Log.i("[Google Map Fragmt]", "Updated - LAT: "+currentLatitude+", LNG: "+currentLongitude+", ALT: "+currentAltitude+", ACC: "+currentAccuracy);

            if (currentLatLng != null) {
                prevLatLng = currentLatLng;
            }

            currentLatLng = new LatLng(currentLatitude, currentLongitude);

            if (cameraMovingEnabled) {
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f), 4000, null);// Updated coordinate
            }

            if (currentMarker == null) {
                currentMarker = gMap.addMarker(new MarkerOptions().position(currentLatLng).flat(true));
            }

            currentMarker.setTitle("LOCATION");
            currentMarker.setSnippet("LAT: "+currentLatitude+"\nLNG: "+currentLongitude+"\nALT: "+currentAltitude);
            currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker));

            animateMarker(currentMarker, currentLatLng, false);

            if (footprintTrackEnabled) {
                // If user moved, record the movement
                if (prevLatLng != null && currentLatLng != null) {
                    Polyline polyline =
                            gMap.addPolyline((new PolylineOptions())
                                    .add(prevLatLng, currentLatLng)
                                    .geodesic(true)
                                    .width(9)
                                    .color(Color.GRAY)
                                    .visible(true));

                    int index = trackpointIndex+1;

                    HashMap<String, Object> trackpoint = new HashMap<>();
                    trackpoint.put("index", index);
                    Date trackpointDate = new Date(System.currentTimeMillis());
                    trackpoint.put("time", dateTimeFormatter.formatDateToString(trackpointDate, "default"));
                    trackpoint.put("lat", locationForUpdate.getLatitude());
                    trackpoint.put("lon", locationForUpdate.getLongitude());
                    trackpoint.put("allInfo", locationForUpdate.toString());

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
                        .setTitle("Unable to update your location #0340")
                        .setMessage("Please try again later. \nDEBUG INFO:"+e.getMessage())
                        .setPositiveButton("OK", (dialog, which) ->
                                Timber.d(this.getContext().toString()))
                        .show();
            }

        }

    }
    @Override
    public void onStart() {
        locationDataCacheViewModel.getLatLngForDirection().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(@Nullable LatLng targetLatLng) {
                if (targetLatLng != null && currentLatLng != null) {
                    Log.i("[GoogleMapsFragment]", "Current Latlng: "+currentLatLng);
                    try {
                        ((NavigationActivity)getActivity()).updateGoogleLocationService(3,2); // location update interval (second)
                        getDirectionPath(currentLatLng.latitude, currentLatLng.longitude, targetLatLng.latitude, targetLatLng.longitude, MapDrawer.MODE_WALK);
                        cameraMovingEnabled = false;
                        directionEnabled = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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
                        .replace(R.id.fragment_container, new MapBoxMapsFragment(), MapBoxMapsFragment.TAG)
                        .commit();

                return true;
            case R.id.action_layers_google_maps:
                Toast.makeText(getActivity(), "Switch to satellite map", Toast.LENGTH_SHORT).show();
                if (gMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                return true;

            case R.id.action_clear_direction:
                if (directionPolyline != null) {
                    directionPolyline.remove();
                }
                //gMap.clear();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Create a confirmation dialog, ask if user want to start recording footprint
     */
    private void showStartTrackingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Recording Footprint");
        builder.setMessage("You are about to start recording your footprint, continue?");

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the operation
                        footprintTrackEnabled = false;
                        trackpointIndex = 0;

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User approved the operation
                        footprintTrackEnabled = true;
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
                    footprintTrackEnabled = false;
                    trackpointIndex = 0;
                    clearAllTrackpoints();
                    clearAllPolylines();
                }
            })
            .setNegativeButton("Keep Recording", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the operation, continue tracking
                    footprintTrackEnabled = true;
                }
            })
            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User approved the operation, footprint tracking stopped, save footprint
                    footprintTrackEnabled = false;
                    //Date saveTime = calendar.getTime();
                    Date saveTime = new Date(System.currentTimeMillis());

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
        Integer privacy = 0;

        Footprint footprint = new Footprint(title, desc, nodeListJSON, timeCreated, privacy);
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
     *  Title: Enable Marker Animation
     *    Author: GrIsHu
     *    Date: 11 April 2013
     *    Code version: 1.0
     *    Availability: https://stackoverflow.com/questions/13872803/
     *
     * @param marker Current marker on the map
     * @param targetLocation Destination of marker movement
     * @param hideMarker Marker visibility
     */
    public void animateMarker(final Marker marker, final LatLng targetLocation,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = gMap.getProjection();
        android.graphics.Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * targetLocation.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * targetLocation.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Move every 16ms to get 60fps
                    handler.postDelayed(this, 16);

                } else {
                    if (hideMarker) {
                        marker.setVisible(false);

                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void getDirectionPath(double startLat, double startLng,
                                          double endLat, double endLng, String mode) {

        MapDrawer mapDrawer = new MapDrawer();
        String requestUrl = mapDrawer.makeDirectionRequestURL(startLat, startLng,
                        endLat, endLng, MapDrawer.MODE_WALK);

        new ProcessDirectionPathAsyncTask(requestUrl, this).execute();
    }

    @Override
    public void startProcessPath() {

    }

    @Override
    public void showPath(List<LatLng> pathList) {
        Log.i("[GoogleMapsFragment]", "Got path: "+pathList.toString());
        directionPolyline.remove();
        directionPolyline = gMap.addPolyline(new PolylineOptions()
                .addAll(pathList)
                .width(9)
                .color(Color.GRAY)
                .geodesic(true)
        );
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

        for (LatLng point : pathList) {
            latLngBoundsBuilder.include(point);
        }

        LatLngBounds bounds = latLngBoundsBuilder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.10); // offset from edges of the map 20% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        gMap.animateCamera(cu);
        locationDataCacheViewModel.setLatLngForDirection(null);
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
        try {
            mCallback = (DataPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ " did not implement DataPassListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clearContext();
    }

}