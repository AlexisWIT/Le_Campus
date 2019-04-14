package com.uol.yt120.lecampus;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.RectF;
import android.os.AsyncTask;
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
import com.uol.yt120.lecampus.dataAccessObjects.UserEventDAO;
import com.uol.yt120.lecampus.database.LocalDatabase;
import com.uol.yt120.lecampus.domain.UserEvent;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;
import com.uol.yt120.lecampus.utility.TextValidator;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TimetableWeekChildFragment extends Fragment implements EventClickListener<UserEvent>, MonthChangeListener<UserEvent>,
        EventLongPressListener<UserEvent>, EmptyViewLongPressListener {

    public static final String TAG = TimetableWeekChildFragment.class.getSimpleName();
    DataPassListener mCallback;

    private UserEventViewModel userEventViewModel;
    private DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();
    private DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
    String currentDateWithTime = dateTimeCalculator.getToday(true);

    private View timetableWeekView;
    private WeekView<UserEvent> weekView;
    private TextView dateTextView;
    private TextValidator textValidator = new TextValidator();


    final int BOUND = 299;
    final Object[] eventGroup= new Object[300];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventGroup[BOUND] = "NO";
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
                Calendar calendar = weekView.getFirstVisibleDay();
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                weekView.goToDate(calendar);
                updateDateTextView();
            }
        });

        ImageView nextWeekAction = timetableWeekView.findViewById(R.id.action_timetable_next_week);
        nextWeekAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = weekView.getFirstVisibleDay();
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                weekView.goToDate(calendar);
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
        userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
        String firstDateNoTime = dateTimeFormatter.formatDateToString(firstDate.getTime(), "no_time");
        String lastDateNoTime = dateTimeFormatter.formatDateToString(lastDate.getTime(), "no_time");

        List<UserEvent> eventList = userEventViewModel.getNonLiveUserEventListByDateRange(firstDateNoTime, lastDateNoTime);

        for (UserEvent userEvent : eventList) {
            userEvent.setContext(getContext());
            updatedUserEventList.add(userEvent.toWeekViewEvent(getContext()));
        }

        Log.w("[Event List]", updatedUserEventList.toString());

        //new UpdateMonthlyEventListAsyncTask().execute();
        return updatedUserEventList;

    }

    private class UpdateMonthlyEventListAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserEventDAO userEventDAO;

        @Override
        protected Void doInBackground(Void... voids) {

//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            weekView.notifyDataSetChanged();
            return null;
        }
    }

    private void updateDateTextView() {
        String firstDayThisWeek = dateTimeFormatter.formatDateToString(
                weekView.getFirstVisibleDay().getTime(), "uni_date");

        String lastDayThisWeek = dateTimeFormatter.formatDateToString(
                weekView.getLastVisibleDay().getTime(), "uni_date");
        String textToDisplay = firstDayThisWeek + " - " + lastDayThisWeek;
        dateTextView.setText(textToDisplay);
    }
}
