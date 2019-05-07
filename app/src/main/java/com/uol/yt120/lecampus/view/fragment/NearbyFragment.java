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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.restAPI.RestApiClient;
import com.uol.yt120.lecampus.model.restDomain.PublicEvent;
import com.uol.yt120.lecampus.utility.HttpHandler;
import com.uol.yt120.lecampus.view.adapter.NearbyAdapter;
import com.uol.yt120.lecampus.view.adapter.PublicEventAdapter;
import com.uol.yt120.lecampus.viewModel.PublicEventCacheViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyFragment extends Fragment {

    private PublicEventCacheViewModel publicEventCacheViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_nearby));
        View nearbyView = inflater.inflate(R.layout.fragment_nearby, container, false);

        loadEventData();

        RecyclerView recyclerView = (RecyclerView)nearbyView.findViewById(R.id.nearby_list);
        recyclerView.setHasFixedSize(true);

        final NearbyAdapter nearbyAdapter = new NearbyAdapter(getActivity());
        recyclerView.setAdapter(nearbyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        publicEventCacheViewModel = ViewModelProviders.of(getActivity()).get(PublicEventCacheViewModel.class);
        publicEventCacheViewModel.getPublicEventData().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> eventDataMap) {
                if (!eventDataMap.isEmpty()) {
                    Log.w("[Nearby Fragment]", "Event data found in Cache ViewModel");
                    nearbyAdapter.setPublicEventBlockList(eventDataMap);

                } else {
                    Toast.makeText(getActivity(), "Please connect to the server", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return nearbyView;
    }

    public void loadEventData() {
        HttpHandler httpHandler = new HttpHandler();
        RestApiClient restApiClient = httpHandler.initRestApiClient();
        Call<List<PublicEvent>> crimeCall = restApiClient.getPublicEvents();
        Log.w("[Nearby Fragment]", "Loading Event Data");
        crimeCall.enqueue(new Callback<List<PublicEvent>>() {
            @Override
            public void onResponse(Call<List<PublicEvent>> call, Response<List<PublicEvent>> response) {
                if (!response.isSuccessful()) {
                    Log.e("[Nearby Fragment]", "Error occurred, Code: "+response.code()); // 404 or other
                    return;
                }
                List<PublicEvent> publicEvents = response.body();
                Log.w("[Nearby Fragment]", "Event data received");
                if (!publicEvents.isEmpty()) {
                    setupEvents(publicEvents);
                }

            }

            @Override
            public void onFailure(Call<List<PublicEvent>> call, Throwable t) {
                Log.e("[Nearby Fragment]", "Error occurred, Info: " + t.getMessage());
            }
        });
    }


    public void setupEvents(List<PublicEvent> eventList) {
        Map<String, Object> eventData = new HashMap<>();
        List<List<PublicEvent>> blockList = new ArrayList<>();
        List<String> filterNameList = new ArrayList<>();
        List<String> filterDescList = new ArrayList<>();

        List<PublicEvent> recentEventList = new ArrayList<>();
        List<PublicEvent> nearbyEventList = new ArrayList<>();
        List<PublicEvent> featuredEventList = new ArrayList<>();
        List<PublicEvent> offersEventList = new ArrayList<>();

        for (PublicEvent event: eventList) {
            if (recentEventList.size() <= 10) {
                recentEventList.add(event);
            }

            if (isNearBy(event) && nearbyEventList.size() <= 10) {
                nearbyEventList.add(event);
            }

            if (isFeatured(event) && featuredEventList.size() <= 10) {
                featuredEventList.add(event);
            }

            if (isOffered(event) && offersEventList.size() <= 10) {
                offersEventList.add(event);
            }
        }

        blockList.add(recentEventList);
        blockList.add(nearbyEventList);
        blockList.add(featuredEventList);
        blockList.add(offersEventList);

        filterNameList.add("Recent");
        filterNameList.add("Nearby");
        filterNameList.add("Featured");
        filterNameList.add("Offers");

        filterDescList.add("Recent events");
        filterDescList.add("Nearby events");
        filterDescList.add("Featured events");
        filterDescList.add("Offered events");

        eventData.put("blockList", blockList);
        eventData.put("filter", filterNameList);
        eventData.put("filterDesc", filterDescList);

        //Log.w("[Nearby Fragment]", eventData.toString());

        publicEventCacheViewModel = ViewModelProviders.of(getActivity()).get(PublicEventCacheViewModel.class);
        publicEventCacheViewModel.setPublicEventData(eventData);

    }

    private boolean isNearBy(PublicEvent publicEvent) {
        boolean result = true;

        return result;
    }

    private boolean isFeatured(PublicEvent publicEvent) {
        boolean result = true;

        return result;
    }

    private boolean isOffered(PublicEvent publicEvent) {
        boolean result = true;

        return result;
    }



    // Options specifically for Nearby fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_nearby, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search_nearby:
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_opt_1_nearby:
                Toast.makeText(getActivity(), "Option_1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_opt_2_nearby:
                Toast.makeText(getActivity(), "Option_2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
