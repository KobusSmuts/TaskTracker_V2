package com.example.tasktracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.rpc.context.AttributeContext;

public class CreateTaskActivity extends AppCompatActivity {
    private EditText etTaskTitle, etTaskDescription;
    private Button btnAddTask;
    private FirebaseDatabaseService databaseService;

    private FirebaseAuthService firebaseAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        etTaskTitle = findViewById(R.id.employeeEmail);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnAddTask = findViewById(R.id.btnAddTask);
        databaseService = new FirebaseDatabaseService();

        btnAddTask.setOnClickListener(view -> {
            FirebaseUser user = firebaseAuthService.getCurrentUser();
            String uid = user.getUid();
            String title = etTaskTitle.getText().toString();
            String description = etTaskDescription.getText().toString();
            Task task = new Task(uid, employeeEmail, title, description, "Not Started");
            databaseService.addTask(task);
            finish();
        });
    }
}

