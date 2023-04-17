package com.fum1h1to.NetTrafficARVisualizer.capture;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PacketCreater implements LocationListener {

    static final String TAG = "PacketCreater";
    static final String UNITY_SCRIPT_GAMEOBJECT_NAME = "ScriptObject";
    private Activity mActivity;
    private LocationManager mLocationManager;
    private String bestProvider;
    private double mNowLat;
    private double mNowLon;
    private Geolocation mGeolocation;

    public PacketCreater(Activity activity) {
        mActivity = activity;

        initLocationManager();
        locationStart();
        getNowLocation();

        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Boolean> futureResult = ex.submit(new GeoipSettings(activity));
        try {
            if(futureResult.get()) {
                mGeolocation = new Geolocation(mActivity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void createPacket(String srcAddr, String dstAddr, String protocol, int length) {
        if(mGeolocation == null ) {
            return;
        }
        double[] srcLatLng = mGeolocation.getLatLng(srcAddr);

        UnityPlayer.UnitySendMessage(UNITY_SCRIPT_GAMEOBJECT_NAME, "CreateInboundPacketObject",
                "test");
    }

    private void initLocationManager() {
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
    }

    private void checkPermission() {
        if (mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可を取得する
            mActivity.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
    }

    private void getNowLocation() {
        locationStart();
        locationStop();
    }

    private void locationStart() {
        checkPermission();
        mLocationManager.requestLocationUpdates(bestProvider, 60000, 3, this);
    }

    private void locationStop() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "called onLocationChanged");
        Log.d(TAG, "lat : " + location.getLatitude());
        Log.d(TAG, "lon : " + location.getLongitude());
        mNowLat = location.getLatitude();
        mNowLon = location.getLongitude();
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
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "called onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "called onProviderEnabled");
    }
}
