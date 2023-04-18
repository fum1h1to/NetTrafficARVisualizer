package com.fum1h1to.NetTrafficARVisualizer.capture;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

public class PacketCreater implements LocationListener, SensorEventListener {

    static final String TAG = "PacketCreater";
    static final String UNITY_SCRIPT_GAMEOBJECT_NAME = "ScriptObject";
    private Activity mActivity;
    private LocationManager mLocationManager;
    private String bestProvider;
    private double mNowLat;
    private double mNowLon;
    private Geolocation mGeolocation;
    private SensorManager mSensorManager = null;

    private float[] fAccell = null;
    private float[] fMagnetic = null;
    private double deviceBearing = 361;

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
        if(mGeolocation == null && deviceBearing == 361) {
            return;
        }
        // まずは、inboudの方から作成。
        double[] srcLatLng = mGeolocation.getLatLng(srcAddr);
        if (srcLatLng == null) {
            return;
        }

        double bearing = getBearing(srcLatLng[0], srcLatLng[1], mNowLat, mNowLon);
        Log.d(TAG, "srcAddr Bearing: " + Math.toDegrees(bearing));
        double vBearing = bearing - deviceBearing; // -180 ～ 180
        double x = 0;
        double y = 0;
        double z = 0;

        double radius = 1;
        x = radius * Math.sin(vBearing);
        z = radius * Math.cos(vBearing);

        String message = "{" +
                "\"x\": " + Double.toString(x) + "," +
                "\"y\": " + Double.toString(0) + "," +
                "\"z\": " + Double.toString(z) + "," +
                "\"srcAddr\": \"" + srcAddr + "\"," +
                "\"dstAddr\": \"" + dstAddr + "\"," +
                "\"protocol\": \"" + protocol + "\"" +
                "}";

        UnityPlayer.UnitySendMessage(UNITY_SCRIPT_GAMEOBJECT_NAME, "CreateInboundPacketObject", message);
    }

    private double getBearing(double lat1, double lon1, double lat2, double lon2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double radLon1 = Math.toRadians(lon1);
        double radLon2 = Math.toRadians(lon2);

        double dLon = radLon2 - radLon1;

        double y = Math.sin(dLon) * Math.cos(radLat2);
        double x = Math.cos(radLat1) * Math.sin(radLat2) - Math.sin(radLat1) * Math.cos(radLat2) * Math.cos(dLon);

        double bearing = Math.atan2(y, x);
        return bearing;
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

    public void initSensor() {
        mSensorManager = (SensorManager)mActivity.getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                SensorManager.SENSOR_DELAY_UI );
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD ),
                SensorManager.SENSOR_DELAY_UI );
    }

    public void stopSensor() {
        mSensorManager.unregisterListener( this );
    }

    public double getDeviceBearing() {
        return deviceBearing;
    }

    @Override
    public void onSensorChanged (SensorEvent event) {
        // センサの取得値をそれぞれ保存しておく
        switch( event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                fAccell = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                fMagnetic = event.values.clone();
                break;
        }

        // fAccell と fMagnetic から傾きと方位角を計算する
        if( fAccell != null && fMagnetic != null ) {
            // 回転行列を得る
            float[] inR = new float[9];
            SensorManager.getRotationMatrix(
                    inR,
                    null,
                    fAccell,
                    fMagnetic );
            // ワールド座標とデバイス座標のマッピングを変換する
            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(
                    inR,
                    SensorManager.AXIS_X, SensorManager.AXIS_Y,
                    outR );
            // 姿勢を得る
            float[] fAttitude = new float[3];
            SensorManager.getOrientation(
                    outR,
                    fAttitude );
            double beT = Math.toDegrees(fAttitude[0]);
            if(beT < 0) {
                beT += 360;
            }
            deviceBearing = Math.toRadians(beT);
            stopSensor();
        }
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {}
}
