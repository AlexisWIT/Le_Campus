package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.domain.UserEvent;
import com.uol.yt120.lecampus.repository.UserEventRepository;

import java.util.List;

public class UserEventViewModel extends AndroidViewModel {
    private UserEventRepository userEventRepository;
    private LiveData<UserEvent> userEventLiveData;
    private LiveData<List<UserEvent>> allUsereventsLiveData;

    public UserEventViewModel(@NonNull Application application) {
        super(application);
        userEventRepository = new UserEventRepository(application);
        allUsereventsLiveData = userEventRepository.getAllUserEventsLiveData();
    }

    public void insert(UserEvent userEvent) {
        userEventRepository.insert(userEvent);
    }

    public void update(UserEvent userEvent) {
        userEventRepository.update(userEvent);
    }

    public void delete(UserEvent userEvent) {
        userEventRepository.delete(userEvent);
    }

    public void deleteAll() {
        userEventRepository.deleteAllUserEvents();
    }

    public LiveData<List<UserEvent>> getAllUserEvents() {
        return allUsereventsLiveData;
    }

    public LiveData<UserEvent> getUserEventById(int id) { return userEventRepository.getFootprintById(id); }
}
