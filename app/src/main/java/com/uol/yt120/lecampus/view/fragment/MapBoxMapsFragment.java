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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.uol.yt120.lecampus.R;

public class MapBoxMapsFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = MapBoxMapsFragment.class.getSimpleName();

    private MapboxMap mMap;

    //private MapView mapView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public MapBoxMapsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_maps_access_token));

        getActivity().setTitle(getString(R.string.title_fragment_campus_map));

        View mapBoxViewLayout = inflater.inflate(R.layout.fragment_mapbox_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapbox_map);
        //mapView = (MapView) mapBoxViewLayout.findViewById(R.id.mapbox_map);
        //mapFragment.onCreate(savedInstanceState);
        mapFragment.getMapAsync(this);

        return mapBoxViewLayout;

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        mMap = mapboxMap;
        mMap.setStyleUrl(Style.MAPBOX_STREETS);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                // University of Leicester
                .target(new LatLng(52.6217, -1.1241)).zoom(16).tilt(20)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    // For MapBox ------------------------------------------------
    @Override
    public void onStart() {
        super.onStart();
        //mapView.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //mapView.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
        //mapView.onStop();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mapView.onLowMemory();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mapView.onSaveInstanceState(outState);
    }
    // For MapBox----------------------------------------------------

    // Options specifically for Nearby fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_mapbox_maps, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_switch_to_google_map:
                Toast.makeText(getActivity(), "Switching to online map", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new GoogleMapsFragment()).commit();

                return true;
            case R.id.action_opt_2:
                Toast.makeText(getActivity(), "Option_2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
