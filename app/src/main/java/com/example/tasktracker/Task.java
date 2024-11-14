package com.example.tasktracker;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity (tableName = "tasks")
public class Task {
    @PrimaryKey
    @NonNull
    private String uID;
    @NonNull
    private String employeeEmail;
    private String name;
    private String description;
    private String status;

    // No-argument constructor required by Firebase
    public Task() {
        employeeEmail = "";
        uID = "";
    }

    // Constructor with parameters
    public Task(@NonNull String uID, @NonNull String employeeEmail, String name, String description, String status) {
        this.uID = uID;
        this.employeeEmail = employeeEmail;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // Getters and setters

    @NonNull
    public String getuID() {
        return uID;
    }

    public void setuID(@NonNull String uID) {
        this.uID = uID;
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
}

