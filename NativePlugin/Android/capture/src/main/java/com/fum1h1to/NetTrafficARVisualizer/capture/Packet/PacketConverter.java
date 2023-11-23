package com.fum1h1to.NetTrafficARVisualizer.capture.Packet;

import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Geolocation;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.PacketRawDataQueue;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketConverter {
    private static final String TAG = "PacketConverter";
    private Geolocation mGeolocation;
    private BlockingQueue<PacketModel> nativeToUnityQueue;
    ExecutorService pool;

    public PacketConverter(
            Geolocation geolocation,
            BlockingQueue<PacketModel> nativeToUnityQueue
    ) {
        mGeolocation = geolocation;
        this.nativeToUnityQueue = nativeToUnityQueue;
        pool = Executors.newFixedThreadPool(3);
    }

    public void submitPacketRaws(List<Packet> packetRaws) {
        pool.submit(new Executor(packetRaws, nativeToUnityQueue, mGeolocation));
    }

    private class Executor implements Runnable {
        private List<Packet> convertPacket;
        private BlockingQueue<PacketModel> nativeToUnityQueue;
        private Geolocation mGeolocation;

        public Executor(
                List<Packet> convertPacket,
                BlockingQueue<PacketModel> nativeToUnityQueue,
                Geolocation geolocation
        ) {
            this.convertPacket = convertPacket;
            this.nativeToUnityQueue = nativeToUnityQueue;
            mGeolocation = geolocation;
        }

        @Override
        public void run() {
            // packetの加工

            // nativeToUnityQueueに渡す
//            nativeToUnityQueue.offer(packetModel);
        }
    }
}
