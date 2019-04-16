package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.domain.User;
import com.uol.yt120.lecampus.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private LiveData<User> userLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void insert(User user) { userRepository.insert(user);}

    public void update(User user) { userRepository.update(user);}

    public void delete(User user) { userRepository.delete(user);}

    public void deleteAllUser() { userRepository.deleteAllUser();}

    public LiveData<User> getUserLiveDataById(int id) {return userRepository.getUserLiveDataById(id);}

    public LiveData<User> getUserLiveDataByServerId(int serverId) {return userRepository.getUserLiveDataByServerId(serverId);}
}
