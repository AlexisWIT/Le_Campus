package com.uol.yt120.lecampus;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uol.yt120.lecampus.adapter.FootprintAdapter;
import com.uol.yt120.lecampus.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FootprintFragment extends Fragment {
    public static final String TAG = FootprintFragment.class.getSimpleName();

    public static final int ADD_FOOTPRINT_REQUEST = 1;
    public static final int VIEW_FOOTPRINT_REQUEST = 2;
    public static final int EDIT_FOOTPRINT_REQUEST = 3;
    private FootprintViewModel footprintViewModel;

    DataPassListener mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_footprint));

        View footprintView = inflater.inflate(R.layout.fragment_footprint, container, false);

        FloatingActionButton buttonAddFootprint = (FloatingActionButton) footprintView.findViewById(R.id.button_add_footprint);
        buttonAddFootprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirmation dialog pop out, Redirect to Google map fragment and start tracking

                FragmentManager fragmentManager = getChildFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new GoogleMapsFragment())
                        .addToBackStack(null)
                        .commit();
//                Intent intent = new Intent(getActivity(), GoogleMapsFragment.class);
//                startActivityForResult(intent, ADD_FOOTPRINT_REQUEST);
            }
        });

        RecyclerView recyclerView = (RecyclerView)footprintView.findViewById(R.id.recycler_view_footprint);

        recyclerView.setHasFixedSize(true);

        final FootprintAdapter footprintAdapter = new FootprintAdapter();
        recyclerView.setAdapter(footprintAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setItemAnimator();

        footprintViewModel = ViewModelProviders.of(getActivity()).get(FootprintViewModel.class);
        footprintViewModel.getAllFootprints().observe(this, new Observer<List<Footprint>>() {
            @Override
            public void onChanged(@Nullable List<Footprint> footprintList) {
                // Update recycleView
                if (footprintList.isEmpty()) {
                    Toast.makeText(getActivity(), "No footprint to show.", Toast.LENGTH_SHORT).show();
                }
                //footprintAdapter.submitList(null);
                footprintAdapter.setFootprintList(footprintList);
                Log.w("[Footprint Fragment]", "First footprint in list:"+footprintList.get(0).toString());

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
                Integer footprintId = footprint.getId();
                footprintViewModel.setFootprintMutableLiveData(footprint);










                //sendDateThroughActivity(footprint, footprintId);

            }
        });

        return footprintView;
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


    private void sendDateThroughActivity(Footprint footprint, Integer id) {
        JSONObject footprintDetailJSON = new JSONObject();

        String title = footprint.getTitle();
        String desc = footprint.getDescription();
        String timeCreated = footprint.getCreateTime();
        //String publisher = footprint.getUsername();
        String publisher = "default";
        String trackpointList = footprint.getNodeList();
//        ArrayList<HashMap<String, Object>> currentTrackpointList = footprint.getNodeList();
//
//        JSONArray trackpointJSONArray = new JSONArray();
//        for (HashMap<String, Object> trackpoint : trackpointList) {
//            JSONObject trackpointJSONElement = new JSONObject(trackpoint);
//            trackpointJSONArray.put(trackpointJSONElement);
//        }

        try {
            footprintDetailJSON.put("from", TAG);
            footprintDetailJSON.put("to", FootprintDetailFragment.TAG);
            footprintDetailJSON.put("footprintId", id);
            footprintDetailJSON.put("title", title);
            footprintDetailJSON.put("desc", desc);
            footprintDetailJSON.put("timeCreated", timeCreated);
            footprintDetailJSON.put("publisher", publisher);
            footprintDetailJSON.put("footprint", trackpointList);

            String footprintDetailJSONString = footprintDetailJSON.toString();
            Log.w("[DEBUG INFO]", "Ready to send: ["+footprintDetailJSONString+"]");
            mCallback.passData(footprintDetailJSONString);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Makes sure that the host activity has implemented the callback interface
        try {
            mCallback = (DataPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ " did not implement DataPassListener");
        }
    }

}
