package com.example.adoptadog.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptadog.R;
import com.example.adoptadog.models.Dog;
import com.example.adoptadog.ui.details.DogDetailActivity;
import com.example.adoptadog.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView dogListRecyclerView;
    private DogAdapter dogAdapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dogListRecyclerView = findViewById(R.id.dogList);
        dogListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getDogs().observe(this, dogs -> {
            if (dogs != null) {
                // Set up the adapter with the retrieved dogs
                dogAdapter = new DogAdapter(dogs, MainActivity.this, this::onDogItemClick);
                dogListRecyclerView.setAdapter(dogAdapter);
            }
        });

        mainViewModel.getError().observe(this, error -> {
            if (error != null) {
                // Display a toast with the error message
                Toast.makeText(MainActivity.this, "Error fetching dogs: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch dogs from the API via the ViewModel
        mainViewModel.fetchDogsFromApi();
    }

    // Handle click event on dog item in RecyclerView
    private void onDogItemClick(Dog dog) {
        // Create an Intent to navigate to DogDetailActivity
        Intent intent = new Intent(MainActivity.this, DogDetailActivity.class);

        // Send dog data via Intent
        intent.putExtra("dogName", dog.getName());
        intent.putExtra("dogPersonality", dog.getPersonalityDescription());
        intent.putExtra("dogAge", dog.getAge());
        intent.putExtra("dogImage", dog.getImageUrl());
        intent.putExtra("dogGender", dog.getGender());

        // Start the DogDetailActivity
        startActivity(intent);
    }
}
