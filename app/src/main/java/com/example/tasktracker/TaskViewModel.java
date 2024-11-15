package com.example.tasktracker;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
    }

    public LiveData<List<Task>> getAllTasksFromRoom() {
        return taskRepository.getAllTasksFromRoom();
    }

    public LiveData<Task> getTaskById(long taskId) {
        return taskRepository.getTaskById(taskId);
    }
}