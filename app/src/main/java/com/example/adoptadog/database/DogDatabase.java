package com.example.adoptadog.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.adoptadog.models.Dog;
import com.example.adoptadog.models.AdoptionForm;  // Asegúrate de que esta clase esté importada

@Database(entities = {Dog.class, AdoptionForm.class}, version = 4)
public abstract class DogDatabase extends RoomDatabase {

    private static volatile DogDatabase INSTANCE; //
    private static final String DB_NAME = "dog_database"; //

    public abstract DogDAO dogDAO();
    public abstract AdoptionDAO adoptionDAO();

    public static DogDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DogDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DogDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
