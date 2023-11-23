package com.fum1h1to.NetTrafficARVisualizer.capture.Packet;

import android.app.Activity;

import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Geolocation;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketService {
    private BlockingQueue<PacketModel> nativeToUnityQueue;
    private ExecutorService executorService;
    private Geolocation mGeolocation;

    public PacketService(
            Activity activity
    ) {
        mGeolocation = new Geolocation(activity);
        mGeolocation.initDb();

    }

    public void packetConvertStart() {
        nativeToUnityQueue = new ArrayBlockingQueue<>(1000);
        executorService = Executors.newFixedThreadPool(2);

        PacketConverter packetConverter = new PacketConverter(mGeolocation, nativeToUnityQueue);

        executorService.submit(new PacketReceiver(packetConverter));
        executorService.submit(new PacketSender(nativeToUnityQueue));
    }

    public void packetConvertStop() {
        nativeToUnityQueue = null;
        if(executorService != null) executorService.shutdownNow();
    }
}
