package com.fum1h1to.NetTrafficARVisualizer.capture.packet;

import android.app.Activity;
import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.geoip.Geolocation;
import com.fum1h1to.NetTrafficARVisualizer.capture.packet.emulate.PacketEmulater;
import com.fum1h1to.NetTrafficARVisualizer.capture.packet.emulate.pattern.TestPattern;
import com.fum1h1to.NetTrafficARVisualizer.capture.threat.ThreatService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketService {
    private static final String TAG = "PacketService";
    private BlockingQueue<PacketModel> nativeToUnityQueue;
    private ExecutorService executorService;
    private Geolocation mGeolocation;
    private ThreatService mThreatService;

    public PacketService(
            Activity activity
    ) {

        mThreatService = new ThreatService(activity);
        mGeolocation = new Geolocation(activity);
        mGeolocation.initDb();
    }

    public void packetConvertStart() {
        Log.d(TAG, "packetConvertStart");
        nativeToUnityQueue = new ArrayBlockingQueue<>(1000);
        executorService = Executors.newFixedThreadPool(2);

        PacketConverter packetConverter = new PacketConverter(mGeolocation, mThreatService ,nativeToUnityQueue);

        executorService.submit(new PacketReceiver(packetConverter));
        executorService.submit(new PacketSender(nativeToUnityQueue));
    }

    public void packetConvertStop() {
        Log.d(TAG, "packetConvertStop");
        nativeToUnityQueue = null;
        if(executorService != null) executorService.shutdownNow();
    }

    public void packetTest() {
        TestPattern tp = new TestPattern(mGeolocation, mThreatService);
        PacketEmulater packetTestEmulater = new PacketEmulater(tp);
        packetTestEmulater.emulateStart();
    }
}
