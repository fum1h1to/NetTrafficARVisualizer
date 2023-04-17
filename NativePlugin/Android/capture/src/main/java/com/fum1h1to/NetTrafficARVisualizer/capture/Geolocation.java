package com.fum1h1to.NetTrafficARVisualizer.capture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Geolocation {
    private static final String TAG = "Geolocation";
    private final Context mContext;
    private DatabaseReader mCityReader;

    public Geolocation(Context ctx) {
        mContext = ctx;
        openDb();
    }

    @Override
    public void finalize() {
        Utils.safeClose(mCityReader);
        mCityReader = null;
    }

    private void openDb() {
        try {
            mCityReader = new DatabaseReader.Builder(getCityFile(mContext)).build();
            Log.d(TAG, "City DB loaded: " + mCityReader.getMetadata());

        } catch (IOException e) {
            Log.i(TAG, "Geolocation is not available");
        }
    }

    private static File getCityFile(Context ctx) {
        return new File(ctx.getFilesDir() + "/dbip_city_lite.mmdb");
    }

    public static Date getDbDate(File file) throws IOException {
        try(Reader reader = new Reader(file)) {
            return reader.getMetadata().getBuildDate();
        }
    }

//    public static Date getDbDate(Context ctx) {
//        try {
//            return getDbDate(getCityFile(ctx));
//        } catch (IOException ignored) {
//            return null;
//        }
//    }
//
//    public static long getDbSize(Context ctx) {
//        return getCityFile(ctx).length();
//    }

//    @SuppressWarnings("ResultOfMethodCallIgnored")
//    public void deleteDb(Context ctx) {
//        getCityFile(ctx).delete();
//    }

    @SuppressLint("SimpleDateFormat")
    public static boolean downloadDb(Context ctx) {
        String dateid = new SimpleDateFormat("yyyy-MM").format(new Date());
        String city_url = "https://download.db-ip.com/free/dbip-city-lite-" + dateid + ".mmdb.gz";

        try {
            return downloadAndUnzip(ctx, "city", city_url, getCityFile(ctx));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean downloadAndUnzip(Context ctx, String label, String url, File dst) throws IOException {
        File tmp_file = new File(ctx.getCacheDir() + "/geoip_db.zip");

        boolean rv = Utils.downloadFile(url, tmp_file.getAbsolutePath());
        if(!rv) {
            Log.w(TAG, "Could not download " + label + " db from " +  url);
            return false;
        }

        try(FileInputStream is = new FileInputStream(tmp_file.getAbsolutePath())) {
            if(!Utils.ungzip(is, dst.getAbsolutePath())) {
                Log.w(TAG, "ungzip of " + tmp_file + " failed");
                return false;
            }

            // Verify - throws IOException on error
            getDbDate(dst);

            return true;
        } finally {
            //noinspection ResultOfMethodCallIgnored
            tmp_file.delete();
        }
    }

    public double[] getLatLng(String ip) {
        if(mCityReader != null) {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                CityResponse response = mCityReader.city(ipAddress);
                Location location = response.getLocation();
                Log.d(TAG, String.format("lat: %s, lng: %s\n",
                        location.getLatitude(), location.getLongitude()));

                return new double[]{location.getLatitude(), location.getLongitude()};
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // fallback
        return null;
    }

}
