package com.example.adoptadog.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DogResponse {
    @SerializedName("data")
    private List<Dog> dogs;

    // Getter and Setter
    public List<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(List<Dog> dog) {
        this.dogs = dogs;
    }
}
