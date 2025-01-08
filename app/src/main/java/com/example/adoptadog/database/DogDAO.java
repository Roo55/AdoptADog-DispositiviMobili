package com.example.adoptadog.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.adoptadog.models.Dog;

import java.util.List;

@Dao
public interface DogDAO {

    @Query("SELECT * FROM dogs")
    LiveData<List<Dog>> getAllDogs();

    @Query("SELECT * FROM dogs WHERE isFavorite = 1")
    List<Dog> getFavoriteDogs();

    @Query("SELECT * FROM dogs WHERE id = :dogId")
    Dog getDogById(int dogId);

    @Update
    void updateDog(Dog dog);

    @Query("UPDATE dogs SET isFavorite = :isFavorite WHERE id = :dogId")
    void updateFavoriteStatus(int dogId, boolean isFavorite);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDog(Dog dog);

    @Query("DELETE FROM dogs WHERE id = :dogId")
    void deleteDog(int dogId);


    @Query("SELECT COUNT(*) FROM dogs WHERE id = :dogId")
    int checkIfDogExists(int dogId);
}
