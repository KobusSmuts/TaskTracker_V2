package com.example.tasktracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreateTaskActivity extends AppCompatActivity {
    private EditText etTaskTitle, etTaskDescription;
    private Button btnAddTask;
    private FirebaseDatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnAddTask = findViewById(R.id.btnAddTask);
        databaseService = new FirebaseDatabaseService();

        btnAddTask.setOnClickListener(view -> {
            String title = etTaskTitle.getText().toString();
            String description = etTaskDescription.getText().toString();
            Task task = new Task(title, description, "Not Started");
            databaseService.addTask(task);
            finish();
        });
    }
}

