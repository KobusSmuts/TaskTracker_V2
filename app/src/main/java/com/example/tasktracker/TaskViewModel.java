package com.example.tasktracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;
    private LiveData<List<Task>> allTasks;

    // Constructor
    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);

        // Assuming TaskRepository is already filtering tasks based on UID and email
        allTasks = taskRepository.getAllTasks();  // Get all tasks from the repository
    }

    // Method to get all tasks for the current user (UID or email)
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    // Insert a new task into the repository
    public void insertTask(Task task) {
        taskRepository.insert(task);
    }

    // Update an existing task
    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    // Delete a specific task
    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    // Delete all tasks
    public void deleteAllTasks() {
        taskRepository.deleteAllTasks();
    }

    // Refresh tasks (re-query or reload data)
    public void refreshTasks() {
        // Re-fetch tasks by calling the repository method to get data again
        allTasks = taskRepository.getAllTasks();
    }
}
