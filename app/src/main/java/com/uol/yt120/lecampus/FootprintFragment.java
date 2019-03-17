package com.uol.yt120.lecampus;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uol.yt120.lecampus.adapter.FootprintAdapter;
import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

import java.util.List;

public class FootprintFragment extends Fragment {
    private FootprintViewModel footprintViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_view_footprint);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        FootprintAdapter footprintAdapter = new FootprintAdapter();
        recyclerView.setAdapter(footprintAdapter);

        footprintViewModel = ViewModelProviders.of(this).get(FootprintViewModel.class);
        footprintViewModel.getAllFootprints().observe(this, new Observer<List<Footprint>>() {
            @Override
            public void onChanged(@Nullable List<Footprint> footprintList) {
                // Update recycleView
                footprintAdapter.setFootprintList(footprintList);
                Toast.makeText(getActivity(), "Footprints updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_route));
        View routeView = inflater.inflate(R.layout.fragment_footprint_empty, container, false);
        return routeView;
    }
}
