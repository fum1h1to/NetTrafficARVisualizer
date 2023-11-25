package com.fum1h1to.NetTrafficARVisualizer.capture.threat;

import android.app.Activity;

import java.util.concurrent.Callable;

public class ThreatDatabaseSettings implements Callable<Boolean> {
    private static final String TAG = "ThreatListSettings";
    private Activity mActivity;
    private IThreatDatabase settingTarget;

    public ThreatDatabaseSettings(Activity activity, IThreatDatabase settingTarget) {
        mActivity = activity;
        this.settingTarget = settingTarget;
    }

    @Override
    public Boolean call() {
        if(settingTarget.isThreatListFound()) {
            return true;
        }
        return settingTarget.downloadThreatList();
    }

}
