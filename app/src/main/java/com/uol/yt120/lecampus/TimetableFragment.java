package com.uol.yt120.lecampus;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.uol.yt120.lecampus.adapter.TimetablePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TimetableFragment extends Fragment {

    private Activity mActivity;
    private Context mContext;

    boolean timetableObtained = false;
    boolean internalFileFound = false;
    boolean externalFileFound = false;

    String timetableFileName = "timetable.json";
    String timetableFolderName = "timetable";
    String timetableContent = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        checkFilePath();

        Log.i("[Timetable Fragmt]", "Timetable Fragment created");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_timetable));

        if (timetableObtained) {
            View timetableView = inflater.inflate(R.layout.fragment_timetable, container, false);

            ViewPager viewPager = (ViewPager) timetableView.findViewById(R.id.viewpager_timetable);
            setupViewPager(viewPager);

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

    private void setupViewPager(ViewPager viewPager) {
        TimetablePagerAdapter adapter = new TimetablePagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TimetableDayChildFragment(), "Day");
        adapter.addFragment(new TimetableWeekChildFragment(), "Week");
        adapter.addFragment(new TimetableMonthChildFragment(), "Month");
        viewPager.setAdapter(adapter);
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


    /**
     *
     * @param timetableContent
     * @return
     */
    public SimpleAdapter loadTimetableIntoAdapter(String timetableContent) {

        SimpleAdapter finalAdapter = null;
        String[] from = {"Name", "Type", "Location", "Lat", "Lon","StartTime","EndTime",};
        int[] to = {R.id.event_name, R.id.event_type, R.id.event_location, R.id.event_lat, R.id.event_lon, R.id.event_startTime, R.id.event_endTime};
        ArrayList<HashMap<String, String>> eventArrayList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashmap;

        try {
            JSONObject json = new JSONObject(timetableContent);
            JSONArray jArray = json.getJSONArray("timetable");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject event = jArray.getJSONObject(i);
                Log.i("[Account Fragmt]", "=== Event "+i+" ===");

                String eventName = event.getString("moduleName");
                Log.i("[Account Fragmt]", "Event: "+eventName);

                String eventType = event.getString("moduleType");
                Log.i("[Account Fragmt]", "Type: "+eventType);

                String eventLocation = event.getString("building");
                Log.i("[Account Fragmt]", "Building: "+eventLocation);

                String eventLat = event.getString("buildingLatitude");
                Log.i("[Account Fragmt]", "LAT: "+eventLat);

                String eventLon = event.getString("buildingLongitude");
                Log.i("[Account Fragmt]", "LON: "+eventLon);

                String eventStartTime = event.getString("start");
                Log.i("[Account Fragmt]", "Start Time: "+eventStartTime);

                String eventEndTime = event.getString("end");
                Log.i("[Account Fragmt]", "End Time: "+eventEndTime);

                if (!event.getString("lecturer").equals("someone")) {
                    hashmap = new HashMap<String, String>();
                    hashmap.put("Name", "" + eventName);
                    hashmap.put("Type", "" + eventType);
                    hashmap.put("Location", "" + eventLocation);
                    hashmap.put("Lat", "" + eventLat);
                    hashmap.put("Lon", "" + eventLon);
                    hashmap.put("StartTime", "" + eventStartTime);
                    hashmap.put("EndTime", "" + eventEndTime);
                    eventArrayList.add(hashmap);

                }

            }

            SimpleAdapter adapter = new SimpleAdapter(mContext, eventArrayList, R.layout.fragment_timetable_item, from, to);
            finalAdapter = adapter;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalAdapter;
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

    private void checkFilePath() {
        String internalFilePath = mContext.getFilesDir()+ File.separator+timetableFolderName;
        String externalFilePath = Objects.requireNonNull(mContext.getExternalFilesDir(timetableFolderName)).getAbsolutePath();

        File internalFolder = new File(internalFilePath);
        File externalFolder = new File(externalFilePath);

        File internalFile = new File(internalFolder, timetableFileName);
        File externalFile = new File(externalFolder, timetableFileName);

        if (!externalFile.exists() || externalFile==null) {
            Log.i("[Timetable Fragmt]", "Timetable file in external storage not found");
            externalFileFound = false;
        } else {
            Log.i("[Timetable Fragmt]", "Found Timetable file in external storage");
            externalFileFound = true;
        }

        if (!internalFile.exists() || internalFile==null) {
            Log.i("[Timetable Fragmt]", "Timetable file in internal storage not found");
            internalFileFound = false;
        } else {
            Log.i("[Timetable Fragmt]", "Found timetable file in internal storage");
            internalFileFound = true;
        }

        if (externalFileFound) {
            timetableObtained = true;
            timetableContent = readFromFile(externalFilePath+File.separator+timetableFileName);

        } else if (internalFileFound) {
            timetableObtained = true;
            timetableContent = readFromFile(internalFilePath+File.separator+timetableFileName);

        } else {
            timetableObtained = false;
            timetableContent = "";
        }
    }

    private void refreshTimetable() {
        //WeekView weekView = getChildFragmentManager().findFragmentByTag()
    }


    /**
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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

            case R.id.action_export_to_calender:
                Toast.makeText(getActivity(), "This function is temporarily unavailable", Toast.LENGTH_SHORT).show();
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
