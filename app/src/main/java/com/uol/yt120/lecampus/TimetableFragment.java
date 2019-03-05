package com.uol.yt120.lecampus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class TimetableFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.title_fragment_timetable));
        View timetableView = inflater.inflate(R.layout.fragment_timetable, container, false);
        return timetableView;
    }


    // Options specifically for Timetable fragment
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

//                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("timetableFrag");
//                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                ft.detach(fragment);
//                ft.attach(fragment);
//                ft.commit();
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof TimetableFragment) {
                FragmentTransaction fragTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();}

                Log.i("[Account Fragmt]", "Reload Timetable");
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
