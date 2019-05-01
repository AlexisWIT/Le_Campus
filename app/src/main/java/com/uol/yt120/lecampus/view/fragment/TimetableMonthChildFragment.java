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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.view.adapter.UserEventAdapter;
import com.uol.yt120.lecampus.model.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.model.domain.UserEvent;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class TimetableMonthChildFragment extends Fragment {

    public static final String TAG = TimetableMonthChildFragment.class.getSimpleName();
    DataPassListener mCallback;
    private CalendarView calendarView;
    private UserEventViewModel userEventViewModel;
    private Calendar currentDate;
    private Calendar selectedDate;
    private DateTimeFormatter dtf = new DateTimeFormatter();
    private String dateForQuery = "";
    View timetableMonthView;
    private UserEventAdapter userEventMonthAdapter = new UserEventAdapter();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentDate = Calendar.getInstance();
        selectedDate = Calendar.getInstance();

        timetableMonthView = inflater.inflate(R.layout.fragment_timetable_month, container, false);

        calendarView = (CalendarView) timetableMonthView.findViewById(R.id.timetable_month_calendar);
        calendarView.setDate(currentDate);

        setupMonthRecyclerView();

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                calendarView.setDate(eventDay.getCalendar());
                setSelectedDate(eventDay.getCalendar());
                dateForQuery = dtf.formatDateToString(selectedDate.getTime(), "no_time");
                setupMonthRecyclerView();
                Log.w("[Timetable Month]", "Date For Query: ["+ dateForQuery +"]");
            }
        });

        return timetableMonthView;

    }

    public void setSelectedDate(Calendar date){
        this.selectedDate = date;
    }

    public void setupMonthRecyclerView() {
        userEventMonthAdapter = new UserEventAdapter();
        RecyclerView recyclerView = timetableMonthView.findViewById(R.id.timetable_list_month);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        dateForQuery = dtf.formatDateToString(selectedDate.getTime(), "no_time");
        //Log.w("[Timetable Month]", "Date For Query 2: ["+ dateForQuery +"]");

        userEventMonthAdapter.setTarget("month_frag");
        recyclerView.setAdapter(userEventMonthAdapter);

        userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
        userEventViewModel.getUserEventListByDate(dateForQuery).observe(this, new Observer<List<UserEvent>>() {
            @Override
            public void onChanged(@Nullable List<UserEvent> userEventList) {
                userEventMonthAdapter.setUserEventList(userEventList);
            }
        });

        userEventMonthAdapter.setOnItemClickListener(new UserEventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserEvent userEvent) {
                JSONObject userEventDetailJSON = new JSONObject();
                Integer userEventId = userEvent.getLocalId();

                try {
                    userEventDetailJSON.put("from", TAG);
                    userEventDetailJSON.put("to", UserEventDetailFragment.TAG);
                    userEventDetailJSON.put("id", userEventId);
                    String userEventDetailJSONString = userEventDetailJSON.toString();
                    Log.w("[DEBUG INFO]", "Ready to send: ["+userEventDetailJSONString+"]");
                    mCallback.passData(userEventDetailJSONString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
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


