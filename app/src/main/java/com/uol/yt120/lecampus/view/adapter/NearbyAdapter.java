package com.uol.yt120.lecampus.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.restDomain.PublicEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NearbyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;

    private List<List<PublicEvent>> publicEventBlockList = new ArrayList<>();
    private List<String> filterList = new ArrayList<>();
    private List<String> filterDescList = new ArrayList<>();
    //private OnItemClickListener listener;


    public NearbyAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0:
                //Log.w("[Nearby Adapter]", "create image banner");
                View imageBannerView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.fragment_nearby_banner, viewGroup, false);
                return new BannerViewHolder(imageBannerView, context);

            default:
                //Log.w("[Nearby Adapter]", "create event blocks");
                View eventBlockView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.fragment_nearby_block, viewGroup, false);
                return new EventBlockViewHolder(eventBlockView);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 0: // Load image banner
                //Log.w("[Nearby Adapter]", "load image banner");
                BannerViewHolder bannerViewHolder = (BannerViewHolder) viewHolder;
                final ImageViewPagerAdapter imageViewPagerAdapter = new ImageViewPagerAdapter(context);
                bannerViewHolder.imageViewPager.setAdapter(imageViewPagerAdapter);

                break;

            default: // Load event blocks - list of event list
                //Log.w("[Nearby Adapter]", "load event blocks");
                EventBlockViewHolder eventBlockViewHolder = (EventBlockViewHolder) viewHolder;
                List<PublicEvent> currentPublicEventList = publicEventBlockList.get(position-1);
                String currentPublicEventFilter = filterList.get(position-1);
                String currentPublicEventFilterDesc = filterDescList.get(position-1);
                final PublicEventAdapter publicEventAdapter = new PublicEventAdapter();
                publicEventAdapter.setPublicEventList(currentPublicEventList, currentPublicEventFilter);

                eventBlockViewHolder.publicEventBlock.setAdapter(publicEventAdapter);
                eventBlockViewHolder.publicEventBlock.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                eventBlockViewHolder.publicEventBlock.setHasFixedSize(true);

                eventBlockViewHolder.publicEventBlockName.setText(currentPublicEventFilter);
                eventBlockViewHolder.publicEventBlockDesc.setText(currentPublicEventFilterDesc);
                eventBlockViewHolder.publicEventBlockButton.setText("MORE");
                break;
        }

    }

    public void setPublicEventBlockList(Map<String, Object> blockList) {
        this.publicEventBlockList = (List<List<PublicEvent>>) blockList.get("blockList");
        this.filterList = (List<String>) blockList.get("filter");
        this.filterDescList = (List<String>) blockList.get("filterDesc");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return publicEventBlockList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Log.w("[Nearby Adapter]", "itemView Type (position): "+position);
        return position;
    }



    class BannerViewHolder extends RecyclerView.ViewHolder {
        private ViewPager imageViewPager;

        public BannerViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            imageViewPager = itemView.findViewById(R.id.viewPager_image);
            imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float posotionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }
    }

    class EventBlockViewHolder extends RecyclerView.ViewHolder {
        private TextView publicEventBlockName;
        private TextView publicEventBlockDesc;
        private RecyclerView publicEventBlock;
        private Button publicEventBlockButton;

        public EventBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            publicEventBlockName = itemView.findViewById(R.id.label_nearby_block_title);
            publicEventBlockDesc = itemView.findViewById(R.id.text_nearby_block_desc);
            publicEventBlock = itemView.findViewById(R.id.recyclerview_nearby_block);
            publicEventBlockButton = itemView.findViewById(R.id.button_nearby_block_more);

//            publicEventBlockButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition();
//                    if (listener != null && position != RecyclerView.NO_POSITION) {
//                        listener.onItemClick(publicEventBlockList.get(position));
//                    }
//                }
//            });
        }
    }

//    public interface OnItemClickListener {
//        void onItemClick(List<PublicEvent> publicEventBlockList);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
}
