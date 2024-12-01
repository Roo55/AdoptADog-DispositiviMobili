package com.example.adoptadog.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.adoptadog.models.Dog;

import java.util.List;

@Dao
public interface DogDAO {
    @Insert
    void insert(Dog dog);

    @Query("SELECT * FROM dogs")
    List<Dog> getAllDogs();
}
