package com.example.tasktracker;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity (tableName = "tasks")
public class Task {
    @PrimaryKey
    @NonNull
    private String UID;
    @NonNull
    private String employeeEmail;
    private String name;
    private String description;
    private String status;

    // No-argument constructor required by Firebase
    public Task() {
    }

    // Constructor with parameters
    public Task(@NonNull String UID, @NonNull String employeeEmail, String name, String description, String status) {
        this.UID = UID;
        this.employeeEmail = employeeEmail;
        this.name = name;
        this.description = description;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Implement equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Check for reference equality
        if (o == null || getClass() != o.getClass()) return false; // Check if objects are of the same type
        Task task = (Task) o;
        return UID.equals(task.UID) &&
                employeeEmail.equals(task.employeeEmail) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UID, employeeEmail, name, description, status);
    }
}

