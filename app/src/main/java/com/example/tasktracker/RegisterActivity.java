package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etCompanyName, etUsername, etPassword;
    private Button btnRegister;
    private FirebaseAuthService authService;

    public void onStart() {
        super.onStart();
        //Check if user already signed in
        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etCompanyName = findViewById(R.id.etCompanyName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        authService = new FirebaseAuthService();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                authService.registerUser(email, password, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

