package com.example.tasktracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkerActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private RecyclerView taskRecyclerView; // Changed to RecyclerView
    private TaskAdapter taskAdapter; // Adapter for displaying tasks
    private EditText taskStatusInput;
    private Button updateStatusButton;
    private FirebaseDatabaseService firebaseDatabaseService; // Firebase service reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        // Initialize ViewModel and Firebase service
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        firebaseDatabaseService = new FirebaseDatabaseService();

        // Bind UI components
        taskRecyclerView = findViewById(R.id.task_recycler_view); // Changed ID to match XML
        taskStatusInput = findViewById(R.id.task_status_input);
        updateStatusButton = findViewById(R.id.update_status_button);

        // Initialize adapter and set it to RecyclerView
        taskAdapter = new TaskAdapter(new TaskAdapter.TaskDiff());
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(taskAdapter);

        // Observe tasks from ViewModel
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskAdapter.submitList(tasks); // Update adapter with new task list
            }
        });

        // Set listener for the update status button
        updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTaskStatus();
            }
        });
    }

    private void updateTaskStatus() {
        // Add logic to get the selected task (for RecyclerView, you might need to handle item clicks in the adapter)
        int position = taskAdapter.getSelectedPosition(); // Implement a method to get selected position
        if (position != RecyclerView.NO_POSITION) {
            Task task = taskAdapter.getTaskAtPosition(position);
            String newStatus = taskStatusInput.getText().toString().trim();
            if (!newStatus.isEmpty()) {
                task.setStatus(newStatus);
                taskViewModel.updateTask(task); // Update task in Room
                firebaseDatabaseService.updateTaskStatus(task.getId(), newStatus); // Sync to Firebase
                taskStatusInput.setText(""); // Clear input
                Toast.makeText(this, "Task status updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a status", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select a task", Toast.LENGTH_SHORT).show();
        }
    }
}
