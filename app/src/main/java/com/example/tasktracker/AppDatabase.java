package com.example.tasktracker;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import android.content.Context;

@Database(entities = {Task.class}, version = 2, exportSchema = false)  // Incremented version
public abstract class AppDatabase extends RoomDatabase {

    // Abstract method to get DAO instance
    public abstract TaskDao taskDao();

    // Singleton instance of the database
    private static AppDatabase instance;

    /**
     * Returns the singleton instance of the AppDatabase.
     * @param context Application context.
     * @return AppDatabase instance.
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "task_tracker_db")
                    .build();
        }
        return instance;
    }

}
