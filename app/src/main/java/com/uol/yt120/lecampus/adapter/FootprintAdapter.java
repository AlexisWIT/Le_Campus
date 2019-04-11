package com.uol.yt120.lecampus.adapter;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uol.yt120.lecampus.*;
import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.utility.ObjectComparetor;

import java.util.ArrayList;
import java.util.List;

public class FootprintAdapter extends ListAdapter<Footprint, FootprintAdapter.FootprintHolder> {

    //private List<Footprint> footprintList = new ArrayList<>();
    //private Footprint footprint = new Footprint();
    private OnItemClickListener listener;
    private static ObjectComparetor objectComparetor;

    public FootprintAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Footprint> DIFF_CALLBACK = new DiffUtil.ItemCallback<Footprint>() {
        @Override
        public boolean areItemsTheSame(@NonNull Footprint oldFootprint, @NonNull Footprint newFootprint) {
            return oldFootprint.getId() == newFootprint.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Footprint oldFootprint, @NonNull Footprint newFootprint) {
            return objectComparetor.areSameFootprints(oldFootprint, newFootprint);
        }
    };

    @NonNull
    @Override
    public FootprintHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_footprint_item, viewGroup, false);
        return new FootprintHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FootprintHolder footprintHolder, int position) {
        Footprint currentFootprint = getItem(position);
        footprintHolder.textViewTitle.setText(currentFootprint.getTitle());
        footprintHolder.textViewDesc.setText(currentFootprint.getDescription());
        footprintHolder.textViewDate.setText(currentFootprint.getCreateTime());
        // Footprint map thumbnail image
        // Add here

    }


    public Footprint getFootprintAt(int position) {
        return getItem(position);
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
                        listener.onItemClick(getItem(position));
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

}
