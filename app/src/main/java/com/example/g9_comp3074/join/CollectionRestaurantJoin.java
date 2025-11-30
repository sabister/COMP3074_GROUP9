package com.example.g9_comp3074.join;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "collection_restaurant_join")
public class CollectionRestaurantJoin {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int collectionId;
    public int restaurantId;

    public CollectionRestaurantJoin(int collectionId, int restaurantId) {
        this.collectionId = collectionId;
        this.restaurantId = restaurantId;
    }
}
