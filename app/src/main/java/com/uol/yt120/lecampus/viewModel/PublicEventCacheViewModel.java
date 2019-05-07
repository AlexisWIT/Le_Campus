package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.Map;

public class PublicEventCacheViewModel extends AndroidViewModel {

    private MutableLiveData<Map<String, Object>> publicEventData = new MutableLiveData<>();

    public PublicEventCacheViewModel(@NonNull Application application) {
        super(application);
    }

    public void setPublicEventData(Map<String, Object> data) {
        publicEventData.setValue(data);
    }

    public MutableLiveData<Map<String, Object>> getPublicEventData() {
        return publicEventData;
    }
}
