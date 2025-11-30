package com.example.g9_comp3074.collection_data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Collection.class}, version = 1, exportSchema = false)
public abstract class CollectionDatabase extends RoomDatabase {

    public abstract CollectionDao collectionDao();

    private static com.example.g9_comp3074.collection_data.CollectionDatabase INSTANCE;

    public static synchronized com.example.g9_comp3074.collection_data.CollectionDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            com.example.g9_comp3074.collection_data.CollectionDatabase.class, "collection_database")
                    .allowMainThreadQueries() // optional, for testing only; use AsyncTask or coroutines for real apps
                    .build();
        }
        return INSTANCE;
    }
}