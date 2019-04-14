package com.uol.yt120.lecampus.publicAsyncTasks;

import android.os.AsyncTask;

import com.uol.yt120.lecampus.dataAccessObjects.UserEventDAO;
import com.uol.yt120.lecampus.domain.UserEvent;
import com.uol.yt120.lecampus.repository.UserEventRepository;

import java.util.List;

//Use new UpdateMonthlyEventListAsyncTask(String date1, String date2).execute();

/**
 * AsyncTask for loading weekly events <Params, Progress, Result>
 *     ref: https://developer.android.com/reference/android/os/AsyncTask
 */
public class UpdateWeeklyEventListAsyncTask extends AsyncTask<Void, Void, List<UserEvent>> {

    private UserEventDAO userEventDAO;
    private String startDate;
    private String endDate;
    private List<UserEvent> eventList;
    private UserEventRepository userEventRepository;

    public interface Response {
        void loadFinish(List<UserEvent> userEventListOutput);
    }

    public Response response = null;

    public UpdateWeeklyEventListAsyncTask(UserEventRepository userEventRepository, String startDate, String endDate, Response response ){
        this.userEventRepository = userEventRepository;
        this.response = response;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected List<UserEvent> doInBackground(Void... voids) {
        eventList = userEventRepository.getNonLiveUserEventListByDateRange(startDate, endDate);
        return eventList;
    }

    //@Override
//    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
//    }

//    @Override
    protected void onPostExecute(List<UserEvent> result) {
        //showDialog("Downloaded " + result + " items");
        response.loadFinish(result);
    }
}