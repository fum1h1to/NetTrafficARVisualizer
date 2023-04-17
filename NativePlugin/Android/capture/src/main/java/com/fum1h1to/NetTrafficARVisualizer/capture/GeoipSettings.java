package com.fum1h1to.NetTrafficARVisualizer.capture;

import android.app.Activity;
import android.util.Log;

import java.util.concurrent.Callable;

public class GeoipSettings implements Callable<Boolean> {
    private Activity mActivity;
    private static final String TAG = "GeoipSettings";

    public GeoipSettings(Activity activity) {
        mActivity = activity;
    }

    @Override
    public Boolean call() {
        return Geolocation.downloadDb(mActivity);
    }
}
