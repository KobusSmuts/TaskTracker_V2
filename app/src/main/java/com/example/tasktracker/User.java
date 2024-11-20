package com.example.tasktracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Objects;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String userEmail;
    @NonNull
    private String UID;
    private int role;

    // No-argument constructor required by Firebase
    public User() {
    }

    // Constructor with parameters
    public User(@NonNull String userEmail, @NonNull String UID, int role) {
        this.userEmail = userEmail;
        this.UID = UID;
        this.role = role;
    }

    @NonNull
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@NonNull String userEmail) {
        this.userEmail = userEmail;
    }

    @NonNull
    public String getUID() {
        return UID;
    }

    public void setUID(@NonNull String UID) {
        this.UID = UID;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return role == user.role && Objects.equals(userEmail, user.userEmail) && Objects.equals(UID, user.UID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmail, UID, role);
    }
}