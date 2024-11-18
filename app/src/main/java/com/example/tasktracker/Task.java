package com.example.tasktracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Objects;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey
    @NonNull
    private String taskID;

    @NonNull
    private String UID;

    @NonNull
    private String employeeEmail;

    private String name;
    private String description;
    @ColumnInfo(defaultValue = "1")
    private int status;

    // No-argument constructor required by Firebase
    public Task(String uid, String employeeEmail, String title, String description, int i) {
    }

    // Constructor with parameters
    public Task(@NonNull String taskID, @NonNull String UID, @NonNull String employeeEmail, String name, String description, int status) {
        this.taskID = taskID;
        this.UID = UID;
        this.employeeEmail = employeeEmail;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @NonNull
    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(@NonNull String taskID) {
        this.taskID = taskID;
    }

    // Getters and setters
    @NonNull
    public String getUID() {
        return UID;
    }

    public void setUID(@NonNull String UID) {
        this.UID = UID;
    }

    @NonNull
    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(@NonNull String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return UID.equals(task.UID) &&
                employeeEmail.equals(task.employeeEmail) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskID, UID, employeeEmail, name, description, status);
    }
}