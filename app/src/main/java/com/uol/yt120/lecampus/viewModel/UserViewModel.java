package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.domain.User;
import com.uol.yt120.lecampus.repository.UserEventRepository;

public class UserViewModel extends AndroidViewModel {

    private UserEventRepository userEventRepository;
    private LiveData<User> userLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userEventRepository = new UserEventRepository(application);
    }
}
