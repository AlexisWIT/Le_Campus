/*
 * Copyright 2019 yt120@student.le.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uol.yt120.lecampus.view.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.view.adapter.FootprintAdapter;
import com.uol.yt120.lecampus.model.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.model.domain.Footprint;
import com.uol.yt120.lecampus.utility.JsonDataProcessor;
import com.uol.yt120.lecampus.viewModel.FootprintCacheViewModel;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FootprintFragment extends Fragment {
    public static final String TAG = FootprintFragment.class.getSimpleName();

    public static final int ADD_FOOTPRINT_REQUEST = 1;
    public static final int VIEW_FOOTPRINT_REQUEST = 2;
    public static final int EDIT_FOOTPRINT_REQUEST = 3;
    private FootprintViewModel footprintViewModel;
    private FootprintCacheViewModel footprintCacheViewModel;

    private boolean deleteConfirmed = true;
    private List<Footprint> currentFootprintList = new ArrayList<>();
    private FloatingActionButton buttonAddFootprint;

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

        buttonAddFootprint = (FloatingActionButton) footprintView.findViewById(R.id.button_add_footprint);
        buttonAddFootprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirmation dialog pop out, Redirect to Google map fragment and start tracking

                notifyMapToStartRecording();
//                FragmentManager fragmentManager = getChildFragmentManager();
//                fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                        .replace(R.id.fragment_container, new GoogleMapsFragment(),"GoogleMapsFragment")
//                        .commit();
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
                currentFootprintList = footprintList;
                //footprintAdapter.submitList(null);
                footprintAdapter.setFootprintList(footprintList);
                Log.w("[Footprint Fragment]", "First footprint in list:"+footprintList.toString());

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

                Snackbar.make(footprintView, "Footprint deleted", Snackbar.LENGTH_LONG)
                        .setDuration(3000)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteConfirmed = false;
                            }
                        }).show();

                Footprint footprint = footprintAdapter.getFootprintAt(viewHolder.getAdapterPosition());
                Handler handler=new Handler();
                Runnable r=new Runnable() {
                    public void run() {
                        if (deleteConfirmed) {
                            // Delete item
                            footprintViewModel.delete(footprint);

                            if (getActivity()!=null) {
                                Toast.makeText(getActivity(), "Footprint deleted", Toast.LENGTH_SHORT).show();
                                //((NavigationActivity) getActivity()).showToast("Footprint deleted", Toast.LENGTH_SHORT);
                            }

                        } else {
                            // Reload list
                            footprintAdapter.setFootprintList(currentFootprintList);
                            deleteConfirmed = true;
                        }
                    }
                };
                handler.postDelayed(r, 3100);
            }
        }).attachToRecyclerView(recyclerView);

        footprintAdapter.setOnItemClickListener(new FootprintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Footprint footprint) {
                buttonAddFootprint.hide();
                Integer footprintId = footprint.getId();
                //footprintViewModel.setFootprintMutableLiveData(footprint);
                footprintCacheViewModel = ViewModelProviders.of(getActivity()).get(FootprintCacheViewModel.class);
                footprintCacheViewModel.setSelectedFootprint(footprint);

                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, new FootprintDetailFragment(), FootprintDetailFragment.TAG)
                        .addToBackStack(TAG)
                        .commit();
                //sendDataThroughActivity(footprint, footprintId);
            }
        });

        buttonAddFootprint.show();
        return footprintView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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


    private void sendDataThroughActivity(Footprint footprint, Integer id) {
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

    public void notifyMapToStartRecording() {
        JsonDataProcessor jsonDataProcessor = new JsonDataProcessor();
        String message =
                jsonDataProcessor.createJSONStringForPassData(FootprintFragment.TAG, GoogleMapsFragment.TAG, GoogleMapsFragment.START_RECORDING_CODE);
        mCallback.passData(message);

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
