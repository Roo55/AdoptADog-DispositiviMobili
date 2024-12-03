package com.example.adoptadog.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.adoptadog.api.ApiClient;
import com.example.adoptadog.models.Dog;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Make these final as they will not be reassigned
    private final MutableLiveData<List<Dog>> dogsLiveData;
    private final MutableLiveData<String> errorLiveData;

    public MainViewModel(Application application) {
        super(application);
        // Initialize MutableLiveData in the constructor
        dogsLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }

    // Method to get LiveData of the dog list
    public LiveData<List<Dog>> getDogs() {
        return dogsLiveData;
    }

    // Method to get LiveData of the error message
    public LiveData<String> getError() {
        return errorLiveData;
    }

    // Fetch dogs from the API
    public void fetchDogsFromApi() {
        ApiClient.fetchDogsFromApi(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(List<Dog> dogs) {
                List<Dog> limitedDogs = dogs.size() > 10 ? dogs.subList(0, 10) : dogs;

                dogsLiveData.postValue(limitedDogs);

            }

            @Override
            public void onFailure(String error) {
                errorLiveData.postValue(error); // Update LiveData with the error message
            }
        });
    }
}
