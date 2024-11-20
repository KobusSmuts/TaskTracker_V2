package com.example.tasktracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Spinner spnRole;
    private Button btnRegister, btnLogin;
    private FirebaseAuthService authService;
    private FirebaseDatabaseService databaseService;

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
        spnRole = findViewById(R.id.spinner_register);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        authService = new FirebaseAuthService();
        databaseService = new FirebaseDatabaseService();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.register_role_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnRole.setAdapter(adapter);

        btnRegister.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            int selectedRole = spnRole.getSelectedItemPosition();

            if (email.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                return;
            }
            authService.registerUser(email, password, task -> {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();
                    User user = new User();
                    user.setUserEmail(email);
                    user.setUID(uid);
                    user.setRole(selectedRole);

                    Log.d("RegisterActivity", "user email = " + email);
                    Log.d("RegisterActivity", "user selectedRole = " + selectedRole);
                    Log.d("RegisterActivity", "user UID = " + uid);

                    databaseService.addUser(user);

                    UserPreferences.saveUserRole(this, selectedRole);
                    UserPreferences.saveUserEmail(this, email);

                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            });


        });

        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

