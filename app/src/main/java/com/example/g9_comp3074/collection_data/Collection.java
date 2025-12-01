package com.example.g9_comp3074.collection_data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Collection_table")
public class Collection {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
    public String tags;

    public Collection(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public Collection() {}

}
