package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class TaskDetailsActivity extends AppCompatActivity {
    private TextView textViewTaskName, textViewTaskDescription;
    private Spinner spnViewTaskStatus;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        // Initialize TextViews and Spinner
        textViewTaskName = findViewById(R.id.text_view_task_name);
        spnViewTaskStatus = findViewById(R.id.spinner_task_status);
        textViewTaskDescription = findViewById(R.id.text_view_task_description);

        // Set up spinner with task statuses
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnViewTaskStatus.setAdapter(adapter);

        // Get task ID passed from TaskListActivity
        Intent intent = getIntent();
        long taskId = intent.getLongExtra("TASK_ID", 0L);

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        if (taskId > 0) {
            taskViewModel.getTaskById(taskId).observe(this, task -> {
                if (task != null) {
                    textViewTaskName.setText(task.getName());
                    textViewTaskDescription.setText(task.getDescription());
                    // Set the spinner selection based on the task status
                    int statusPosition = adapter.getPosition(task.getStatus());
                    spnViewTaskStatus.setSelection(statusPosition);
                }
            });
        }
    }
}
