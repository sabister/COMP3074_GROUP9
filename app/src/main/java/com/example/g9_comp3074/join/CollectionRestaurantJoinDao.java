package com.example.g9_comp3074.join;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CollectionRestaurantJoinDao {

    // Insert a single pair
    @Insert
    void insert(CollectionRestaurantJoin join);

    // Get all restaurant IDs for a specific collection
    @Query("SELECT restaurantId FROM collection_restaurant_join WHERE collectionId = :collectionId")
    List<Integer> getRestaurantIdsForCollection(int collectionId);

    // Remove one restaurant from a collection
    @Query("DELETE FROM collection_restaurant_join WHERE collectionId = :collectionId AND restaurantId = :restaurantId")
    void deletePair(int collectionId, int restaurantId);

    // Optional: delete all rows for a collection (used if deleting an entire collection)
    @Query("DELETE FROM collection_restaurant_join WHERE collectionId = :collectionId")
    void deleteAllForCollection(int collectionId);
}
