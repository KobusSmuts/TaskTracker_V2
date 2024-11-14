package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import androidx.activity.viewModels;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private SyncManager syncManager;

    private FirebaseAuthService authService;
    private AppDatabase db;
    private TaskDao taskDao;

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

        // Initialize SyncManager
        syncManager = new SyncManager(this, taskDao);
        syncManager.getSyncStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean syncSuccessful) {
                if (syncSuccessful) {
                    // Update UI or notify user of success

                    Toast.makeText(MainActivity.this, "Sync successful", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle sync failure
                    Toast.makeText(MainActivity.this, "Sync failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sync tasks when needed
        btnSync.setOnClickListener(view -> syncManager.syncTasks());

        btnTasks.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

