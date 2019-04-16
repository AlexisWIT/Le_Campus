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

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

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
import com.uol.yt120.lecampus.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.viewModel.FootprintDetailViewModel;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

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
    private FootprintDetailViewModel footprintDetailViewModel;
    private FloatingActionButton buttonStartFootprint;

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
        getActivity().setTitle("Footprint Detail");
        View view = inflater.inflate(R.layout.fragment_footprint_detail, container, false);
        buttonStartFootprint = view.findViewById(R.id.button_footprint_detail_direction);
        buttonStartFootprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        footprintTitleView = (TextView) view.findViewById(R.id.text_footprint_detail_title);
        footprintDescView = (TextView) view.findViewById(R.id.text_footprint_detail_description);
        footprintTimeCreated = (TextView) view.findViewById(R.id.text_footprint_detail_timecreated);
        footprintPublisher = (TextView) view.findViewById(R.id.text_footprint_detail_publisher);

        mapView = (MapView) view.findViewById(R.id.google_map_for_detail);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        updateView();
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_footprint_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_footprint_detail_edit:
                mCallback.passData(footprintDataForEdit);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateView() {
        footprintDetailViewModel = ViewModelProviders.of(getActivity()).get(FootprintDetailViewModel.class);
        footprintDetailViewModel.getSelectedFootprint().observe(this, new Observer<Footprint>() {
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

        if (footprintId != 0) {
            FootprintViewModel footprintViewModel = ViewModelProviders.of(getActivity()).get(FootprintViewModel.class);
            footprintViewModel.getFootprintById(footprintId).observe(this, new Observer<Footprint>() {
                @Override
                public void onChanged(@Nullable Footprint footprint) {

                }
            });
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        Bundle args = getArguments();
//        if (args != null) {
//            String dataReceived = args.getString(KEY_FOOTPRINT_DATA_RECEIVED);
//            Log.w("[DEBUG INFO]", "Data received: ["+dataReceived+"]");
//            footprintDetailData = dataReceived;
//
//            try {
//                JSONObject footprintDetailJSON = new JSONObject(footprintDetailData);
//                footprintId = footprintDetailJSON.getInt("footprintId");
//
//                footprintTitleView.setText(footprintDetailJSON.getString("title"));
//                footprintDescView.setText(footprintDetailJSON.getString("desc"));
//                footprintTimeCreated.setText(footprintDetailJSON.getString("timeCreated"));
//                footprintPublisher.setText(footprintDetailJSON.getString("publisher"));
//                trackpointJSONArray = new JSONArray(footprintDetailJSON.getString("footprint"));
//
//                JSONObject footprintDataForEditJSON = footprintDetailJSON;
//                try {
//                    footprintDataForEditJSON.put("from", TAG);
//                    footprintDataForEditJSON.put("to", FootprintEditFragment.TAG);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                footprintDataForEdit = footprintDataForEditJSON.toString();
//
//
//            } catch (JSONException e) {
//                Log.e(FootprintDetailFragment.TAG, "Invalid incoming JSON data.");
//                e.printStackTrace();
//            }
//
//        }
//        Log.i("FootprintDetailFragment", "No new data received.");
//    }

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

        // Add marker to map location
//        gMap.addMarker(new MarkerOptions().position(uniOfLeicester).title("Charles Wilson Building")
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker)));
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
