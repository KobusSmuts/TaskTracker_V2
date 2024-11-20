package com.example.tasktracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE = 100;
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

        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_CODE);
        } else {
            checkNetworkStatus();
        }

        btnLogin.setOnClickListener(view -> {
            String email = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

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
                                Toast.makeText(LoginActivity.this, "Login failed! Please enter valid credentials!", Toast.LENGTH_SHORT).show());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkNetworkStatus();
            } else {
                Toast.makeText(this, "Permission denied. Unable to check network status.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkNetworkStatus() {
        boolean online = NetworkUtil.isOnline(this);
        if (online) {
            Toast.makeText(this, "Connected to internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
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
