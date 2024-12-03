package com.example.adoptadog.ui.main;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptadog.R;
import com.example.adoptadog.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView dogListRecyclerView;
    private DogAdapter dogAdapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        dogListRecyclerView = findViewById(R.id.dogList);
        dogListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Observe the dogs LiveData from the ViewModel
        mainViewModel.getDogs().observe(this, dogs -> {
            if (dogs != null) {
                // Set up the adapter with the retrieved dogs
                dogAdapter = new DogAdapter(dogs, MainActivity.this);
                dogListRecyclerView.setAdapter(dogAdapter);
            }
        });

        // Observe the error LiveData from the ViewModel
        mainViewModel.getError().observe(this, error -> {
            if (error != null) {
                // Display a toast with the error message
                Toast.makeText(MainActivity.this, "Error fetching dogs: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch dogs from the API via the ViewModel
        mainViewModel.fetchDogsFromApi();
    }
}
