package com.example.g9_comp3074;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "restaurant_table")
public class Restaurant {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
    public String phone;
    public String hours;
    public String address;
    public String tags;

    public Restaurant(String name, String description, String phone, String hours, String address, String tags) {
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.hours = hours;
        this.address = address;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }
    public String getTags() {
        return tags;
    }
}