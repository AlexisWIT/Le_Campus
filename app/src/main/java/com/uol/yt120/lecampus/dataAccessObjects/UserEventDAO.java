package com.uol.yt120.lecampus.dataAccessObjects;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.uol.yt120.lecampus.domain.UserEvent;

import java.util.List;

@Dao
public interface UserEventDAO {

    @Insert
    void insert(UserEvent userEvent);
    @Update
    void update(UserEvent userEvent);
    @Delete
    void delete(UserEvent userEvent);

    @Query("DELETE FROM userevent_table")
    void deleteAllUserEvents();

    @Query("SELECT * FROM userevent_table ORDER BY startTime ASC") //Ascending
    LiveData<List<UserEvent>> getAllUserEvents();

    @Query("SELECT * FROM userevent_table WHERE id = :id LIMIT 1")
    LiveData<UserEvent> getUserEventById(int id);

    @Query("SELECT * FROM userevent_table WHERE starttime "+
            "LIKE '%' || :date || '%' ORDER BY startTime ASC")
    LiveData<List<UserEvent>> getUserEventListByDate(String date);

}
