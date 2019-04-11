package com.uol.yt120.lecampus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uol.yt120.lecampus.adapter.ImageViewAdapter;

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
        return nearbyView;
    }

    // Options specifically for Nearby fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
