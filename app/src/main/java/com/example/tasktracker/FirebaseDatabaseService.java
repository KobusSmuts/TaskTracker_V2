package com.example.tasktracker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseService {
    private DatabaseReference database;

    public FirebaseDatabaseService() {
        database = FirebaseDatabase.getInstance().getReference("tasks");
    }

    // Add task to Firebase
    public void addTask(Task task) {
        database.child(task.getId()).setValue(task);
    }

    // Update task status
    public void updateTaskStatus(String taskId, String status) {
        database.child(taskId).child("status").setValue(status);
    }

    // Get all tasks
    public void getAllTasks(final FirebaseTasksCallback callback) {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                callback.onCallback(taskList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public interface FirebaseTasksCallback {
        void onCallback(List<Task> taskList);
        void onFailure(Exception e);
    }
}
