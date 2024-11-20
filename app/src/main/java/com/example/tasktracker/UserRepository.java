package com.example.tasktracker;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private TaskDao taskDao;
    private final LiveData<List<User>> allUsers;
    private static final int THREAD_POOL_SIZE = 4;
    private final ExecutorService repositoryExecutor;
    private final Handler mainHandler;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        taskDao = db.taskDao();
        allUsers = userDao.getAllUsers();
        repositoryExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public void insert(User user) {
        repositoryExecutor.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        repositoryExecutor.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        repositoryExecutor.execute(() -> userDao.delete(user));
    }

    public LiveData<List<Task>> getTasksForUser(String email) {
        return taskDao.getTasksByEmployeeEmail(email);
    }
}