package com.example.tasktracker;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

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
                taskDao.insertAll(taskList);
                syncStatus.setValue(true);
            }

            @Override
            public void onFailure(Exception e) {
                syncStatus.setValue(false);
            }
        });
    }

    public void syncRoomToFirebase() {
        LiveData<List<Task>> localTasks = taskDao.getAllTasks();
        localTasks.observeForever(tasks -> {
            for (Task task : tasks) {
                firebaseDatabaseService.addTask(task);
            }
        });
    }
}
