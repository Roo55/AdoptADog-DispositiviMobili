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
import com.example.adoptadog.models.Dog;
import com.example.adoptadog.repository.DogRepository;
import com.google.android.material.button.MaterialButton;
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
        int dogId = intent.getIntExtra("dogId", -1);

        if (dogId == -1) {
            Log.e("DogDetailActivity", "No valid dogId received!");
            finish();
            return;
        }

        new Thread(() -> {
            Log.d("DogDetailActivity", "Attempting to fetch dog with ID: " + dogId);
            dog = dogDao.getDogById(dogId);
            runOnUiThread(() -> {
                if (dog != null) {
                    populateDogDetails(dog);
                    btnFavorite.setEnabled(true);
                } else {
                    populateDogDetailsFromIntent(intent);
                    btnFavorite.setEnabled(true);
                }
            });
        }).start();

        btnAdopt.setOnClickListener(v -> {
            // PENDING
        });
    }

    /**
     * ML Kit.
     */
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
        tvDogAge.setText(formatAge(dog.getAge()));

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
        } else{
            tvSterilizedStatus.setText("NO");
            tvSterilizedStatus.setBackgroundResource(R.drawable.sterilized_status_no);
        }



        translateText(cleanHtmlTags(dog.getPersonalityDescription()), translatedText -> tvDogPersonality.setText(translatedText));
        btnFavorite.setIconResource(dog.isFavorite() ? R.drawable.ic_favorite_border_filled : R.drawable.ic_favorite_border);

        Log.d("DogDetailActivity", "Sterilized value from database: " + dog.getSterilized());
        updateSterilizedStatus(dog.getSterilized());
    }

    /**
     * Fill UI with Data from the Intent.
     */
    private void populateDogDetailsFromIntent(Intent intent) {
        tvDogName.setText(intent.getStringExtra("dogName"));
        tvDogAge.setText(formatAge(intent.getStringExtra("dogAge")));

        Glide.with(this)
                .load(intent.getStringExtra("dogImage"))
                .into(ivDogImage);

        String dogGender = intent.getStringExtra("dogGender");
        if ("macho".equalsIgnoreCase(dogGender)) {
            ivGenderIcon.setImageResource(R.drawable.ic_macho);
        } else if ("hembra".equalsIgnoreCase(dogGender)) {
            ivGenderIcon.setImageResource(R.drawable.ic_hembra);
        }

        int sterilized = intent.getIntExtra("dogSterilized", 0);
        updateSterilizedStatus(sterilized);

        translateText(cleanHtmlTags(intent.getStringExtra("dogPersonality")), translatedText -> tvDogPersonality.setText(translatedText));
        btnFavorite.setIconResource(R.drawable.ic_favorite_border);

    }

    /**
     * Actualiza el estado de esterilización en la UI.
     */
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

    /**
     *     ML Kit.
     */
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
