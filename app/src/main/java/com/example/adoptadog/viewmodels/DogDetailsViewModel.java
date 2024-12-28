package com.example.adoptadog.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.adoptadog.database.DatabaseClient;
import com.example.adoptadog.database.DogDAO;
import com.example.adoptadog.models.Dog;

public class DogDetailsViewModel extends AndroidViewModel {
    private final DogDAO dogDao;
    private final MutableLiveData<Dog> currentDog;

    public DogDetailsViewModel(Application application) {
        super(application);
        dogDao = DatabaseClient.getInstance(application).dogDAO();
        currentDog = new MutableLiveData<>();
    }

    public LiveData<Dog> getCurrentDog() {
        return currentDog;
    }

    public void loadDogById(int dogId) {
        new Thread(() -> {
            Dog dog = dogDao.getDogById(dogId); // Buscar perro por ID
            if (dog != null) {
                currentDog.postValue(dog); // Actualizar LiveData en el hilo principal
            }
        }).start();
    }

    public void toggleFavorite(int dogId) {
        new Thread(() -> {
            Dog dog = dogDao.getDogById(dogId);
            if (dog != null) {
                dog.setFavorite(!dog.isFavorite());
                dogDao.updateDog(dog); // Actualizar en la base de datos
            }
        }).start();
    }
}
