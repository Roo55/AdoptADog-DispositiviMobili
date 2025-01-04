package com.example.adoptadog.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptadog.R;
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
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("AdoptADog");
        }

        // Configurar RecyclerView
        dogListRecyclerView = findViewById(R.id.dogList);
        dogListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar ViewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getDogs().observe(this, dogs -> {
            if (dogs != null) {
                dogAdapter = new DogAdapter(dogs, this, this::onDogItemClick);
                dogListRecyclerView.setAdapter(dogAdapter);
            }
        });

        mainViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(MainActivity.this, "Error fetching dogs: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        mainViewModel.fetchDogsFromApi();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true; // Inflar el menú
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        MenuItem loginItem = menu.findItem(R.id.action_login);
        MenuItem logoutItem = menu.findItem(R.id.action_logout);

        if (isLoggedIn) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
        } else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_login) {
            // Si el usuario selecciona "Login", ir a LoginActivity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            return true;
        }

        if (item.getItemId() == R.id.action_logout) {
            // Si el usuario selecciona "Logout", realizar el logout
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false); // Cambiar estado a no logueado
            editor.apply();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Forzar la actualización del menú
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item); // Delegar el manejo de otros ítems
    }




    private void onDogItemClick(Dog dog) {
        if (dog.getId() != -1) {
            Intent intent = new Intent(MainActivity.this, DogDetailActivity.class);
            intent.putExtra("dogId", dog.getId());
            intent.putExtra("dogName", dog.getName());
            intent.putExtra("dogPersonality", dog.getPersonalityDescription());
            intent.putExtra("dogAge", dog.getAge());
            intent.putExtra("dogImage", dog.getImageUrl());
            intent.putExtra("dogGender", dog.getGender());
            intent.putExtra("dogSterilized",dog.getSterilized());
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Error: Invalid Dog ID", Toast.LENGTH_SHORT).show();
        }
    }
}
