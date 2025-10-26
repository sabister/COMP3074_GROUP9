package com.example.g9_comp3074;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Restaurant.class}, version = 1)
public abstract class RestaurantDatabase extends RoomDatabase {
    public abstract RestaurantDao restaurantDao();
}