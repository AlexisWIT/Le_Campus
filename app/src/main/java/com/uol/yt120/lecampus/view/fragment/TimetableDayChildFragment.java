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
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.view.adapter.UserEventAdapter;
import com.uol.yt120.lecampus.model.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.model.domain.UserEvent;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.viewModel.UserEventCacheViewModel;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TimetableDayChildFragment extends Fragment {

    public static final String TAG = TimetableDayChildFragment.class.getSimpleName();
    DataPassListener mCallback;

    private UserEventViewModel userEventViewModel;
    private UserEventCacheViewModel userEventCacheViewModel;
    private DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();

    private String currentDateWithTime;
    private String currentDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View timetableDayView = inflater.inflate(R.layout.fragment_timetable_day, container, false);

        RecyclerView recyclerView = timetableDayView.findViewById(R.id.timetable_day_item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true); // Card size will not change

        final UserEventAdapter userEventAdapter = new UserEventAdapter();
        recyclerView.setAdapter(userEventAdapter);
        currentDateWithTime = dateTimeCalculator.getToday(true);
        currentDate = dateTimeCalculator.getToday(false);
        TextView textView = timetableDayView.findViewById(R.id.label_timetable_day_date);
        textView.setText(dateTimeCalculator.getTodayDate());

        Log.w("[DEBUG INFO]", "Today: ["+ currentDate +"]" + currentDateWithTime);
        userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
        userEventViewModel.getUserEventListByDate(currentDate).observe(this, new Observer<List<UserEvent>>() {
            @Override
            public void onChanged(@Nullable List<UserEvent> userEventList) {
                Log.w("[DEBUG INFO]", "Got Event List: ["+ userEventList.toString() +"]");
                userEventAdapter.setUserEventList(userEventList);
                //Toast.makeText(getActivity(), "Timetable updated", Toast.LENGTH_SHORT).show();
            }
        });


        userEventAdapter.setOnItemClickListener(new UserEventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserEvent userEvent) {
                JSONObject userEventDetailJSON = new JSONObject();
                Integer userEventId = userEvent.getLocalId();

                userEventCacheViewModel = ViewModelProviders.of(getActivity()).get(UserEventCacheViewModel.class);
                userEventCacheViewModel.setMutableUserEvent(userEvent);

                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, new UserEventDetailFragment(), UserEventDetailFragment.TAG)
                        .addToBackStack(TAG)
                        .commit();

//                try {
//                    userEventDetailJSON.put("from", TAG);
//                    userEventDetailJSON.put("to", UserEventDetailFragment.TAG);
//                    userEventDetailJSON.put("id", userEventId);
//                    String userEventDetailJSONString = userEventDetailJSON.toString();
//                    Log.w("[DEBUG INFO]", "Ready to send: ["+userEventDetailJSONString+"]");
//                    mCallback.passData(userEventDetailJSONString);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
        return timetableDayView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Makes sure that the host activity has implemented the callback interface
        try {
            mCallback = (DataPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ " did not implement DataPassListener");
        }
    }

}