package com.fum1h1to.NetTrafficARVisualizer.capture.packet.emulate;

import com.fum1h1to.NetTrafficARVisualizer.capture.packet.PacketModel;
import com.fum1h1to.NetTrafficARVisualizer.capture.packet.PacketSender;
import com.fum1h1to.NetTrafficARVisualizer.capture.packet.emulate.pattern.IEmulatePattern;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PacketEmulater {
    private BlockingQueue<PacketModel> nativeToUnityQueue;
    private IEmulatePattern mEmulatePattern;

    public PacketEmulater(IEmulatePattern emulatePattern) {
        mEmulatePattern = emulatePattern;
    }

    private void setup() {
        nativeToUnityQueue = new ArrayBlockingQueue<>(1000);
        mEmulatePattern.setNativeToUnityQueue(nativeToUnityQueue);
    }

    public void emulateStart() {
        setup();
        Thread thread = new Thread(new PacketSender(nativeToUnityQueue));
        thread.start();

        ExecutorService emulateTask = Executors.newSingleThreadExecutor();
        Future<Boolean> futureResult = emulateTask.submit(mEmulatePattern);
        try {
            futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
            thread.interrupt();
        } finally {
            emulateTask.shutdown();
        }

        while(nativeToUnityQueue.size() != 0) continue;
        thread.interrupt();
    }
}
