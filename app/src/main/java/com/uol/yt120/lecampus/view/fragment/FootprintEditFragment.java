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

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.model.dataAccessObjects.DataPassListener;
import com.uol.yt120.lecampus.model.domain.Footprint;
import com.uol.yt120.lecampus.viewModel.FootprintCacheViewModel;
import com.uol.yt120.lecampus.viewModel.FootprintViewModel;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Edit Footprint information
 * Access the footprint data by FootprintCacheViewModel
 *
 * Transaction:
 *  - cancel: back to footprint detail page
 *  - save: back to footprint list page
 *  - failed to save: stay with notification
 */
public class FootprintEditFragment extends Fragment {
    public static final String TAG = FootprintEditFragment.class.getSimpleName();

    public static final String KEY_FOOTPRINT_EDIT_DATA_RECEIVED = "com.uol.yt120.lecampus.KEY_FOOTPRINT_EDIT_DATA_RECEIVED";

    private EditText editTextTitle;
    private EditText editTextDescription;

    //private FootprintEditViewModel footprintEditViewModel;
    private FootprintCacheViewModel footprintCacheViewModel;

    DataPassListener mCallback;
    private String footprintDataForEdit;
    private JSONObject footprintDataForEditJSON;
    private View footprintEditView;
    private Switch privacySwitch;
    private Button saveEditButton;
    private boolean shareEnabled;
    private Footprint footprintEdited;

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

        footprintEditView = inflater.inflate(R.layout.fragment_footprint_edit, container, false);

        editTextTitle = footprintEditView.findViewById(R.id.text_footprint_edit_title);
        editTextDescription = footprintEditView.findViewById(R.id.text_footprint_edit_desc);

        privacySwitch = footprintEditView.findViewById(R.id.switch_footprint_edit_privacy);
        privacySwitch.setChecked(false);
        privacySwitch.setOnCheckedChangeListener((switchView, isChecked) -> {
            shareEnabled = isChecked;
        });

        saveEditButton = footprintEditView.findViewById(R.id.button_footprint_edit_save);
        saveEditButton.setOnClickListener(buttonView -> {
            hideSoftKeyboard(getActivity());
            saveEditedFootprint();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }

        });

        return footprintEditView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFootprintInfoForEdit();
    }

    private void loadFootprintInfoForEdit(){
        try {
            footprintCacheViewModel = ViewModelProviders.of(getActivity()).get(FootprintCacheViewModel.class);
            footprintCacheViewModel.getSelectedFootprint().observe(this, new Observer<Footprint>() {
                @Override
                public void onChanged(@Nullable Footprint footprint) {
                    assert footprint != null;
                    boolean isSharedFootprint = footprint.getPrivacy() == 1;


                    editTextTitle.setText("");
                    editTextDescription.setText("");

                    footprintEdited = footprint;
                    editTextTitle.setText(footprint.getTitle());
                    editTextDescription.setText(footprint.getDescription());
                    privacySwitch.setChecked(isSharedFootprint);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveEditedFootprint(){
        String titleEdited = editTextTitle.getText().toString();
        String descriptionEdited = editTextDescription.getText().toString();
        Integer privacyEdited = shareEnabled ? 1 : 0;

        if (titleEdited.trim().isEmpty() || descriptionEdited.trim().isEmpty()) {
            Toast.makeText(getContext(), "Please insert a title and description.", Toast.LENGTH_SHORT).show();
            return;
        }

        FootprintViewModel footprintViewModel = ViewModelProviders.of(getActivity()).get(FootprintViewModel.class);

        footprintEdited.setTitle(titleEdited);
        footprintEdited.setDescription(descriptionEdited);
        footprintEdited.setPrivacy(privacyEdited);

        // Update footprint info in local database
        footprintViewModel.update(footprintEdited);

        // Update footprint info in cache ViewModel
        footprintCacheViewModel.setSelectedFootprint(footprintEdited);

        Toast.makeText(getContext(), "Footprint saved.", Toast.LENGTH_SHORT).show();

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
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
