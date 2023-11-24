package com.fum1h1to.NetTrafficARVisualizer.capture.geoip;

import android.app.Activity;

import java.util.concurrent.Callable;

public class GeoipSettings implements Callable<Boolean> {
    private Activity mActivity;
    private static final String TAG = "GeoipSettings";

    public GeoipSettings(Activity activity) {
        mActivity = activity;
    }

    @Override
    public Boolean call() {
        if(Geolocation.isDbFound(mActivity)) {
            return true;
        }
        return Geolocation.downloadDb(mActivity);
    }
}
