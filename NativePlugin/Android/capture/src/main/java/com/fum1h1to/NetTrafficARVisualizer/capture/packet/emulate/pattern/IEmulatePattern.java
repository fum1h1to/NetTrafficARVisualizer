package com.fum1h1to.NetTrafficARVisualizer.capture.packet.emulate.pattern;

import com.fum1h1to.NetTrafficARVisualizer.capture.packet.PacketModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public interface IEmulatePattern extends Callable<Boolean> {

    public void setNativeToUnityQueue(BlockingQueue<PacketModel> nativeToUnityQueue);

    @Override
    public Boolean call();
}
