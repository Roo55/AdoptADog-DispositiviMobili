package com.example.adoptadog.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.adoptadog.api.ApiClient;
import com.example.adoptadog.database.DogDAO;
import com.example.adoptadog.database.DogDatabase;
import com.example.adoptadog.models.Dog;

import java.util.List;

public class DogRepository {

    private final DogDAO dogDAO;
    private final MutableLiveData<String> errorLiveData;

    public DogRepository(Application application) {
        dogDAO = DogDatabase.getInstance(application).dogDAO(); // Acceso a la base de datos
        errorLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Dog>> getDogs() {
        return dogDAO.getAllDogs(); // Recupera los perros desde la base de datos
    }

    // Fetch dogs from API and insert them into the database
    public void fetchDogsFromApi() {
        ApiClient.fetchDogsFromApi(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(List<Dog> dogs) {
                // Inserta los perros en la base de datos
                new Thread(() -> {
                    for (Dog dog : dogs) {
                        dogDAO.deleteDog(dog.getId());

                        dogDAO.insertDog(dog);
                        Log.d("DogRepository", "Dog inserted: " + dog.getName());
                    }
                }).start();
            }

            @Override
            public void onFailure(String error) {
                errorLiveData.postValue(error); // Actualiza el LiveData de error
            }
        });
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }
}
