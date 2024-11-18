package com.example.tasktracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class TaskDao {

    @Transaction
    public void syncTasks(List<Task> tasks) {
        deleteAll();
        insertAll(tasks);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<Task> tasks);

    @Query("UPDATE tasks SET status = :status, name = :name, description = :description WHERE taskID = :taskId")
    public abstract void update(int status, String name, String description, String taskId);

    @Delete
    public abstract void delete(Task task);

    @Query("DELETE FROM tasks")
    public abstract void deleteAll();

    @Query("SELECT COUNT(*) FROM tasks")
    public abstract int getTaskCount();

    @Query("SELECT * FROM tasks")
    public abstract LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE (uid = :uid OR employeeEmail = :employeeEmail) AND name = :name")
    public abstract Task getTaskById(String uid, String employeeEmail, String name);

    @Query("SELECT * FROM tasks WHERE taskID = :taskId")
    public abstract LiveData<Task> observeTaskById(String taskId);
}