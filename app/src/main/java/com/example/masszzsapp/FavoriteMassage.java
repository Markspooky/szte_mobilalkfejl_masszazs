package com.example.masszzsapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoriteMassage {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String massageName;

    public FavoriteMassage(String massageName) {
        this.massageName = massageName;
    }
}