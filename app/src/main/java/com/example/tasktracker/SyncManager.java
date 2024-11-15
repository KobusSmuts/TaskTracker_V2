package com.example.tasktracker;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SyncManager {
    private final FirebaseDatabaseService firebaseDatabaseService;
    private final TaskDao taskDao;
    private final ConnectivityChecker connectivityChecker;
    private final MutableLiveData<Boolean> syncStatus = new MutableLiveData<>();
    private final MutableLiveData<String> syncError = new MutableLiveData<>();
    private final ExecutorService syncExecutor = Executors.newSingleThreadExecutor();

    public SyncManager(Context context, TaskDao taskDao) {
        this.firebaseDatabaseService = new FirebaseDatabaseService();
        this.taskDao = taskDao;
        this.connectivityChecker = new ConnectivityChecker(context);
    }

    public LiveData<Boolean> getSyncStatus() {
        return syncStatus;
    }

    public LiveData<String> getSyncError() {
        return syncError;
    }

    public void syncTasks() {
        if (connectivityChecker.isConnected()) {
            syncExecutor.execute(this::syncFirebaseToRoom);
        } else {
            syncStatus.postValue(false);
            syncError.postValue("No internet connection.");
        }
    }

    private void syncFirebaseToRoom() {
        Log.d("SyncManager", "syncFirebaseToRoom() entered");
        firebaseDatabaseService.getAllTasks(new FirebaseDatabaseService.FirebaseTasksCallback() {
            @Override
            public void onCallback(List<Task> taskList) {
                if (taskList != null && !taskList.isEmpty()) {
                    Log.d("SyncManager", "syncFirebaseToRoom: " + taskList.size() + " tasks found");
                    if (!taskList.isEmpty()) {
                        syncExecutor.execute(() -> {
                            taskDao.deleteAll(); // Clear existing data before inserting new data
                            try {
                                taskDao.insertAll(taskList); // Optimize for batch inserts if needed
                                syncStatus.postValue(true);
                            } catch (Exception e) {
                                Log.e("SyncManager", "Error inserting tasks", e);
                                syncStatus.postValue(false);
                                syncError.postValue("Database error: " + e.getMessage());
                            }
                        });
                    }
                } else {
                    syncStatus.postValue(false);
                    syncError.postValue("No tasks available to sync.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SyncManager", "Failed to sync tasks from Firebase", e);
                syncStatus.postValue(false);
                syncError.postValue("Sync failed: " + e.getMessage());
            }
        });
    }

    public void cleanup() {
        syncExecutor.shutdown();
        try {
            if (!syncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                syncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            syncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
