package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CreateTaskActivity extends AppCompatActivity {
    private EditText etTaskTitle, etTaskDescription, etEmployeeEmail;
    private Button btnAddTask, btnViewTask, btnHome;
    private FirebaseDatabaseService databaseService;
    private TaskRepository localDatabase;
    private final ExecutorService taskCreationExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        initializeViews();
        setupTaskCreation();

        btnHome.setOnClickListener(view -> {
            Intent intent = new Intent(CreateTaskActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnViewTask.setOnClickListener(view -> {
            Intent intent = new Intent(CreateTaskActivity.this, TaskListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initializeViews() {
        etEmployeeEmail = findViewById(R.id.etEmployeeEmail);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnViewTask = findViewById(R.id.btnViewTasks);
        btnHome = findViewById(R.id.btnHome);
        databaseService = new FirebaseDatabaseService();
        localDatabase = new TaskRepository(getApplication());
    }

    private void setupTaskCreation() {
        FirebaseUser user = AuthManager.getCurrentUser();
        if (user != null) {
            btnAddTask.setOnClickListener(view -> createTaskAsync(user));
        } else {
            btnAddTask.setEnabled(false);
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void createTaskAsync(FirebaseUser user) {
        String employeeEmail = etEmployeeEmail.getText().toString().trim();
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();

        if (validateInput(employeeEmail, title, description)) {
            taskCreationExecutor.execute(() -> {
                Task task = new Task(user.getUid(), employeeEmail, title, description, 1);
                databaseService.addTask(task);
                mainHandler.post(() -> {
                    Toast.makeText(CreateTaskActivity.this,
                            "Task created successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                });
            });
        }
    }

    private boolean validateInput(String email, String title, String description) {
        if (email.isEmpty() || title.isEmpty() || description.isEmpty()) {
            mainHandler.post(() ->
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show());
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskCreationExecutor.shutdown();
        try {
            if (!taskCreationExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                taskCreationExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            taskCreationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}