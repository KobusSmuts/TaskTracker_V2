package com.example.tasktracker;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;
    private static final int THREAD_POOL_SIZE = 4;
    private final ExecutorService repositoryExecutor;
    private final Handler mainHandler;

    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
        repositoryExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        repositoryExecutor.execute(() -> taskDao.insert(task));
    }

    public void insertTasks(List<Task> tasks) {
        repositoryExecutor.execute(() -> taskDao.insertAll(tasks));
    }

    public void update(Task task) {
        repositoryExecutor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        repositoryExecutor.execute(() -> taskDao.delete(task));
    }

    public void deleteTaskAsync(Task task, Runnable onComplete) {
        repositoryExecutor.execute(() -> {
            taskDao.delete(task);
            mainHandler.post(onComplete);
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
}