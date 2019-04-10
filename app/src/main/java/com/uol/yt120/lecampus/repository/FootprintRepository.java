package com.uol.yt120.lecampus.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.uol.yt120.lecampus.dataAccessObjects.FootprintDAO;
import com.uol.yt120.lecampus.database.LocalDatabase;
import com.uol.yt120.lecampus.domain.Footprint;

import java.util.List;

public class FootprintRepository {
    private FootprintDAO footprintDAO;
    private LiveData<Footprint> footprintLiveData;
    private LiveData<List<Footprint>> allFootprintsLiveData;

    public FootprintRepository(Application application) {
        LocalDatabase database = LocalDatabase.getInstance(application);
        footprintDAO = database.footprintDAO();
        allFootprintsLiveData = footprintDAO.getAllFootprints();
    }

    // Room doesnt allow database operations to be run in the main thread
    // so make it running in background
    public void insert(Footprint footprint) {
        new InsertFootprintAsyncTask(footprintDAO).execute(footprint);
    }

    public void update(Footprint footprint) {
        new UpdateFootprintAsyncTask(footprintDAO).execute(footprint);
    }

    public void delete(Footprint footprint) {
        new DeleteFootprintAsyncTask(footprintDAO).execute(footprint);
    }

    public void deleteAllFootprints() {
        new DeleteAllFootprintAsyncTask(footprintDAO).execute();
    }

    public LiveData<List<Footprint>> getAllFootprintsLiveData() {
        return allFootprintsLiveData;
    }

    public LiveData<Footprint> getFootprintById(int id) { return footprintDAO.getFootprintById(id); }

    // Make this class static to prevent memory leak
    private static class InsertFootprintAsyncTask extends AsyncTask<Footprint, Void, Void> {
        private FootprintDAO footprintDAO;

        private InsertFootprintAsyncTask(FootprintDAO footprintDAO) {
            this.footprintDAO = footprintDAO;
        }

        @Override
        protected Void doInBackground(Footprint... footprints) {
            footprintDAO.insert(footprints[0]);
            return null;
        }
    }

    // Make this class static to prevent memory leak
    private static class UpdateFootprintAsyncTask extends AsyncTask<Footprint, Void, Void> {
        private FootprintDAO footprintDAO;

        private UpdateFootprintAsyncTask(FootprintDAO footprintDAO) {
            this.footprintDAO = footprintDAO;
        }

        @Override
        protected Void doInBackground(Footprint... footprints) {
            footprintDAO.update(footprints[0]);
            return null;
        }
    }

    // Make this class static to prevent memory leak
    private static class DeleteFootprintAsyncTask extends AsyncTask<Footprint, Void, Void> {
        private FootprintDAO footprintDAO;

        private DeleteFootprintAsyncTask(FootprintDAO footprintDAO) {
            this.footprintDAO = footprintDAO;
        }

        @Override
        protected Void doInBackground(Footprint... footprints) {
            footprintDAO.delete(footprints[0]);
            return null;
        }
    }

    // Make this class static to prevent memory leak
    private static class DeleteAllFootprintAsyncTask extends AsyncTask<Footprint, Void, Void> {
        private FootprintDAO footprintDAO;

        private DeleteAllFootprintAsyncTask(FootprintDAO footprintDAO) {
            this.footprintDAO = footprintDAO;
        }

        @Override
        protected Void doInBackground(Footprint... footprints) {
            footprintDAO.deleteAllFootprints();
            return null;
        }
    }
}
