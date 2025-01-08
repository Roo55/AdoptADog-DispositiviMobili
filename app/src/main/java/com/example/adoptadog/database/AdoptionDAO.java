package com.example.adoptadog.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.adoptadog.models.AdoptionForm;

import java.util.List;

@Dao
public interface AdoptionDAO {

    @Insert
    void insert(AdoptionForm adoptionForm);

    @Query("SELECT * FROM adoption_forms")
    List<AdoptionForm> getAllAdoptionForms();

    @Query("SELECT * FROM adoption_forms WHERE id = :id")
    AdoptionForm getAdoptionFormById(int id);

    @Update
    void update(AdoptionForm adoptionForm);

    @Query("DELETE FROM adoption_forms WHERE id = :id")
    void deleteById(int id);
}
