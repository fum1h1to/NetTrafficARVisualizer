package com.fum1h1to.NetTrafficARVisualizer.capture.packet;

import java.net.InetAddress;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;

public class PacketModel {
    private TrafficType trafficType;
    @JsonProperty("lat")
    private Double lat;
    @JsonProperty("lng")
    private Double lng;
    @JsonProperty("srcAddr")
    private InetAddress srcAddr;
    @JsonProperty("dstAddr")
    private InetAddress dstAddr;
    @JsonProperty("count")
    private int count;
    @JsonProperty("protocol")
    private Packet.IP4Header.TransportProtocol protocol;
    @JsonProperty("countryCode")
    private String countryCode;
    @JsonProperty("isSpamhaus")
    private boolean isSpamhaus;

    public PacketModel() {}

    public PacketModel(
            TrafficType trafficType,
            Double lat,
            Double lng,
            InetAddress srcAddr,
            InetAddress dstAddr,
            int count,
            Packet.IP4Header.TransportProtocol protocol,
            String countryCode,
            boolean isSpamhaus
    ) {
        this.trafficType = trafficType;
        this.lat = lat;
        this.lng = lng;
        this.srcAddr = srcAddr;
        this.dstAddr = dstAddr;
        this.count = count;
        this.protocol = protocol;
        this.countryCode = countryCode;
        this.isSpamhaus = isSpamhaus;
    }

    public enum TrafficType {
        INBOUND,
        OUTBOUND;
    }

    public String toJsonText() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonData = mapper.writeValueAsString(this);
            return jsonData;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isSpamhaus() {
        return isSpamhaus;
    }

    public void setSpamhaus(boolean spamhaus) {
        isSpamhaus = spamhaus;
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
