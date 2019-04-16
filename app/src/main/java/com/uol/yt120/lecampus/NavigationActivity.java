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

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uol.yt120.lecampus.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.domain.User;
import com.uol.yt120.lecampus.viewModel.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 *
 */

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DataPassListener {

    public static final String ACTIVITY_NAVIGATION = "NavigationActivity";
    public static final String FRAGMENT_ACCOUNT = "AccountFragment";
    public static final String FRAGMENT_FOOTPRINT = "FootprintFragment";
    public static final String FRAGMENT_FOOTPRINT_DETAIL = "FootprintDetailFragment";
    public static final String FRAGMENT_FOOTPRINT_EDIT = "FootprintEditFragment";
    public static final String FRAGMENT_GOOGLE_MAPS = "GoogleMapsFragment";
    public static final String FRAGMENT_MAPBOX_MAPS = "MapBoxMapsFragment";
    public static final String FRAGMENT_NEARBY = "NearbyFragment";
    public static final String FRAGMENT_SECURITY = "SecurityFragment";
    public static final String FRAGMENT_TIMETABLE = "TimetableFragment";
    public static final String FRAGMENT_CHILD_TIMETABLE_DAY = "TimetableDayChildFragment";
    public static final String FRAGMENT_CHILD_TIMETABLE_MONTH = "TimetableMonthChildFragment";
    public static final String FRAGMENT_CHILD_TIMETABLE_WEEK = "TimetableWeekChildFragment";
    public static final String FRAGMENT_USEREVENT_DETAIL = "UserEventDetailFragment";

    public static final String BACKSTACK_CLEAN_ALL = "all";
    public static final String BACKSTACK_CLEAN_LIMIT = "default"; // Clean backstack when achieved given limit
    public static final int BACKSTACK_LIMIT = 5;

    public static final String CHANNEL_ID = "Security_Service_Channel";

    private long backPressedTime;
    private Toast backToast;
    private NavigationView navigationView;
    private Intent backIntent;
    private List<Fragment> fragmentList = new ArrayList<>();

    private TextView username;
    private TextView useremail;
    private ImageView useravater;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            GoogleMapsFragment googleMapsFragment = (GoogleMapsFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GOOGLE_MAPS);
//        }

        setContentView(R.layout.activity_navigation);


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


        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUserLiveDataById(1).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    setupHeaderInfo(user.getRealname(), user.getUolEmail());
                }
            }
        });

