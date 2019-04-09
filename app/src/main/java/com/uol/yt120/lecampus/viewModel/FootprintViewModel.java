package com.uol.yt120.lecampus.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.uol.yt120.lecampus.domain.Footprint;
import com.uol.yt120.lecampus.repository.FootprintRepository;

import java.util.List;

public class FootprintViewModel extends AndroidViewModel {

    private FootprintRepository footprintRepository;
    private LiveData<List<Footprint>> allFootprints;

    public FootprintViewModel(@NonNull Application application) {
        super(application);
        footprintRepository = new FootprintRepository(application);
        allFootprints = footprintRepository.getAllFootprints();
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
}
