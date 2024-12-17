package com.example.adoptadog.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.adoptadog.R;
import com.google.android.material.button.MaterialButton;

public class DogDetailActivity extends AppCompatActivity {

    private ImageView ivDogImage;
    private TextView tvDogName, tvDogPersonality, tvDogAge;
    private ImageView ivGenderIcon;
    private MaterialButton btnFavorite, btnAdopt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_detail);

        ivDogImage = findViewById(R.id.ivDogImage);
        tvDogName = findViewById(R.id.tvDogName);
        tvDogPersonality = findViewById(R.id.tvDogPersonality);
        tvDogAge = findViewById(R.id.tvDogAge);
        ivGenderIcon = findViewById(R.id.ivGenderIcon);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnAdopt = findViewById(R.id.btnAdopt);

        Intent intent = getIntent();
        String dogName = intent.getStringExtra("dogName");
        String dogPersonality = intent.getStringExtra("dogPersonality");
        String dogAge = intent.getStringExtra("dogAge");
        String dogImage = intent.getStringExtra("dogImage");
        String dogGender = intent.getStringExtra("dogGender");

        tvDogName.setText(dogName);
        tvDogPersonality.setText(dogPersonality);
        tvDogAge.setText(dogAge);

        Glide.with(this)
                .load(dogImage)
                .into(ivDogImage);

        if ("macho".equals(dogGender)) {
            ivGenderIcon.setImageResource(R.drawable.ic_macho);
        } else if ("hembra".equals(dogGender)) {
            ivGenderIcon.setImageResource(R.drawable.ic_hembra);
        }

        btnFavorite.setOnClickListener(v -> {

        });

        btnAdopt.setOnClickListener(v -> {

        });
    }
}
