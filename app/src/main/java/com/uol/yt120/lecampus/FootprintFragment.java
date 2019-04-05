package com.uol.yt120.lecampus;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uol.yt120.lecampus.adapter.FootprintAdapter;
import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FootprintFragment extends Fragment {
    public static final int ADD_FOOTPRINT_REQUEST = 1;
    private FootprintViewModel footprintViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton buttonAddFootprint = getActivity().findViewById(R.id.button_add_footprint);
        buttonAddFootprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirmation dialog pop out, Redirect to Google map fragment and start tracking

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new GoogleMapsFragment()).commit();
//                Intent intent = new Intent(getActivity(), GoogleMapsFragment.class);
//                startActivityForResult(intent, ADD_FOOTPRINT_REQUEST);
            }
        });

        RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_view_footprint);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final FootprintAdapter footprintAdapter = new FootprintAdapter();
        recyclerView.setAdapter(footprintAdapter);

        footprintViewModel = ViewModelProviders.of(getActivity()).get(FootprintViewModel.class);
        footprintViewModel.getAllFootprints().observe(this, new Observer<List<Footprint>>() {
            @Override
            public void onChanged(@Nullable List<Footprint> footprintList) {
                // Update recycleView
                footprintAdapter.setFootprintList(footprintList);
                Toast.makeText(getActivity(), "Footprints updated", Toast.LENGTH_SHORT).show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
               footprintViewModel.delete(footprintAdapter.getFootprintAt(viewHolder.getAdapterPosition()));
               Toast.makeText(getActivity(), "Footprint deleted", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(recyclerView);

        footprintAdapter.setOnItemClickListener(new FootprintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Footprint footprint) {
                Intent intent = new Intent(getActivity(), FootprintDetailFragment.class);
                //intent.putExtra()
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_route));
        View routeView = inflater.inflate(R.layout.fragment_footprint, container, false);
        return routeView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_footprint, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_footprint:
                footprintViewModel.deleteAll();
                Toast.makeText(getActivity(), "All footprints deleted", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_opt_1_footprint:
                Toast.makeText(getActivity(), "This function is temporarily unavailable", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_opt_2_footprint:
                Toast.makeText(getActivity(), "This function is temporarily unavailable", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == ADD_FOOTPRINT_REQUEST && resultCode == RESULT_OK) {
//            String title = data.getStringExtra(Add)
//        }
//
//    }
}
