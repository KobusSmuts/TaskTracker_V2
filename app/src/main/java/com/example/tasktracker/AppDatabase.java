package com.example.tasktracker;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Task.class, User.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    // Modified to return our TaskDao implementation
    public abstract TaskDao taskDao();
    public abstract UserDao userDao();


    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "task_tracker_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}