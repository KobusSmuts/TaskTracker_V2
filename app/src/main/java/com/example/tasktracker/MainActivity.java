package com.example.tasktracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 100;
    private SyncManager syncManager;
    private ExecutorService executorService;
    private Handler mainThreadHandler;
    private TextView syncStatus;
    private ConnectivityManager.NetworkCallback networkCallback;
    private static final AtomicInteger notificationId = new AtomicInteger(0);
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;
    private boolean lowBandwidthNotified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSync = findViewById(R.id.sync_button);
        Button btnUsersOrTasks = findViewById(R.id.btnUsersOrTasks);
        Button btnLogout = findViewById(R.id.btnLogout);
        syncStatus = findViewById(R.id.sync_status);

        if (UserPreferences.getUserRole(this) == 0) {
            btnUsersOrTasks.setText("View Staff");
        } else {
            btnUsersOrTasks.setText("GO TO TASKS");
        }

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
                    updateNetworkStatus(); // Update online/offline status immediately
                    handleBandwidthChange();
                });
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    updateNetworkStatus(); // Update online/offline status immediately
                });
            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                runOnUiThread(MainActivity.this::handleBandwidthChange);
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_CODE);
        } else {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        }

        btnSync.setOnClickListener(view -> {
            Toast.makeText(this, "Starting sync...", Toast.LENGTH_SHORT).show();
            executorService.execute(() -> syncManager.syncTasks());
        });

        btnUsersOrTasks.setOnClickListener(view -> {
            if (UserPreferences.getUserRole(this) == 0) {
                Intent intent = new Intent(MainActivity.this, UserListActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
                startActivity(intent);
                finish();
            }

        });

        btnLogout.setOnClickListener(view -> {
            executorService.execute(() -> {
                AuthManager.signOut();
                UserPreferences.clearPreferences(MainActivity.this);
                mainThreadHandler.post(() -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            });
        });
    }

//    private void initializeSyncManager() {
//        syncManager = new SyncManager(this, AppDatabase.getInstance(this).taskDao());
//        syncManager.getSyncStatus().observe(this, syncSuccessful -> {
//            if (syncSuccessful != null) {
//                String message = syncSuccessful ? "Sync successful" : "Sync failed";
//                mainThreadHandler.post(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
//            }
//        });
//    }

    private void handleBandwidthChange() {
        debounceHandler.removeCallbacks(debounceRunnable);
        debounceRunnable = () -> {
            String bandwidthInfo = NetworkUtil.getBandwidthInfo(MainActivity.this);
            if (NetworkUtil.isLowBandwidth(MainActivity.this)) {
                if (!lowBandwidthNotified) {
                    showNotification("Low Bandwidth", "You are in a low bandwidth environment. " + bandwidthInfo);
                    lowBandwidthNotified = true;
                }
            } else {
                if (lowBandwidthNotified) {
                    showNotification("Bandwidth Sufficient", "The bandwidth is now sufficient for stable app functionality. " + bandwidthInfo);
                    lowBandwidthNotified = false;
                } else if (bandwidthInfo != null) {
                    showNotification("Bandwidth Changed", "The bandwidth has changed significantly. " + bandwidthInfo);
                }
            }
        };
        debounceHandler.postDelayed(debounceRunnable, 1000); // 1-second debounce period
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(), networkCallback);
        } else {
            Toast.makeText(this, "Permission denied. Unable to check network status.", Toast.LENGTH_SHORT).show();
        }
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
        syncStatus.setText(online ? "Online" : "Offline");
        syncStatus.setTextColor(getResources().getColor(online ? R.color.green : R.color.red));
    }

    private void showNotification(String title, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "network_status_channel")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId.incrementAndGet(), builder.build());
        }
    }
}
