package com.example.tasktracker;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
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
                    .addMigrations(MIGRATION_1_2) // Add migration to handle schema changes
                    .build();
        }
        return instance;
    }

    // Define migration strategy
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Example: Adding a new column to the 'task' table
            // Adjust based on your schema changes
            database.execSQL("ALTER TABLE task ADD COLUMN new_column INTEGER DEFAULT 0 NOT NULL");
        }
    };
}
