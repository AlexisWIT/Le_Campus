package com.uol.yt120.lecampus.dataAccessObjects;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.uol.yt120.lecampus.domain.User;

@Dao
public interface UserDAO {

    @Insert
    void insert(User user);
    @Update
    void update(User user);
    @Delete
    void delete(User user);

    @Query("DELETE FROM user_table WHERE id = :id")
    void deleteUser(int id);

    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    LiveData<User> getUserById(int id);
}
