package com.uol.yt120.lecampus.utility;

import com.uol.yt120.lecampus.model.domain.Footprint;

public class ObjectComparetor {

    public boolean areSameFootprints(Footprint oldFootprint, Footprint newFootprint) {
        return oldFootprint.getTitle().equals(newFootprint.getTitle()) &&
                oldFootprint.getDescription().equals(newFootprint.getDescription());
    }
}
