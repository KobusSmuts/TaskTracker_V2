package com.example.tasktracker;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.google.firebase.auth.FirebaseUser;
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
        allTasks = taskDao.getAllTasks();
        repositoryExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public LiveData<List<Task>> getAllTasksFromRoom() {
        return allTasks;
    }

    public LiveData<Task> getTaskById(long taskID, Application application) {
        LiveData<Task> taskLiveData = null;
        try {
            FirebaseUser user = AuthManager.getCurrentUser();
            taskLiveData = taskDao.observeTaskById(taskID);
        } catch (Exception e) {
            Log.e("TaskRepository", "Error getting task by ID", e);
        }
        return taskLiveData;
    }

    public void insert(Task task) {
        repositoryExecutor.execute(() -> {
            try {
                long id = taskDao.insert(task);  // Capture the generated ID
                task.setTaskID(id);  // Set the generated ID back to the task
            } catch (Exception e) {
                Log.e("TaskRepository", "Error inserting task", e);
            }
        });
    }

    public void insertTasks(List<Task> tasks) {
        repositoryExecutor.execute(() -> {
            try {
                List<Long> ids = taskDao.insertAll(tasks);  // Modify TaskDao to return List<Long>
                // Update tasks with their generated IDs if needed
                for (int i = 0; i < tasks.size(); i++) {
                    tasks.get(i).setTaskID(ids.get(i));
                }
            } catch (Exception e) {
                Log.e("TaskRepository", "Error inserting tasks", e);
            }
        });
    }

    public void update(Task task) {
        repositoryExecutor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        repositoryExecutor.execute(() -> taskDao.delete(task));
    }

    public void deleteTaskAsync(Task task, Runnable onComplete) {
        repositoryExecutor.execute(() -> {
            try {
                taskDao.delete(task);
            } catch (Exception e) {
                Log.e("TaskRepository", "Error deleting task", e);
            } finally {
                mainHandler.post(onComplete);
            }
        });
    }

    public void deleteAllTasks() {
        repositoryExecutor.execute(taskDao::deleteAll);
    }

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