package com.example.adoptadog.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptadog.R;
import com.example.adoptadog.firebase.AuthManager;
import com.example.adoptadog.models.Dog;
import com.example.adoptadog.ui.auth.LoginActivity;
import com.example.adoptadog.ui.details.DogDetailActivity;
import com.example.adoptadog.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView dogListRecyclerView;
    private DogAdapter dogAdapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si el usuario está autenticado antes de continuar
        if (AuthManager.getInstance().getCurrentUser() == null) {
            // Si no está autenticado, redirigir al login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Asegúrate de que no se quede en la actividad actual
            return; // Detener el flujo de la actividad actual
        }

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
        // Verifica que el dogId sea válido
        if (dog.getId() != -1) {
            Log.d("MainActivity", "Selected Dog ID: " + dog.getId());

            Intent intent = new Intent(MainActivity.this, DogDetailActivity.class);

            // Pasar datos del perro a través del Intent
            intent.putExtra("dogId", dog.getId());
            intent.putExtra("dogName", dog.getName());
            intent.putExtra("dogPersonality", dog.getPersonalityDescription());
            intent.putExtra("dogAge", dog.getAge());
            intent.putExtra("dogImage", dog.getImageUrl());
            intent.putExtra("dogGender", dog.getGender());

            // Iniciar la actividad DogDetailActivity
            startActivity(intent);
        } else {
            Log.e("MainActivity", "Invalid dogId: " + dog.getId());
            Toast.makeText(MainActivity.this, "Error: Invalid Dog ID", Toast.LENGTH_SHORT).show();
        }
    }
}
