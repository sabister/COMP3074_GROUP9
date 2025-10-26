package com.example.g9_comp3074;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RestaurantDao {

    @Insert
    void insert(Restaurant restaurant);

    @Query("SELECT * FROM restaurant_table")
    List<Restaurant> getAllRestaurants();

    @Query("SELECT * FROM restaurant_table WHERE id = :id")
    Restaurant getRestaurantById(int id);

    @Delete
    void delete(Restaurant restaurant);
}