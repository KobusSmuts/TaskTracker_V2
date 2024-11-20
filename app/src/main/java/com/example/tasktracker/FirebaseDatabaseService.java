package com.example.tasktracker;

import androidx.annotation.NonNull;
import android.util.Log;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FirebaseDatabaseService {
    private final FirebaseDatabase database;
    private final FirebaseAuthService firebaseAuthService;
    private final ExecutorService firebaseExecutor;
    private static final String TAG = "FirebaseDatabaseService";

    public FirebaseDatabaseService() {
        database = FirebaseDatabase.getInstance("https://tasktracker-67e10-default-rtdb.europe-west1.firebasedatabase.app");
        firebaseAuthService = new FirebaseAuthService();
        firebaseExecutor = Executors.newFixedThreadPool(2);
    }

    public void addUser(User user) {
        firebaseExecutor.execute(() -> {
            try {
                DatabaseReference tasksRef = database.getReference("users");
                tasksRef.push().setValue(user)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "User added successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error adding user", e));
            } catch (Exception e) {
                Log.e(TAG, "Error in addUser", e);
            }
        });
    }

    public void addTask(Task task) {
        firebaseExecutor.execute(() -> {
            try {
                DatabaseReference tasksRef = database.getReference("tasks");
                tasksRef.push().setValue(task)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Task added successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error adding task", e));
            } catch (Exception e) {
                Log.e(TAG, "Error in addTask", e);
            }
        });
    }

    public void updateTaskStatus(String uniqueTaskID, Task task) {
        firebaseExecutor.execute(() -> {
            try {
                DatabaseReference tasksRef = database.getReference("tasks");
                tasksRef.child(uniqueTaskID).setValue(task)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Status updated successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error updating status", e));
            } catch (Exception e) {
                Log.e(TAG, "Error in updateTaskStatus", e);
            }
        });
    }

    public void getAllTasks(final FirebaseTasksCallback callback) {
        DatabaseReference tasksRef = database.getReference("tasks");
        FirebaseUser currentUser = firebaseAuthService.getCurrentUser();

        if (currentUser == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        String currentUserUid = currentUser.getUid();
        String currentUserEmail = currentUser.getEmail();

        firebaseExecutor.execute(() -> {
            tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Task> taskList = new ArrayList<>();
                    for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                        Task task = taskSnapshot.getValue(Task.class);
                        if (task != null && (currentUserUid.equals(task.getUID()) ||
                                currentUserEmail.equals(task.getEmployeeEmail()))) {
                            task.setUniqueId(taskSnapshot.getKey());
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
        });
    }

    public void deleteTask(String uniqueTaskID) {
        firebaseExecutor.execute(() -> {
            try {
                DatabaseReference tasksRef = database.getReference("tasks");
                tasksRef.child(uniqueTaskID).removeValue()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Task deleted successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error deleting task", e));
            } catch (Exception e) {
                Log.e(TAG, "Error in updateTaskStatus", e);
            }
        });
    }

    public void cleanup() {
        firebaseExecutor.shutdown();
        try {
            if (!firebaseExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                firebaseExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            firebaseExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public interface FirebaseTasksCallback {
        void onCallback(List<Task> taskList);
        void onFailure(Exception e);
    }
}