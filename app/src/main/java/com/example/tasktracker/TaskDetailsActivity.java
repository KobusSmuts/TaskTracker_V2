package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskDetailsActivity extends AppCompatActivity {
    private EditText textViewTaskName, textViewTaskDescription;
    private Spinner spnViewTaskStatus;

    private Button btnApply;
    private String uniqueTaskID, taskID;
    private Task selectedTask;
    private FirebaseDatabaseService databaseService;
    private TaskRepository localDatabase;
    private final ExecutorService taskUpdateExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        databaseService = new FirebaseDatabaseService();
        localDatabase = new TaskRepository(getApplication());

        // Initialize TextViews and Spinner
        textViewTaskName = findViewById(R.id.text_view_task_name);
        spnViewTaskStatus = findViewById(R.id.spinner_task_status);
        textViewTaskDescription = findViewById(R.id.text_view_task_description);
        btnApply = findViewById(R.id.btnApply);
        Button btnBack = findViewById(R.id.btnBack);

        // Set up spinner with task statuses
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnViewTaskStatus.setAdapter(adapter);

        // Get task ID passed from TaskListActivity
        Intent intent = getIntent();
        uniqueTaskID = intent.getStringExtra("TASK_UNIQUE_ID");
        taskID = intent.getStringExtra("TASK_ID");
        Log.d("TaskDetailsActivity", "Task ID: " + uniqueTaskID);


        // Initialize ViewModel
        TaskViewModel taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        if (!uniqueTaskID.isEmpty()) {
            taskViewModel.getTaskById(taskID).observe(this, task -> {
                if (task != null) {
                    selectedTask = task;
                    textViewTaskName.setText(task.getName());
                    textViewTaskDescription.setText(task.getDescription());
                    // Set the spinner selection based on the task status
                    int statusPosition = task.getStatus();
                    spnViewTaskStatus.setSelection(statusPosition);
                }
            });
        }


        // Add code to update task status
        setupUpdateTask();


        btnBack.setOnClickListener(v -> {
            Intent intentBack = new Intent(this, TaskListActivity.class);
            startActivity(intentBack);
            finish();
        });
    }


    private void setupUpdateTask() {
        FirebaseUser user = AuthManager.getCurrentUser();
        if (user != null) {
            btnApply.setOnClickListener(view -> updateTaskAsync());
        } else {
            btnApply.setEnabled(false);
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTaskAsync() {
        String taskName = textViewTaskName.getText().toString().trim();
        int taskStatus = spnViewTaskStatus.getSelectedItemPosition();
        String taskDescription = textViewTaskDescription.getText().toString().trim();

        selectedTask.setName(taskName);
        selectedTask.setDescription(taskDescription);
        selectedTask.setStatus(taskStatus);

        if (validateInput(taskName, taskDescription)) {
            taskUpdateExecutor.execute(() -> {
                localDatabase.update(selectedTask);
                databaseService.updateTaskStatus(uniqueTaskID, selectedTask);
                mainHandler.post(() -> {
                    Toast.makeText(TaskDetailsActivity.this,
                            "Task update successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    startActivity(new Intent(this, TaskListActivity.class));
                    finish();
                });
            });
        }
    }

    private boolean validateInput(String taskName, String taskDescription) {
        if (taskName.isEmpty() || taskDescription.isEmpty()) {
            mainHandler.post(() ->
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show());
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskUpdateExecutor.shutdown();
        try {
            if (!taskUpdateExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                taskUpdateExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            taskUpdateExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}















