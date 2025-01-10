package com.example.adoptadog.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.adoptadog.R;
import com.example.adoptadog.database.DatabaseClient;
import com.example.adoptadog.database.DogDAO;
import com.example.adoptadog.firebase.AuthManager;
import com.example.adoptadog.models.Dog;
import com.example.adoptadog.repository.DogRepository;
import com.example.adoptadog.ui.adoption.AdoptionFormActivity;
import com.example.adoptadog.ui.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class DogDetailActivity extends AppCompatActivity {

    private ImageView ivDogImage;
    private TextView tvDogName, tvDogPersonality, tvDogAge, tvSterilizedStatus;
    private ImageView ivGenderIcon;
    private MaterialButton btnFavorite, btnAdopt;
    private DogDAO dogDao;
    private Dog dog;
    private Translator translator;
    private boolean isDogLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_detail);

        dogDao = DatabaseClient.getInstance(getApplicationContext()).dogDAO();

        ImageView btnBack = findViewById(R.id.btnBack);
        ivDogImage = findViewById(R.id.ivDogImage);
        tvDogName = findViewById(R.id.tvDogName);
        tvDogPersonality = findViewById(R.id.tvDogPersonality);
        tvDogAge = findViewById(R.id.tvDogAge);
        ivGenderIcon = findViewById(R.id.ivGenderIcon);
        tvSterilizedStatus = findViewById(R.id.tvSterilizedStatus);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnAdopt = findViewById(R.id.btnAdopt);

        btnFavorite.setEnabled(false);

        setupTranslator();

        DogRepository dogRepository = new DogRepository(getApplication());
        dogRepository.fetchDogsFromApi();

        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        dog = (Dog) intent.getSerializableExtra("dog");

        if (dog == null) {
            Log.e("DogDetailActivity", "Dog object is null in the intent!");
            finish();
            return;
        }

        populateDogDetails(dog);
        btnFavorite.setEnabled(true);
        isDogLoaded = true;

        setupAdoptButtonClickListener();
    }


    private void setupAdoptButtonClickListener() {
        btnAdopt.setOnClickListener(v -> {
            if (!isUserLoggedIn()) {
                Snackbar.make(v, "You must be logged in to adopt a dog.", Snackbar.LENGTH_LONG).show();

                Intent loginIntent = new Intent(DogDetailActivity.this, LoginActivity.class);
                loginIntent.putExtra("redirectReason", "You must be logged in to fill out the adoption form!");
                startActivity(loginIntent);

            } else if (!isDogLoaded) {
                Snackbar.make(v, "Please wait, dog data is still loading.", Snackbar.LENGTH_LONG).show();
            } else {
                if (dog != null) {
                    // Aquí estamos pasando el dogId al Intent
                    Intent adoptIntent = new Intent(DogDetailActivity.this, AdoptionFormActivity.class);
                    adoptIntent.putExtra("dogId", dog.getId());  // Pasar el dogId aquí
                    startActivity(adoptIntent);
                } else {
                    Log.e("DogDetailActivity", "Dog object is null when attempting to adopt.");
                    Snackbar.make(v, "Error: Dog data not available.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }




    private boolean isUserLoggedIn() {
        return AuthManager.getInstance().getCurrentUser() != null;

    }

    private void setupTranslator() {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.SPANISH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();
        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> Log.d("MLKit", "Translation model downloaded"))
                .addOnFailureListener(e -> Log.e("MLKit", "Error downloading translation model: " + e.getMessage()));
    }

    private void populateDogDetails(Dog dog) {
        if (dog == null) {
            Log.e("DogDetailActivity", "Dog is null in populateDogDetails!");
            return;
        }

        tvDogName.setText(dog.getName());
        translateText(dog.getAge(), translatedAge -> tvDogAge.setText(translatedAge)); // Traducir la edad

        Glide.with(this)
                .load(dog.getImageUrl())
                .into(ivDogImage);

        if ("macho".equalsIgnoreCase(dog.getGender())) {
            ivGenderIcon.setImageResource(R.drawable.ic_macho);
        } else if ("hembra".equalsIgnoreCase(dog.getGender())) {
            ivGenderIcon.setImageResource(R.drawable.ic_hembra);
        }

        if (dog.getSterilized() == 1) {
            tvSterilizedStatus.setText("YES");
            tvSterilizedStatus.setBackgroundResource(R.drawable.sterilized_status_yes);
        } else {
            tvSterilizedStatus.setText("NO");
            tvSterilizedStatus.setBackgroundResource(R.drawable.sterilized_status_no);
        }

        translateText(cleanHtmlTags(dog.getPersonalityDescription()), translatedText -> tvDogPersonality.setText(translatedText));
        btnFavorite.setIconResource(dog.isFavorite() ? R.drawable.ic_favorite_border_filled : R.drawable.ic_favorite_border);

        Log.d("DogDetailActivity", "Sterilized value from database: " + dog.getSterilized());
        updateSterilizedStatus(dog.getSterilized());
    }

    private void updateSterilizedStatus(int sterilized) {
        if (sterilized == 1) {
            tvSterilizedStatus.setText("YES");
            tvSterilizedStatus.setBackgroundResource(R.drawable.sterilized_status_yes);
        } else {
            tvSterilizedStatus.setText("NO");
            tvSterilizedStatus.setBackgroundResource(R.drawable.sterilized_status_no);
        }
    }

    private String cleanHtmlTags(String text) {
        if (text == null) return "";
        return text.replaceAll("<p>", "").replaceAll("</p>", "").trim();
    }

    private void translateText(String text, OnTranslationCompleteListener listener) {
        if (text == null || text.isEmpty()) {
            listener.onTranslationComplete("");
            return;
        }

        translator.translate(text)
                .addOnSuccessListener(translatedText -> {
                    Log.d("MLKit", "Translation successful: " + translatedText);
                    listener.onTranslationComplete(translatedText);
                })
                .addOnFailureListener(e -> {
                    Log.e("MLKit", "Translation failed: " + e.getMessage());
                    listener.onTranslationComplete(text);
                });
    }

    interface OnTranslationCompleteListener {
        void onTranslationComplete(String translatedText);
    }
}
