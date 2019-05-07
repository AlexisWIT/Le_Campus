package com.uol.yt120.lecampus;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class LeCampusApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
    }
}
