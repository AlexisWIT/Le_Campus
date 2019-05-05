package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * The location broadcasted by service will be cached here
 *
 */
public class LocationDataCacheViewModel extends AndroidViewModel {
    private MutableLiveData<Location> mutableCurrentLocationLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> mutableLocationListLiveData = new MutableLiveData<>();
    private MutableLiveData<LatLng> latLngForDirection = new MutableLiveData<>();

    public LocationDataCacheViewModel(@NonNull Application application) {
        super(application);
    }

    public void setMutableCurrentLocationLiveData(Location location) {
        mutableCurrentLocationLiveData.setValue(location);
    }

    public MutableLiveData<Location> getMutableCurrentLocationLiveData() {
        return mutableCurrentLocationLiveData;
    }

    public void setMutableLocationListLiveData(List<Location> locationList) {
        mutableLocationListLiveData.setValue(locationList);
    }

    public MutableLiveData<List<Location>> getMutableLocationListLiveData() {
        return mutableLocationListLiveData;
    }

    public MutableLiveData<LatLng> getLatLngForDirection() {
        return latLngForDirection;
    }

    public void setLatLngForDirection(LatLng latLng) {
        latLngForDirection.setValue(latLng);
    }
}
