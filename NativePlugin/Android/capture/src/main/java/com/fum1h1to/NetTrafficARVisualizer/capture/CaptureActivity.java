package com.fum1h1to.NetTrafficARVisualizer.capture;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.pcap4j.packet.IpV4Packet;

import java.util.Observable;
import java.util.Observer;

public class CaptureActivity extends UnityPlayerActivity implements Observer {

    static final String PCAPDROID_PACKAGE = "com.emanuelef.remote_capture.debug"; // add ".debug" for the debug build of PCAPdroid
    static final String CAPTURE_CTRL_ACTIVITY = "com.emanuelef.remote_capture.activities.CaptureCtrl";
    static final String CAPTURE_STATUS_ACTION = "com.emanuelef.remote_capture.CaptureStatus";
    static final String TAG = "CaptureActivity";
    static final int START_CAPTURE_CODE = 1000;
    static final int STOP_CAPTURE_CODE = 1001;
    static final int STATUS_CAPTURE_CODE = 1002;
    CaptureThread mCapThread;
    PacketCreater mPacketCreater;
    boolean mCaptureRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if((savedInstanceState != null) && savedInstanceState.containsKey("capture_running"))
            setCaptureRunning(savedInstanceState.getBoolean("capture_running"));
        else
            queryCaptureStatus();

        mPacketCreater = new PacketCreater(this);

        // will call the "update" method when the capture status changes
        MyBroadcastReceiver.CaptureObservable.getInstance().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyBroadcastReceiver.CaptureObservable.getInstance().deleteObserver(this);
        stopCaptureThread();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPacketCreater.init();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // アプリ起動時に、デバイスの向いている方角を取得
        mPacketCreater.getLocalSensorManager().updateDeviceBearing();

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

    public void test() {
        mPacketCreater.createPacket("8.8.8.8", "192.168.1.1", "TCP", 1);
    }

    void queryCaptureStatus() {
        Log.d(TAG, "Querying PCAPdroid");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName(PCAPDROID_PACKAGE, CAPTURE_CTRL_ACTIVITY);
        intent.putExtra("action", "get_status");

        try {
            startActivityForResult(intent, STATUS_CAPTURE_CODE);
        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "PCAPdroid package not found: " + PCAPDROID_PACKAGE, Toast.LENGTH_LONG).show();
        }
    }

    void startCapture() {
        Log.d(TAG, "Starting PCAPdroid");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName(PCAPDROID_PACKAGE, CAPTURE_CTRL_ACTIVITY);

        intent.putExtra("action", "start");
        intent.putExtra("broadcast_receiver", "com.emanuelef.pcap_receiver.MyBroadcastReceiver");
        intent.putExtra("pcap_dump_mode", "udp_exporter");
        intent.putExtra("collector_ip_address", "127.0.0.1");
        intent.putExtra("collector_port", "5123");
        //intent.putExtra("app_filter", "org.mozilla.firefox");

        startActivityForResult(intent, START_CAPTURE_CODE);
    }

    void stopCapture() {
        Log.d(TAG, "Stopping PCAPdroid");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName(PCAPDROID_PACKAGE, CAPTURE_CTRL_ACTIVITY);
        intent.putExtra("action", "stop");

        startActivityForResult(intent, STOP_CAPTURE_CODE);
    }

    void setCaptureRunning(boolean running) {
        mCaptureRunning = running;
        Log.d(TAG, running ? "Stop Capture" : "Start Capture");

        if(mCaptureRunning && (mCapThread == null)) {
            mCapThread = new CaptureThread(this);
            mCapThread.start();
        } else if(!mCaptureRunning)
            stopCaptureThread();
    }

    void stopCaptureThread() {
        if(mCapThread == null)
            return;

        mCapThread.stopCapture();
        mCapThread.interrupt();
        mCapThread = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (START_CAPTURE_CODE):
                Log.d(TAG, "PCAPdroid start result: " + resultCode);
                if (resultCode == RESULT_OK) {
                    setCaptureRunning(true);
                } else {

                }
                break;
            case (STOP_CAPTURE_CODE):
                Log.d(TAG, "PCAPdroid stop result: " + resultCode);
                if (resultCode == RESULT_OK) {
                    setCaptureRunning(false);
                } else {

                }

                if((data != null) && (data.hasExtra("bytes_sent")))
                    logStats(data);
                break;
            case (STATUS_CAPTURE_CODE):
                Log.d(TAG, "PCAPdroid status result: " + resultCode);
                if (resultCode == RESULT_OK) {
                    boolean running = data.getBooleanExtra("running", false);
                    int verCode = data.getIntExtra("version_code", 0);
                    String verName = data.getStringExtra("version_name");

                    if(verName == null)
                        verName = "<1.4.6";

                    Log.d(TAG, "PCAPdroid " + verName + "(" + verCode + "): running=" + running);
                    setCaptureRunning(running);
                } else {

                }
                break;
            default:
                break;
        }
    }

    void logStats(Intent intent) {
        String stats = "*** Stats ***" +
                "\nBytes sent: " +
                intent.getLongExtra("bytes_sent", 0) +
                "\nBytes received: " +
                intent.getLongExtra("bytes_rcvd", 0) +
                "\nPackets sent: " +
                intent.getIntExtra("pkts_sent", 0) +
                "\nPackets received: " +
                intent.getIntExtra("pkts_rcvd", 0) +
                "\nPackets dropped: " +
                intent.getIntExtra("pkts_dropped", 0) +
                "\nPCAP dump size: " +
                intent.getLongExtra("bytes_dumped", 0);

        Log.i("stats", stats);
    }

    public boolean getCaptureRunning() {
        return mCaptureRunning;
    }
}
