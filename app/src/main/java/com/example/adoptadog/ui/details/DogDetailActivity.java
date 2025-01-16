package com.example.adoptadog.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.adoptadog.R;
import com.example.adoptadog.models.Dog;
import com.example.adoptadog.ui.adoption.AdoptionFormActivity;
import com.example.adoptadog.viewmodels.DogDetailViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.flexbox.FlexboxLayout;
import java.util.List;

public class DogDetailActivity extends AppCompatActivity {

    private ImageView ivDogImage, ivGenderIcon,btnBack;
    private TextView tvDogName, tvDogPersonality, tvDogAge, tvSterilizedStatus;
    private MaterialButton btnAdopt;
    private FlexboxLayout traitsContainer;
    private DogDetailViewModel viewModel;
    private Dog dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_detail);

        initViews();

        viewModel = new ViewModelProvider(this).get(DogDetailViewModel.class);

        btnBack.setOnClickListener(v -> finish());


        Intent intent = getIntent();
        dog = (Dog) intent.getSerializableExtra("dog");

        if (dog != null) {
            populateDogDetails(dog);
        }

        setupAdoptButtonClickListener();
    }

    private void initViews() {
        ivDogImage = findViewById(R.id.ivDogImage);
        ivGenderIcon = findViewById(R.id.ivGenderIcon);
        tvDogName = findViewById(R.id.tvDogName);
        tvDogPersonality = findViewById(R.id.tvDogPersonality);
        tvDogAge = findViewById(R.id.tvDogAge);
        tvSterilizedStatus = findViewById(R.id.tvSterilizedStatus);
        btnAdopt = findViewById(R.id.btnAdopt);
        traitsContainer = findViewById(R.id.traitsContainer);
        btnBack = findViewById(R.id.btnBack);
    }

    private void populateDogDetails(Dog dog) {
        tvDogName.setText(dog.getName());

        viewModel.translateText(dog.getAge()).observe(this, tvDogAge::setText);

        Glide.with(this)
                .load(dog.getImageUrl())
                .into(ivDogImage);

        ivGenderIcon.setImageResource("macho".equalsIgnoreCase(dog.getGender()) ? R.drawable.ic_macho : R.drawable.ic_hembra);

        tvSterilizedStatus.setText(dog.getSterilized() == 1 ? "YES" : "NO");
        tvSterilizedStatus.setBackgroundResource(dog.getSterilized() == 1 ? R.drawable.sterilized_status_yes : R.drawable.sterilized_status_no);

        viewModel.translateText(dog.getPersonalityDescription()).observe(this, tvDogPersonality::setText);

        viewModel.getRandomTraits(4).observe(this, this::showTraitsWithAnimation);
    }

    private void setupAdoptButtonClickListener() {
        btnAdopt.setOnClickListener(v -> {
            if (dog != null) {
                Intent adoptIntent = new Intent(DogDetailActivity.this, AdoptionFormActivity.class);
                adoptIntent.putExtra("dogId", dog.getId());
                adoptIntent.putExtra("dogName", dog.getName());
                startActivity(adoptIntent);
            }
        });
    }

    private void showTraitsWithAnimation(List<String> traits) {
        traitsContainer.removeAllViews();
        Handler handler = new Handler();
        long delay = 500;

        for (int i = 0; i < traits.size(); i++) {
            int index = i;
            handler.postDelayed(() -> {
                View chip = LayoutInflater.from(this).inflate(R.layout.item_trait_chip, traitsContainer, false);
                TextView tvTrait = chip.findViewById(R.id.tvTrait);
                tvTrait.setText(traits.get(index));
                traitsContainer.addView(chip);

                chip.setScaleX(0f);
                chip.setScaleY(0f);
                chip.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
            }, delay);

            delay += 200;
        }
    }
}
