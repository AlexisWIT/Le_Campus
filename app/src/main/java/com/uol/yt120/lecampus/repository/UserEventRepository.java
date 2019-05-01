package com.uol.yt120.lecampus.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.uol.yt120.lecampus.model.dataAccessObjects.UserEventDAO;
import com.uol.yt120.lecampus.model.database.LocalDatabase;
import com.uol.yt120.lecampus.model.domain.UserEvent;

import java.util.List;

public class UserEventRepository {
    private UserEventDAO userEventDAO;
    private MutableLiveData<UserEvent> userEventLiveData;
    private MutableLiveData<UserEvent> mutableUserEventLiveData;
    private MutableLiveData<List<UserEvent>> mutableUserEventListByDateLiveData;
    private MutableLiveData<List<UserEvent>> mutableUserEventListByDateRangeLiveData;
    private LiveData<List<UserEvent>> allUserEventListLiveData;

    public UserEventRepository(Application application) {
        LocalDatabase database = LocalDatabase.getInstance(application);
        userEventDAO = database.userEventDAO();
        allUserEventListLiveData = userEventDAO.getAllUserEvents();
    }

    // Room doesnt allow database operations to be run in the main thread
    // so make it running in background
    public void insert(UserEvent userEvent) {
        new InsertUserEventAsyncTask(userEventDAO).execute(userEvent);
    }

    public void update(UserEvent userEvent) {
        new UpdateUserEventAsyncTask(userEventDAO).execute(userEvent);
    }

    public void delete(UserEvent userEvent) {
        new DeleteUserEventAsyncTask(userEventDAO).execute(userEvent);
    }

    public void deleteAllUserEvents() {
        new DeleteAllUserEventsAsyncTask(userEventDAO).execute();
    }

    public LiveData<List<UserEvent>> getAllUserEventListLiveData() {
        return allUserEventListLiveData;
    }


    public LiveData<UserEvent> getUserEventById(int id) {
        //new GetUserEventByIdAsyncTask(userEventDAO, id).execute();
        return userEventDAO.getUserEventById(id);
    }

    public LiveData<List<UserEvent>> getUserEventListByDate(String date) {
        Log.w("[UserEventRepository]", "Date received in Repository: ["+ date +"]");
        return userEventDAO.getUserEventListByDate(date);
    }

    public LiveData<List<UserEvent>> getUserEventListByDateRange(String startDate, String endDate) {
        Log.w("[UserEventRepository]", "Date Range received in Repository: From ["+ startDate +"] to ["+endDate+"]");
        return userEventDAO.getUserEventListByDateRange(startDate, endDate);
    }

    public List<UserEvent> getNonLiveUserEventListByDateRange(String startDate, String endDate) {
        Log.w("[UserEventRepository]", "Date Range received in Repository: From ["+ startDate +"] to ["+endDate+"]");
        return userEventDAO.getNonLiveUserEventListByDateRange(startDate, endDate);
    }

    public UserEvent getNonLiveUserEventByTitle(String eventTitle) {
        return userEventDAO.getNonLiveUserEventByTitle(eventTitle);
    }


    /**
     * Below are Asynctasks for database read and write
     */

    // Make this class static to prevent memory leak
    private static class InsertUserEventAsyncTask extends AsyncTask<UserEvent, Void, Void> {
        private UserEventDAO userEventDAO;

        private InsertUserEventAsyncTask(UserEventDAO userEventDAO) {
            this.userEventDAO = userEventDAO;
        }

        @Override
        protected Void doInBackground(UserEvent... userEvents) {
            userEventDAO.insert(userEvents[0]);
            return null;
        }
    }

    // Make this class static to prevent memory leak
    private static class UpdateUserEventAsyncTask extends AsyncTask<UserEvent, Void, Void> {
        private UserEventDAO userEventDAO;

        private UpdateUserEventAsyncTask(UserEventDAO userEventDAO) {
            this.userEventDAO = userEventDAO;
        }

        @Override
        protected Void doInBackground(UserEvent... userEvents) {
            userEventDAO.update(userEvents[0]);
            return null;
        }
    }

    // Make this class static to prevent memory leak
    private static class DeleteUserEventAsyncTask extends AsyncTask<UserEvent, Void, Void> {
        private UserEventDAO userEventDAO;

        private DeleteUserEventAsyncTask(UserEventDAO userEventDAO) {
            this.userEventDAO = userEventDAO;
        }

        @Override
        protected Void doInBackground(UserEvent... userEvents) {
            userEventDAO.delete(userEvents[0]);
            return null;
        }
    }

    // Make this class static to prevent memory leak
    private static class DeleteAllUserEventsAsyncTask extends AsyncTask<UserEvent, Void, Void> {
        private UserEventDAO userEventDAO;

        private DeleteAllUserEventsAsyncTask(UserEventDAO UserEventDAO) {
            this.userEventDAO = UserEventDAO;
        }

        @Override
        protected Void doInBackground(UserEvent... userEvents) {
            userEventDAO.deleteAllUserEvents();
            return null;
        }
    }

    // Make this class static to prevent memory leak
    private static class GetUserEventByIdAsyncTask extends AsyncTask<UserEvent, Void, Void> {
        private UserEventDAO userEventDAO;
        private int userEventId;
        public GetUserEventByIdAsyncResponse response = null;

        public interface GetUserEventByIdAsyncResponse {
            void getUserEventByIdFinish(String output);
        }

        private GetUserEventByIdAsyncTask(UserEventDAO UserEventDAO, int id) {
            this.userEventDAO = UserEventDAO;
            this.userEventId = id;
        }

        @Override
        protected Void doInBackground(UserEvent... userEvents) {

            userEventDAO.getUserEventById(userEventId);
            return null;
        }
    }

    // Make this class static to prevent memory leak
//    private static class DeleteAllUserEventsAsyncTask extends AsyncTask<UserEvent, Void, Void> {
//        private UserEventDAO userEventDAO;
//
//        private DeleteAllUserEventsAsyncTask(UserEventDAO UserEventDAO) {
//            this.userEventDAO = UserEventDAO;
//        }
//
//        @Override
//        protected Void doInBackground(UserEvent... userEvents) {
//            //userEventLiveData userEventDAO.deleteAllUserEvents();
//            return null;
//        }
//    }
}
