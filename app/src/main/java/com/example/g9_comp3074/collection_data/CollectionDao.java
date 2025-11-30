package com.example.g9_comp3074.collection_data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CollectionDao {
    @Insert
    void insert(Collection collection);

    @Query("SELECT * FROM collection_table")
    List<Collection> getAllCollections();

    @Query("SELECT * FROM collection_table WHERE id = :id")
    Collection getCollectionById(int id);

    @Update
    void updateCollection(Collection collection);

    @Delete
    void delete(Collection collection);
}
