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

    // Obtener todos los perros
    @Query("SELECT * FROM dogs")
    LiveData<List<Dog>> getAllDogs();

    // Obtener perros favoritos
    @Query("SELECT * FROM dogs WHERE isFavorite = 1")
    List<Dog> getFavoriteDogs();

    // Obtener un perro por ID
    @Query("SELECT * FROM dogs WHERE id = :dogId")
    Dog getDogById(int dogId);

    // Actualizar un perro
    @Update
    void updateDog(Dog dog);

    // Alternar estado de favorito
    @Query("UPDATE dogs SET isFavorite = :isFavorite WHERE id = :dogId")
    void updateFavoriteStatus(int dogId, boolean isFavorite);

    // Insertar perros

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDog(Dog dog);

    // Eliminar un perro (opcional)
    @Query("DELETE FROM dogs WHERE id = :dogId")
    void deleteDog(int dogId);


    @Query("SELECT COUNT(*) FROM dogs WHERE id = :dogId")
    int checkIfDogExists(int dogId); // Verifica si el perro ya existe
}
