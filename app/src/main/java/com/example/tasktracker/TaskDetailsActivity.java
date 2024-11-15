package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class TaskDetailsActivity extends AppCompatActivity {
    private TextView textViewTaskName, textViewTaskStatus, textViewTaskDescription;
    private TaskViewModel taskViewModel;

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
        long taskId = intent.getLongExtra("TASK_ID", 0L);

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        if (taskId > 0) {
            taskViewModel.getTaskById(taskId).observe(this, task -> {
                if (task != null) {
                    textViewTaskName.setText(task.getName());
                    textViewTaskStatus.setText(task.getStatus());
                    textViewTaskDescription.setText(task.getDescription());
                }
            });
        }
    }
}