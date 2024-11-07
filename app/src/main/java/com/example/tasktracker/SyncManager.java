package com.example.tasktracker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executors;

public class SyncManager {
    private FirebaseDatabaseService firebaseDatabaseService;
    private TaskDao taskDao;
    private ConnectivityChecker connectivityChecker;
    private MutableLiveData<Boolean> syncStatus = new MutableLiveData<>();

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
            syncFirebaseToRoom();
        } else {
            syncStatus.setValue(false);
        }
    }

    private void syncFirebaseToRoom() {
        firebaseDatabaseService.getAllTasks(new FirebaseDatabaseService.FirebaseTasksCallback() {
            @Override
            public void onCallback(List<Task> taskList) {
                // Check if the taskList is not empty
                if (taskList != null && !taskList.isEmpty()) {
                    // Use ExecutorService to run database operations in a background thread
                    Executors.newSingleThreadExecutor().execute(() -> {
                        taskDao.insertAll(taskList); // Insert tasks from Firebase to Room
                        syncStatus.postValue(true); // Update sync status
                    });
                } else {
                    syncStatus.postValue(false); // No tasks found to sync
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("SyncManager", "Failed to sync tasks from Firebase", e);
                syncStatus.postValue(false); // Handle failure
            }
        });
    }

//    public void syncRoomToFirebase() {
//        LiveData<List<Task>> localTasks = taskDao.getAllTasks();
//        localTasks.observeForever(tasks -> {
//            for (Task task : tasks) {
//                firebaseDatabaseService.addTask(task);
//            }
//        });
//    }
}
