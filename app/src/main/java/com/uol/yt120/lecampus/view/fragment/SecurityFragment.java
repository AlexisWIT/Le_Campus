package com.uol.yt120.lecampus.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.uol.yt120.lecampus.R;

public class SecurityFragment extends Fragment implements OnMapReadyCallback {

    private MapView crimeMapView;
    private View securityView;
    private GoogleMap crimeMap;
    private Switch securitySwitch;

    private boolean securityNotifierEnabled;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_security));
        securityView = inflater.inflate(R.layout.fragment_security, container, false);
        securitySwitch = securityView.findViewById(R.id.switch_security_notifier);

        crimeMapView = securityView.findViewById(R.id.crime_map);
        crimeMapView.onCreate(savedInstanceState);
        crimeMapView.getMapAsync(this);

        return securityView;
    }

    @Override
    public void onStart() {
        securitySwitch.setOnCheckedChangeListener((switchView, isChecked) -> {
            securityNotifierEnabled = isChecked;
        });

        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        //inflater.inflate(R.menu.menu_fragment_security, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        crimeMap = googleMap;
        crimeMap.getUiSettings().setMapToolbarEnabled(false);
        crimeMap.getUiSettings().setAllGesturesEnabled(false);
        crimeMap.getUiSettings().setCompassEnabled(false);

        LatLng city = new LatLng(52.633904, -1.131657);

        crimeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city,14));

        crimeMapView.onResume();
    }
}
