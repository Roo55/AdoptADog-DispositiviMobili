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
        dogDAO = DogDatabase.getInstance(application).dogDAO();
        errorLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Dog>> getDogs() {
        return dogDAO.getAllDogs();
    }

    public void fetchDogsFromApi() {
        ApiClient.fetchDogsFromApi(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(List<Dog> dogs) {
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
                errorLiveData.postValue(error);
            }
        });
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }
}
