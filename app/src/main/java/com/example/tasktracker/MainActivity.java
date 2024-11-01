package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
                } else {
                    // Handle sync failure
                }
            }
        });

        // Sync tasks when needed
        syncManager.syncTasks();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

