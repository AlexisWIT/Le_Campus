package com.uol.yt120.lecampus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class TimetableFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_timetable));
        View timetableView = inflater.inflate(R.layout.fragment_timetable, container, false);
        return timetableView;
    }


    // Options specifically for Nearby fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_timetable, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh_timetable:
                Toast.makeText(getActivity(), "Refreshing timetable...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_opt_1:
                Toast.makeText(getActivity(), "Option_1 pressed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_opt_2:
                Toast.makeText(getActivity(), "Option_2 pressed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
