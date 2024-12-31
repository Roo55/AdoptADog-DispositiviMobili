package com.example.adoptadog.ui.main;

import android.content.Intent;
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

        // Configurar el título de la barra de herramientas
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Adopt a Dog");
        }

        // Configuración del RecyclerView
        dogListRecyclerView = findViewById(R.id.dogList);
        dogListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar el ViewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Observar cambios en la lista de perros
        mainViewModel.getDogs().observe(this, dogs -> {
            if (dogs != null) {
                dogAdapter = new DogAdapter(dogs, this, this::onDogItemClick);
                dogListRecyclerView.setAdapter(dogAdapter);
            }
        });

        // Observar errores
        mainViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(MainActivity.this, "Error fetching dogs: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Llamar a la API para obtener los perros
        mainViewModel.fetchDogsFromApi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú con el botón de login
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneja los clics en los elementos del menú
        if (item.getItemId() == R.id.action_login) {
            // Navegar a la pantalla de login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Error: Invalid Dog ID", Toast.LENGTH_SHORT).show();
        }
    }
}
