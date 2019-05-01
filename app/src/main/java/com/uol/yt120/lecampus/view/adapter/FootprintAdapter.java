package com.uol.yt120.lecampus.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uol.yt120.lecampus.*;
import com.uol.yt120.lecampus.model.domain.Footprint;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class FootprintAdapter extends RecyclerView.Adapter<FootprintAdapter.FootprintHolder> {

    private List<Footprint> footprintList = new ArrayList<>();
    //private Footprint footprint = new Footprint();
    private OnItemClickListener listener;

    private DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();
    private DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();

    @NonNull
    @Override
    public FootprintHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_footprint_item, viewGroup, false);
        return new FootprintHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FootprintHolder footprintHolder, int position) {

        Footprint currentFootprint = footprintList.get(position);
        Log.w("[Footprint Adapter]", "Current Footprint: "+currentFootprint.toString());
        footprintHolder.textViewTitle.setText(currentFootprint.getTitle());
        footprintHolder.textViewDesc.setText(currentFootprint.getDescription());
        footprintHolder.textViewDate.setText(currentFootprint.getCreateTime());
        // Footprint map thumbnail image
        // Add here

    }

    @Override
    public int getItemCount(){
        return footprintList.size();
    }

    public void setFootprintList(List<Footprint> fprintList) {
        this.footprintList = fprintList;
        notifyDataSetChanged();
    }


    public Footprint getFootprintAt(int position) {
        return footprintList.get(position);
    }


    class FootprintHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDesc;
        private TextView textViewDate;

        public FootprintHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_footprint_title);
            textViewDesc = itemView.findViewById(R.id.text_footprint_desc);
            textViewDate = itemView.findViewById(R.id.text_footprint_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(footprintList.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Footprint footprint);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     *  This method has to be override simply because
     *  the dev is sleepy when they create the original method.
     * @param list new (or not?) list to update on UI
     */
//    @Override
//    public void submitList(final List<Footprint> list) {
//        Log.w("[Footprint Adapter]", "Current Footprint: "+list.get(0).toString());
//        super.submitList(list != null ? new ArrayList<>(list) : null);
//
//    }

}
