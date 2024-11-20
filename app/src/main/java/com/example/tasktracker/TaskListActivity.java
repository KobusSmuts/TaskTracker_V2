package com.example.tasktracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Set;

public class TaskListActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private FirebaseDatabaseService databaseService;
    private Button btnCreateTask, btnHome;
    private Button btnDeleteTasks;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        databaseService = new FirebaseDatabaseService();

        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        searchView = findViewById(R.id.searchView);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        btnDeleteTasks = findViewById(R.id.btnDeleteTasks);
        btnHome = findViewById(R.id.btnBackToTask);
        btnDeleteTasks.setVisibility(View.GONE);

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

        taskAdapter.setOnSelectionChangedListener(selectedCount -> {
            btnCreateTask.setVisibility(selectedCount > 0 ? View.GONE : View.VISIBLE);
            btnDeleteTasks.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
        });

        btnDeleteTasks.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Selected Tasks")
                    .setMessage("Are you sure you want to delete the selected tasks?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        Set<Task> selectedTasks = taskAdapter.getSelectedTasks();
                        int selectedCount = selectedTasks.size();
                        for (Task task : selectedTasks) {
                            taskViewModel.delete(task);
                            databaseService.deleteTask(task.getUniqueId());
                        }
                        taskAdapter.exitSelectionMode();
                        Toast.makeText(this, selectedCount + " tasks deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasksFromRoom().observe(this, tasks -> {
            if (tasks == null || tasks.isEmpty()) {
                Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
            }
            taskAdapter.submitFullList(tasks);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskAdapter.getFilter().filter(newText);
                return false;
            }
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
