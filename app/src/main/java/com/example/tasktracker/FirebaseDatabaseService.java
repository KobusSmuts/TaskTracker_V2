package com.example.tasktracker;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseService {
    private FirebaseDatabase database;
    FirebaseAuthService firebaseAuthService;

    public FirebaseDatabaseService() {
        database = FirebaseDatabase.getInstance("https://tasktracker-67e10-default-rtdb.europe-west1.firebasedatabase.app");
    }

    // Add task to Firebase
    public void addTask(Task task) {
        DatabaseReference tasksRef = database.getReference("tasks");
        tasksRef.push().setValue(task);
    }

    // Update task status
    public void updateTaskStatus(String taskId, String status) {
        DatabaseReference tasksRef = database.getReference("tasks");
        tasksRef.child(taskId).child("status").setValue(status);
    }

    // Get all tasks
    public void getAllTasks( final FirebaseTasksCallback callback) {
        DatabaseReference tasksRef = database.getReference("tasks");
        FirebaseUser currentUser = firebaseAuthService.getCurrentUser();
        String currentUserUid = currentUser.getUid();
        String currentUserEmail = currentUser.getEmail();


        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null &&
                            (currentUserUid.equals(task.getUID()) || currentUserEmail.equals(task.getEmployeeEmail()))) {
                        taskList.add(task);
                    }
                }
                callback.onCallback(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public interface FirebaseTasksCallback {
        void onCallback(List<Task> taskList);
        void onFailure(Exception e);
    }
}
