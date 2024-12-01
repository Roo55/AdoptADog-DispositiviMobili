package com.example.adoptadog.api;

import com.example.adoptadog.models.DogResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiService {

    @GET("animals?type=dog")
    Call<DogResponse> getDogs(@Header("Authorization") String token);
}
