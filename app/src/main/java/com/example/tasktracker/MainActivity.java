package com.example.tasktracker;

import android.os.Bundle;
//import androidx.activity.viewModels;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private SyncManager syncManager;
    private AppDatabase db;
    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Room database and DAO
        db = AppDatabase.getInstance(this);
        taskDao = db.taskDao();

        // Initialize SyncManager
        syncManager = new SyncManager(this, taskDao);
        syncManager.getSyncStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean syncSuccessful) {
                if (syncSuccessful) {
                    // Update UI or notify user of success
                } else {
                    // Handle sync failure
                }
            }
        });

        // Sync tasks when needed
        syncManager.syncTasks();
    }
}

