package com.fum1h1to.NetTrafficARVisualizer.capture.threat;

import android.app.Activity;

import java.net.InetAddress;

public class ThreatService {
    private static final String TAG = "ThreatService";
    private Spamhaus mSpamhaus;

    public ThreatService(
            Activity activity
    ) {
        mSpamhaus = new Spamhaus(activity);
    }

    public boolean getIsSpamhaus(InetAddress targetIp) {
        return mSpamhaus.isIpThreat(targetIp);
    }
}
