package com.example.tasktracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SyncManager syncManager;
    private ExecutorService executorService;
    private Handler mainThreadHandler;
    private TextView syncStatus;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSync = findViewById(R.id.sync_button);
        Button btnTasks = findViewById(R.id.btnTasks);
        Button btnLogout = findViewById(R.id.btnLogout);
        syncStatus = findViewById(R.id.sync_status);

        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        syncManager = new SyncManager(this, AppDatabase.getInstance(this).taskDao());

        syncManager.getSyncStatus().observe(this, syncSuccessful -> {
            if (syncSuccessful != null) {
                String message = syncSuccessful ? "Sync successful" : "Sync failed";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        updateNetworkStatus();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> {
                    syncStatus.setText("Online");
                    syncStatus.setTextColor(getResources().getColor(R.color.green));
                });
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    syncStatus.setText("Offline");
                    syncStatus.setTextColor(getResources().getColor(R.color.red));
                });
            }
        };
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);

        btnSync.setOnClickListener(view -> {
            Toast.makeText(this, "Starting sync...", Toast.LENGTH_SHORT).show();
            executorService.execute(() -> syncManager.syncTasks());
        });

        btnTasks.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(view -> {
            executorService.execute(() -> {
                AuthManager.signOut();
                mainThreadHandler.post(() -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        syncManager.cleanup();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    private void updateNetworkStatus() {
        boolean online = NetworkUtil.isOnline(this);
        if (online){
            Toast.makeText(this, "Connected to internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
        syncStatus.setText(online ? "Online" : "Offline");
        syncStatus.setTextColor(getResources().getColor(online ? R.color.green : R.color.red));
    }
}
