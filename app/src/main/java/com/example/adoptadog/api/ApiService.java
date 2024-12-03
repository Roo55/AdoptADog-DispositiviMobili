package com.example.adoptadog.api;

import com.example.adoptadog.models.Dog;
import com.example.adoptadog.models.DogResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    // Endpoint to fetch dogs by type (no token needed)
    @GET("api/animales/tipo/perro")
    Call<DogResponse> getDogs();
}
