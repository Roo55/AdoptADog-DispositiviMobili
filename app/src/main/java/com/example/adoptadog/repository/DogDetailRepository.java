package com.example.adoptadog.repository;

import android.app.Application;

import com.example.adoptadog.models.Dog;

import java.util.Arrays;
import java.util.List;

public class DogDetailRepository {

    public DogDetailRepository(Application application) {
    }

    public List<String> getMockTraits() {
        return Arrays.asList(
                "Euphoric", "Friendly", "Timid", "Destructive",
                "Loyal", "Playful", "Independent", "Energetic",
                "Obedient", "Protective", "Affectionate", "Gentle",
                "Clever", "Quiet", "Curious", "Sociable",
                "Alert", "Lazy", "Cuddly", "Adventurous"
        );
    }

    public String getSterilizedStatus(Dog dog) {
        return dog.getSterilized() == 1 ? "YES" : "NO";
    }
}
