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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.model.domain.Footprint;
import com.uol.yt120.lecampus.viewModel.FootprintCacheViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class FootprintDetailFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = FootprintDetailFragment.class.getSimpleName();

    public static final int ADD_FOOTPRINT_REQUEST = 1;
    public static final int VIEW_FOOTPRINT_REQUEST = 2;
    public static final int EDIT_FOOTPRINT_REQUEST = 3;

    public static final String KEY_FOOTPRINT_DATA_RECEIVED = "com.uol.yt120.lecampus.KEY_FOOTPRINT_DATA_RECEIVED";

    public static final String EXTRA_ID = "com.uol.yt120.lecampus.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.uol.yt120.lecampus.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.uol.yt120.lecampus.EXTRA_DESC";
    public static final String EXTRA_NODELIST = "com.uol.yt120.lecampus.EXTRA_NODELIST";
    public static final String EXTRA_TIMECREATED = "com.uol.yt120.lecampus.EXTRA_TIMECREATED";
    public static final String EXTRA_PUBLISHER = "com.uol.yt120.lecampus.EXTRA_PUBLISHER";

    DataPassListener mCallback;
    String footprintDetailData;
    String footprintDataForEdit;
    JSONArray trackpointJSONArray;

    private TextView footprintTitleView;
    private TextView footprintDescView;
    private TextView footprintTimeCreated;
    private TextView footprintPublisher;
    private FootprintCacheViewModel footprintCacheViewModel;
    //private FootprintEditViewModel footprintEditViewModel;
    private FloatingActionButton buttonStartFootprintDirection;

    private GoogleMap gMap;
    MapView mapView;
    int footprintId = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().setTitle("Footprint Detail");
        }
        View footprintDetailView = inflater.inflate(R.layout.fragment_footprint_detail, container, false);
        buttonStartFootprintDirection = footprintDetailView.findViewById(R.id.button_footprint_detail_direction);

        buttonStartFootprintDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container, new GoogleMapsFragment(), GoogleMapsFragment.TAG)
                            .commit();
                }
            }
        });

        footprintTitleView = (TextView) footprintDetailView.findViewById(R.id.text_footprint_detail_title);
        footprintDescView = (TextView) footprintDetailView.findViewById(R.id.text_footprint_detail_description);
        footprintTimeCreated = (TextView) footprintDetailView.findViewById(R.id.text_footprint_detail_timecreated);
        footprintPublisher = (TextView) footprintDetailView.findViewById(R.id.text_footprint_detail_publisher);

        mapView = (MapView) footprintDetailView.findViewById(R.id.google_map_for_detail);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return footprintDetailView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_footprint_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_footprint_detail_edit:
                // Footprint data in cacheViewModel has been set in Footprint fragment;
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, new FootprintEditFragment())
                        .addToBackStack(FootprintDetailFragment.TAG)
                        .commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    private void updateView() {
        footprintCacheViewModel = ViewModelProviders.of(getActivity()).get(FootprintCacheViewModel.class);
        footprintCacheViewModel.getSelectedFootprint().observe(this, new Observer<Footprint>() {
            @Override
            public void onChanged(@Nullable Footprint footprint) {
                footprintTitleView.setText(footprint.getTitle());
                footprintDescView.setText(footprint.getDescription());
                footprintTimeCreated.setText(footprint.getCreateTime());
                footprintPublisher.setText("default");

                try {
                    trackpointJSONArray = new JSONArray(footprint.getNodeList());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        boolean isCorrectData = true;
        Location lastLocation;
        Integer prevIndex = 0;
        Integer currentIndex = null;

        Polyline polyline;
        LatLng lastLatLng = null;
        LatLng currentLatLng = null;
        LatLng uniOfLeicester = new LatLng(52.6217, -1.1241);
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

        Marker lastMarker;
        Marker currentMarker;

        gMap.getUiSettings().setMapToolbarEnabled(false);

        /*
            MAP_TYPE_NORMAL: Basic map.
            MAP_TYPE_SATELLITE: Satellite imagery.
            MAP_TYPE_HYBRID: Satellite imagery with roads and labels.
            MAP_TYPE_TERRAIN: Topographic data.
            MAP_TYPE_NONE: No base map tiles.

            gMap.setMapType();
         */
        //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(uniOfLeicester,18));

        try {
            Log.w("[DEBUG INFO]", "NodeList JSONArray casted: ["+trackpointJSONArray+"]");
            JSONArray nodeJSONArray = trackpointJSONArray;

            for (int i=0; i<nodeJSONArray.length(); i++) {
                if (currentLatLng != null) { lastLatLng = currentLatLng; }
                if (currentIndex != null) { prevIndex = currentIndex; }

                JSONObject nodeJSON = nodeJSONArray.getJSONObject(i);
                currentIndex = nodeJSON.getInt("index");
                String dateTime = nodeJSON.getString("time");
                String locationInfo = (String)nodeJSON.get("allInfo");

                if (currentIndex == prevIndex+1) {

                    if (currentIndex == 1) {
                        // Add marker as start point
                    } else if ( currentIndex == nodeJSONArray.length()) {
                        // Add marker as end point
                    }

                    double lat = (double)nodeJSON.get("lat");
                    double lon = (double)nodeJSON.get("lon");
                    currentLatLng = new LatLng(lat, lon);

                    latLngBoundsBuilder.include(currentLatLng);

                    if (lastLatLng != null) {
                        polyline = gMap.addPolyline((new PolylineOptions()).add(lastLatLng, currentLatLng).width(9).color(Color.GRAY).visible(true));
                    }

                } else {
                    Log.e(FootprintDetailFragment.TAG, "Node JSON data contains error.");
                }

            }

            LatLngBounds bounds = latLngBoundsBuilder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (height * 0.10); // offset from edges of the map 20% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            // Camera effect: flating, rotating. etc

            gMap.animateCamera(cu);

        } catch (Exception e) {
            Log.e(FootprintDetailFragment.TAG, "Invalid node JSON data.");
            e.printStackTrace();
        }

        mapView.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Makes sure that the host activity has implemented the callback interface
        try {
            mCallback = (DataPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ " did not implement DataPassListener");
        }
    }

    /**
     * Set screen auto-rotation disabled in current Fragment.
     */
    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

}
