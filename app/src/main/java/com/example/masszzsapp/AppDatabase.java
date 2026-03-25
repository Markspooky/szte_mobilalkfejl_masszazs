package com.example.masszzsapp;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {FavoriteMassage.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoriteDao favoriteDao();
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "massage_db").allowMainThreadQueries().build();
        }
        return instance;
    }
}