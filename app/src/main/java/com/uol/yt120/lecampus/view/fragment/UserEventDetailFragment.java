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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.model.domain.UserEvent;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.viewModel.LocationDataCacheViewModel;
import com.uol.yt120.lecampus.viewModel.UserEventCacheViewModel;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class UserEventDetailFragment extends Fragment {
    public static final String TAG = UserEventDetailFragment.class.getSimpleName();

    public static final String KEY_USEREVENT_DATA_RECEIVED = "com.uol.yt120.lecampus.KEY_USEREVENT_DATA_RECEIVED";

    DataPassListener mCallback;
    int userEventId;
    TextView textEventTitle;
    TextView textEventTime;
    TextView textEventType;
    TextView textEventCode;
    TextView textEventLocation;
    TextView textEventLocation2;
    TextView textEventDesc;
    ImageView iconEventLocation;
    ImageView iconEventDesc;
    double locationLat;
    double locationLng;

    private UserEventViewModel userEventViewModel;
    private UserEventCacheViewModel userEventCacheViewModel;
    private LocationDataCacheViewModel locationDataCacheViewModel;
    private DateTimeCalculator dtc = new DateTimeCalculator();
    private DateTimeFormatter dtf = new DateTimeFormatter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Event Detail");
        View view = inflater.inflate(R.layout.fragment_userevent_detail, container, false);

        textEventTitle = view.findViewById(R.id.text_userevent_detail_title);
        textEventTime = view.findViewById(R.id.text_userevent_detail_time);
        textEventType = view.findViewById(R.id.text_userevent_detail_type);
        textEventCode = view.findViewById(R.id.text_userevent_detail_code);
        textEventLocation = view.findViewById(R.id.text_userevent_detail_location);
        textEventLocation2 = view.findViewById(R.id.text_userevent_detail_location_2);
        textEventDesc = view.findViewById(R.id.text_userevent_detail_desc);

        iconEventLocation = view.findViewById(R.id.icon_event_location);
        iconEventDesc = view.findViewById(R.id.icon_event_desc);

        userEventCacheViewModel = ViewModelProviders.of(getActivity()).get(UserEventCacheViewModel.class);
        userEventCacheViewModel.getMutableUserEvent().observe(this, new Observer<UserEvent>() {
            @Override
            public void onChanged(@Nullable UserEvent userEvent) {
                if (userEvent != null) {
                    updateEventInfo(userEvent.getLocalId());
                    //userEventLocalId = userEvent.getLocalId();
                    Log.w("[UserEventDetail]", "Got local Id: "+userEvent.getLocalId());
                }
            }
        });



        return view;
    }

    private void updateEventInfo(int id) {
        userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
        userEventViewModel.getUserEventById(id).observe(this, new Observer<UserEvent>() {
            @Override
            public void onChanged(@Nullable UserEvent userEvent) {
                if (userEvent != null) {
                    setupInterface(userEvent);
                    Log.w("[UserEventDetail]", "Got event info: "+userEvent.toString());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        iconEventLocation.setOnClickListener(this::onLocationClick);
        //textEventLocation.setOnClickListener(this::onLocationClick);
        //textEventLocation2.setOnClickListener(this::onLocationClick);
    }

    private void onLocationClick(View view) {
        LatLng locationLatLng = new LatLng(locationLat, locationLng);
        if (getActivity() != null) {
            locationDataCacheViewModel = ViewModelProviders.of(getActivity()).get(LocationDataCacheViewModel.class);
            locationDataCacheViewModel.setLatLngForDirection(locationLatLng);

            Fragment cachedFrag = getActivity().getSupportFragmentManager().findFragmentByTag(GoogleMapsFragment.TAG);
            if (cachedFrag instanceof GoogleMapsFragment){
                Log.i("[UserEventDetail]", "Found instance of Google Map");
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, cachedFrag, GoogleMapsFragment.TAG).commit();
            } else {
                Log.i("[UserEventDetail]", "Instance of Google Map not found");
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, new GoogleMapsFragment(), GoogleMapsFragment.TAG).commit();
            }

        }
    }

    private void setupInterface(UserEvent userEvent) {
        textEventTitle.setText(userEvent.getEventTitle());
        String eventTimeText = null;
        try {
            Date startDate = dtf.parseStringToDate(userEvent.getStartTime(), "default");
            Date endDate = dtf.parseStringToDate(userEvent.getEndTime(), "default");
            String startTimeText = dtc.getTimeFormatOfText(startDate);
            String startTime = dtf.formatDateToString(startDate, "time_only");
            String endTime = dtf.formatDateToString(endDate, "time_only");
            eventTimeText = startTimeText+" / "+startTime+" - "+endTime;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        textEventTime.setText(eventTimeText);
        textEventType.setText(userEvent.getEventType());
        textEventCode.setText(userEvent.getEventCode());
        textEventLocation.setText(userEvent.getLocation());

        String address = userEvent.getAddress();
        String postCode = userEvent.getPostCode();
        String city = userEvent.getCity();
        String country = userEvent.getCountry();
        String locationDetail = address+", "+city+" "+postCode+", "+country;
        textEventLocation2.setText(locationDetail);

        String description = userEvent.getEventDesc();
        String contactEmail = userEvent.getEmail();
        String desc = description+"\n"+contactEmail;
        textEventDesc.setText(desc);

        locationLat = Double.parseDouble(userEvent.getLat());
        locationLng = Double.parseDouble(userEvent.getLon());
        Log.i("[UserEventDetail]", "Target Latlng: "+locationLat+", "+locationLng);
    }

}
