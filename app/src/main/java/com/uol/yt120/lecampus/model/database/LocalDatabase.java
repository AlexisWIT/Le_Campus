package com.uol.yt120.lecampus.model.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.model.dataAccessObjects.FootprintDAO;
import com.uol.yt120.lecampus.model.dataAccessObjects.UserDAO;
import com.uol.yt120.lecampus.model.dataAccessObjects.UserEventDAO;
import com.uol.yt120.lecampus.model.domain.Footprint;
import com.uol.yt120.lecampus.model.domain.User;
import com.uol.yt120.lecampus.model.domain.UserEvent;

@Database(entities = {Footprint.class, UserEvent.class, User.class}, exportSchema = false, version = 2)
public abstract class LocalDatabase extends RoomDatabase {

    private static LocalDatabase instance;

    public abstract FootprintDAO footprintDAO();
    public abstract UserEventDAO userEventDAO();
    public abstract UserDAO userDAO();

    public static synchronized LocalDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LocalDatabase.class, "app_database")
                    .fallbackToDestructiveMigration() // Tell Room the migration stratege when version number increased
                    .addCallback(roomCallback)
                    .allowMainThreadQueries()
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
        private UserEventDAO userEventDAO;
        private UserDAO userDAO;

        private PopulateDatabaseAsyncTask(LocalDatabase localDatabase) {
            footprintDAO = localDatabase.footprintDAO();
            userEventDAO = localDatabase.userEventDAO();
            userDAO = localDatabase.userDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Footprint example_footprint = createExampleFootprint();
            //User defaultUser = createDefaultUser();
            //UserEvent example_userevent = createExampleUserEvent();

            // Default example footprint data in database
            //userDAO.insert(defaultUser);
            footprintDAO.insert(example_footprint);
            return null;
        }
    }

    private static Footprint createExampleFootprint() {
        Footprint example_footprint;

        String example_title =
                "Tour in Leicester City Centre";

        String example_desc =
                "Leicester is a city and unitary authority area in the East Midlands of " +
                "England, and the county town of Leicestershire. The city lies on the " +
                "River Soar and close to the eastern end of the National Forest." +
                "\n\n" +
                "The 2016 mid year estimate of the population of the City of Leicester " +
                "unitary authority was 348,300, an increase of approximately 18,500 " +
                "(Increase 5.6%) from the 2011 census figure of 329,839, making it the " +
                "most populous municipality in the East Midlands region. The associated " +
                "urban area is also the 11th most populous in England and the 13th most " +
                "populous in the United Kingdom. [Source: Wikipedia]";

        String example_nodeList =
                "[{\"lon\":-1.131445,\"index\":1,\"time\":\"2019-04-10 03:00:20\",\"lat\":52.634439,\"allInfo\":\"\"}," +
                "{\"lon\":-1.131700,\"index\":2,\"time\":\"2019-04-10 03:02:20\",\"lat\":52.634621,\"allInfo\":\"\"}," +
                "{\"lon\":-1.131987,\"index\":3,\"time\":\"2019-04-10 03:04:20\",\"lat\":52.634849,\"allInfo\":\"\"}," +
                "{\"lon\":-1.132210,\"index\":4,\"time\":\"2019-04-10 03:06:20\",\"lat\":52.635078,\"allInfo\":\"\"}," +
                "{\"lon\":-1.132427,\"index\":5,\"time\":\"2019-04-10 03:08:20\",\"lat\":52.635326,\"allInfo\":\"\"}," +
                "{\"lon\":-1.132568,\"index\":6,\"time\":\"2019-04-10 03:10:20\",\"lat\":52.635438,\"allInfo\":\"\"}," +
                "{\"lon\":-1.132855,\"index\":7,\"time\":\"2019-04-10 03:12:20\",\"lat\":52.635349,\"allInfo\":\"\"}," +
                "{\"lon\":-1.133155,\"index\":8,\"time\":\"2019-04-10 03:14:20\",\"lat\":52.635279,\"allInfo\":\"\"}," +
                "{\"lon\":-1.133506,\"index\":9,\"time\":\"2019-04-10 03:16:20\",\"lat\":52.635140,\"allInfo\":\"\"}," +
                "{\"lon\":-1.133773,\"index\":10,\"time\":\"2019-04-10 03:18:20\",\"lat\":52.634888,\"allInfo\":\"\"}," +
                "{\"lon\":-1.133882,\"index\":11,\"time\":\"2019-04-10 03:20:20\",\"lat\":52.634675,\"allInfo\":\"\"}," +
                "{\"lon\":-1.133971,\"index\":12,\"time\":\"2019-04-10 03:22:20\",\"lat\":52.634458,\"allInfo\":\"\"}," +
                "{\"lon\":-1.134201,\"index\":13,\"time\":\"2019-04-10 03:24:20\",\"lat\":52.634304,\"allInfo\":\"\"}," +
                "{\"lon\":-1.134488,\"index\":14,\"time\":\"2019-04-10 03:26:20\",\"lat\":52.634195,\"allInfo\":\"\"}," +
                "{\"lon\":-1.134749,\"index\":15,\"time\":\"2019-04-10 03:28:20\",\"lat\":52.634133,\"allInfo\":\"\"}," +
                "{\"lon\":-1.134915,\"index\":16,\"time\":\"2019-04-10 03:26:20\",\"lat\":52.634292,\"allInfo\":\"\"}," +
                "{\"lon\":-1.135145,\"index\":17,\"time\":\"2019-04-10 03:28:20\",\"lat\":52.634431,\"allInfo\":\"\"}," +
                "{\"lon\":-1.135560,\"index\":18,\"time\":\"2019-04-10 03:30:20\",\"lat\":52.634381,\"allInfo\":\"\"}]";

        String example_createTime =
                "2019-04-10 03:46:20";

        example_footprint = new Footprint(example_title, example_desc, example_nodeList, example_createTime, 0);

        return example_footprint;
    }

    private static User createDefaultUser() {
        User defaultUser;
        defaultUser = new User("123456789", "00000000", "Default User", "Default", "1970-01-02", "du001@student.le.ac.uk");
        return defaultUser;
    }
}
