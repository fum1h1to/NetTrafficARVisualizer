package com.fum1h1to.NetTrafficARVisualizer.capture;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.core.LocalVPNService;

import org.pcap4j.packet.IpV4Packet;

import java.util.Observable;
import java.util.Observer;

public class CaptureActivity extends UnityPlayerActivity implements Observer {

    static final String TAG = "CaptureActivity";
    static final int START_CAPTURE_CODE = 1000;
    static final int STOP_CAPTURE_CODE = 1001;
    static final int STATUS_CAPTURE_CODE = 1002;
    PacketCreater mPacketCreater;
    boolean mCaptureRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if((savedInstanceState != null) && savedInstanceState.containsKey("capture_running"))
            setCaptureRunning(savedInstanceState.getBoolean("capture_running"));

        mPacketCreater = new PacketCreater(this);
        mPacketCreater.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCapture();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPacketCreater.getLocalLocationManager().updateNowLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void update(Observable o, Object arg) {
        boolean capture_running = (boolean)arg;
        Log.d(TAG, "capture_running: " + capture_running);
        setCaptureRunning(capture_running);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("capture_running", mCaptureRunning);
        super.onSaveInstanceState(bundle);
    }

    void onPacketReceived(IpV4Packet pkt) {
        IpV4Packet.IpV4Header hdr = pkt.getHeader();
        Log.i(TAG, String.format("[%s] %s -> %s [%d B]\n",
                hdr.getProtocol(),
                hdr.getSrcAddr().getHostAddress(), hdr.getDstAddr().getHostAddress(),
                pkt.length()));

        mPacketCreater.createPacket(hdr.getSrcAddr().getHostAddress(), hdr.getDstAddr().getHostAddress(), String.valueOf(hdr.getProtocol()), pkt.length());
    }

    void setCaptureRunning(boolean running) {
        mCaptureRunning = running;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (START_CAPTURE_CODE):
                Log.d(TAG, "capture start result: " + resultCode);
                if (resultCode == RESULT_OK) {
                    setCaptureRunning(true);
                    startService(new Intent(this, LocalVPNService.class));
                } else {

                }
                break;
            case (STOP_CAPTURE_CODE):
                Log.d(TAG, "capture stop result: " + resultCode);
                if (resultCode == RESULT_OK) {
                    setCaptureRunning(false);
                    stopService(new Intent(this, LocalVPNService.class));
                } else {

                }
                break;
            default:
                break;
        }
    }

    /* ---------------------------------------------------------------------------------------------
    unity use
    --------------------------------------------------------------------------------------------- */

    public boolean getCaptureRunning() {
        return mCaptureRunning;
    }

    public void test() {
        mPacketCreater.createPacket("8.8.8.8", "10.215.173.1", "TCP", 1);
        mPacketCreater.createPacket("10.215.173.1","8.8.8.8", "TCP", 1);
    }

    public void startCapture() {
        Log.d(TAG, "start capture");

        Intent vpnIntent = LocalVPNService.prepare(this);

        if (vpnIntent != null)
            startActivityForResult(vpnIntent, START_CAPTURE_CODE);
        else
            onActivityResult(START_CAPTURE_CODE, RESULT_OK, null);
    }

    public void stopCapture() {
        Log.d(TAG, "Stopping PCAPdroid");

        onActivityResult(STOP_CAPTURE_CODE, RESULT_OK, null);
    }
}
