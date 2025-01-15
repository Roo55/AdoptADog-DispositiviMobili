package com.example.adoptadog.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
import com.example.adoptadog.database.DatabaseClient;
import com.example.adoptadog.viewmodels.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView dogListRecyclerView;
    private DogAdapter dogAdapter;
    private MainViewModel mainViewModel;
    private Button userButton;
    private ImageView userIcon;
    private ImageView appLogo;
    private Spinner genderFilter;
    private Spinner ageFilter;
    private Button applyFilterButton;
    private FloatingActionButton fabFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userButton = findViewById(R.id.user_button);
        userIcon = findViewById(R.id.user_icon);
        dogListRecyclerView = findViewById(R.id.dogList);
        appLogo = findViewById(R.id.app_logo);
        genderFilter = findViewById(R.id.gender_filter);
        ageFilter = findViewById(R.id.age_filter);
        applyFilterButton = findViewById(R.id.apply_filter_button);
        fabFilter = findViewById(R.id.fab_filter);

        LinearLayout filterLayout = findViewById(R.id.filter_layout);
        filterLayout.setVisibility(View.GONE);

        updateUserIcon();

        dogListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getDogs().observe(this, dogs -> {
            if (dogs != null) {
                new Thread(() -> DatabaseClient.getInstance(getApplicationContext()).dogDAO()).start();
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

        applyFilterButton.setOnClickListener(v -> applyFilters());

        fabFilter.setOnClickListener(v -> toggleFilterVisibility());

        appLogo.setOnClickListener(v -> dogListRecyclerView.smoothScrollToPosition(0));
    }

    private void updateUserIcon() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            userIcon.setVisibility(View.VISIBLE);
            userButton.setVisibility(View.GONE);
            userIcon.setOnClickListener(this::showUserMenu);
        } else {
            userIcon.setVisibility(View.GONE);
            userButton.setVisibility(View.VISIBLE);
            userButton.setText("Login");
            userButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        }
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
            startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
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
        updateUserIcon();
    }

    private void applyFilters() {
        String selectedGender = genderFilter.getSelectedItem().toString();
        String selectedAge = ageFilter.getSelectedItem().toString();

        mainViewModel.getDogs().observe(this, dogs -> {
            if (dogs != null) {
                List<Dog> filteredDogs = new ArrayList<>(dogs);

                if (!selectedGender.equals("All")) {
                    String genderFilterValue = selectedGender.equals("Male") ? "macho" : "hembra";
                    filteredDogs.removeIf(dog -> !dog.getGender().equalsIgnoreCase(genderFilterValue));
                }

                if (!selectedAge.equals("All")) {
                    filteredDogs.removeIf(dog -> {
                        int dogAgeInMonths = parseAgeToMonths(dog.getAge());
                        switch (selectedAge) {
                            case "Less than 1 year":
                                return dogAgeInMonths >= 12;
                            case "Between 1 and 5 years":
                                return dogAgeInMonths < 12 || dogAgeInMonths > 60;
                            case "Over 5 years":
                                return dogAgeInMonths <= 60;
                        }
                        return false;
                    });
                }

                dogAdapter.updateDogs(filteredDogs);
            }
        });
    }

    private int parseAgeToMonths(String edad) {
        if (edad.contains("Mes")) {
            return Integer.parseInt(edad.split(" ")[0]);
        } else if (edad.contains("Año")) {
            return Integer.parseInt(edad.split(" ")[0]) * 12;
        }
        return 0;
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

    private void toggleFilterVisibility() {
        LinearLayout filterLayout = findViewById(R.id.filter_layout);
        if (filterLayout.getVisibility() == View.VISIBLE) {
            filterLayout.setVisibility(View.GONE);
        } else {
            filterLayout.setVisibility(View.VISIBLE); //
        }
    }
}
