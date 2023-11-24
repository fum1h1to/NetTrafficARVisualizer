package com.fum1h1to.NetTrafficARVisualizer.capture.geoip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fum1h1to.NetTrafficARVisualizer.capture.Utils;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Geolocation {
    private static final String TAG = "Geolocation";
    private Activity mActivity;
    private final Context mContext;
    private Reader mCountryReader;
    private DatabaseReader mCityReader;

    public Geolocation(Activity activity) {
        mContext = activity;
        mActivity = activity;
    }

    @Override
    public void finalize() {
        Utils.safeClose(mCityReader);
        Utils.safeClose(mCountryReader);
        mCityReader = null;
    }

    public void initDb() {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<Boolean> futureResult = ex.submit(new GeoipSettings(mActivity));
        try {
            if(futureResult.get()) {
                openDb();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDb() {
        try {
            mCountryReader = new Reader(getCityFile(mContext));
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

    public static boolean isDbFound(Context ctx) {
        try {
            getDbDate(getCityFile(ctx));
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteDb(Context ctx) {
        getCityFile(ctx).delete();
    }

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

    public Coordinate getLatLng(String ip) {
        if(mCityReader != null) {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                CityResponse response = mCityReader.city(ipAddress);
                Location location = response.getLocation();

                return new Coordinate(location.getLatitude(), location.getLongitude());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // fallback
        return null;
    }

    public String getCountryCode(String ip) {
        if(mCountryReader != null) {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                Geomodel.CountryResult res = mCountryReader.get(ipAddress, Geomodel.CountryResult.class);

                if ((res != null) && (res.getCountry() != null))
                    return res.getCountry().getIsoCode();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
