package com.uol.yt120.lecampus.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uol.yt120.lecampus.*;
import com.uol.yt120.lecampus.domain.Footprint;

import java.util.ArrayList;
import java.util.List;

public class FootprintAdapter extends RecyclerView.Adapter<FootprintAdapter.FootprintHolder> {

    private List<Footprint> footprintList = new ArrayList<>();

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
        footprintHolder.textViewTitle.setText(currentFootprint.getTitle());
        footprintHolder.textViewDesc.setText(currentFootprint.getDescription());
        footprintHolder.textViewDate.setText(currentFootprint.getCreateTime());
        // Footprint map thumbnail image
        // Add here

    }

    /**
     * Get how many item we want to display in recycle view
     * @return size of footprintList
     */
    @Override
    public int getItemCount() {
        return footprintList.size();
    }

    public void setFootprintList(List<Footprint> footprintList) {
        this.footprintList = footprintList;
        notifyDataSetChanged();
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
        }
    }
}
