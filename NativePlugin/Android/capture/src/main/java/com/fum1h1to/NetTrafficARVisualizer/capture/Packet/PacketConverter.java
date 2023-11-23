package com.fum1h1to.NetTrafficARVisualizer.capture.Packet;

import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Coordinate;
import com.fum1h1to.NetTrafficARVisualizer.capture.Geoip.Geolocation;
import com.fum1h1to.NetTrafficARVisualizer.capture.config.Config;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.PacketRawDataQueue;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        private static final String TAG = "PacketConverter.Executor";
        private List<Packet> convertPacket;
        private BlockingQueue<PacketModel> nativeToUnityQueue;
        private Geolocation mGeolocation;
        private InetAddress vpnAddress;

        public Executor(
                List<Packet> convertPacket,
                BlockingQueue<PacketModel> nativeToUnityQueue,
                Geolocation geolocation
        ) {
            this.convertPacket = convertPacket;
            this.nativeToUnityQueue = nativeToUnityQueue;
            mGeolocation = geolocation;

            try {
                vpnAddress = InetAddress.getByName(Config.VPN_ADDRESS);
            } catch (UnknownHostException e) {
                Log.e(TAG, "VPN_ADDRESS is failed");
                return;
            }
        }

        @Override
        public void run() {
            Map<KeyPair, Integer> inboundMap = new HashMap<>();
            Map<KeyPair, Integer> outboundMap = new HashMap<>();

            // packetのカウント
            convertPacket.forEach(value -> {
                if(value.ip4Header.destinationAddress.equals(vpnAddress)) {
                    // inboundの時
                    InetAddress srcip = value.ip4Header.sourceAddress;
                    Packet.IP4Header.TransportProtocol protocol = value.ip4Header.protocol;

                    KeyPair key = new KeyPair(srcip, protocol);
                    if(inboundMap.containsKey(key)) {
                        inboundMap.put(key, inboundMap.get(key) + 1);
                    } else {
                        inboundMap.put(key, 1);
                    }
                } else if(value.ip4Header.sourceAddress.equals(vpnAddress)) {
                    // outboundの時
                    InetAddress dstip = value.ip4Header.destinationAddress;
                    Packet.IP4Header.TransportProtocol protocol = value.ip4Header.protocol;

                    KeyPair key = new KeyPair(dstip, protocol);
                    if(outboundMap.containsKey(key)) {
                        outboundMap.put(key, outboundMap.get(key) + 1);
                    } else {
                        outboundMap.put(key, 1);
                    }
                }
            });

            // packetModelに変換
            inboundMap.forEach((k, v) -> {
                PacketModel packetModel = new PacketModel();
                String srcip = k.getIp().getHostAddress();

                Coordinate srcLatLng = mGeolocation.getLatLng(srcip);
                if (srcLatLng == null) {
                    return;
                }
                String countryCode = mGeolocation.getCountryCode(srcip);

                packetModel.setTrafficType(PacketModel.TrafficType.INBOUND);
                packetModel.setLat(srcLatLng.getLatitude());
                packetModel.setLng(srcLatLng.getLongitude());
                packetModel.setSrcAddr(k.getIp());
                packetModel.setDstAddr(vpnAddress);
                packetModel.setCount(v);
                packetModel.setProtocol(k.getProtocol());
                packetModel.setCountryCode(countryCode);

                nativeToUnityQueue.offer(packetModel);
            });

            outboundMap.forEach((k, v) -> {
                PacketModel packetModel = new PacketModel();
                String dstip = k.getIp().getHostAddress();

                Coordinate dstLatLng = mGeolocation.getLatLng(dstip);
                if (dstLatLng == null) {
                    return;
                }
                String countryCode = mGeolocation.getCountryCode(dstip);

                packetModel.setTrafficType(PacketModel.TrafficType.OUTBOUND);
                packetModel.setLat(dstLatLng.getLatitude());
                packetModel.setLng(dstLatLng.getLongitude());
                packetModel.setSrcAddr(vpnAddress);
                packetModel.setDstAddr(k.getIp());
                packetModel.setCount(v);
                packetModel.setProtocol(k.getProtocol());
                packetModel.setCountryCode(countryCode);

                nativeToUnityQueue.offer(packetModel);
            });
        }

        private class KeyPair {
            private InetAddress ip;
            private Packet.IP4Header.TransportProtocol protocol;

            public KeyPair(InetAddress ip, Packet.IP4Header.TransportProtocol protocol) {
                this.ip = ip;
                this.protocol = protocol;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof KeyPair)) {
                    return false;
                }
                KeyPair keyPair = (KeyPair) obj;
                return ip.equals(keyPair.ip) && protocol.equals(keyPair.protocol);
            }

            @Override
            public int hashCode() {
                return Objects.hash(ip, protocol);
            }

            public InetAddress getIp() {
                return ip;
            }

            public Packet.IP4Header.TransportProtocol getProtocol() {
                return protocol;
            }
        }
    }
}
