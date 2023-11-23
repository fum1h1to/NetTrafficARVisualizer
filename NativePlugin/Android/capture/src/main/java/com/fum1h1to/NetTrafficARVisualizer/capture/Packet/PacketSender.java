package com.fum1h1to.NetTrafficARVisualizer.capture.Packet;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.util.concurrent.BlockingQueue;

public class PacketSender implements Runnable {
    private static final String TAG = "PacketSender";

    static final String UNITY_SCRIPT_GAMEOBJECT_NAME = "ScriptObject";
    private BlockingQueue<PacketModel> nativeToUnityQueue;

    public PacketSender(BlockingQueue<PacketModel> nativeToUnityQueue) {
        this.nativeToUnityQueue = nativeToUnityQueue;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                PacketModel packetModel = nativeToUnityQueue.take();

                if (packetModel.getTrafficType() == PacketModel.TrafficType.INBOUND) {
                    UnityPlayer.UnitySendMessage(UNITY_SCRIPT_GAMEOBJECT_NAME, "CreateInboundPacket", packetModel.toJsonText());
                } else if (packetModel.getTrafficType() == PacketModel.TrafficType.OUTBOUND) {
                    UnityPlayer.UnitySendMessage(UNITY_SCRIPT_GAMEOBJECT_NAME, "CreateOutboundPacket", packetModel.toJsonText());
                }
            }

        } catch(InterruptedException e) {
            Log.i(TAG, "nativeToUnityQueue finish");
        }


    }

}
