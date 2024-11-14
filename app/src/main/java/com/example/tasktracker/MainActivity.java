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
    private FirebaseAuthService authService;
    private AppDatabase db;
    private TaskDao taskDao;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSync = findViewById(R.id.sync_button);
        Button btnTasks = findViewById(R.id.btnTasks);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Initialize Room database and DAO
        db = AppDatabase.getInstance(this);
        taskDao = db.taskDao();

        // Initialize ExecutorService and Handler
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        // Initialize SyncManager
        syncManager = new SyncManager(this, taskDao);
        syncManager.getSyncStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean syncSuccessful) {
                mainThreadHandler.post(() -> {
                    if (syncSuccessful) {
                        // Update UI or notify user of success
                        Toast.makeText(MainActivity.this, "Sync successful", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle sync failure
                        Toast.makeText(MainActivity.this, "Sync failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Sync tasks when needed
        btnSync.setOnClickListener(view -> executorService.execute(syncManager::syncTasks));

        btnTasks.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
            startActivity(intent);
            finish();
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
    }
}
