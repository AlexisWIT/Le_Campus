package com.uol.yt120.lecampus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.uol.yt120.lecampus.dataAccessObjects.DataPassListener;

import org.json.JSONException;
import org.json.JSONObject;


public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DataPassListener {

    public static final String FRAGMENT_ACCOUNT = "AccountFragment";
    public static final String FRAGMENT_FOOTPRINT = "FootprintFragment";
    public static final String FRAGMENT_FOOTPRINT_DETAIL = "FootprintDetailFragment";
    public static final String FRAGMENT_FOOTPRINT_EDIT = "FootprintEditFragment";
    public static final String FRAGMENT_GOOGLE_MAPS = "GoogleMapsFragment";
    public static final String FRAGMENT_MAPBOX_MAPS = "MapBoxMapsFragment";
    public static final String FRAGMENT_NEARBY = "NearbyFragment";
    public static final String FRAGMENT_TIMETABLE = "TimetableFragment";
    public static final String FRAGMENT_CHILD_TIMETABLE_DAY = "TimetableDayChildFragment";
    public static final String FRAGMENT_CHILD_TIMETABLE_MONTH = "TimetableMonthChildFragment";
    public static final String FRAGMENT_CHILD_TIMETABLE_WEEK = "TimetableWeekChildFragment";
    public static final String FRAGMENT_USEREVENT_DETAIL = "UserEventDetailFragment";

    private long backPressedTime;
    private Toast backToast;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = (Toolbar) findViewById(R.id.acitvity_tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // For account login function by pressing nav-hader
        View headerView = navigationView.getHeaderView(0);
//            TextView username = (TextView) headerView.findViewById(R.id.username);
//            username.setText("username")

        // Lambda expression for onClick method
        headerView.setOnClickListener(v -> {
            Toast.makeText(this, "Fetching account information... ", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container, new AccountFragment(),"accountFrag").addToBackStack(null).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // set default screen
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new GoogleMapsFragment(), "googleMapFrag")
                    .addToBackStack(null)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_map);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (currentFrag instanceof GoogleMapsFragment || currentFrag instanceof MapBoxMapsFragment) {
            if (backStackEntryCount == 0) {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel();
                    super.onBackPressed();
                    return;
                } else {
                    backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                    backToast.show();
                }

                backPressedTime = System.currentTimeMillis();
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

        switch (id) {
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new GoogleMapsFragment(), "googleMapFrag")
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_timetable:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TimetableFragment(), "timetableFrag")
                        .commit();
                break;

            case R.id.nav_nearby:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new NearbyFragment(), "nearbyFrag")
                        .commit();
                break;

            case R.id.nav_footprint:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FootprintFragment(), "routeFrag")
                        .addToBackStack(null)
                        .commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void resetToolBar(boolean childAction, int drawerMode)
    {
        if (childAction) {
            // [Undocumented?] trick to get up button icon to show
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
        } else {
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        }
        drawerLayout.setDrawerLockMode(drawerMode);
    }

    @Override
    public void passData(String data) {
        try {
            JSONObject dataJSON = new JSONObject(data);
            String from = (String)dataJSON.get("from");
            String destination = (String)dataJSON.get("to");
            Log.i("NavigationActivity", "Data Transmission: ["+from+"] -> ["+destination+"]");
            Log.w("[DEBUG INFO]", "Ready to relay: ["+data+"]");

            switch (destination) {
                case FRAGMENT_FOOTPRINT_DETAIL:
                    FootprintDetailFragment footprintDetailFragment = new FootprintDetailFragment();

                    Bundle args1 = new Bundle();
                    args1.putString(FootprintDetailFragment.KEY_FOOTPRINT_DATA_RECEIVED, data);
                    footprintDetailFragment.setArguments(args1);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, footprintDetailFragment).commit();
                    break;

                case FRAGMENT_FOOTPRINT_EDIT:
                    FootprintEditFragment footprintEditFragment = new FootprintEditFragment();

                    Bundle args2 = new Bundle();
                    args2.putString(FootprintEditFragment.KEY_FOOTPRINT_EDIT_DATA_RECEIVED, data);
                    footprintEditFragment.setArguments(args2);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, footprintEditFragment).commit();
                    break;

                case FRAGMENT_USEREVENT_DETAIL:
                    UserEventDetailFragment userEventDetailFragment = new UserEventDetailFragment();

                    Bundle args3 = new Bundle();
                    args3.putString(UserEventDetailFragment.KEY_USEREVENT_DATA_RECEIVED, data);
                    userEventDetailFragment.setArguments(args3);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, userEventDetailFragment).commit();
                    break;

            }

        } catch (JSONException e) {
            Log.e("NavigationActivity", "Invalid pass data.");
            e.printStackTrace();
        }

    }

}
