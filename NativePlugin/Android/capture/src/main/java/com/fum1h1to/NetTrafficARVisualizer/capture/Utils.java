package com.fum1h1to.NetTrafficARVisualizer.capture;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class Utils {
    static final String TAG = "Utils";

    public static void safeClose(Closeable obj) {
        if(obj == null)
            return;

        try {
            obj.close();
        } catch (IOException e) {
            Log.w(TAG, e.getLocalizedMessage());
        }
    }

    public static boolean downloadFile(String _url, String path) {
        boolean has_contents = false;

        try (FileOutputStream out = new FileOutputStream(path + ".tmp")) {
            try (BufferedOutputStream bos = new BufferedOutputStream(out)) {
                URL url = new URL(_url);

                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                try {
                    // Necessary otherwise the connection will stay open
                    con.setRequestProperty("Connection", "Close");
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);

                    try(InputStream in = new BufferedInputStream(con.getInputStream())) {
                        byte[] bytesIn = new byte[4096];
                        int read;
                        while ((read = in.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                            has_contents |= (read > 0);
                        }
                    } catch (SocketTimeoutException _ignored) {
                        Log.w(TAG, "Timeout while fetching " + _url);
                    }
                } finally {
                    con.disconnect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!has_contents) {
            try {
                //noinspection ResultOfMethodCallIgnored
                (new File(path + ".tmp")).delete(); // if exists
            } catch (Exception ignored) {
                // ignore
            }
            return false;
        }

        // Only write the target path if it was successful
        return (new File(path + ".tmp")).renameTo(new File(path));
    }

    public static boolean ungzip(InputStream is, String dst) {
        try(GZIPInputStream gis = new GZIPInputStream(is)) {
            try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dst))) {
                byte[] bytesIn = new byte[4096];
                int read;
                while ((read = gis.read(bytesIn)) != -1)
                    bos.write(bytesIn, 0, read);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
