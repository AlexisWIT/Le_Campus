package com.uol.yt120.lecampus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SecurityService extends Service {
    public SecurityService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
