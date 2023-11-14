package com.fum1h1to.NetTrafficARVisualizer.capture.core;


import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.core.bio.BioTcpHandler;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.bio.BioUdpHandler;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.bio.NioSingleThreadTcpHandler;
import com.fum1h1to.NetTrafficARVisualizer.capture.config.Config;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.protocol.tcpip.Packet;
import com.fum1h1to.NetTrafficARVisualizer.capture.core.util.ByteBufferPool;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalVPNService extends VpnService {
    private static final String TAG = LocalVPNService.class.getSimpleName();
    private static LocalVPNService INSTANCE;

    private ParcelFileDescriptor vpnInterface = null;

    private PendingIntent pendingIntent;

    private BlockingQueue<Packet> deviceToNetworkUDPQueue;
    private BlockingQueue<Packet> deviceToNetworkTCPQueue;
    private BlockingQueue<ByteBuffer> networkToDeviceQueue;
    private ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();

        setupVPN();
        deviceToNetworkUDPQueue = new ArrayBlockingQueue<Packet>(1000);
        deviceToNetworkTCPQueue = new ArrayBlockingQueue<Packet>(1000);
        networkToDeviceQueue = new ArrayBlockingQueue<>(1000);

        executorService = Executors.newFixedThreadPool(10);
        executorService.submit(new BioUdpHandler(deviceToNetworkUDPQueue, networkToDeviceQueue, this));
//        executorService.submit(new BioTcpHandler(deviceToNetworkTCPQueue, networkToDeviceQueue, this));
        executorService.submit(new NioSingleThreadTcpHandler(deviceToNetworkTCPQueue, networkToDeviceQueue, this));

        executorService.submit(new VPNRunnable(vpnInterface.getFileDescriptor(),
                deviceToNetworkUDPQueue, deviceToNetworkTCPQueue, networkToDeviceQueue));

        INSTANCE = this;
        Log.i(TAG, "Started");
    }

    private void setupVPN() {
        try {
            if (vpnInterface == null) {
                Builder builder = new Builder();
                builder.addAddress(Config.VPN_ADDRESS, 32);
                builder.addRoute(Config.VPN_ROUTE, 0);
                builder.addDnsServer(Config.dns);
                vpnInterface = builder.setSession("NetTrafficARVisualizer").setConfigureIntent(pendingIntent).establish();
            }
        } catch (Exception e) {
            Log.e(TAG, "error", e);
            System.exit(0);
        }
    }

    public static void stopService() {
        LocalVPNService lvs = INSTANCE;
        lvs.stopSelf();
        lvs.stopVPN();
    }

    private void stopVPN() {
        executorService.shutdownNow();
        cleanup();
        Log.i(TAG, "Stopped");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onRevoke() {
        stopVPN();
        super.onRevoke();
    }

    @Override
    public void onDestroy() {
        stopVPN();
        super.onDestroy();
    }

    private void cleanup() {
        deviceToNetworkTCPQueue = null;
        deviceToNetworkUDPQueue = null;
        networkToDeviceQueue = null;
        closeResources(vpnInterface);
    }

    // TODO: Move this to a "utils" class for reuse
    private static void closeResources(Closeable... resources) {
        for (Closeable resource : resources) {
            try {
                resource.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private static class VPNRunnable implements Runnable {
        private static final String TAG = VPNRunnable.class.getSimpleName();

        private FileDescriptor vpnFileDescriptor;

        private BlockingQueue<Packet> deviceToNetworkUDPQueue;
        private BlockingQueue<Packet> deviceToNetworkTCPQueue;
        private BlockingQueue<ByteBuffer> networkToDeviceQueue;

        public VPNRunnable(FileDescriptor vpnFileDescriptor,
                           BlockingQueue<Packet> deviceToNetworkUDPQueue,
                           BlockingQueue<Packet> deviceToNetworkTCPQueue,
                           BlockingQueue<ByteBuffer> networkToDeviceQueue) {
            this.vpnFileDescriptor = vpnFileDescriptor;
            this.deviceToNetworkUDPQueue = deviceToNetworkUDPQueue;
            this.deviceToNetworkTCPQueue = deviceToNetworkTCPQueue;
            this.networkToDeviceQueue = networkToDeviceQueue;
        }


        static class WriteVpnThread implements Runnable {
            FileChannel vpnOutput;
            private BlockingQueue<ByteBuffer> networkToDeviceQueue;

            WriteVpnThread(FileChannel vpnOutput, BlockingQueue<ByteBuffer> networkToDeviceQueue) {
                this.vpnOutput = vpnOutput;
                this.networkToDeviceQueue = networkToDeviceQueue;
            }

            @Override
            public void run() {
                while (true) {
                    try {
                        ByteBuffer bufferFromNetwork = networkToDeviceQueue.take();
                        bufferFromNetwork.flip();
                        while (bufferFromNetwork.hasRemaining()) {
                            int w = vpnOutput.write(bufferFromNetwork);
                            if (w > 0) {
//                                MainActivity.downByte.addAndGet(w);
                            }
                            if (Config.logRW) {
                                Log.d(TAG, "vpn write " + w);
                            }
                        }

                        bufferFromNetwork.rewind();
                        Packet packet = new Packet(bufferFromNetwork);
                        CaptureQueue.queue.offer(packet);
                    } catch (Exception e) {
                        Log.i(TAG, "WriteVpnThread fail", e);
                    }
                }

            }
        }

        @Override
        public void run() {
            Log.i(TAG, "Started");
            FileChannel vpnInput = new FileInputStream(vpnFileDescriptor).getChannel();
            FileChannel vpnOutput = new FileOutputStream(vpnFileDescriptor).getChannel();
            Thread t = new Thread(new WriteVpnThread(vpnOutput, networkToDeviceQueue));
            t.start();
            try {
                ByteBuffer bufferToNetwork = null;
                while (!Thread.interrupted()) {
                    bufferToNetwork = ByteBufferPool.acquire();
                    int readBytes = vpnInput.read(bufferToNetwork);

//                    MainActivity.upByte.addAndGet(readBytes);

                    if (readBytes > 0) {
                        bufferToNetwork.flip();

                        Packet packet = new Packet(bufferToNetwork);
                        if (packet.isUDP()) {
                            if (Config.logRW) {
                                Log.i(TAG, "read udp" + readBytes);
                            }
                            deviceToNetworkUDPQueue.offer(packet);
                        } else if (packet.isTCP()) {
                            if (Config.logRW) {
                                Log.i(TAG, "read tcp " + readBytes);
                            }
                            deviceToNetworkTCPQueue.offer(packet);
                        } else {
                            Log.w(TAG, String.format("Unknown packet protocol type %d", packet.ip4Header.protocolNum));
                        }

                        CaptureQueue.queue.offer(packet);

                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                Log.w(TAG, e.toString(), e);
            } finally {
                closeResources(vpnInput, vpnOutput);
            }
        }
    }
}

