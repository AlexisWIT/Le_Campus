package com.uol.yt120.lecampus;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uol.yt120.lecampus.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.domain.UserEvent;
import com.uol.yt120.lecampus.viewModel.UserEventViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class UserEventDetailFragment extends Fragment {
    public static final String TAG = UserEventDetailFragment.class.getSimpleName();

    public static final String KEY_USEREVENT_DATA_RECEIVED = "com.uol.yt120.lecampus.KEY_USEREVENT_DATA_RECEIVED";

    DataPassListener mCallback;
    int userEventId;
    TextView textEventTitle;
    TextView textEventType;
    TextView textEventCode;
    TextView textEventLocation;

    private UserEventViewModel userEventViewModel;

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
        textEventType = view.findViewById(R.id.text_userevent_detail_type);
        textEventCode = view.findViewById(R.id.text_userevent_detail_code);
        textEventLocation = view.findViewById(R.id.text_userevent_detail_location);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            String dataReceived = args.getString(KEY_USEREVENT_DATA_RECEIVED);
            Log.w("[DEBUG INFO]", "UserEventDetailFragment - Data received: ["+dataReceived+"]");

            try {
                JSONObject userEventIdJSON = new JSONObject(dataReceived);
                userEventId = userEventIdJSON.getInt("id");

                userEventViewModel = ViewModelProviders.of(getActivity()).get(UserEventViewModel.class);
                userEventViewModel.getUserEventById(userEventId).observe(this, new Observer<UserEvent>() {
                    @Override
                    public void onChanged(@Nullable UserEvent userEvent) {
                        textEventTitle.setText(userEvent.getEventTitle());
                        textEventType.setText(userEvent.getEventType());
                        textEventCode.setText(userEvent.getEventCode());
                        textEventLocation.setText(userEvent.getLocation());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
