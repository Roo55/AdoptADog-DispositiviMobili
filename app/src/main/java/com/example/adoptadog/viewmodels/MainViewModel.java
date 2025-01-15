package com.example.adoptadog.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.adoptadog.api.ApiClient;
import com.example.adoptadog.models.Dog;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Dog>> dogsLiveData;
    private final MutableLiveData<String> errorLiveData;

    public MainViewModel(Application application) {
        super(application);

        dogsLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Dog>> getDogs() {
        return dogsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void fetchDogsFromApi() {
        ApiClient.fetchDogsFromApi(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(List<Dog> dogs) {

                List<Dog> filteredDogs = new ArrayList<>();
                for (Dog dog : dogs) {
                    if (dog != null && !dog.getName().equalsIgnoreCase("Popeye") && !dog.getName().equalsIgnoreCase("Karey")) {
                        filteredDogs.add(dog);
                    }
                }

                List<Dog> limitedDogs = filteredDogs.size() > 20 ? filteredDogs.subList(0, 20) : filteredDogs;
                dogsLiveData.postValue(limitedDogs);
            }

            @Override
            public void onFailure(String error) {
                errorLiveData.postValue(error);
            }
        });
    }


}
