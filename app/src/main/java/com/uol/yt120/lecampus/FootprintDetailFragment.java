package com.uol.yt120.lecampus;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 */
public class FootprintDetailFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = FootprintDetailFragment.class.getSimpleName();

    public static final int ADD_FOOTPRINT_REQUEST = 1;
    public static final int VIEW_FOOTPRINT_REQUEST = 2;
    public static final int EDIT_FOOTPRINT_REQUEST = 3;

    public static final String KEY_DATA_RECEIVED = "com.uol.yt120.lecampus.KEY_DATA_RECEIVED";

    public static final String EXTRA_ID = "com.uol.yt120.lecampus.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.uol.yt120.lecampus.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.uol.yt120.lecampus.EXTRA_DESC";
    public static final String EXTRA_NODELIST = "com.uol.yt120.lecampus.EXTRA_NODELIST";
    public static final String EXTRA_TIMECREATED = "com.uol.yt120.lecampus.EXTRA_TIMECREATED";
    public static final String EXTRA_PUBLISHER = "com.uol.yt120.lecampus.EXTRA_PUBLISHER";

    String footprintDetailData;
    JSONArray trackpointJSONArray;

    TextView footprintTitleView;
    TextView footprintDescView;
    TextView footprintTimeCreated;
    TextView footprintPublisher;
    private FootprintViewModel footprintViewModel;

    private GoogleMap gMap;
    MapView mapView;
    int footprintId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Footprint Detail");
        View view = inflater.inflate(R.layout.fragment_footprint_detail, container, false);

        footprintTitleView = (TextView) view.findViewById(R.id.text_footprint_detail_title);
        footprintDescView = (TextView) view.findViewById(R.id.text_footprint_detail_description);
        footprintTimeCreated = (TextView) view.findViewById(R.id.text_footprint_detail_timecreated);
        footprintPublisher = (TextView) view.findViewById(R.id.text_footprint_detail_publisher);

        mapView = (MapView) view.findViewById(R.id.google_map_for_detail);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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
                Intent intent = new Intent(getActivity(), FootprintEditFragment.class);
                intent.putExtra(FootprintDetailFragment.EXTRA_ID, footprintId);
                intent.putExtra(FootprintDetailFragment.EXTRA_TITLE, footprintTitleView.getText());
                intent.putExtra(FootprintDetailFragment.EXTRA_DESC, footprintDescView.getText());
                intent.putExtra(FootprintDetailFragment.EXTRA_NODELIST, footprintDetailData);
                intent.putExtra(FootprintDetailFragment.EXTRA_TIMECREATED, footprintTimeCreated.getText());
//                intent.putExtra(FootprintDetailFragment.EXTRA_PRIVACY, footprintPrivacyView.getText());
                startActivityForResult(intent, EDIT_FOOTPRINT_REQUEST);


            default:
                return super.onOptionsItemSelected(item);
        }

    }



    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            Log.i("FootprintDetailFragment", "New data received.");
            String dataReceived = args.getString(KEY_DATA_RECEIVED);
            footprintDetailData = dataReceived;

            try {
                JSONObject footprintDetailJSON = new JSONObject(footprintDetailData);
                footprintId = footprintDetailJSON.getInt("footprintId");

                footprintTitleView.setText((String) footprintDetailJSON.get("title"));
                footprintDescView.setText((String) footprintDetailJSON.get("desc"));
                footprintTimeCreated.setText((String) footprintDetailJSON.get("timeCreated"));
                footprintPublisher.setText((String) footprintDetailJSON.get("publisher"));
                trackpointJSONArray = new JSONArray((String)footprintDetailJSON.get("footprint"));


            } catch (JSONException e) {
                Log.e(FootprintDetailFragment.TAG, "Invalid incoming JSON data.");
                e.printStackTrace();
            }

        }
        Log.i("FootprintDetailFragment", "No new data received.");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        boolean isCorrectData = true;
        Location lastLocation;
        Integer lastIndex = 0;
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
            JSONArray nodeJSONArray = new JSONArray(trackpointJSONArray);

            for (int i=0; i<nodeJSONArray.length(); i++) {
                if (currentLatLng != null) { lastLatLng = currentLatLng; }
                if (currentIndex != null) { lastIndex = currentIndex; }

                JSONObject nodeJSON = nodeJSONArray.getJSONObject(i);
                currentIndex = nodeJSON.getInt("index");
                String dateTime = nodeJSON.getString("time");
                Location currentLocation = (Location)nodeJSON.get("location");

                if (currentIndex == lastIndex+1) {

                    if (currentIndex == 1) {
                        // Add marker as start point
                    } else if ( currentIndex == nodeJSONArray.length()) {
                        // Add marker as end point
                    }

                    double lat = currentLocation.getLatitude();
                    double lon = currentLocation.getLongitude();
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
            int padding = (int) (height * 0.20); // offset from edges of the map 20% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            gMap.animateCamera(cu);

        } catch (Exception e) {
            Log.e(FootprintDetailFragment.TAG, "Invalid node JSON data.");
            e.printStackTrace();
        }

        // Add marker to map location
//        gMap.addMarker(new MarkerOptions().position(uniOfLeicester).title("Charles Wilson Building")
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_FOOTPRINT_REQUEST && resultCode == Activity.RESULT_OK) {
            int id = data.getIntExtra(FootprintEditFragment.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getActivity(), "Update footprint failed.", Toast.LENGTH_SHORT);
                return;
            }

            String title = data.getStringExtra(FootprintEditFragment.EXTRA_TITLE);
            String desc = data.getStringExtra(FootprintEditFragment.EXTRA_DESC);
            String nodeList = data.getStringExtra(FootprintEditFragment.EXTRA_NODELIST);
            String timeCreated = data.getStringExtra(FootprintEditFragment.EXTRA_TIMECREATED);

            footprintViewModel = ViewModelProviders.of(getActivity()).get(FootprintViewModel.class);
            Footprint footprint = new Footprint(title, desc, nodeList, timeCreated);
            footprint.setId(id);
            footprintViewModel.update(footprint);

            Toast.makeText(getActivity(), "Footprint updated.", Toast.LENGTH_SHORT);

        } else {
            Toast.makeText(getActivity(), "Edit footprint not saved.", Toast.LENGTH_SHORT).show();
        }
    }


}
