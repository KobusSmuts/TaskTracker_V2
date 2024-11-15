package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private Button btnCreateTask, btnHome;
    private TaskAdapter taskAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // Initialize views
        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        btnHome = findViewById(R.id.btnBackToTask);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        // Setup click listener
        taskAdapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(TaskListActivity.this, TaskDetailsActivity.class);
            intent.putExtra("TASK_ID", task.getTaskID());
            startActivity(intent);
        });

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Observe tasks
        taskViewModel.getAllTasks().observe(this, tasks -> {
            taskAdapter.submitList(tasks);
        });

        // Set click listeners
        btnCreateTask.setOnClickListener(v -> {
            Intent intent = new Intent(TaskListActivity.this, CreateTaskActivity.class);
            startActivity(intent);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(TaskListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
}