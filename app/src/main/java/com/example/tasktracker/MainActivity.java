package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SyncManager syncManager;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSync = findViewById(R.id.sync_button);
        Button btnTasks = findViewById(R.id.btnTasks);
        Button btnLogout = findViewById(R.id.btnLogout);

        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        syncManager = new SyncManager(this, AppDatabase.getInstance(this).taskDao());

        syncManager.getSyncStatus().observe(this, syncSuccessful -> {
            if (syncSuccessful != null) {
                String message = syncSuccessful ? "Sync successful" : "Sync failed";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

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
    }
}