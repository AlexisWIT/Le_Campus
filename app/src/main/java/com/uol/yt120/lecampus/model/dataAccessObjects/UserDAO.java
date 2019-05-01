package com.uol.yt120.lecampus.model.dataAccessObjects;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.uol.yt120.lecampus.model.domain.User;

@Dao
public interface UserDAO {

    @Insert
    void insert(User user);
    @Update
    void update(User user);
    @Delete
    void delete(User user);

    @Query("DELETE FROM user_table")
    void deleteAllUser();

    @Query("DELETE FROM user_table WHERE localId = :id")
    void deleteUser(int id);

    @Query("SELECT * FROM user_table LIMIT 1")
    LiveData<User> getUser();

    @Query("SELECT * FROM user_table WHERE localId = :id LIMIT 1")
    LiveData<User> getUserById(int id);

    @Query("SELECT * FROM user_table WHERE serverId = :serverId LIMIT 1")
    LiveData<User> getUserByServerId(int serverId);
}
