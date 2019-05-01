package com.uol.yt120.lecampus.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.uol.yt120.lecampus.model.dataAccessObjects.UserDAO;
import com.uol.yt120.lecampus.model.database.LocalDatabase;
import com.uol.yt120.lecampus.model.domain.User;

public class UserRepository {
    private UserDAO userDAO;
    private LiveData<User> userLiveData;

    public UserRepository(Application application) {
        LocalDatabase database = LocalDatabase.getInstance(application);
        userDAO = database.userDAO();
    }

    public void insert(User user) {
        new InsertUserAsyncTask(userDAO).execute(user);
    }

    public void update(User user) {
        new UpdateUserAsyncTask(userDAO).execute(user);
    }

    public void delete(User user) {
        new DeleteUserAsyncTask(userDAO).execute(user);
    }

    public void deleteAllUser() {new DeleteAllUserAsyncTask(userDAO).execute();}

    public LiveData<User> getUserLiveData() { return  userDAO.getUser(); }

    public LiveData<User> getUserLiveDataById(int id) {
        return userDAO.getUserById(id);
    }

    public LiveData<User> getUserLiveDataByServerId (int serverId) {
        return userDAO.getUserByServerId(serverId);
    }

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDAO userDAO;

        private InsertUserAsyncTask(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.insert(users[0]);
            return null;
        }
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDAO userDAO;

        private UpdateUserAsyncTask(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.update(users[0]);
            return null;
        }
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDAO userDAO;

        private DeleteUserAsyncTask(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.delete(users[0]);
            return null;
        }
    }

    private static class DeleteAllUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDAO userDAO;

        private DeleteAllUserAsyncTask(UserDAO userDAO) { this.userDAO = userDAO; }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.deleteAllUser();
            return null;
        }
    }

}
