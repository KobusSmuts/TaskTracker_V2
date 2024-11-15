package com.example.tasktracker;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;
    private final SyncManager syncManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);

        if (application == null) {
            Log.e("SyncManager", "Application is null");
        }
        if (taskRepository.getTaskDao() == null) {
            Log.e("SyncManager", "TaskDao is null");
        }

        syncManager = new SyncManager(application, taskRepository.getTaskDao());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (syncManager != null) {
            syncManager.cleanup();
        }
        taskRepository.cleanup();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<Task>> getAllTasks() {
        LiveData<List<Task>> tasksFromRoom = getAllTasksFromRoom();
        if (tasksFromRoom.getValue() == null || tasksFromRoom.getValue().isEmpty()) {
            syncTasksFromFirebase();
        }
        return tasksFromRoom;
    }

    public LiveData<List<Task>> getAllTasksFromRoom() {
        return taskRepository.getAllTasksFromRoom();
    }

    private void syncTasksFromFirebase() {
        if (syncManager != null) {
            isLoading.setValue(true);
            syncManager.syncTasks();
            syncManager.getSyncStatus().observeForever(success -> {
                isLoading.setValue(false);
                if (!success) {
                    Log.e("TaskViewModel", "Failed to sync tasks from Firebase");
                }
            });
        } else {
            Log.e("TaskViewModel", "SyncManager not initialized");
        }
    }

    public LiveData<Task> getTaskById(long taskID) {
        LiveData<Task> taskFromRoom = taskRepository.getTaskById(taskID, getApplication());
        if (taskFromRoom.getValue() == null && syncManager != null) {
            syncTasksFromFirebase();
        }
        return taskFromRoom;
    }
}