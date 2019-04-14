package com.uol.yt120.lecampus;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.uol.yt120.lecampus.adapter.UserEventAdapter;
import com.uol.yt120.lecampus.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.dataAccessObjects.UserEventDAO;
import com.uol.yt120.lecampus.domain.UserEvent;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TimetableDayChildFragment extends Fragment {

    public static final String TAG = TimetableDayChildFragment.class.getSimpleName();
    DataPassListener mCallback;

    private UserEventViewModel userEventViewModel;
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
        recyclerView.setHasFixedSize(false);

        final UserEventAdapter userEventAdapter = new UserEventAdapter();
        recyclerView.setAdapter(userEventAdapter);
        currentDateWithTime = dateTimeCalculator.getToday(true);
        currentDate = dateTimeCalculator.getToday(false);
        TextView textView = timetableDayView.findViewById(R.id.label_timetable_day_date);
        textView.setText(dateTimeCalculator.getTodayDate());
        // Return "08-29"

        Log.w("[DAY EVENT]", "Today: ["+ currentDate +"]" + currentDateWithTime);
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//                return false;
//            }
//        }).attachToRecyclerView(recyclerView);

        userEventAdapter.setOnItemClickListener(new UserEventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserEvent userEvent) {
                JSONObject userEventDetailJSON = new JSONObject();
                Integer userEventId = userEvent.getId();

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
        return timetableDayView;

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
