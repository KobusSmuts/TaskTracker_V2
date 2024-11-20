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
    private static final String TAG = "SyncManager";
    private final FirebaseDatabaseService firebaseDatabaseService;
    private final TaskDao taskDao;
    private final ConnectivityChecker connectivityChecker;
    private final MutableLiveData<Boolean> syncStatus = new MutableLiveData<>();
    private final ExecutorService syncExecutor;

    public SyncManager(Context context, TaskDao taskDao) {
        this.firebaseDatabaseService = new FirebaseDatabaseService();
        this.taskDao = taskDao;
        this.connectivityChecker = new ConnectivityChecker(context);
        this.syncExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<Boolean> getSyncStatus() {
        return syncStatus;
    }

    public void syncTasks() {
        if (!connectivityChecker.isConnected()) {
            syncStatus.postValue(false);
            return;
        }

        syncExecutor.execute(() -> {
            try {
                firebaseDatabaseService.getAllTasks(new FirebaseDatabaseService.FirebaseTasksCallback() {
                    @Override
                    public void onCallback(List<Task> tasks) {
                        if (tasks != null && !tasks.isEmpty()) {
                            syncExecutor.execute(() -> {
                                try {
                                    taskDao.deleteAll();
                                    taskDao.insertAll(tasks);
                                    syncStatus.postValue(true);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error syncing tasks to local database", e);
                                    syncStatus.postValue(false);
                                }
                            });
                        } else {
                            syncStatus.postValue(true);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to fetch tasks from Firebase", e);
                        syncStatus.postValue(false);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error during sync operation", e);
                syncStatus.postValue(false);
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