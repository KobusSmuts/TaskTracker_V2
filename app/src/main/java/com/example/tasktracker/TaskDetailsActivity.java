package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

    private TextView textViewTaskName, textViewTaskStatus, textViewTaskDescription;

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

        if (taskId != null) {
            // Fetch task details using taskId (This part depends on how you fetch tasks from your repository)
            // Example: You might have a method like taskRepository.getTaskById(taskId) to retrieve the task from DB

            Task task = getTaskById(taskId);  // Assume you have a method to fetch a Task by ID

            // Set the task details to the UI
            if (task != null) {
                textViewTaskName.setText(task.getName());
                textViewTaskStatus.setText(task.getStatus());
                textViewTaskDescription.setText(task.getDescription());
            }
        }
    }

    // Example method to fetch Task by ID (this could be done using a ViewModel/Repository depending on your architecture)
    private Task getTaskById(String taskId) {
        // This is a placeholder for actual logic to fetch the task from your repository
        // You could use a ViewModel to get the task, for example
        return new Task(taskId, "employee@example.com", "Sample Task", "This is a task description.", "In Progress");
    }
}

