package com.fum1h1to.NetTrafficARVisualizer.capture;

import android.app.Activity;
import android.hardware.SensorEventListener;
import android.location.LocationListener;
import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Coordinate;
import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.GeoipSettings;
import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Geolocation;
import com.fum1h1to.NetTrafficARVisualizer.capture.Manager.LocalLocationManager;
import com.fum1h1to.NetTrafficARVisualizer.capture.Manager.LocalSensorManager;
import com.unity3d.player.UnityPlayer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PacketCreater {

    static final String TAG = "PacketCreater";
    static final String UNITY_SCRIPT_GAMEOBJECT_NAME = "ScriptObject";
    private Activity mActivity;
    private Geolocation mGeolocation;
    private LocalLocationManager mLocalLocationManager;
    private LocalSensorManager mLocalSensorManager;

    public PacketCreater(Activity activity) {
        mActivity = activity;
        mGeolocation = new Geolocation(activity);
        mLocalLocationManager = new LocalLocationManager(activity);
        mLocalSensorManager = new LocalSensorManager(activity);
    }

    public void init() {
        mGeolocation.initDb();
        mLocalLocationManager.init();
        mLocalSensorManager.init();
    }

    public void createPacket(String srcAddr, String dstAddr, String protocol, int length) {
        if(mLocalSensorManager.getDeviceBearing() == 361) {
            return;
        }

        if (srcAddr.equals("10.215.173.1")) {
            // outboundの時
            Coordinate dstLatLng = mGeolocation.getLatLng(dstAddr);
            if (dstLatLng == null) {
                return;
            }

            Coordinate nowLocation = mLocalLocationManager.getNowLocation();
            double bearing = getBearing(nowLocation.getLatitude(), nowLocation.getLongitude(), dstLatLng.getLatitude(), dstLatLng.getLongitude());
            Log.d(TAG, String.format("srcAddr: %s, lat1: %.5f, lon1: %.5f, lat2: %.5f, lon2: %.5f -> bearing: %.5f",
                    srcAddr, nowLocation.getLatitude(), nowLocation.getLongitude(), dstLatLng.getLatitude(), dstLatLng.getLongitude(), Math.toDegrees(bearing)));
            double vBearing = (bearing - mLocalSensorManager.getDeviceBearing()) % Math.toRadians(360);
            double x = 0;
            double y = 0;
            double z = 0;

            double radius = 5;
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

            UnityPlayer.UnitySendMessage(UNITY_SCRIPT_GAMEOBJECT_NAME, "CreateOutboundPacketObject", message);

        } else {
            // inboundの時
            Coordinate srcLatLng = mGeolocation.getLatLng(srcAddr);
            if (srcLatLng == null) {
                return;
            }

            Coordinate nowLocation = mLocalLocationManager.getNowLocation();
            double bearing = getBearing(nowLocation.getLatitude(), nowLocation.getLongitude(), srcLatLng.getLatitude(), srcLatLng.getLongitude());
            Log.d(TAG, String.format("srcAddr: %s, lat1: %.5f, lon1: %.5f, lat2: %.5f, lon2: %.5f -> bearing: %.5f",
                    srcAddr, nowLocation.getLatitude(), nowLocation.getLongitude(), srcLatLng.getLatitude(), srcLatLng.getLongitude(), Math.toDegrees(bearing)));
            double vBearing = (bearing - mLocalSensorManager.getDeviceBearing()) % Math.toRadians(360);
            double x = 0;
            double y = 0;
            double z = 0;

            double radius = 5;
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

    }

    private double getBearing(double lat1, double lon1, double lat2, double lon2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double radLon1 = Math.toRadians(lon1);
        double radLon2 = Math.toRadians(lon2);

        double dLon = radLon2 - radLon1;
        double y = Math.sin(dLon) * Math.cos(radLat2);
        double x = Math.cos(radLat1) * Math.sin(radLat2) - Math.sin(radLat1) * Math.cos(radLat2) * Math.cos(dLon);

        double bearing = (Math.atan2(y, x) + Math.toRadians(360)) % Math.toRadians(360);
        return bearing;
    }

    public LocalSensorManager getLocalSensorManager() {
        return mLocalSensorManager;
    }

    public LocalLocationManager getLocalLocationManager() {
        return mLocalLocationManager;
    }
}
