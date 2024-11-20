package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnSave, btnBack;

    private FirebaseDatabaseService firebaseDatabaseService;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        firebaseDatabaseService = new FirebaseDatabaseService();
        userRepository = new UserRepository(getApplication());

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Set up save button click listener
        btnSave.setOnClickListener(view -> saveUser());
        btnBack.setOnClickListener(view -> startActivity(new Intent(this, UserListActivity.class)));
    }

    private void saveUser() {
        String email = etEmail.getText().toString().trim();

        Log.d("AddUserActivity", "saveUser() -> email: " + email);

        // Validate input
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

//        if (userRepository.getUserByEmail(email) != null) {
//            // User already exists
//            Toast.makeText(this, "User with this email already added", Toast.LENGTH_SHORT).show();
//        } else {
            firebaseDatabaseService.getUserByEmail(email, user -> {
                if (user != null) {
                    // Insert into local database
                    userRepository = new UserRepository(getApplication());
                    userRepository.insert(user);

                    Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // User already exists
                    Toast.makeText(this, "Error finding user with this email.", Toast.LENGTH_SHORT).show();
                }
            });
//        }
    }
}