package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.domain.Footprint;

public class FootprintCacheViewModel extends AndroidViewModel {

    private MutableLiveData<Footprint> selectedFootprint = new MutableLiveData<>();

    public FootprintCacheViewModel(@NonNull Application application) {
        super(application);
    }

    public void setSelectedFootprint(Footprint footprint) {
        selectedFootprint.setValue(footprint);
    }

    public MutableLiveData<Footprint> getSelectedFootprint() {
        return selectedFootprint;
    }
}
