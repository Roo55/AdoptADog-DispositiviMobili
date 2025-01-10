package com.example.adoptadog.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    private ImageView userIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar Toolbar
        userIcon = findViewById(R.id.user_icon);

        // Revisar si el usuario está logueado
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Cambiar ícono según el estado de login
        if (isLoggedIn) {
            userIcon.setImageResource(R.drawable.person_icon);  // Ícono de persona
            userIcon.setOnClickListener(this::showUserMenu); // Habilitar el PopupMenu si está logueado
        } else {
            userIcon.setImageResource(R.drawable.ic_login);  // Ícono de login
            userIcon.setOnClickListener(v -> {
                // Si no está logueado, redirigir a la actividad de login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            });
        }

        // Configurar RecyclerView
        dogListRecyclerView = findViewById(R.id.dogList);
        dogListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    private void showUserMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view, 0, 0, R.style.CustomPopupMenu);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.user_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(this::onUserMenuItemClick);
        popupMenu.show();
    }

    private boolean onUserMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_my_account) {
            Toast.makeText(this, "My Account clicked", Toast.LENGTH_SHORT).show();
            // Implementar lógica más adelante
            return true;
        } else if (id == R.id.menu_log_out) {
            logOutUser();
            return true;
        }

        return false;
    }

    private void logOutUser() {
        AuthManager.getInstance().logOut();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Cambiar el ícono de usuario de nuevo a login
        userIcon.setImageResource(R.drawable.ic_login);
        userIcon.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    private void onDogItemClick(Dog dog) {
        if (dog.getId() != -1) {
            Intent intent = new Intent(MainActivity.this, DogDetailActivity.class);
            intent.putExtra("dog", dog);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Error: Invalid Dog ID", Toast.LENGTH_SHORT).show();
        }
    }
}
