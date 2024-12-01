package com.example.adoptadog.api;

import com.example.adoptadog.models.Dog;
import com.example.adoptadog.models.DogResponse;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiClient {

    private static final String BASE_URL = "https://api.petfinder.com/v2/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void fetchDogsFromApi(String token, final ApiCallback callback) {
        ApiService apiService = getClient().create(ApiService.class);

        Call<DogResponse> call = apiService.getDogs("Bearer " + token);

        call.enqueue(new Callback<DogResponse>() {
            @Override
            public void onResponse(Call<DogResponse> call, Response<DogResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getAnimals());
                } else {
                    callback.onFailure("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DogResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface ApiCallback {
        void onSuccess(List<Dog> dogs);
        void onFailure(String error);
    }
}
