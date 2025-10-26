package com.example.g9_comp3074;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Restaurant.class}, version = 1, exportSchema = false)
public abstract class RestaurantDatabase extends RoomDatabase {

    public abstract RestaurantDao restaurantDao();

    private static RestaurantDatabase INSTANCE;

    public static synchronized RestaurantDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RestaurantDatabase.class, "restaurant_database")
                    .allowMainThreadQueries() // optional, for testing only; use AsyncTask or coroutines for real apps
                    .build();
        }
        return INSTANCE;
    }
}