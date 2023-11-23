package com.fum1h1to.NetTrafficARVisualizer.capture.Packet;

import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.core.PacketRawDataQueue;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PacketReceiver implements Runnable {
    private static final String TAG = "PacketReceiver";
    private PacketConverter mPacketConverter;

    public PacketReceiver(PacketConverter packetConverter) {
        mPacketConverter = packetConverter;
    }

    @Override
    public void run() {
        // 1秒間隔でpacketを取り入れる
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                List<Packet> packetRaws = new ArrayList<>();
                PacketRawDataQueue.queue.drainTo(packetRaws);

                if (packetRaws.size() != 0) mPacketConverter.submitPacketRaws(packetRaws);
            }
        };
        timer.scheduleAtFixedRate(task,0,1000);
    }
}
