package com.example.adoptadog.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.adoptadog.models.Dog;

@Database(entities = {Dog.class}, version = 1)
public abstract class DogDatabase extends RoomDatabase {
    public abstract DogDAO dogDao();
}
