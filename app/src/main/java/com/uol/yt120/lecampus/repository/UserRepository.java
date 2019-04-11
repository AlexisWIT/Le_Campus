package com.uol.yt120.lecampus.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.uol.yt120.lecampus.dataAccessObjects.UserDAO;
import com.uol.yt120.lecampus.database.LocalDatabase;
import com.uol.yt120.lecampus.domain.User;

public class UserRepository {
    private UserDAO userDAO;
    private LiveData<User> userLiveData;

//    public UserRepository(Application application) {
//        LocalDatabase database = LocalDatabase.getInstance(application);
//        userDAO = database.userDAO();
//        userLiveData = userDAO.getUserById();
//    }

}
