package com.example.tasktracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.TextView;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private TextView syncStatus;

    public NetworkChangeReceiver(TextView syncStatus) {
        this.syncStatus = syncStatus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (noConnectivity) {
                Toast.makeText(context, "Disconnected from internet", Toast.LENGTH_SHORT).show();
                syncStatus.setText("Offline");
            } else {
                Toast.makeText(context, "Connected to internet", Toast.LENGTH_SHORT).show();
                syncStatus.setText("Online");
            }
        }
    }
}
