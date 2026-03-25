package com.example.masszzsapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE massageName = :name LIMIT 1")
    FavoriteMassage getByName(String name);

    @Query("SELECT * FROM favorites")
    List<FavoriteMassage> getAll();

    @Insert
    void insert(FavoriteMassage fav);

    @Delete
    void delete(FavoriteMassage fav);
}