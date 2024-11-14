package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private FirebaseAuthService authService;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = AuthManager.getCurrentUser();
        Intent intent;
        if (user != null) {
            // User is logged in, continue with app flow
            intent = new Intent(LoginActivity.this, MainActivity.class);
        } else {
            // Redirect to login screen
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        authService = new FirebaseAuthService();

        btnLogin.setOnClickListener(view -> {
            String email = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            authService.loginUser(email, password, task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
