package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {
    private TextView textViewTaskName, textViewTaskStatus, textViewTaskDescription;
    private TaskRepository taskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        // Initialize TextViews
        textViewTaskName = findViewById(R.id.text_view_task_name);
        textViewTaskStatus = findViewById(R.id.text_view_task_status);
        textViewTaskDescription = findViewById(R.id.text_view_task_description);

        // Get task ID passed from TaskListActivity
        Intent intent = getIntent();
        String taskId = intent.getStringExtra("TASK_ID");

        taskRepository = new TaskRepository(getApplication());

        if (taskId != null) {
            taskRepository.getTaskById(taskId, task -> {
                // Set the task details to the UI on the main thread
                runOnUiThread(() -> {
                    if (task != null) {
                        textViewTaskName.setText(task.getName());
                        textViewTaskStatus.setText(task.getStatus());
                        textViewTaskDescription.setText(task.getDescription());
                    }
                });
            });
        }
    }
}