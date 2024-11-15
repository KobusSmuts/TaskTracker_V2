package com.example.tasktracker;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

@Database(entities = {Task.class}, version = 3, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "task_tracker_db")
                    // No migrations are added here
                    .fallbackToDestructiveMigration()  // This will recreate the database if needed (you can remove this if you don't want it)
                    .build();
        }
        return instance;
    }
}