package com.example.tasktracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;

public class ManagerActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private FirebaseDatabaseService firebaseDatabaseService;

    private EditText taskNameInput;
    private Button addTaskButton, viewTasksButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // Initialize ViewModel and Firebase service
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        firebaseDatabaseService = new FirebaseDatabaseService();

        // Bind UI elements
        taskNameInput = findViewById(R.id.task_name_input);
        addTaskButton = findViewById(R.id.add_task_button);
        viewTasksButton = findViewById(R.id.view_tasks_button);

        // Set up click listeners
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        viewTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, TaskListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addTask() {
        String taskName = taskNameInput.getText().toString().trim();
        if (!taskName.isEmpty()) {
            String taskId = UUID.randomUUID().toString(); // Generate unique ID
            Task newTask = new Task(taskId, taskName, "Pending");

            // Save the task to Firebase and Room database
            firebaseDatabaseService.addTask(newTask);
            taskViewModel.insertTask(newTask); // Save to local Room database

            // Clear input field
            taskNameInput.setText("");
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show();
        }
    }
}


