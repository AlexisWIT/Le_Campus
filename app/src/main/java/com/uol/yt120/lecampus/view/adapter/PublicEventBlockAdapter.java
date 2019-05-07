package com.uol.yt120.lecampus.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.domain.UserEvent;

import java.util.ArrayList;
import java.util.List;

public class PublicEventBlockAdapter extends RecyclerView.Adapter<PublicEventBlockAdapter.PublicEventBlockHolder> {

    private List<List<UserEvent>> publicEventBlockList = new ArrayList<List<UserEvent>>();
    private String filter;
    private String filterDesc;
    private OnItemClickListener listener;
    private PublicEventAdapter adapter;

    @NonNull
    @Override
    public PublicEventBlockHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View eventBlockView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_nearby_block, viewGroup, false);
        return new PublicEventBlockHolder(eventBlockView);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicEventBlockHolder publicEventBlockHolder, int blockPosition) {
        List<UserEvent> currentPublicEventList = publicEventBlockList.get(blockPosition);
        publicEventBlockHolder.publicEventBlockName.setText(this.filter);
        publicEventBlockHolder.publicEventBlockDesc.setText(this.filterDesc);
        //publicEventBlockHolder.publicEventBlockButton.setOnClickListener();
        publicEventBlockHolder.publicEventBlock.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return publicEventBlockList.size();
    }

    public void setPublicEventListBlockList(List<List<UserEvent>> blockList, String filter, String filterDesc, PublicEventAdapter adapter) {
        this.publicEventBlockList = blockList;
        this.filter = filter;
        this.filterDesc = filterDesc;
        this.adapter = adapter;
        notifyDataSetChanged();
    }

    public List<UserEvent> getPublicEventListBlockAt(int position) {
        return publicEventBlockList.get(position);
    }

    class PublicEventBlockHolder extends RecyclerView.ViewHolder{
        private TextView publicEventBlockName;
        private TextView publicEventBlockDesc;
        private RecyclerView publicEventBlock;
        private Button publicEventBlockButton;

        public PublicEventBlockHolder(View itemView) {
            super(itemView);
            publicEventBlockName = itemView.findViewById(R.id.label_nearby_block_title);
            publicEventBlockDesc = itemView.findViewById(R.id.text_nearby_block_desc);
            publicEventBlock = itemView.findViewById(R.id.recyclerview_nearby_block);
            publicEventBlockButton = itemView.findViewById(R.id.button_nearby_block_more);

            publicEventBlockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(publicEventBlockList.get(position));
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(List<UserEvent> publicEventBlockList);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
