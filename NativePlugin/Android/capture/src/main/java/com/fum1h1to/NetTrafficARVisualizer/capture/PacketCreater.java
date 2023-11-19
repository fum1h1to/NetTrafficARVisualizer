package com.fum1h1to.NetTrafficARVisualizer.capture;

import android.app.Activity;
import android.hardware.SensorEventListener;
import android.location.LocationListener;
import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Coordinate;
import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.GeoipSettings;
import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Geolocation;
import com.fum1h1to.NetTrafficARVisualizer.capture.config.Config;
import com.unity3d.player.UnityPlayer;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PacketCreater {

    static final String TAG = "PacketCreater";
    static final String UNITY_SCRIPT_GAMEOBJECT_NAME = "ScriptObject";
    private Activity mActivity;
    private Geolocation mGeolocation;

    public PacketCreater(Activity activity) {
        mActivity = activity;
        mGeolocation = new Geolocation(activity);
    }

    public void init() {
        mGeolocation.initDb();
    }

    public void createPacket(String srcAddr, String dstAddr, String protocol, int length) {

        if (srcAddr.equals(Config.VPN_ADDRESS)) {
            // outboundの時
            Coordinate dstLatLng = mGeolocation.getLatLng(dstAddr);
            if (dstLatLng == null) {
                return;
            }

            String countryCode = mGeolocation.getCountryCode(dstAddr);

            String message = "{" +
                    "\"lat\": " + Double.toString(dstLatLng.getLatitude()) + "," +
                    "\"lng\": " + Double.toString(dstLatLng.getLongitude()) + "," +
                    "\"srcAddr\": \"" + srcAddr + "\"," +
                    "\"dstAddr\": \"" + dstAddr + "\"," +
                    "\"protocol\": \"" + protocol + "\"," +
                    "\"countryCode\": \"" + countryCode + "\"" +
                    "}";

            UnityPlayer.UnitySendMessage(UNITY_SCRIPT_GAMEOBJECT_NAME, "CreateOutboundPacketObject", message);

        } else {
            // inboundの時
            Coordinate srcLatLng = mGeolocation.getLatLng(srcAddr);
            if (srcLatLng == null) {
                return;
            }

            String countryCode = mGeolocation.getCountryCode(srcAddr);

            String message = "{" +
                    "\"lat\": " + Double.toString(srcLatLng.getLatitude()) + "," +
                    "\"lng\": " + Double.toString(srcLatLng.getLongitude()) + "," +
                    "\"srcAddr\": \"" + srcAddr + "\"," +
                    "\"dstAddr\": \"" + dstAddr + "\"," +
                    "\"protocol\": \"" + protocol + "\"," +
                    "\"countryCode\": \"" + countryCode + "\"" +
                    "}";

            UnityPlayer.UnitySendMessage(UNITY_SCRIPT_GAMEOBJECT_NAME, "CreateInboundPacketObject", message);
        }

    }
}
