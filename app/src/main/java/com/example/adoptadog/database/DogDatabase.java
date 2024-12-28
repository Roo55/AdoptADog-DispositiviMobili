package com.example.adoptadog.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.adoptadog.models.Dog;
@Database(entities = {Dog.class}, version = 1)
public abstract class DogDatabase extends RoomDatabase {

    private static volatile DogDatabase INSTANCE; // Variable para almacenar la instancia
    private static final String DB_NAME = "dog_database"; // Nombre de la base de datos

    public abstract DogDAO dogDAO();

    // Método estático para obtener la instancia de la base de datos
    public static DogDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DogDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DogDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration() // Borra la base de datos y la crea de nuevo si hay un cambio en el esquema
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Método opcional para destruir la instancia de la base de datos manualmente si lo necesitas
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
