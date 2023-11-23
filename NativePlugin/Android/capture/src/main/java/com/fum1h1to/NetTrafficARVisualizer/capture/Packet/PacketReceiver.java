package com.fum1h1to.NetTrafficARVisualizer.capture.Packet;

import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.core.PacketRawDataQueue;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketReceiver implements Runnable {
    private static final String TAG = "PacketReceiver";
    private PacketConverter mPacketConverter;

    public PacketReceiver(PacketConverter packetConverter) {
        mPacketConverter = packetConverter;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                // 一秒間隔でpacketをまとめる
                Packet packet = PacketRawDataQueue.queue.take();
                List<Packet> packetRaws = new ArrayList<>();

                // まとめたものをpacketConverterへ
                mPacketConverter.submitPacketRaws(packetRaws);

            }

        } catch(InterruptedException e){
            Log.i(TAG, "CaptureThread finish");
        } catch (Exception e) {
            Log.i(TAG, "capture fail", e);
        }
    }
}
