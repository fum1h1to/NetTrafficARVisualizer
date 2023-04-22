package com.fum1h1to.NetTrafficARVisualizer.capture.Permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

public class PermissionManager {

    public static void checkPermission(Activity mActivity) {
        if (mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可を取得する
            mActivity.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
    }
}
