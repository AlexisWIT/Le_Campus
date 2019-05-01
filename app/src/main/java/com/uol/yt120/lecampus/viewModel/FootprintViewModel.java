package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.model.domain.Footprint;
import com.uol.yt120.lecampus.repository.FootprintRepository;

import java.util.List;

public class FootprintViewModel extends AndroidViewModel {

    private FootprintRepository footprintRepository;
    private LiveData<List<Footprint>> allFootprints;
    private final MutableLiveData<Footprint> footprintMutableLiveData = new MutableLiveData<>();

    public FootprintViewModel(@NonNull Application application) {
        super(application);
        footprintRepository = new FootprintRepository(application);
        allFootprints = footprintRepository.getAllFootprintsLiveData();
    }

    public void insert(Footprint footprint) {
        footprintRepository.insert(footprint);
    }

    public void update(Footprint footprint) {
        footprintRepository.update(footprint);
    }

    public void delete(Footprint footprint) {
        footprintRepository.delete(footprint);
    }

    public void deleteAll() {
        footprintRepository.deleteAllFootprints();
    }

    public LiveData<List<Footprint>> getAllFootprints() {
        return allFootprints;
    }

    public LiveData<Footprint> getFootprintById(int id) { return footprintRepository.getFootprintById(id); }

    public void setFootprintMutableLiveData (Footprint footprint) {
        footprintMutableLiveData.setValue(footprint);
    }

    public LiveData<Footprint> getFootprintLiveData() {
        return footprintMutableLiveData;
    }
}
