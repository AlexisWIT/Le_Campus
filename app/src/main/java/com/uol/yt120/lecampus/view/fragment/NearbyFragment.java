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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.uol.yt120.lecampus.view.adapter.ImageViewAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyFragment extends Fragment {

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

        ViewPager viewPager = nearbyView.findViewById(R.id.viewPager_image);
        ImageViewAdapter adapter = new ImageViewAdapter(getActivity());
        viewPager.setAdapter(adapter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        //RecyclerView recyclerView = (RecyclerView)nearbyView.findViewById(


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
                Log.e("[Nearby Fragment]", "Event data received");
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
