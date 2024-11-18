package com.example.tasktracker;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskRepository {
    private TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private static final int THREAD_POOL_SIZE = 4;
    private final ExecutorService repositoryExecutor;
    private final Handler mainHandler;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks(); // Fetch tasks from Room (local DB)
        repositoryExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    // Get all tasks from Room (local DB)
    public LiveData<List<Task>> getAllTasksFromRoom() {
        return allTasks; // Returns tasks from the local Room database
    }

    // Get task by ID from Room (local DB)
    public LiveData<Task> getTaskById(String taskID) {
        LiveData<Task> taskLiveData = null;
        try {
            // Only fetch from local Room database, no online sync
            taskLiveData = taskDao.observeTaskById(taskID);
        } catch (Exception e) {
            Log.e("TaskRepository", "getTaskById() -> Error getting task by ID", e);
        }
        return taskLiveData;
    }

    // Insert a new task into Room (local DB)
    public void insert(Task task) {
        repositoryExecutor.execute(() -> {
            try {
                long id = taskDao.insert(task);  // Insert the task into the local DB
                task.setTaskID(id);  // Set the generated ID back to the task
            } catch (Exception e) {
                Log.e("TaskRepository", "Error inserting task", e);
            }
        });
    }

    // Insert a list of tasks into Room (local DB)
    public void insertTasks(List<Task> tasks) {
        repositoryExecutor.execute(() -> {
            try {
                List<Long> ids = taskDao.insertAll(tasks);  // Insert tasks into the local DB
                // Update tasks with their generated IDs if needed
                for (int i = 0; i < tasks.size(); i++) {
                    tasks.get(i).setTaskID(ids.get(i));
                }
            } catch (Exception e) {
                Log.e("TaskRepository", "Error inserting tasks", e);
            }
        });
    }

    // Update an existing task in Room (local DB)
    public void update(Task task) {
        repositoryExecutor.execute(() -> taskDao.update(task)); // Update the task in the local DB
    }

    // Delete a task from Room (local DB)
    public void delete(Task task) {
        repositoryExecutor.execute(() -> taskDao.delete(task)); // Delete the task from the local DB
    }

    // Delete a task asynchronously from Room (local DB)
    public void deleteTaskAsync(Task task, Runnable onComplete) {
        repositoryExecutor.execute(() -> {
            try {
                taskDao.delete(task); // Delete task from local DB
            } catch (Exception e) {
                Log.e("TaskRepository", "Error deleting task", e);
            } finally {
                mainHandler.post(onComplete); // Notify completion
            }
        });
    }

    // Delete all tasks from Room (local DB)
    public void deleteAllTasks() {
        repositoryExecutor.execute(taskDao::deleteAll); // Delete all tasks from the local DB
    }

    // Cleanup repository resources
    public void cleanup() {
        repositoryExecutor.shutdown();
        try {
            if (!repositoryExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                repositoryExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            repositoryExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public interface OnTaskRetrievedCallback {
        void onTaskRetrieved(Task task);
    }
}
