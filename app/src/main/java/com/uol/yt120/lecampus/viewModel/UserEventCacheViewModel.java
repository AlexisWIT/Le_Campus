package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.model.domain.UserEvent;

public class UserEventCacheViewModel extends AndroidViewModel {

    private MutableLiveData<UserEvent> selectedUserEvent = new MutableLiveData<>();

    public UserEventCacheViewModel(@NonNull Application application) {
        super(application);
    }

    public void setMutableUserEvent(UserEvent userEvent) {
        selectedUserEvent.setValue(userEvent);
    }

    public MutableLiveData<UserEvent> getMutableUserEvent() {
        return selectedUserEvent;
    }
}
