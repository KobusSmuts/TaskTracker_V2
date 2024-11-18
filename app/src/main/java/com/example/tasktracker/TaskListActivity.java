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

    private TaskDao taskDao;
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        Button btnCreateTask = findViewById(R.id.btnCreateTask);
        Button btnHome = findViewById(R.id.btnBackToTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(TaskListActivity.this, TaskDetailsActivity.class);
            intent.putExtra("TASK_UNIQUE_ID", task.getUniqueId());
            intent.putExtra("TASK_ID", task.getTaskID());
            startActivity(intent);
            finish();
        });

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasksFromRoom().observe(this, tasks -> {
            if (tasks == null || tasks.isEmpty()) {
                Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
            }taskAdapter.submitList(tasks);
        });



        btnCreateTask.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateTaskActivity.class));
            finish();
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}