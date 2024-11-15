package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private Button btnCreateTask, btnHome;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        // Setup click listener for task item
        taskAdapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(TaskListActivity.this, TaskDetailsActivity.class);
            intent.putExtra("TASK_ID", task.getTaskID());
            startActivity(intent);
        });

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Observe tasks from the ViewModel
        taskViewModel.getAllTasksFromRoom().observe(this, tasks -> {
            if (tasks == null || tasks.isEmpty()) {
                Toast.makeText(TaskListActivity.this, "No tasks found", Toast.LENGTH_SHORT).show();
            } else {
                taskAdapter.submitList(tasks);
            }
        });

        // Set click listeners for buttons
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
