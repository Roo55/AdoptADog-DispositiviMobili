package com.example.adoptadog.ui.details;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class DogDetailActivity extends AppCompatActivity {

    private ImageView ivDogImage;
    private TextView tvDogName, tvDogPersonality, tvDogAge, tvSterilizedStatus;
    private ImageView ivGenderIcon;
    private MaterialButton btnAdopt;
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
        btnAdopt = findViewById(R.id.btnAdopt);

        setupTranslator();

        DogRepository dogRepository = new DogRepository(getApplication());
        dogRepository.fetchDogsFromApi();

        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        dog = (Dog) intent.getSerializableExtra("dog");

        if (dog != null) {
            populateDogDetails(dog);
            isDogLoaded = true;
        }

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
                    Intent adoptIntent = new Intent(DogDetailActivity.this, AdoptionFormActivity.class);
                    adoptIntent.putExtra("dogId", dog.getId());
                    startActivity(adoptIntent);
                } else {
                    Snackbar.make(v, "Error: Dog data not available.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isUserLoggedIn() {
        return AuthManager.getInstance().getCurrentUser() != null;
    }

    private void setupTranslator() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.SPANISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        translator = Translation.getClient(options);
        translator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> {})
                .addOnFailureListener(e -> {});
    }

    private void populateDogDetails(Dog dog) {
        if (dog == null) {
            return;
        }

        tvDogName.setText(dog.getName());
        translateText(dog.getAge(), translatedAge -> tvDogAge.setText(translatedAge));

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
    }

    private String cleanHtmlTags(String text) {
        if (text == null) return "";
        return text.replaceAll("<p>", "").replaceAll("</p>", "").trim();
    }

    private void translateText(String text, TranslationCallback callback) {
        if (translator != null && text != null && !text.isEmpty()) {
            translator.translate(text)
                    .addOnSuccessListener(callback::onTranslationCompleted)
                    .addOnFailureListener(e -> {});
        } else {
            callback.onTranslationCompleted(text);
        }
    }

    @Override
    protected void onDestroy() {
        if (translator != null) {
            translator.close();
        }
        super.onDestroy();
    }

    private interface TranslationCallback {
        void onTranslationCompleted(String translatedText);
    }
}
