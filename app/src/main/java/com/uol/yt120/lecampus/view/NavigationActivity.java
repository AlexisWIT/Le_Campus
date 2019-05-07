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

package com.uol.yt120.lecampus.view;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.model.domain.User;
import com.uol.yt120.lecampus.service.GoogleLocationService;
import com.uol.yt120.lecampus.service.SkyhookLocationService;
import com.uol.yt120.lecampus.broadcastReceiver.GoogleLocationServiceReceiver;
import com.uol.yt120.lecampus.broadcastReceiver.SkyhookLocationServiceReceiver;
import com.uol.yt120.lecampus.utility.LocationDataProcessor;
import com.uol.yt120.lecampus.utility.LocationServiceController;
import com.uol.yt120.lecampus.view.fragment.*;
import com.uol.yt120.lecampus.viewModel.LocationDataCacheViewModel;
import com.uol.yt120.lecampus.viewModel.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the only main activity in this application which is hosting several fragments
 * this activity is responsible for:
 *  - the interaction of navigation drawer
 *  - communicating between fragment and service/receiver, fragment and other fragment
 *  - managing the logic of back button (backstack)
 */
public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, DataPassListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = NavigationActivity.class.getSimpleName();

    public static final String ACTIVITY_NAVIGATION = "NavigationActivity";
    public static final String FRAGMENT_ACCOUNT = "AccountFragment";
    public static final String FRAGMENT_FOOTPRINT = "FootprintFragment";
    public static final String FRAGMENT_GOOGLE_MAPS = "GoogleMapsFragment";
    public static final String FRAGMENT_MAPBOX_MAPS = "MapBoxMapsFragment";
    public static final String FRAGMENT_NEARBY = "NearbyFragment";
    public static final String FRAGMENT_SECURITY = "SecurityFragment";
    public static final String FRAGMENT_TIMETABLE = "TimetableFragment";
    public static final String FRAGMENT_USEREVENT_DETAIL = "UserEventDetailFragment";

    public static final String LAST_KNOWN_LAT = "Last_Known_Latitude";
    public static final String LAST_KNOWN_LNG = "Last_Known_Longitude";

    public static final String SHARED_PREFS = "Shared_Preferences";

    private static final int REQUEST_PERMISSION_CODE = 123;

    private GoogleLocationServiceReceiver gleReceiver;
    private SkyhookLocationServiceReceiver shkReceiver;

    private GoogleLocationService gleService = null;
    private SkyhookLocationService shkService = null;

    private boolean isBoundToGoogleService = false;
    private boolean isBoundToSkyhookService = false; //Use startService() to start Skyhook rather than binder;

    private long backPressedTime;

    private NavigationView navigationView;

    private TextView username;
    private TextView useremail;
    private ImageView useravater;
    private int backStackEntryCount = 0;

    private SharedPreferences sharedPreferences;
    private LocationDataCacheViewModel locationDataCacheViewModel;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GoogleLocationService.LocalBinder binder = (GoogleLocationService.LocalBinder) service;
            gleService = binder.getService();
            isBoundToGoogleService = true;
            Log.w("[NavActivity]", "Service bound");
            startGoogleLocationService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            gleService = null;
            isBoundToGoogleService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gleReceiver = new GoogleLocationServiceReceiver();
        shkReceiver = new SkyhookLocationServiceReceiver();

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        setContentView(R.layout.activity_navigation);

        locationDataCacheViewModel = ViewModelProviders.of(this).get(LocationDataCacheViewModel.class);

        if (LocationServiceController.isAPPRequestingService(this)) {
            if (!locationPermissionIsGranted()) {
                requestLocationPermission();
            }
        }

        // Loading UI components
        toolbar = (Toolbar) findViewById(R.id.acitvity_tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // For account login function by pressing nav-hader
        View headerView = navigationView.getHeaderView(0);
        username = (TextView) headerView.findViewById(R.id.header_account_name);
        useremail = (TextView) headerView.findViewById(R.id.header_account_email);
        useravater = (ImageView) headerView.findViewById(R.id.header_account_avatar);

        headerView.setOnClickListener(v -> {
            Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            FragmentTransaction fragmentTransactionAccount =
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            fragmentTransactionAccount.replace(R.id.fragment_container, new AccountFragment(), FRAGMENT_ACCOUNT);
            try {
                if (currentFrag.getTag().equals(FRAGMENT_GOOGLE_MAPS) || currentFrag.getTag().equals(FRAGMENT_MAPBOX_MAPS)) {
                    fragmentTransactionAccount.addToBackStack(currentFrag.getTag());
                }
            } catch (Exception e) { }

            fragmentTransactionAccount.commit();
            drawerLayout.closeDrawers();
            try {
                navigationView.getCheckedItem().setChecked(false);
            } catch (Exception e) {
            }

        });

        // set default(Root) screen
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.fragment_container, new GoogleMapsFragment(), FRAGMENT_GOOGLE_MAPS)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_map);
        }
    }

    /**
     * Start Google location service here
     */
    @Override
    protected void onStart(){
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

//        if (!locationPermissionIsGranted()) {
//            requestLocationPermission();
//        } else {
//            gleService.requestGoogleLocationUpdates();
//        }

        bindService(new Intent(this, GoogleLocationService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        // Define a toast here and show in If statements
        Toast backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        Fragment cachedFrag;

        /*
            If current fragment is Root fragment (Map), notify exit message
         */
        if (currentFrag instanceof GoogleMapsFragment || currentFrag instanceof MapBoxMapsFragment) {
            if (backStackEntryCount != 0) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }

            if (backPressedTime + 2000 > System.currentTimeMillis()) { // Second press less than 2 sec
                backToast.cancel();
                Log.w("[NavActivityBACK]", "Pressed Twice, Current Frag is [" + currentFrag.getTag() + "], BackStack: " + backStackEntryCount);
                //cleanBackStack(BACKSTACK_CLEAN_ALL);
                super.onBackPressed();
                return;

            } else {
                backToast.show();
                Log.w("[NavActivityBACK]", "Pressed Once, Current Frag is [" + currentFrag.getTag() + "]");
            }

            backPressedTime = System.currentTimeMillis();

        /*
            If current fragment is other main fragment (Account, Timetable, Nearby, Security), back to map fragment
         */
        } else if (currentFrag instanceof AccountFragment || currentFrag instanceof TimetableFragment ||
                currentFrag instanceof NearbyFragment || currentFrag instanceof SecurityFragment || currentFrag instanceof FootprintFragment) {
            Log.w("[NavActivityBACK]", "Current Frag is Other Main Frag [" + currentFrag.getTag() + "], BackStack: " + backStackEntryCount);

            cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_MAPS);
            if (backStackEntryCount == 0) {
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, new GoogleMapsFragment(), FRAGMENT_GOOGLE_MAPS).commit();

            } else if (cachedFrag != null || cachedFrag instanceof GoogleMapsFragment){
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, cachedFrag, FRAGMENT_GOOGLE_MAPS).commit();

            } else {
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, new GoogleMapsFragment(), FRAGMENT_GOOGLE_MAPS).commit();
            }
            navigationView.setCheckedItem(R.id.nav_map);
            return;

        /*
            If current fragment is childFragment, or created by other main fragment
         */
        } else {

            Log.w("[NavActivityBACK]", "Current Frag is Other Main Frag [" + currentFrag.getTag() + "], BackStack: " + backStackEntryCount);
            if (backStackEntryCount != 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, new GoogleMapsFragment(), FRAGMENT_GOOGLE_MAPS).commit();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle menu_navigation view item clicks here.
        int id = item.getItemId();
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Fragment cachedFrag;

        switch (id) {
            case R.id.nav_map:
                Log.w("[NavActivitySwitch]", "Planned Transaction: From [" + currentFrag.getTag() + "] to [" + FRAGMENT_GOOGLE_MAPS + "]");
                String targetFragTag = FRAGMENT_GOOGLE_MAPS;
                String currentFragTag = "";

                if (currentFrag.getTag() != null) {
                    currentFragTag = currentFrag.getTag();
                }
                if (!currentFragTag.equals(targetFragTag)) {
                    cachedFrag = getSupportFragmentManager().findFragmentByTag(targetFragTag);
                    FragmentTransaction fragmentTransactionMap =
                            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                    if (cachedFrag instanceof GoogleMapsFragment) {
                        fragmentTransactionMap
                                .replace(R.id.fragment_container, cachedFrag, targetFragTag);
                        Log.w("[NavActivitySwitch]", "Found instance of [" + cachedFrag.getTag() + "] in backstack");

                    } else {
                        fragmentTransactionMap
                                .replace(R.id.fragment_container, new GoogleMapsFragment(), targetFragTag);
                    }

                    fragmentTransactionMap
                            .commit();
                }

                break;

            case R.id.nav_timetable:
                Log.w("[NavActivitySwitch]", "Planned Transaction: From [" + currentFrag.getTag() + "] to [" + FRAGMENT_TIMETABLE + "]");

                cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TIMETABLE);
                FragmentTransaction fragmentTransactionTimetable =
                        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                if (cachedFrag instanceof TimetableFragment) {
                    fragmentTransactionTimetable
                            .replace(R.id.fragment_container, cachedFrag, FRAGMENT_TIMETABLE);
                    Log.w("[NavActivitySwitch]", "Found instance of [" + cachedFrag.getTag() + "] in backstack");

                } else {
                    fragmentTransactionTimetable
                            .replace(R.id.fragment_container, new TimetableFragment(), FRAGMENT_TIMETABLE);
                }

                try {
                    if (currentFrag.getTag().equals(FRAGMENT_GOOGLE_MAPS) || currentFrag.equals(FRAGMENT_MAPBOX_MAPS)) {
                        fragmentTransactionTimetable.addToBackStack(currentFrag.getTag());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentTransactionTimetable
                        .commit();
                break;

            case R.id.nav_nearby:
                Log.w("[NavActivitySwitch]", "Planned Transaction: From [" + currentFrag.getTag() + "] to [" + FRAGMENT_NEARBY + "]");

                cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_NEARBY);
                FragmentTransaction fragmentTransactionNearby = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                if (cachedFrag instanceof NearbyFragment) {
                    fragmentTransactionNearby
                            .replace(R.id.fragment_container, cachedFrag, FRAGMENT_NEARBY);
                    Log.w("[NavActivitySwitch]", "Found instance of [" + cachedFrag.getTag() + "] in backstack");
                } else {
                    fragmentTransactionNearby
                            .replace(R.id.fragment_container, new NearbyFragment(), FRAGMENT_NEARBY);
                }

                try {
                    if (currentFrag.getTag().equals(FRAGMENT_GOOGLE_MAPS) || currentFrag.equals(FRAGMENT_MAPBOX_MAPS)) {
                        fragmentTransactionNearby.addToBackStack(currentFrag.getTag());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentTransactionNearby
                        .commit();
                break;

            case R.id.nav_footprint:
                Log.w("[NavActivitySwitch]", "Planned Transaction: From [" + currentFrag.getTag() + "] to [" + FRAGMENT_FOOTPRINT + "]");

                cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_FOOTPRINT);
                FragmentTransaction fragmentTransactionFootprint = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                if (cachedFrag instanceof FootprintFragment) {
                    fragmentTransactionFootprint
                            .replace(R.id.fragment_container, cachedFrag, FRAGMENT_FOOTPRINT);
                    Log.w("[NavActivitySwitch]", "Found instance of [" + cachedFrag.getTag() + "] in backstack");
                } else {
                    fragmentTransactionFootprint
                            .replace(R.id.fragment_container, new FootprintFragment(), FRAGMENT_FOOTPRINT);
                }

                try {
                    if (currentFrag.getTag().equals(FRAGMENT_GOOGLE_MAPS) || currentFrag.equals(FRAGMENT_MAPBOX_MAPS)) {
                        fragmentTransactionFootprint.addToBackStack(currentFrag.getTag());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentTransactionFootprint
                        .commit();
                break;

            case R.id.nav_security:
                Log.w("[NavActivitySwitch]", "Planned Transaction: From [" + currentFrag.getTag() + "] to [" + FRAGMENT_SECURITY + "]");

                cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_SECURITY);
                FragmentTransaction fragmentTransactionSecurity = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                if (cachedFrag instanceof SecurityFragment) {
                    fragmentTransactionSecurity.replace(R.id.fragment_container, cachedFrag, FRAGMENT_SECURITY);
                    Log.w("[NavActivitySwitch]", "Found instance of [" + cachedFrag.getTag() + "] in backstack");

                } else {
                    fragmentTransactionSecurity.replace(R.id.fragment_container, new SecurityFragment(), FRAGMENT_SECURITY);
                }

                try {
                    if (currentFrag.getTag().equals(FRAGMENT_GOOGLE_MAPS) || currentFrag.equals(FRAGMENT_MAPBOX_MAPS)) {
                        fragmentTransactionSecurity.addToBackStack(currentFrag.getTag());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                fragmentTransactionSecurity
                        .commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void resetToolBar(boolean childAction, int drawerMode) {
        if (childAction) {
            // trick to get UP button icon to show
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
            actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else {
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        }
        drawerLayout.setDrawerLockMode(drawerMode);
    }

    /**
     * Data relayed from global receiver will be retrieved here.
     */
    private BroadcastReceiver localLocationDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocationDataProcessor processor = new LocationDataProcessor();

            if (intent != null) {
                // Broadcast from google service
                if (intent.getAction().equals(GoogleLocationServiceReceiver.ACTION_GLE_SERVICE_BROADCAST_RELAY)) {
                    //Log.w("[NavigationActivity]", "Received Data from Global [Google Location Service] Broadcast Receiver");

                    String gleLocationJSON =
                            intent.getStringExtra(GoogleLocationServiceReceiver.ACTION_GLE_LOCATION_DATA);

                    Location gleLocation = processor.encapStringToLocation(gleLocationJSON);
                    locationDataCacheViewModel.setGoogleLocationLiveData(gleLocation);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat(LAST_KNOWN_LAT, (float)gleLocation.getLatitude());
                    editor.putFloat(LAST_KNOWN_LNG, (float)gleLocation.getLongitude());
                    editor.apply();

                // Broadcast from Skyhook service
                } else if (intent.getAction().equals(SkyhookLocationServiceReceiver.ACTION_SHK_SERVICE_BROADCAST_RELAY)){
                    String shkLocationJSON =
                            intent.getStringExtra(SkyhookLocationServiceReceiver.ACTION_SHK_LOCATION_DATA);
                    int geofenceTriggerType =
                            intent.getIntExtra(SkyhookLocationServiceReceiver.ACTION_SHK_LOCATION_GEOFENCE, -1);

                    Location shkLocation = processor.encapStringToLocation(shkLocationJSON);
                    locationDataCacheViewModel.setSkyhookLocationLiveData(shkLocation);

                    switch (geofenceTriggerType) {
                        case SkyhookLocationServiceReceiver.GEOFENCE_NULL:

                            break;

                        case SkyhookLocationServiceReceiver.GEOFENCE_IN:

//                            // Vibrate the phone when the user crosses the geofence
//                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                            v.vibrate(1000);

                            break;

                        case SkyhookLocationServiceReceiver.GEOFENCE_OUT:

                            break;

                        default:
                            break;
                    }

                }

            }

        }

    };



    /**
     * Register the receiver to Service
     * Update the header info
     */
    @Override
    public void onResume() {
        super.onResume();
        setupHeaderInfo();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(gleReceiver, new IntentFilter(GoogleLocationService.ACTION_BROADCAST));

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(shkReceiver, new IntentFilter(SkyhookLocationService.LOCATION_UPDATED));

        // Register the local broadcast receiver with shk service
        IntentFilter localLocationIntentFilter = new IntentFilter();
        localLocationIntentFilter.addAction(GoogleLocationServiceReceiver.ACTION_GLE_SERVICE_BROADCAST_RELAY);
        localLocationIntentFilter.addAction(SkyhookLocationServiceReceiver.ACTION_SHK_SERVICE_BROADCAST_RELAY);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(localLocationDataReceiver, localLocationIntentFilter);

    }

    @Override
    public void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(gleReceiver);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(localLocationDataReceiver);
        super.onPause();
    }

    @Override
    protected void onStop(){
        // Tell the service this activity is no longer available to receive data, and promote itself to foreground service.
        if(isBoundToGoogleService) {
            unbindService(serviceConnection);
            isBoundToGoogleService = false;
        }

        if (!securityServiceEnabled()) {
            SkyhookLocationService.stopService(getApplicationContext());
        }


        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private boolean securityServiceEnabled() {
        return false;
    }

    @Override
    public void passData(String data) {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        try {
            JSONObject dataJSON = new JSONObject(data);
            String from = (String) dataJSON.get("from");
            String destination = (String) dataJSON.get("to");
            Log.i("[NavActivity]", "Data Transmission: [" + from + "] -> [" + destination + "]");
            Log.w("[DEBUG INFO]", "Ready to relay: [" + data + "]");

            switch (destination) {
//                case FRAGMENT_FOOTPRINT_DETAIL:
//                    FootprintDetailFragment footprintDetailFragment = new FootprintDetailFragment();
//
//                    Bundle args1 = new Bundle();
//                    args1.putString(FootprintDetailFragment.KEY_FOOTPRINT_DATA_RECEIVED, data);
//                    footprintDetailFragment.setArguments(args1);
//                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            //.addToBackStack(from)
//                            .replace(R.id.fragment_container, footprintDetailFragment).commit();
//                    break;
//
//                case FRAGMENT_FOOTPRINT_EDIT:
//                    FootprintEditFragment footprintEditFragment = new FootprintEditFragment();
//
//                    Bundle args2 = new Bundle();
//                    args2.putString(FootprintEditFragment.KEY_FOOTPRINT_EDIT_DATA_RECEIVED, data);
//                    footprintEditFragment.setArguments(args2);
//                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            //.addToBackStack(from)
//                            .replace(R.id.fragment_container, footprintEditFragment).commit();
//                    break;

                case FRAGMENT_USEREVENT_DETAIL:
                    UserEventDetailFragment userEventDetailFragment = new UserEventDetailFragment();

                    Bundle args3 = new Bundle();
                    args3.putString(UserEventDetailFragment.KEY_USEREVENT_DATA_RECEIVED, data);
                    userEventDetailFragment.setArguments(args3);
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //.addToBackStack(from)
                            .replace(R.id.fragment_container, userEventDetailFragment).commit();
                    break;

                case FRAGMENT_GOOGLE_MAPS:
                    Fragment newGoogleMapsFrag = new GoogleMapsFragment();
                    Bundle args4 = new Bundle();
                    args4.putString(GoogleMapsFragment.KEY_GOOGLE_MAPS_DATA, data);

                    if (backStackEntryCount != 0) {
                        Fragment cachedFrag = getSupportFragmentManager().findFragmentByTag(GoogleMapsFragment.TAG);
                        if (cachedFrag instanceof GoogleMapsFragment) {
                            cachedFrag.setArguments(args4);
                            fragmentTransaction.replace(R.id.fragment_container, cachedFrag, GoogleMapsFragment.TAG);
                        } else {
                            newGoogleMapsFrag.setArguments(args4);
                            fragmentTransaction.replace(R.id.fragment_container, newGoogleMapsFrag, GoogleMapsFragment.TAG);
                        }
                    } else {
                        newGoogleMapsFrag.setArguments(args4);
                        fragmentTransaction.replace(R.id.fragment_container, newGoogleMapsFrag, GoogleMapsFragment.TAG);
                    }
                    fragmentTransaction.commit();
                    break;

            }

        } catch (JSONException e) {
            Log.e("NavigationActivity", "Invalid pass data.");
            e.printStackTrace();
        }

    }

    public void startGoogleLocationService() {
        if (!locationPermissionIsGranted()) {
            requestLocationPermission();
        } else {
            if(isBoundToGoogleService) {
                gleService.requestGoogleLocationUpdates();
            }
        }
    }

    public void updateGoogleLocationService(long interval, long fast_interval) {
        if (!locationPermissionIsGranted()) {
            requestLocationPermission();
        } else {
            if(isBoundToGoogleService) {
                gleService.changeSetting(interval*1000, fast_interval*1000);
            }
        }
    }

    public void stopGoogleLocationService() {
        gleService.removeGoogleLocationUpdates();
    }


    public void startSecurityLocationService() {
        if (locationPermissionIsGranted()) {
            Intent startServiceIntent = new Intent(NavigationActivity.this, GoogleLocationService.class);
            startService(startServiceIntent);
            Log.w("[NavigationActivity]", "Google Location Service started");

        } else {
            requestLocationPermission();
        }

    }

    public void stopSecurityLocationService() {
        Intent stopServiceIntent = new Intent(NavigationActivity.this, GoogleLocationService.class);
        stopService(stopServiceIntent);
        Log.w("[NavigationActivity]", "Google Location Service stoped");
    }

    // For getting precise location
    private boolean locationPermissionIsGranted() {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestLocationPermission() {
        // Check if system is able to show permission granting dialog to user
        boolean shouldShowRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldShowRationale) {
            AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                    .setTitle("Location Permission Required")
                    .setMessage("Location and tracking function need permission to run.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(NavigationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSION_CODE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(NavigationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_CODE);
        }
    }

    // For save shared files
    private boolean filePermissionIsGranted() {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    // For sync timetable to system calendar
    private boolean timetablePermissionIsGranted() {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
    }

    // For set avatar or social function
    private boolean cameraPermissionIsGranted() {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    }

    /**
     * Last time check permission
     * @param code
     * @param permissions
     * @param results
     */
    @Override
    public void onRequestPermissionsResult (int code, String[] permissions, int[] results) {
        if (code == REQUEST_PERMISSION_CODE) {
            if (results.length <=0) { // failed
                Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();

            } else if (results[0] == PackageManager.PERMISSION_GRANTED) { // granted

                gleService.requestGoogleLocationUpdates();
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else { // denied
                AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                        .setTitle("Permission Error")
                        .setMessage("Location and tracking function need permission to run.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(NavigationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_PERMISSION_CODE);
                            }
                        })
                        .show();

            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    public void showToast(String content, int length) {
        Toast.makeText(this, content, length).show();
    }

    public void setupHeaderInfo() {
        username.setText(sharedPreferences.getString(AccountFragment.USER_NAME, "No Login"));
        useremail.setText(sharedPreferences.getString(AccountFragment.USER_EMAIL, ""));
    }

}
