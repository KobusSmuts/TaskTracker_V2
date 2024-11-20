package com.example.tasktracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.core.app.ActivityCompat;

public class NetworkUtil {
    private static final String PREFS_NAME = "NetworkUtilPrefs";
    private static final String LAST_DOWNLINK_BANDWIDTH = "LastDownlinkBandwidth";
    private static final String LAST_UPLINK_BANDWIDTH = "LastUplinkBandwidth";
    private static final int BANDWIDTH_THRESHOLD = 500; // Kbps
    private static final int LOW_BANDWIDTH_THRESHOLD = 1500; // Example threshold for low bandwidth
    private static final int SUFFICIENT_BANDWIDTH_THRESHOLD = 3000; // Example threshold for sufficient bandwidth

    public static boolean isOnline(Context context) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            boolean isConnected = false;

            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkCapabilities capabilities =
                            connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                    if (capabilities != null) {
                        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                            isConnected = true;
                        }
                    }
                } else {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        isConnected = true;
                    }
                }
            }
            return isConnected;
        } else {
            return false;
        }
    }

    public static String getBandwidthInfo(Context context) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkCapabilities capabilities =
                            connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                    if (capabilities != null) {
                        int downlinkBandwidth = capabilities.getLinkDownstreamBandwidthKbps();
                        int uplinkBandwidth = capabilities.getLinkUpstreamBandwidthKbps();

                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        int lastDownlinkBandwidth = prefs.getInt(LAST_DOWNLINK_BANDWIDTH, -1);
                        int lastUplinkBandwidth = prefs.getInt(LAST_UPLINK_BANDWIDTH, -1);

                        boolean significantChange = Math.abs(downlinkBandwidth - lastDownlinkBandwidth) > BANDWIDTH_THRESHOLD ||
                                Math.abs(uplinkBandwidth - lastUplinkBandwidth) > BANDWIDTH_THRESHOLD;

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(LAST_DOWNLINK_BANDWIDTH, downlinkBandwidth);
                        editor.putInt(LAST_UPLINK_BANDWIDTH, uplinkBandwidth);
                        editor.apply();

                        if (significantChange) {
                            return "Downlink: " + downlinkBandwidth + " Kbps, Uplink: " + uplinkBandwidth + " Kbps";
                        } else {
                            return null;
                        }
                    }
                } else {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return "Bandwidth information not available on this device.";
                    }
                }
            }
        }
        return "Permission not granted for network state access.";
    }

    public static boolean isLowBandwidth(Context context) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkCapabilities capabilities =
                            connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                    if (capabilities != null) {
                        int downlinkBandwidth = capabilities.getLinkDownstreamBandwidthKbps();
                        return downlinkBandwidth < LOW_BANDWIDTH_THRESHOLD;
                    }
                } else {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return activeNetworkInfo.getSubtype() < 13;
                    }
                }
            }
        }
        return false;
    }

}
