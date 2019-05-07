package com.uol.yt120.lecampus.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.restAPI.RestApiClient;
import com.uol.yt120.lecampus.model.restDomain.CrimeGeoFence;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.utility.HttpHandler;
import com.uol.yt120.lecampus.view.NavigationActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecurityFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = SecurityFragment.class.getSimpleName();
    public static final String SECURITY_NOTIFIER_STATUS = "Security_Notifier_Status";

    private MapView crimeMapView;
    private View securityView;
    private GoogleMap crimeMap;
    private Switch securitySwitch;
    private SharedPreferences sharedPreferences;
    private HeatmapTileProvider heatmapTileProvider;
    private TileOverlay tileOverlay;
    List<WeightedLatLng> datalist = new ArrayList<>();
    //private List<CrimeGeoFence> crimeGeoFences;

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
        sharedPreferences = getActivity().getSharedPreferences(NavigationActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        securitySwitch = securityView.findViewById(R.id.switch_security_notifier);
        securitySwitch.setChecked(sharedPreferences.getBoolean(SECURITY_NOTIFIER_STATUS, false));
        securitySwitch.setOnClickListener((switchView) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SECURITY_NOTIFIER_STATUS, securitySwitch.isChecked());
            editor.apply();
            if (securitySwitch.isChecked()) {
                Toast.makeText(getContext(), "Security Notifier Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Security Notifier Disabled", Toast.LENGTH_SHORT).show();
            }

        });

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
        datalist.clear();
        loadCrimeData(getDateForRequest(-3));
        loadCrimeData(getDateForRequest(-4));
        loadCrimeData(getDateForRequest(-5));
        crimeMapView.onResume();

    }

    public void loadCrimeData(String dateForRequest) {
        HttpHandler httpHandler = new HttpHandler();
        RestApiClient restApiClient = httpHandler.initRestApiClient();
        Call<List<CrimeGeoFence>> crimeCall = restApiClient.getCrimeGeoFences(dateForRequest);
        Log.w("[Security Fragment]", "Loading Crime Data");
        crimeCall.enqueue(new Callback<List<CrimeGeoFence>>() {
            @Override
            public void onResponse(Call<List<CrimeGeoFence>> call, Response<List<CrimeGeoFence>> response) {
                if (!response.isSuccessful()) {
                    Log.e("[Security Fragment]", "Error occurred, Code: "+response.code()); // 404 or other
                    return;
                }
                List<CrimeGeoFence> crimeGeoFences = response.body();
                Log.e("[Security Fragment]", "Received crime data: " + dateForRequest);
                if (!crimeGeoFences.isEmpty()) {
                    generateHeatMap(crimeGeoFences);
                }

            }

            @Override
            public void onFailure(Call<List<CrimeGeoFence>> call, Throwable t) {
                Log.e("[Security Fragment]", "Error occurred, Info: " + t.getMessage());
            }
        });
    }

    private void generateHeatMap(List<CrimeGeoFence> crimeGeoFences) {

        for (CrimeGeoFence crimeGeoFence: crimeGeoFences) {
            double lat = Double.valueOf(crimeGeoFence.getLat());
            double lng = Double.valueOf(crimeGeoFence.getLng());
            LatLng latLng = new LatLng(lat, lng);
            Log.i("[Security Fragment]", "Data for Heatmap "+latLng.toString());
            datalist.add(new WeightedLatLng(latLng, 1));
        }

        heatmapTileProvider = new HeatmapTileProvider.Builder()
                .weightedData(datalist)
                .build();

        // Add a heat zone overlay to the map
        tileOverlay.clearTileCache();
        tileOverlay = crimeMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
        //crimeMap.addMarker(new MarkerOptions().position(new LatLng(52.633904, -1.131657)).flat(true));

    }

    private String getDateForRequest(int monthsAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthsAgo);
        Date date = calendar.getTime();
        DateTimeFormatter dtf = new DateTimeFormatter();

        return dtf.formatDateToString(date, "year_month");
    }
}
