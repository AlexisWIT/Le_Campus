package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.uol.yt120.lecampus.model.domain.UserEvent;

import java.util.List;

/**
 * The location broadcasted by service will be cached here
 *
 */
public class LocationDataCacheViewModel extends AndroidViewModel {
    private MutableLiveData<Location> googleLocationLiveData = new MutableLiveData<>();
    private MutableLiveData<Location> skyhookLocationLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> mutableLocationListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<UserEvent>> eventForDirection = new MutableLiveData<>();

    public LocationDataCacheViewModel(@NonNull Application application) {
        super(application);
    }

    public void setGoogleLocationLiveData(Location location) {
        googleLocationLiveData.setValue(location);
    }

    public MutableLiveData<Location> getGoogleLocationLiveData() {
        return googleLocationLiveData;
    }

    public void setSkyhookLocationLiveData(Location location) {
        skyhookLocationLiveData.setValue(location);
    }

    public MutableLiveData<Location> getSkyhookLocationLiveData() {
        return skyhookLocationLiveData;
    }

    public void setMutableLocationListLiveData(List<Location> locationList) {
        mutableLocationListLiveData.setValue(locationList);
    }

    public MutableLiveData<List<Location>> getMutableLocationListLiveData() {
        return mutableLocationListLiveData;
    }

    public MutableLiveData<List<UserEvent>> getEventForDirection() {
        return eventForDirection;
    }

    public void setEventForDirection(List<UserEvent> eventList) {
        eventForDirection.setValue(eventList);
    }
}
