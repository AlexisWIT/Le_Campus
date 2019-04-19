package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;

import java.util.List;

public class LocationDataCacheViewModel extends AndroidViewModel {
    private MutableLiveData<Location>  mutableLocationLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> mutableLocationListLiveData = new MutableLiveData<>();

    public LocationDataCacheViewModel(@NonNull Application application) {
        super(application);
    }

    public void setMutableLocationLiveData(Location location) {
        mutableLocationLiveData.setValue(location);
    }

    public MutableLiveData<Location> getMutableLocationLiveData() {
        return mutableLocationLiveData;
    }

    public void setMutableLocationListLiveData(List<Location> locationList) {
        mutableLocationListLiveData.setValue(locationList);
    }

    public MutableLiveData<List<Location>> getMutableLocationListLiveData() {
        return mutableLocationListLiveData;
    }
}
