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
    private final ExecutorService syncExecutor = Executors.newSingleThreadExecutor();

    public SyncManager(Context context, TaskDao taskDao) {
        this.firebaseDatabaseService = new FirebaseDatabaseService();
        this.taskDao = taskDao;
        this.connectivityChecker = new ConnectivityChecker(context);
    }

    public LiveData<Boolean> getSyncStatus() {
        return syncStatus;
    }

    public void syncTasks() {
        if (connectivityChecker.isConnected()) {
            syncExecutor.execute(this::syncFirebaseToRoom);
        } else {
            syncStatus.postValue(false);
        }
    }

    private void syncFirebaseToRoom() {
        firebaseDatabaseService.getAllTasks(new FirebaseDatabaseService.FirebaseTasksCallback() {
            @Override
            public void onCallback(List<Task> taskList) {
                if (taskList != null && !taskList.isEmpty()) {
                    syncExecutor.execute(() -> {
                        try {
                            taskDao.insertAll(taskList);
                            syncStatus.postValue(true);
                        } catch (Exception e) {
                            Log.e("SyncManager", "Error inserting tasks", e);
                            syncStatus.postValue(false);
                        }
                    });
                } else {
                    syncStatus.postValue(false);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SyncManager", "Failed to sync tasks from Firebase", e);
                syncStatus.postValue(false);
            }
        });
    }

    public void cleanup() {
        syncExecutor.shutdown();
        try {
            if (!syncExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                syncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            syncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}