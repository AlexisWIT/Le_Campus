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
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.domain.UserEvent;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.view.adapter.TimetablePagerAdapter;
import com.uol.yt120.lecampus.view.adapter.UserEventAdapter;
import com.uol.yt120.lecampus.viewModel.LocationDataCacheViewModel;
import com.uol.yt120.lecampus.viewModel.UserEventCacheViewModel;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TimetableFragment extends Fragment {
    public static final String TAG = TimetableFragment.class.getSimpleName();

    private Activity mActivity;
    private Context mContext;

    boolean timetableObtained = true;
    boolean internalFileFound = false;
    boolean externalFileFound = false;

    String timetableFileName = "timetable.json";
    String timetableFolderName = "timetable";
    String timetableContent = "";

    private UserEventViewModel userEventViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //checkFilePath();

        //Log.i("[Timetable Fragmt]", "Timetable Fragment created");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_timetable));

        if (timetableObtained) {
            View timetableView = inflater.inflate(R.layout.fragment_timetable, container, false);

            /*
            Setup fab;
             */
            FloatingActionButton buttonAddEvent = timetableView.findViewById(R.id.timetable_fab);
            setupFloatingActionButton(buttonAddEvent);


            /*
            Setup viewpager for tab switch and fab;
             */
            ViewPager viewPager = (ViewPager) timetableView.findViewById(R.id.viewpager_timetable);
            setupViewPager(viewPager, buttonAddEvent);


            /*
            Setup tab layout
             */
            TabLayout tabs = (TabLayout) timetableView.findViewById(R.id.tabLayout_timetable);
            tabs.setupWithViewPager(viewPager);

            //Log.i("[Timetable Fragmt]", "Timetable loaded");
            return timetableView;

        } else {
            View emptyTimetableView = inflater.inflate(R.layout.fragment_timetable_empty, container, false);
            Log.i("[Timetable Fragmt]", "No timetable to display");
            Toast.makeText(mActivity, "No timetable to display, please login first", Toast.LENGTH_SHORT).show();
            return emptyTimetableView;
        }

    }

    private void setupViewPager(ViewPager viewPager, FloatingActionButton floatingActionButton) {
        TimetablePagerAdapter adapter = new TimetablePagerAdapter(getChildFragmentManager());

        Fragment dayFrag = new TimetableDayChildFragment();
        Fragment weekFrag = new TimetableWeekChildFragment();
        Fragment monthFrag = new TimetableMonthChildFragment();

        adapter.addFragment(dayFrag, "Day");
        adapter.addFragment(weekFrag, "Week");
        adapter.addFragment(monthFrag, "Month");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float posotionOffset, int positionOffsetPixels) {
//                    Log.w("[TimetableFragment]", "Page pos:"+position+", offset: "+posotionOffset+", pixel:"+positionOffsetPixels);
                if (positionOffsetPixels != 0) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupFloatingActionButton(FloatingActionButton floatingActionButton) {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    /**
     * Read timetable info from timetable.json
     * @param fileName The file path of timetable.json
     * @return a string "result"
     */
    public String readFromFile(String fileName) {
        Log.i("[Timetable Fragmt]", "Read timetable file from: "+fileName);
        String result = "";
        BufferedReader reader = null;

        try {
            //FileInputStream fis = mContext.openFileInput(fileName);
            FileInputStream fis = new FileInputStream (new File(fileName));
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            result = buffer.toString();
            Log.i("[Timetable Fragmt]", "Read JSON: "+result);

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String deleteLocalTimetable() {
        String finalResult = "";
        String resultEx = "";
        String resultIn = "";

        String internalFilePath = mContext.getFilesDir()+ File.separator+timetableFolderName;
        String externalFilePath = Objects.requireNonNull(mContext.getExternalFilesDir(timetableFolderName)).getAbsolutePath();

        File internalFolder = new File(internalFilePath);
        File externalFolder = new File(externalFilePath);

        File internalFile = new File(internalFolder, timetableFileName);
        File externalFile = new File(externalFilePath, timetableFileName);

        if (!externalFile.exists()) {
            resultEx = "Timetable file not found in External Storage";
            Log.i("[Timetable Fragmt]", resultEx);
            externalFileFound = false;
        } else {
            externalFile.delete();
            resultEx = "Timetable file in External Storage deleted";
            Log.i("[Timetable Fragmt]", resultEx);
            externalFileFound = false;
        }

        if (!internalFile.exists()) {
            resultIn = "Timetable file not found in Internal Storage";
            Log.i("[Timetable Fragmt]", resultIn);
            internalFileFound = false;
        } else {
            internalFile.delete();
            resultIn = "Timetable file in Internal Storage deleted";
            Log.i("[Timetable Fragmt]", resultIn);
            internalFileFound = false;
        }

        timetableObtained = false;

        finalResult = resultEx +"\n"+resultIn;
        return finalResult;
    }


    private void refreshTimetable() {
        //WeekView weekView = getChildFragmentManager().findFragmentByTag()
        UserEventAdapter userEventAdapter = new UserEventAdapter();
        userEventAdapter.notifyDataSetChanged();
    }


    /**
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_timetable, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh_timetable:
                Toast.makeText(getActivity(), "Refreshing timetable...", Toast.LENGTH_SHORT).show();
                refreshTimetable();
                return true;

            case R.id.action_plan_my_day:
                Toast.makeText(getActivity(), "Planning your today's route...", Toast.LENGTH_SHORT).show();

                DateTimeCalculator dtc = new DateTimeCalculator();
                DateTimeFormatter dtf = new DateTimeFormatter();
                Date currentTime = Calendar.getInstance().getTime();
                String currentDate = dtc.getToday(false);
                Log.w("[Timetable Fragment]", "Planning Today: ["+ currentDate +"]");

                userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
                userEventViewModel.getUserEventListByDate(currentDate).observe(this, new Observer<List<UserEvent>>() {
                    @Override
                    public void onChanged(@Nullable List<UserEvent> userEventList) {
                        List<UserEvent> eventsForDirection = new ArrayList<>();
                        for (UserEvent userEvent : userEventList) {
                            try {
                                Date eventEndTime = dtf.parseStringToDate(userEvent.getEndTime(), "default");
                                if (currentTime.before(eventEndTime)) {
                                    eventsForDirection.add(userEvent);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        LocationDataCacheViewModel locationDataCacheViewModel = ViewModelProviders.of(getActivity()).get(LocationDataCacheViewModel.class);
                        locationDataCacheViewModel.setEventForDirection(eventsForDirection);
                        Log.w("[Timetable Fragment]", "Today's events remaining: ["+ eventsForDirection.toString() +"]");

                        Fragment cachedFrag = getActivity().getSupportFragmentManager().findFragmentByTag(GoogleMapsFragment.TAG);
                        if (cachedFrag instanceof GoogleMapsFragment){
                            Log.i("[Timetable Fragment]", "Found instance of Google Map");
                            getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .replace(R.id.fragment_container, cachedFrag, GoogleMapsFragment.TAG).commit();
                        } else {
                            Log.i("[Timetable Fragment]", "Instance of Google Map not found");
                            getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .replace(R.id.fragment_container, new GoogleMapsFragment(), GoogleMapsFragment.TAG).commit();
                        }
                    }
                });
                return true;

            case R.id.action_delete_local_timetable:
                Toast.makeText(getActivity(), "Deleting timetable...", Toast.LENGTH_SHORT).show();
                String result = deleteLocalTimetable();
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                refreshTimetable();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Due to the different lifecycle, Activity may be recycled
     * by the system with the Fragment still existed. To prevent the
     * return value 'null' from getActivity() when the Activity is
     * recycled...
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        mContext = null;
    }

}
