package com.fum1h1to.NetTrafficARVisualizer.capture.threat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Spamhaus implements IThreatDatabase {
    private static final String TAG = "Spamhaus";
    private Activity mActivity;
    private Context mContext;
    private Set<String> threatSet;

    public Spamhaus(Activity activity) {
        mActivity = activity;
        mContext = activity;

        threatSet = new HashSet<>();
        initDb();
    }

    private void initDb() {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Boolean> futureResult = ex.submit(new ThreatDatabaseSettings(mActivity, this));
        try {
            if(futureResult.get()) {
                setupThreatMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupThreatMap() {
        try {
            FileReader fileReader = new FileReader(getSpamhausDropListFile());

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.charAt(0) == ';') {
                    continue;
                }
                int index = line.indexOf(" ; ");
                String ip = line.substring(0, index);

                threatSet.add(ip);
            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isThreatListFound () {
        try {
            FileReader check = new FileReader(getSpamhausDropListFile());
            check.close();
            return true;
        } catch(FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean downloadThreatList() {
        String url = "https://www.spamhaus.org/drop/drop.txt";

        boolean rv = Utils.downloadFile(url, getSpamhausDropListFile().getAbsolutePath());
        if(!rv) {
            Log.w(TAG, "Could not download spamhaus drop.txt from " +  url);
            return false;
        }
        Log.i(TAG, "Spamhaus drop.txt download success");
        return true;
    }

    public File getSpamhausDropListFile() {
        return new File(mContext.getFilesDir() + "drop.txt");
    }

    @Override
    public boolean isIpThreat(InetAddress targetIp) {
        for (String cidr: threatSet) {
            if(isInRange(targetIp.getHostAddress(), cidr)){
                return true;
            }
        }

        return false;
    }

    private boolean isInRange(String ip, String cidr) {
        try {

            String[] parts = cidr.split("/");
            InetAddress cidrIp = InetAddress.getByName(parts[0]);
            int prefix;

            if (parts.length < 2) {
                prefix = 0;
            } else {
                prefix = Integer.parseInt(parts[1]);
            }

            ByteBuffer maskBuffer;
            if (cidrIp.getAddress().length == 4) {
                maskBuffer = ByteBuffer.allocate(4).putInt(-1 << (32 - prefix));
            } else {
                maskBuffer = ByteBuffer.allocate(16).putLong(-1L).putLong(-1L << (64 - prefix));
            }
            byte[] mask = maskBuffer.array();

            ByteBuffer ipBuffer = ByteBuffer.wrap(InetAddress.getByName(ip).getAddress());
            ByteBuffer cidrIpBuffer = ByteBuffer.wrap(cidrIp.getAddress());

            return isMatchingAddress(ipBuffer, cidrIpBuffer, mask);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isMatchingAddress(ByteBuffer ipBuffer, ByteBuffer cidrIpBuffer, byte[] mask) {
        for (int i = 0; i < mask.length; i++) {
            if ((ipBuffer.get(i) & mask[i]) != (cidrIpBuffer.get(i) & mask[i])) {
                return false;
            }
        }
        return true;
    }
}
