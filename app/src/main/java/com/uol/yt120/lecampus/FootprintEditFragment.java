package com.uol.yt120.lecampus;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.uol.yt120.lecampus.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 */
public class FootprintEditFragment extends Fragment {
    public static final String TAG = FootprintEditFragment.class.getSimpleName();

    public static final String KEY_FOOTPRINT_EDIT_DATA_RECEIVED = "com.uol.yt120.lecampus.KEY_FOOTPRINT_EDIT_DATA_RECEIVED";

    private EditText editTextTitle;
    private EditText editTextDescription;

    DataPassListener mCallback;
    String footprintDataForEdit;
    JSONObject footprintDataForEditJSON;

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
        getActivity().setTitle("Edit Footprint");
        View view = inflater.inflate(R.layout.fragment_footprint_edit, container, false);

        editTextTitle = view.findViewById(R.id.text_footprint_edit_title);
        editTextDescription = view.findViewById(R.id.text_footprint_edit_desc);

        Switch privacySwitch = view.findViewById(R.id.switch_footprint_edit_privacy);

        Button saveEditButton = view.findViewById(R.id.button_footprint_edit_save);
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(getActivity());
                saveEditedFootprint();
            }
        });

        return view;
    }

    private void saveEditedFootprint() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String timeCreated = null;
        String nodeList = null;
        try {
            timeCreated = footprintDataForEditJSON.getString("timeCreated");
            nodeList = footprintDataForEditJSON.getString("footprint");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("[DEBUG INFO]", "Unexpected data in JSON for edit.");
            return;
        }

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(getContext(), "Please insert a title and description.", Toast.LENGTH_SHORT);
            return;
        }

        FootprintViewModel footprintViewModel = ViewModelProviders.of(getActivity()).get(FootprintViewModel.class);
        Footprint footprint = new Footprint(title, description, nodeList, timeCreated);
        footprint.setId(footprintId);
        footprintViewModel.update(footprint);
        Toast.makeText(getContext(), "Footprint saved.", Toast.LENGTH_SHORT);

        JSONObject dataEditCompleteJSON = footprintDataForEditJSON;
        try {
            dataEditCompleteJSON.put("title", title);
            dataEditCompleteJSON.put("desc", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateFootprintDetail(dataEditCompleteJSON);

    }

    private void updateFootprintDetail(JSONObject resultJSON){
        try {
            resultJSON.put("from", TAG);
            resultJSON.put("to", FootprintDetailFragment.TAG);
        } catch (JSONException e){
            e.printStackTrace();
        }
        mCallback.passData(resultJSON.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            String dataReceived = args.getString(KEY_FOOTPRINT_EDIT_DATA_RECEIVED);
            Log.w("[DEBUG INFO]", "Data for edit received: ["+dataReceived+"]");

            try {
                footprintDataForEditJSON = new JSONObject(dataReceived);
                footprintId = footprintDataForEditJSON.getInt("footprintId");

                editTextTitle.setText((String) footprintDataForEditJSON.get("title"));
                editTextDescription.setText((String) footprintDataForEditJSON.get("desc"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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

    /**
     * Set screen auto-rotation disabled in current Fragment.
     */
    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
