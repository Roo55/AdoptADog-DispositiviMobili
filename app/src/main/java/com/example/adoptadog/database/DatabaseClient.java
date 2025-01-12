package com.example.adoptadog.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static DogDatabase instance;

    public static synchronized DogDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            DogDatabase.class, "dog-database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
