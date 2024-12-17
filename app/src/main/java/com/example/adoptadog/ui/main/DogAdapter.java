package com.example.adoptadog.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.adoptadog.R;
import com.example.adoptadog.models.Dog;
import com.example.adoptadog.ui.details.DogDetailsActivity;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.List;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.DogViewHolder> {

    private List<Dog> dogList;
    private Context context;
    private Translator translator;

    public DogAdapter(List<Dog> dogList, Context context) {
        this.dogList = dogList;
        this.context = context;
        setupTranslator();
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dog_item, parent, false);
        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        Dog dog = dogList.get(position);

        holder.tvDogName.setText(dog.getName());

        String description = cleanHtmlTags(dog.getPhysicalDescription());
        translateText(description, translatedText -> holder.tvDogBreed.setText(translatedText));

        holder.tvDogAge.setText(formatAge(dog.getAge()));

        if ("macho".equalsIgnoreCase(dog.getGender())) {
            holder.ivGenderIcon.setImageResource(R.drawable.ic_macho);
            holder.ivGenderIcon.setContentDescription(context.getString(R.string.gender_male_icon));
        } else if ("hembra".equalsIgnoreCase(dog.getGender())) {
            holder.ivGenderIcon.setImageResource(R.drawable.ic_hembra);
            holder.ivGenderIcon.setContentDescription(context.getString(R.string.gender_female_icon));
        }

        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(holder.itemView.getContext())
                .load(dog.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .listener(new com.bumptech.glide.request.RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.backgroundImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DogDetailsActivity.class);
            intent.putExtra("dogId", dog.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dogList.size();
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
                .addOnFailureListener(e -> Log.e("MLKit", "Error downloading the model: " + e.getMessage()));
    }

    private void translateText(String text, OnTranslationCompleteListener listener) {
        if (translator == null) {
            Log.e("MLKit", "Translator not initialized.");
            listener.onTranslationComplete(text);
            return;
        }

        translator.translate(text)
                .addOnSuccessListener(listener::onTranslationComplete)
                .addOnFailureListener(e -> {
                    Log.e("MLKit", "Translation error: " + e.getMessage());
                    listener.onTranslationComplete(text);
                });
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

    public static class DogViewHolder extends RecyclerView.ViewHolder {
        TextView tvDogName;
        ImageView backgroundImageView;
        ProgressBar progressBar;
        TextView tvDogBreed;
        TextView tvDogAge;
        ImageView ivGenderIcon;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDogName = itemView.findViewById(R.id.tvDogName);
            backgroundImageView = itemView.findViewById(R.id.backgroundImageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvDogBreed = itemView.findViewById(R.id.tvDogBreed);
            tvDogAge = itemView.findViewById(R.id.tvDogAge);
            ivGenderIcon = itemView.findViewById(R.id.ivGenderIcon);
        }
    }


    interface OnTranslationCompleteListener {
        void onTranslationComplete(String translatedText);
    }
}
