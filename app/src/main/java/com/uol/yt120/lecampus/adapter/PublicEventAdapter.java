package com.uol.yt120.lecampus.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.domain.UserEvent;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class PublicEventAdapter extends RecyclerView.Adapter<PublicEventAdapter.PublicEventHolder> {

    private List<UserEvent> publicEventList = new ArrayList<>();
    private OnItemClickListener listener;

    private DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
    private DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();

    private String filter;
    private static final String FEATURED = "featured";
    private static final String RECENT = "recent";
    private static final String NEARBY = "nearby";
    private static final String OFFERS = "offers";

    @NonNull
    @Override
    public PublicEventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View eventItemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_nearby_block_item, viewGroup, false);
        return new PublicEventHolder(eventItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicEventHolder publicEventHolder, int position) {
        UserEvent currentPublicEvent = publicEventList.get(position);
        publicEventHolder.publicEventTitle.setText(currentPublicEvent.getEventTitle());
        publicEventHolder.publicEventDesc.setText(currentPublicEvent.getEventDesc());

        //publicEventHolder.publicEventImage.setImageResource();

        switch (filter) {
            case FEATURED:
                publicEventHolder.publicEventSupply.setText(currentPublicEvent.getEventType());
                break;
            case RECENT:
                publicEventHolder.publicEventSupply.setText(currentPublicEvent.getStartTime());
                break;
            case NEARBY:
                publicEventHolder.publicEventSupply.setText(currentPublicEvent.getLocation());
                break;
            case OFFERS:
                publicEventHolder.publicEventSupply.setText(currentPublicEvent.getOffers());
                break;
        }


    }

    @Override
    public int getItemCount() {
        return publicEventList.size();
    }

    public void setPublicEventList(List<UserEvent> pEventList, String filter) {
        this.filter = filter;
        this.publicEventList = pEventList;
        notifyDataSetChanged();
    }

    public UserEvent getPublicEventAt(int position) {
        return publicEventList.get(position);
    }

    class PublicEventHolder extends RecyclerView.ViewHolder{

        private ImageView publicEventImage;
        private TextView publicEventTitle;
        private TextView publicEventSupply;
        private TextView publicEventDesc;

        public PublicEventHolder(@NonNull View itemView) {
            super(itemView);
            publicEventImage = itemView.findViewById(R.id.image_nearby_block_item);
            publicEventTitle = itemView.findViewById(R.id.text_nearby_block_item_title);
            publicEventSupply = itemView.findViewById(R.id.text_nearby_block_item_supply);
            publicEventDesc = itemView.findViewById(R.id.text_nearby_block_item_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(publicEventList.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(UserEvent publicEvent);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
