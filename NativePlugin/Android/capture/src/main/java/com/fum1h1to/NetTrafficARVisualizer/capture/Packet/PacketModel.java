package com.fum1h1to.NetTrafficARVisualizer.capture.Packet;

import java.net.InetAddress;

import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;

public class PacketModel {
    private TrafficType trafficType;
    private Double lat;
    private Double lng;
    private InetAddress srcAddr;
    private InetAddress dstAddr;
    private int count;
    private Packet.IP4Header.TransportProtocol protocol;
    private String countryCode;

    public PacketModel(
            TrafficType trafficType,
            Double lat,
            Double lng,
            InetAddress srcAddr,
            InetAddress dstAddr,
            int count,
            Packet.IP4Header.TransportProtocol protocol,
            String countryCode
    ) {
        this.trafficType = trafficType;
        this.lat = lat;
        this.lng = lng;
        this.srcAddr = srcAddr;
        this.dstAddr = dstAddr;
        this.count = count;
        this.protocol = protocol;
        this.countryCode = countryCode;
    }

    public enum TrafficType {
        INBOUND,
        OUTBOUND;
    }

    public TrafficType getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(TrafficType trafficType) {
        this.trafficType = trafficType;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public InetAddress getSrcAddr() {
        return srcAddr;
    }

    public void setSrcAddr(InetAddress srcAddr) {
        this.srcAddr = srcAddr;
    }

    public InetAddress getDstAddr() {
        return dstAddr;
    }

    public void setDstAddr(InetAddress dstAddr) {
        this.dstAddr = dstAddr;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Packet.IP4Header.TransportProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Packet.IP4Header.TransportProtocol protocol) {
        this.protocol = protocol;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
