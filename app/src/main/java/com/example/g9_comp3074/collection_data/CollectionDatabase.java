package com.example.g9_comp3074.collection_data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Collection.class}, version = 2, exportSchema = false)
public abstract class CollectionDatabase extends RoomDatabase {

    public abstract CollectionDao collectionDao();

    private static volatile CollectionDatabase INSTANCE;

    public static CollectionDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CollectionDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CollectionDatabase.class, "collection_database")
                            // --- FIX 2: Remove main thread queries to prevent ANRs ---
                            // .allowMainThreadQueries() // This causes app freezes and should be removed.

                            // --- FIX 3: Add a migration strategy ---
                            .fallbackToDestructiveMigration() // Easiest way to handle schema changes during development
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}