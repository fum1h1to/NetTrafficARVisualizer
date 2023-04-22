package com.fum1h1to.NetTrafficARVisualizer.capture.Manager;

import static android.content.Context.SENSOR_SERVICE;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LocalSensorManager implements SensorEventListener  {
    static final String TAG = "LocalSensorManager";
    private Activity mActivity;
    private SensorManager mSensorManager;
    private float[] fAccell = null;
    private float[] fMagnetic = null;
    private double deviceBearing = 361;

    public LocalSensorManager(Activity activity) {
        mActivity = activity;
    }

    public void init() {
        Log.d(TAG, "called initSensor");
        mSensorManager = (SensorManager)mActivity.getSystemService(SENSOR_SERVICE);
        updateDeviceBearing();
    }

    private void startSensor() {
        Log.d(TAG, "called startSensor");
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                SensorManager.SENSOR_DELAY_UI );
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD ),
                SensorManager.SENSOR_DELAY_UI );
    }

    private void stopSensor() {
        Log.d(TAG, "called stopSensor");
        mSensorManager.unregisterListener( this );
    }

    public void updateDeviceBearing() {
        startSensor();
    }

    public double getDeviceBearing() {
        return deviceBearing;
    }

    @Override
    public void onSensorChanged (SensorEvent event) {
        Log.d(TAG, "called onSensorChanged");
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
    public void onAccuracyChanged (Sensor sensor, int accuracy) {
        Log.d(TAG, "called onAccuracyChanged");
    }
}
