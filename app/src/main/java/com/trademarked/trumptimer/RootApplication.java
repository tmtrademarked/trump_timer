package com.trademarked.trumptimer;

import android.app.Application;

import timber.log.Timber;

/**
 * Base application for the Trump Timer app.
 */
public final class RootApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Timber.
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
