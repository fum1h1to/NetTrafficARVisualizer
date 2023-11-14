package com.fum1h1to.NetTrafficARVisualizer.capture.core;

import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CaptureQueue {
    public static BlockingQueue<Packet> queue = new ArrayBlockingQueue<>(1000);;


}
