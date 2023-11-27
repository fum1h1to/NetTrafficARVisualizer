package com.fum1h1to.NetTrafficARVisualizer.capture.packet.emulate.pattern;

import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.config.Config;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;
import com.fum1h1to.NetTrafficARVisualizer.capture.geoip.Coordinate;
import com.fum1h1to.NetTrafficARVisualizer.capture.geoip.Geolocation;
import com.fum1h1to.NetTrafficARVisualizer.capture.packet.PacketModel;
import com.fum1h1to.NetTrafficARVisualizer.capture.threat.Spamhaus;
import com.fum1h1to.NetTrafficARVisualizer.capture.threat.ThreatService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TestPattern implements IEmulatePattern {
    static final String TAG = "TestPattern";
    private BlockingQueue<PacketModel> nativeToUnityQueue;
    private List<PacketModel> testData = new ArrayList<>(10);
    private Geolocation mGeolocation;
    private ThreatService mThreadService;
    private InetAddress vpnAddress;

    public TestPattern(Geolocation geolocation, ThreatService spamhaus) {
        mGeolocation = geolocation;
        mThreadService = spamhaus;

        try {
            vpnAddress = InetAddress.getByName(Config.VPN_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setupData();
    }

    @Override
    public void setNativeToUnityQueue(BlockingQueue<PacketModel> nativeToUnityQueue) {
        this.nativeToUnityQueue = nativeToUnityQueue;
    }

    @Override
    public Boolean call() {
        for(PacketModel pm: testData) {
            nativeToUnityQueue.offer(pm);
        }
        return true;
    }

    private void setupData() {
        testData.add(makeInboundPacketModel(
            "8.8.8.8", 10, Packet.IP4Header.TransportProtocol.TCP
        ));
        testData.add(makeOutboundPacketModel(
                "8.8.8.8", 10, Packet.IP4Header.TransportProtocol.TCP
        ));
        testData.add(makeInboundPacketModel(
                "1.10.16.1", 5, Packet.IP4Header.TransportProtocol.TCP
        ));
        testData.add(makeOutboundPacketModel(
                "1.10.16.1", 5, Packet.IP4Header.TransportProtocol.TCP
        ));
    }

    private PacketModel makeInboundPacketModel(String srcAddr, int packetCount, Packet.IP4Header.TransportProtocol protocol) {
        InetAddress srcip = null;
        try {
            srcip = InetAddress.getByName(srcAddr);
        } catch (UnknownHostException e) {
            return null;
        }

        Coordinate srcLatLng = mGeolocation.getLatLng(srcip);
        if (srcLatLng == null) {
            return null;
        }

        return new PacketModel(
                PacketModel.TrafficType.INBOUND,
                srcLatLng.getLatitude(),
                srcLatLng.getLongitude(),
                srcip,
                vpnAddress,
                packetCount,
                protocol,
                mGeolocation.getCountryCode(srcip),
                mThreadService.getIsSpamhaus(srcip)
        );
    }

    private PacketModel makeOutboundPacketModel(String dstAddr, int packetCount, Packet.IP4Header.TransportProtocol protocol) {
        InetAddress dstip = null;
        try {
            dstip = InetAddress.getByName(dstAddr);
        } catch (UnknownHostException e) {
            return null;
        }

        Coordinate dstLatLng = mGeolocation.getLatLng(dstip);
        if (dstLatLng == null) {
            return null;
        }

        return new PacketModel(
                PacketModel.TrafficType.OUTBOUND,
                dstLatLng.getLatitude(),
                dstLatLng.getLongitude(),
                vpnAddress,
                dstip,
                packetCount,
                protocol,
                mGeolocation.getCountryCode(dstip),
                mThreadService.getIsSpamhaus(dstip)
        );
    }
}
