package com.fum1h1to.NetTrafficARVisualizer.capture.Manager;

import static android.content.Context.LOCATION_SERVICE;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Coordinate;
import com.fum1h1to.NetTrafficARVisualizer.capture.Permission.PermissionManager;

import java.util.List;

public class LocalLocationManager implements LocationListener {
    static final String TAG = "LocalLocationManager";
    private Activity mActivity;
    private LocationManager mLocationManager;
    private String bestProvider;
    private double nowLat;
    private double nowLon;

    public LocalLocationManager(Activity activity) {
        mActivity = activity;
    }

    public void init() {
        Log.d(TAG, "called initLocationManager");
        // インスタンス生成
        mLocationManager = (LocationManager)mActivity.getSystemService(LOCATION_SERVICE);

        // 詳細設定
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        bestProvider = mLocationManager.getBestProvider(criteria, true);

        updateNowLocation();
    }

    private void locationStart() {
        Log.d(TAG, "called locationStart");
        PermissionManager.checkPermission(mActivity);
        mLocationManager.requestLocationUpdates(bestProvider, 60000, 3, this);
    }

    private void locationStop() {
        Log.d(TAG, "called locationStop");
        mLocationManager.removeUpdates(this);
    }

    public void updateNowLocation() {
        locationStart();
    }

    public Coordinate getNowLocation() {
        return new Coordinate(nowLat, nowLon);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "called onLocationChanged");
        Log.d(TAG, "lat : " + location.getLatitude());
        Log.d(TAG, "lon : " + location.getLongitude());
        nowLat = location.getLatitude();
        nowLon = location.getLongitude();
        locationStop();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "called onStatusChanged");
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d(TAG, "AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d(TAG, "OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d(TAG, "TEMPORARILY_UNAVAILABLE");
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "called onProviderDisabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "called onProviderEnabled");
    }
}
