package com.example.tasktracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Task> tasks);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM tasks")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM tasks")
    int getTaskCount();

    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE (uid = :uid OR employeeEmail = :employeeEmail) AND name = :name")
    Task getTaskById(String uid, String employeeEmail, String name);

    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    LiveData<Task> observeTaskById(long taskId);
}