//            username.setText("username")



        headerView.setOnClickListener(v -> {
            Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            FragmentTransaction fragmentTransactionAccount = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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
            } catch (Exception e) { }

        });

        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissionStart(rxPermissions);

        // set default(Root) screen
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.fragment_container, new GoogleMapsFragment(), FRAGMENT_GOOGLE_MAPS)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_map);
        }
    }

    int backStackEntryCount;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        /*
            If current fragment is Root fragment (Map)
         */
        if (currentFrag instanceof GoogleMapsFragment || currentFrag instanceof MapBoxMapsFragment) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) { // Second press less than 2 sec
                backToast.cancel();
                Log.w("[NavActivityBACK]", "Pressed Twice, Current Frag is ["+currentFrag.getTag()+"], BackStack: "+backStackEntryCount);
                //cleanBackStack(BACKSTACK_CLEAN_ALL);
                super.onBackPressed();
                return;

            } else {
                backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
                Log.w("[NavActivityBACK]", "Pressed Once, Current Frag is ["+currentFrag.getTag()+"]");
            }

            backPressedTime = System.currentTimeMillis();

        /*
            If current fragment is other main fragment (Account, Timetable, Nearby, Security)
         */
        } else if (currentFrag instanceof AccountFragment || currentFrag instanceof TimetableFragment ||
                    currentFrag instanceof NearbyFragment || currentFrag instanceof SecurityFragment || currentFrag instanceof FootprintFragment) {
            Log.w("[NavActivityBACK]", "Current Frag is Other Main Frag ["+currentFrag.getTag()+"], BackStack: "+backStackEntryCount);
//            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                    .replace(R.id.fragment_container, new GoogleMapsFragment(), FRAGMENT_GOOGLE_MAPS)
//                    //.addToBackStack(null)  add current activity/fragment to back stack
//                    .commit();
            if (backStackEntryCount ==0) {
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.fragment_container, new GoogleMapsFragment(), FRAGMENT_GOOGLE_MAPS).commit();
            }
            navigationView.setCheckedItem(R.id.nav_map);
            super.onBackPressed();
            return;

        }

    }

    private void rxPermissionStart(RxPermissions rp) {
        //rp.request(Manifest.permission_group.LOCATION);
        rp.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(granted -> {
            if (granted) { // Always true pre-M
                Log.w("[Activity]", "Permission Granted");
            } else {
                Log.w("[Activity]", "Permission Denied");
                rxPermissionStart(rp);
            }
        });
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
                Log.w("[NavActivitySwitch]", "Planned Transaction: From ["+currentFrag.getTag()+"] to ["+FRAGMENT_GOOGLE_MAPS+"]");
                String targetFragTag = FRAGMENT_GOOGLE_MAPS;
                String currentFragTag ="";

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
                        Log.w("[NavActivitySwitch]", "Found instance of ["+currentFrag.getTag()+"] in backstack");

                    } else {
                        fragmentTransactionMap
                                .replace(R.id.fragment_container, new GoogleMapsFragment(), targetFragTag);
                    }

                    fragmentTransactionMap
                            .commit();
                }

                break;

            case R.id.nav_timetable:
                Log.w("[NavActivitySwitch]", "Planned Transaction: From ["+currentFrag.getTag()+"] to ["+FRAGMENT_TIMETABLE+"]");

                cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TIMETABLE);
                FragmentTransaction fragmentTransactionTimetable =
                        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                if (cachedFrag instanceof TimetableFragment) {
                    fragmentTransactionTimetable
                            .replace(R.id.fragment_container, cachedFrag, FRAGMENT_TIMETABLE);
                    Log.w("[NavActivitySwitch]", "Found instance of ["+currentFrag.getTag()+"] in backstack");

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
                Log.w("[NavActivitySwitch]", "Planned Transaction: From ["+currentFrag.getTag()+"] to ["+FRAGMENT_NEARBY+"]");

                cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_NEARBY);
                FragmentTransaction fragmentTransactionNearby = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                if (cachedFrag instanceof NearbyFragment) {
                    fragmentTransactionNearby
                            .replace(R.id.fragment_container, cachedFrag, FRAGMENT_NEARBY);
                    Log.w("[NavActivitySwitch]", "Found instance of ["+currentFrag.getTag()+"] in backstack");
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
                Log.w("[NavActivitySwitch]", "Planned Transaction: From ["+currentFrag.getTag()+"] to ["+FRAGMENT_FOOTPRINT+"]");

                cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_FOOTPRINT);
                FragmentTransaction fragmentTransactionFootprint = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                if (cachedFrag instanceof FootprintFragment) {
                    fragmentTransactionFootprint
                            .replace(R.id.fragment_container, cachedFrag, FRAGMENT_FOOTPRINT);
                    Log.w("[NavActivitySwitch]", "Found instance of ["+currentFrag.getTag()+"] in backstack");
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
                Log.w("[NavActivitySwitch]", "Planned Transaction: From ["+currentFrag.getTag()+"] to ["+FRAGMENT_SECURITY+"]");

                cachedFrag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_SECURITY);
                FragmentTransaction fragmentTransactionSecurity = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                if (cachedFrag instanceof SecurityFragment) {
                    fragmentTransactionSecurity.replace(R.id.fragment_container, cachedFrag, FRAGMENT_SECURITY);
                    Log.w("[NavActivitySwitch]", "Found instance of ["+currentFrag.getTag()+"] in backstack");

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

    @Override
    public void passData(String data) {
        try {
            JSONObject dataJSON = new JSONObject(data);
            String from = (String) dataJSON.get("from");
            String destination = (String) dataJSON.get("to");
            Log.i("[NavActivity]", "Data Transmission: [" + from + "] -> [" + destination + "]");
            Log.w("[DEBUG INFO]", "Ready to relay: [" + data + "]");

            switch (destination) {
                case FRAGMENT_FOOTPRINT_DETAIL:
                    FootprintDetailFragment footprintDetailFragment = new FootprintDetailFragment();

                    Bundle args1 = new Bundle();
                    args1.putString(FootprintDetailFragment.KEY_FOOTPRINT_DATA_RECEIVED, data);
                    footprintDetailFragment.setArguments(args1);
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //.addToBackStack(from)
                            .replace(R.id.fragment_container, footprintDetailFragment).commit();
                    break;

                case FRAGMENT_FOOTPRINT_EDIT:
                    FootprintEditFragment footprintEditFragment = new FootprintEditFragment();

                    Bundle args2 = new Bundle();
                    args2.putString(FootprintEditFragment.KEY_FOOTPRINT_EDIT_DATA_RECEIVED, data);
                    footprintEditFragment.setArguments(args2);
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //.addToBackStack(from)
                            .replace(R.id.fragment_container, footprintEditFragment).commit();
                    break;

                case FRAGMENT_USEREVENT_DETAIL:
                    UserEventDetailFragment userEventDetailFragment = new UserEventDetailFragment();

                    Bundle args3 = new Bundle();
                    args3.putString(UserEventDetailFragment.KEY_USEREVENT_DATA_RECEIVED, data);
                    userEventDetailFragment.setArguments(args3);
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            //.addToBackStack(from)
                            .replace(R.id.fragment_container, userEventDetailFragment).commit();
                    break;

            }

        } catch (JSONException e) {
            Log.e("NavigationActivity", "Invalid pass data.");
            e.printStackTrace();
        }

    }

//    private void cleanBackStack(String option) {
//        int currentStackCount = getFragmentManager().getBackStackEntryCount();
//        int stackLimit = BACKSTACK_LIMIT;
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        switch (option) {
//            case BACKSTACK_CLEAN_ALL:
//                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                break;
//
//            case BACKSTACK_CLEAN_LIMIT:
//                for(int i = 0; i < currentStackCount; i++) {
//
//                }
//                break;
//
//        }
//    }

    private void setupNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel securityServiceChannel = new NotificationChannel(
                    CHANNEL_ID, "Security Service Channel", NotificationManager.IMPORTANCE_HIGH
            );
        }
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(securityServiceChannel);
    }

    public void setupHeaderInfo(String name, String email) {
        username.setText(name);
        useremail.setText(email);
    }

}
