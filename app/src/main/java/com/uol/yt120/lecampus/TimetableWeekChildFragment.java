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

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.EmptyViewLongPressListener;
import com.alamkanak.weekview.EventClickListener;
import com.alamkanak.weekview.EventLongPressListener;
import com.alamkanak.weekview.MonthChangeListener;
import com.alamkanak.weekview.ScrollListener;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.uol.yt120.lecampus.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.domain.UserEvent;
import com.uol.yt120.lecampus.publicAsyncTasks.UpdateWeeklyEventListAsyncTask;
import com.uol.yt120.lecampus.repository.UserEventRepository;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.utility.TextValidator;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class TimetableWeekChildFragment extends Fragment implements EventClickListener<UserEvent>, MonthChangeListener<UserEvent>,
        EventLongPressListener<UserEvent>, EmptyViewLongPressListener, UpdateWeeklyEventListAsyncTask.Response {

    public static final String TAG = TimetableWeekChildFragment.class.getSimpleName();
    DataPassListener mCallback;

    private UserEventRepository userEventRepository;
    private UserEventViewModel userEventViewModel;

    private DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();
    private DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
    String currentDateWithTime = dateTimeCalculator.getToday(true);

    private View timetableWeekView;
    private WeekView<UserEvent> weekView;
    private TextView dateTextView;
    private TextValidator textValidator = new TextValidator();

    private List<UserEvent> rawEventList1 = new ArrayList<>();
    private List<UserEvent> rawEventList2 = new ArrayList<>();
    List<WeekViewDisplayable<UserEvent>> updatedUserEventList = new ArrayList<>();
//    final int BOUND = 299;
//    final Object[] eventGroup= new Object[300];
    private boolean requesting = false;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //eventGroup[BOUND] = "NO";
        userEventRepository = new UserEventRepository(getActivity().getApplication());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Processing...");

        timetableWeekView = inflater.inflate(R.layout.fragment_timetable_week, container, false);

        dateTextView = timetableWeekView.findViewById(R.id.label_timetable_week_date);
        weekView = timetableWeekView.findViewById(R.id.timetable_week_view);
        weekView.setNumberOfVisibleDays(7);
        weekView.setEventTextSize(10);
        weekView.setOnEventClickListener(this);
        weekView.setMonthChangeListener(this);
        weekView.setEventLongPressListener(this);
        weekView.setEmptyViewLongPressListener(this);
        weekView.setFirstDayOfWeek(Calendar.MONDAY);
        weekView.setEventCornerRadius(2);
        //weekView.setWeekViewLoader();

        weekView.setScrollListener(new ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(
                    @NotNull Calendar newFirstDay,
                    @Nullable Calendar oldFirstDay) {

                updateDateTextView();
            }
        });

        ImageView prevWeekAction = timetableWeekView.findViewById(R.id.action_timetable_prev_week);
        prevWeekAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(weekView.getFirstVisibleDay().getTime());
                calendar1.add(Calendar.DAY_OF_MONTH, -7);
                weekView.goToDate(calendar1);
                updateDateTextView();
            }
        });

        ImageView nextWeekAction = timetableWeekView.findViewById(R.id.action_timetable_next_week);
        nextWeekAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(weekView.getFirstVisibleDay().getTime());
                calendar2.add(Calendar.DAY_OF_MONTH, 7);
                weekView.goToDate(calendar2);
                updateDateTextView();
            }
        });

        return timetableWeekView;
    }

    protected String getTimeInString(Calendar time) {
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        int month = time.get(Calendar.MONTH) + 1;
        int dayOfMonth = time.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.getDefault(), "Event of %02d:%02d %s/%d", hour, minute, month, dayOfMonth);
    }


    @Override
    public void onEmptyViewLongPress(@NotNull Calendar calendar) {
        Toast.makeText(getContext(), "Empty long pressed ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onEventClick(UserEvent userEvent, @NotNull RectF rectF) {
        Toast.makeText(getContext(), "Event Clicked " + userEvent.getEventTitle(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onEventLongPress(UserEvent userEvent, @NotNull RectF rectF) {
        Toast.makeText(getContext(), "Event long pressed " + userEvent.getEventTitle(), Toast.LENGTH_SHORT).show();
        try {
            Objects.requireNonNull(getActivity()).registerForContextMenu(weekView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Objects.requireNonNull(getActivity()).getMenuInflater().inflate(R.menu.menu_childfragment_timetable_item_click, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_edit_event:


                return true;

            case R.id.option_delete_event:


                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @NotNull
    @Override
    public List<WeekViewDisplayable<UserEvent>> onMonthChange(@NotNull Calendar firstDate, @NotNull Calendar lastDate) {
        List<WeekViewDisplayable<UserEvent>> updatedUserEventList = new ArrayList<>();
        String firstDateNoTime = dateTimeFormatter.formatDateToString(firstDate.getTime(), "no_time");
        String lastDateNoTime = dateTimeFormatter.formatDateToString(lastDate.getTime(), "no_time");

        //new UpdateWeeklyEventListAsyncTask(userEventRepository, firstDateNoTime, lastDateNoTime, this).execute();
        userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
        List<UserEvent> eventList = userEventViewModel.getNonLiveUserEventListByDateRange(firstDateNoTime, lastDateNoTime);

        for (UserEvent userEvent : eventList) {
            userEvent.setContext(getContext());
            Log.w("[Async RESULT]", userEvent.toString());

            updatedUserEventList.add(userEvent.toWeekViewEvent(getContext()));
        }

        return updatedUserEventList;
    }
        //userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
        //List<UserEvent> eventList = userEventViewModel.getNonLiveUserEventListByDateRange(firstDateNoTime, lastDateNoTime);

    @Override
    public void loadFinish(List<UserEvent> userEventListOutput) {
        rawEventList1 = new ArrayList<>(userEventListOutput);
        Log.w("[Async RESULT]", rawEventList1.toString());

        if (!userEventListOutput.isEmpty()) {

            if (rawEventList1.hashCode() != (rawEventList2.hashCode())) {
                requesting = false;
                weekView.notifyDataSetChanged();
                rawEventList2 = new ArrayList<>(rawEventList1);
                Log.w("[Async RESULT]", "Notified");
            }

        } else {
            requesting = false;
            Log.w("[Async RESULT]", "No Event found");
        }


    }

    //@Override
    private void updateDateTextView() {
        String firstDayThisWeek = dateTimeFormatter.formatDateToString(
                weekView.getFirstVisibleDay().getTime(), "uni_date");

        String lastDayThisWeek = dateTimeFormatter.formatDateToString(
                weekView.getLastVisibleDay().getTime(), "uni_date");

        String textToDisplay = firstDayThisWeek + " - " + lastDayThisWeek;
        dateTextView.setText(textToDisplay);
    }

}
