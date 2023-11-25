package com.fum1h1to.NetTrafficARVisualizer.capture.threat;

import java.net.InetAddress;

public interface IThreatDatabase {

    public boolean isThreatListFound();
    public boolean downloadThreatList();
    public boolean isIpThreat(InetAddress targetIp);

}
