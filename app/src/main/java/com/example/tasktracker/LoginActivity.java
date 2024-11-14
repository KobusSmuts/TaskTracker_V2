package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etUsername, etPassword;
    private FirebaseAuthService authService;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        authService = new FirebaseAuthService();

        // Initialize ExecutorService and Handler
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        btnLogin.setOnClickListener(view -> {
            String email = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            Log.d(TAG, "btnLogin clicked: email=" + email);

            executorService.execute(() -> {
                authService.loginUser(email, password, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "loginUser: success");
                        mainThreadHandler.post(() -> {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        Log.d(TAG, "loginUser: failed", task.getException());
                        mainThreadHandler.post(() ->
                                Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show());
                    }
                });
            });
        });

        btnRegister.setOnClickListener(view -> {
            Log.d(TAG, "btnRegister clicked");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");

        executorService.execute(() -> {
            FirebaseUser user = AuthManager.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onStart: User is logged in");
                mainThreadHandler.post(() -> {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                Log.d(TAG, "onStart: No user logged in");
                // Don't restart LoginActivity here, just let it continue to show the login screen
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
