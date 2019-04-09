package com.uol.yt120.lecampus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


/**
 *
 */
public class FootprintEditFragment extends Fragment {

    public static final String EXTRA_ID = "com.uol.yt120.lecampus.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.uol.yt120.lecampus.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.uol.yt120.lecampus.EXTRA_DESC";
    public static final String EXTRA_NODELIST = "com.uol.yt120.lecampus.EXTRA_NODELIST";
    public static final String EXTRA_TIMECREATED = "com.uol.yt120.lecampus.EXTRA_TIMECREATED";
    public static final String EXTRA_PUBLISHER = "com.uol.yt120.lecampus.EXTRA_PUBLISHER";
    private EditText editTextTitle;
    private EditText editTextDescription;

    Intent intent;
    int footprintId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        intent = getActivity().getIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Edit Footprint");



        editTextTitle = getActivity().findViewById(R.id.text_footprint_edit_title);
        editTextDescription = getActivity().findViewById(R.id.text_footprint_edit_desc);

        editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
        editTextDescription.setText(intent.getStringExtra(EXTRA_DESC));

        return inflater.inflate(R.layout.fragment_footprint_edit, container, false);
    }

    private void saveEditedFootprint() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String timeCreated = intent.getStringExtra(EXTRA_TIMECREATED);
        String nodeList = intent.getStringExtra(EXTRA_NODELIST);

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(getContext(), "Please insert a title and description.", Toast.LENGTH_SHORT);
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESC, description);
        data.putExtra(EXTRA_NODELIST, nodeList);
        data.putExtra(EXTRA_TIMECREATED, timeCreated);

        footprintId = intent.getIntExtra(EXTRA_ID, -1);
        if (footprintId != -1) {
            data.putExtra(EXTRA_ID, footprintId);

        } else {
            Log.e("FootprintEditFragment", "Intent data input error.");
        }


        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_fragment_footprint_edit, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_changed_footprint:
                saveEditedFootprint();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
