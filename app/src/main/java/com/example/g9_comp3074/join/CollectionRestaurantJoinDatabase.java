package com.example.g9_comp3074.join;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = { CollectionRestaurantJoin.class },
        version = 1,
        exportSchema = false
)
public abstract class CollectionRestaurantJoinDatabase extends RoomDatabase {

    private static volatile CollectionRestaurantJoinDatabase INSTANCE;

    public abstract CollectionRestaurantJoinDao collectionRestaurantJoinDao();

    public static CollectionRestaurantJoinDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CollectionRestaurantJoinDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CollectionRestaurantJoinDatabase.class,
                            "collection_restaurant_join_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
