package com.example.tasktracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Button btnCreateTask = findViewById(R.id.btnCreateTask);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        // Handle task clicks
        taskAdapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(TaskListActivity.this, TaskDetailsActivity.class);
            intent.putExtra("TASK_ID", task.getUID()); // Pass task ID
            startActivity(intent);
        });

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Fetch tasks filtered by the current user (e.g., based on UID or email)
        taskViewModel.getAllTasks().observe(this, tasks -> {
            taskAdapter.submitList(tasks); // Update adapter with task list
        });

        // Handle Back Button
        findViewById(R.id.btnBackToTask).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, 1);
        });

        // handel add button
        btnCreateTask.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateTaskActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            taskViewModel.refreshTasks(); // Refresh tasks if returning from CreateTaskActivity
        }
    }
}
