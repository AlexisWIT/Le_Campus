package com.uol.yt120.lecampus.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.dataAccessObjects.FootprintDAO;
import com.uol.yt120.lecampus.domain.Footprint;

@Database(entities = {Footprint.class}, exportSchema = false, version = 1)
public abstract class FootprintDatabase extends RoomDatabase {

    private static FootprintDatabase instance;

    public abstract FootprintDAO footprintDAO();

    public static synchronized FootprintDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FootprintDatabase.class, "app_database")
                    .fallbackToDestructiveMigration() // Tell Room the migration stratege when version number increased
                    .addCallback(roomCallback)
                    .build();

        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDatabaseAsyncTask(instance).execute();
        }
    };

    private static class PopulateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private FootprintDAO footprintDAO;
        private PopulateDatabaseAsyncTask(FootprintDatabase footprintDatabase) {
            footprintDAO = footprintDatabase.footprintDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Default footprint data in database
            //footprintDAO.insert(new Footprint("example_title", "example_desc", ));
            return null;
        }
    }
}
