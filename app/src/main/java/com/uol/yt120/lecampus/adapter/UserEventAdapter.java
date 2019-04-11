package com.uol.yt120.lecampus.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uol.yt120.lecampus.*;
import com.uol.yt120.lecampus.domain.UserEvent;

import java.util.ArrayList;
import java.util.List;

public class UserEventAdapter extends RecyclerView.Adapter<UserEventAdapter.UserEventHolder> {

    public static final String LOAD_EVENTLIST = "load_eventlist";
    public static final String LOAD_EVENT = "load_event";

    private List<UserEvent> userEventList = new ArrayList<>();
    private UserEvent userEvent = new UserEvent();
    private OnItemClickListener listener;
    private String adapterMode = "load_eventlist";

    @NonNull
    @Override
    public UserEventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        int layoutId;
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_timetable_day_item, viewGroup, false);
        return new UserEventHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserEventHolder userEventHolder, int position) {

        switch (adapterMode) {
            case LOAD_EVENTLIST:
                UserEvent currentUserEvent = userEventList.get(position);
                String eventCode = currentUserEvent.getEventCode();
                String eventTitle = currentUserEvent.getEventTitle();

                String startTime = currentUserEvent.getStartTime();

                String eventType = currentUserEvent.getEventType();
                if (!isEmptyString(eventType)) {
                    eventType = " - "+eventType;
                }

                String address = currentUserEvent.getAddress();
                if (!isEmptyString(address)) {
                    address = " - "+address;
                }

                String duration = " - "+currentUserEvent.getDuration();
                if (!isEmptyString(duration)) {
                    duration = " - "+duration;
                }

                String textTitle;
                if (!isEmptyString(eventCode)) {
                    textTitle = "("+eventCode+") "+eventTitle;
                } else {
                    textTitle = eventTitle;
                }

                String textInfo = startTime+eventType+address+duration;

                userEventHolder.textViewTitle.setText(currentUserEvent.getEventTitle());
                userEventHolder.textViewInfo.setText(textInfo);
                break;

            case LOAD_EVENT:
                break;
        }


    }

    public UserEvent getUserEventAt(int position) {
        return userEventList.get(position);
    }

    public void setUserEventList(List<UserEvent> userEventList) {
        this.userEventList = userEventList;
        setAdapterMode(LOAD_EVENTLIST);
        notifyDataSetChanged();
    }

    public void setUserEvent(UserEvent userEvent) {
        this.userEvent = userEvent;
        setAdapterMode(LOAD_EVENT);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() { return userEventList.size(); }

    class UserEventHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitle;
        private TextView textViewInfo;

        public UserEventHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.label_userevent_item_title);
            textViewInfo = itemView.findViewById(R.id.label_userevent_item_info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(userEventList.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UserEvent userEvent);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public String getAdapterMode() {
        return adapterMode;
    }

    private void setAdapterMode(String adapterMode) {
        this.adapterMode = adapterMode;
    }

    private boolean isEmptyString(String string) {
        return string == null ||
                string.equalsIgnoreCase("null") ||
                (TextUtils.equals(string, "null")) ||
                (TextUtils.isEmpty(string));
    }
}
