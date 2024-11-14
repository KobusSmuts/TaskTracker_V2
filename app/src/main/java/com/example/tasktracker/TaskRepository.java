package com.example.tasktracker;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao; // Assuming you use Room or similar DB
    private LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application); // Database initialization
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();  // Get all tasks from the database
    }

    // Get tasks filtered by UID or email for the current user
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(task));
    }

    public void deleteAllTasks() {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.deleteAll());
    }
}
