package com.example.adoptadog.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.adoptadog.R;
import com.example.adoptadog.database.DatabaseClient;
import com.example.adoptadog.database.DogDAO;
import com.example.adoptadog.models.Dog;
import com.example.adoptadog.repository.DogRepository;
import com.google.android.material.button.MaterialButton;

public class DogDetailActivity extends AppCompatActivity {

    private ImageView ivDogImage;
    private TextView tvDogName, tvDogPersonality, tvDogAge;
    private ImageView ivGenderIcon;
    private MaterialButton btnFavorite, btnAdopt;
    private DogDAO dogDao;
    private Dog dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_detail);

        // Inicializar el DAO usando DatabaseClient
        dogDao = DatabaseClient.getInstance(getApplicationContext()).dogDAO();

        // Inicializar vistas
        ImageView btnBack = findViewById(R.id.btnBack);
        ivDogImage = findViewById(R.id.ivDogImage);
        tvDogName = findViewById(R.id.tvDogName);
        tvDogPersonality = findViewById(R.id.tvDogPersonality);
        tvDogAge = findViewById(R.id.tvDogAge);
        ivGenderIcon = findViewById(R.id.ivGenderIcon);
        btnFavorite = findViewById(R.id.btnFavorite);
        Log.d("DogDetailActivity", "Button Favorite initialized");
        btnAdopt = findViewById(R.id.btnAdopt);

        btnFavorite.setEnabled(false);

        DogRepository dogRepository = new DogRepository(getApplication());
        dogRepository.fetchDogsFromApi();


        // Manejar el botón de retroceso
        btnBack.setOnClickListener(v -> finish());

        // Obtener datos del Intent
        Intent intent = getIntent();
        int dogId = intent.getIntExtra("dogId", -1);

        if (dogId == -1) {
            Log.e("DogDetailActivity", "No valid dogId received!");
            finish(); // Cierra la actividad si no hay un ID válido
            return;
        }

        // Recuperar la información del perro desde la base de datos
        new Thread(() -> {
            Log.d("DogDetailActivity", "Attempting to fetch dog with ID: " + dogId);
            dog = dogDao.getDogById(dogId);
            if (dog == null) {
                Log.e("DogDetailActivity", "No dog found with ID: " + dogId);
            }
            runOnUiThread(() -> {
                if (dog != null) {
                    populateDogDetails(dog);
                    btnFavorite.setEnabled(true);
                } else {
                    Log.w("DogDetailActivity", "No dog found in the database, populating from Intent");
                    populateDogDetailsFromIntent(intent);
                    btnFavorite.setEnabled(true);
                }
            });
        }).start();



        btnAdopt.setOnClickListener(v -> {
            // Lógica para marcar como adoptado (puedes implementarla si es necesario)
        });
    }

    /**
     * Rellena los detalles del perro en la UI.
     */
    private void populateDogDetails(Dog dog) {
        Log.d("DogDetailActivity", "Populating details for Dog ID: " + dog.getId());
        tvDogName.setText(dog.getName());
        tvDogPersonality.setText(cleanHtmlTags(dog.getPersonalityDescription()));
        tvDogAge.setText(formatAge(dog.getAge()));

        Glide.with(this)
                .load(dog.getImageUrl())
                .into(ivDogImage);

        if ("macho".equals(dog.getGender())) {
            ivGenderIcon.setImageResource(R.drawable.ic_macho);
        } else if ("hembra".equals(dog.getGender())) {
            ivGenderIcon.setImageResource(R.drawable.ic_hembra);
        }

        // Configurar el estado inicial del botón de favoritos
        btnFavorite.setIconResource(dog.isFavorite() ? R.drawable.ic_favorite_border_filled : R.drawable.ic_favorite_border);
    }

    /**
     * Rellena los detalles del perro en la UI usando datos del Intent.
     */
    private void populateDogDetailsFromIntent(Intent intent) {
        Log.w("DogDetailActivity", "Populating details from Intent as a fallback.");
        tvDogName.setText(intent.getStringExtra("dogName"));
        tvDogPersonality.setText(cleanHtmlTags(intent.getStringExtra("dogPersonality")));
        tvDogAge.setText(formatAge(intent.getStringExtra("dogAge")));

        Glide.with(this)
                .load(intent.getStringExtra("dogImage"))
                .into(ivDogImage);

        String dogGender = intent.getStringExtra("dogGender");
        if ("macho".equals(dogGender)) {
            ivGenderIcon.setImageResource(R.drawable.ic_macho);
        } else if ("hembra".equals(dogGender)) {
            ivGenderIcon.setImageResource(R.drawable.ic_hembra);
        }

        // Botón de favorito por defecto (no respaldado por la base de datos)
        btnFavorite.setIconResource(R.drawable.ic_favorite_border);
    }

    /**
     * Cambia el estado de favorito del perro y actualiza la base de datos.
     */
    private void toggleFavorite() {
        new Thread(() -> {
            boolean newFavoriteStatus = !dog.isFavorite();

            Log.d("DogDetailActivity", "Previous favorite status: " + dog.isFavorite());
            Log.d("DogDetailActivity", "New favorite status: " + newFavoriteStatus);

            dog.setFavorite(newFavoriteStatus);

            // Actualiza la base de datos
            dogDao.updateDog(dog);

            // Actualiza la imagen en la UI
            runOnUiThread(() -> {
                // Cambia la imagen en el botón de favorito
                if (newFavoriteStatus) {
                    btnFavorite.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_favorite_border_filled)); // Corazón lleno
                    Log.d("DogDetailActivity", "Set icon to filled heart");
                } else {
                    btnFavorite.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_favorite_border)); // Corazón vacío
                    Log.d("DogDetailActivity", "Set icon to empty heart");
                }

                // Actualiza los detalles del perro para reflejar el estado de favorito en la UI
                populateDogDetails(dog);
            });
        }).start();
    }


    private String cleanHtmlTags(String text) {
        if (text == null) return "";
        return text.replaceAll("<p>", "").replaceAll("</p>", "").trim();
    }

    private String formatAge(String age) {
        if (age == null) return "";
        if (age.contains("Meses")) {
            return age.replace("Meses", "Months");
        } else if (age.contains("Año")) {
            return age.replace("Año", "Year");
        } else if (age.contains("Años")) {
            return age.replace("Años", "Years");
        }
        return age;
    }
}