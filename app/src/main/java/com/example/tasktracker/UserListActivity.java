package com.example.tasktracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Set;

public class UserListActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private UserAdapter userAdapter;
    private FirebaseDatabaseService databaseService;
    private Button btnAddUser, btnHome;
    private Button btnRemoveUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        databaseService = new FirebaseDatabaseService();

        RecyclerView recyclerView = findViewById(R.id.rvUsers);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnRemoveUsers = findViewById(R.id.btnRemoveUsers);
        btnHome = findViewById(R.id.btnHome);
        btnRemoveUsers.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        recyclerView.setAdapter(userAdapter);

        // User click listener to navigate to task list
        userAdapter.setOnUserClickListener(user -> {
            Intent intent = new Intent(this, TaskListActivity.class);
            intent.putExtra("USER_EMAIL", user.getUserEmail());
            startActivity(intent);
            finish();
        });

        // Selection change listener
        userAdapter.setOnSelectionChangedListener(selectedCount -> {
            btnAddUser.setVisibility(selectedCount > 0 ? View.GONE : View.VISIBLE);
            btnRemoveUsers.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
        });

        // Remove users button
        btnRemoveUsers.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Remove Selected Users")
                    .setMessage("Are you sure you want to remove the selected users?")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        Set<User> selectedUsers = userAdapter.getSelectedUsers();
                        int selectedCount = selectedUsers.size();
                        for (User user : selectedUsers) {
                            userViewModel.delete(user);
                            databaseService.deleteUser(user.getUID());
                        }
                        userAdapter.exitSelectionMode();
                        Toast.makeText(this, selectedCount + " users deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // ViewModel and user list setup
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, users -> {
            if (users == null || users.isEmpty()) {
                Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
            }
            userAdapter.submitList(users);
        });

        btnAddUser.setOnClickListener(v -> {
            startActivity(new Intent(this, AddUserActivity.class));
            finish();
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}