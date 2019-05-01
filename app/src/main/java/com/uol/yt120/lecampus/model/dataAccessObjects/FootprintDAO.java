package com.uol.yt120.lecampus.model.dataAccessObjects;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.uol.yt120.lecampus.model.domain.Footprint;

import java.util.List;

@Dao
public interface FootprintDAO {

    @Insert
    void insert(Footprint footprint);
    @Update
    void update(Footprint footprint);
    @Delete
    void delete(Footprint footprint);

    @Query("DELETE FROM footprint_table")
    void deleteAllFootprints();

    // By adding "LiveData" here, once the data in database is changed,
    // the data in viewModel will also be changed.
    @Query("SELECT * FROM footprint_table ORDER BY createTime DESC") //Descending
    LiveData<List<Footprint>> getAllFootprints();

    @Query("SELECT * FROM footprint_table WHERE id = :id LIMIT 1")
    LiveData<Footprint> getFootprintById(int id);

}
