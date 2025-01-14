package com.example.adoptadog.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.example.adoptadog.R;
import com.example.adoptadog.models.Dog;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.List;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.DogViewHolder> {

    private List<Dog> dogList;
    private Context context;
    private DogItemClickListener dogItemClickListener;
    private Translator translator;

    public DogAdapter(List<Dog> dogList, Context context, DogItemClickListener dogItemClickListener) {
        this.dogList = dogList;
        this.context = context;
        this.dogItemClickListener = dogItemClickListener;
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

        String description = dog.getPhysicalDescription().replaceAll("<p>|</p>", "");
        holder.tvDogName.setText(dog.getName());
        translateText(dog.getAge(), translatedAge -> holder.tvDogAge.setText(translatedAge));
        translateText(description, translatedDescription -> holder.tvDogBreed.setText(translatedDescription));

        if ("macho".equalsIgnoreCase(dog.getGender())) {
            holder.ivGenderIcon.setImageResource(R.drawable.ic_macho);
        } else if ("hembra".equalsIgnoreCase(dog.getGender())) {
            holder.ivGenderIcon.setImageResource(R.drawable.ic_hembra);
        }

        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(holder.itemView.getContext())
                .load(dog.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .listener(new com.bumptech.glide.request.RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.backgroundImageView);

        holder.itemView.setOnClickListener(v -> dogItemClickListener.onDogItemClick(dog));
    }

    @Override
    public int getItemCount() {
        return dogList.size();
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

    private void translateText(String text, TranslationCallback callback) {
        if (translator != null && text != null && !text.isEmpty()) {
            translator.translate(text)
                    .addOnSuccessListener(callback::onTranslationCompleted)
                    .addOnFailureListener(e -> {});
        } else {
            callback.onTranslationCompleted(text);
        }
    }

    public static class DogViewHolder extends RecyclerView.ViewHolder {
        TextView tvDogName;
        TextView tvDogBreed;
        TextView tvDogAge;
        ImageView ivGenderIcon;
        ImageView backgroundImageView;
        ProgressBar progressBar;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDogName = itemView.findViewById(R.id.tvDogName);
            tvDogBreed = itemView.findViewById(R.id.tvDogBreed);
            tvDogAge = itemView.findViewById(R.id.tvDogAge);
            ivGenderIcon = itemView.findViewById(R.id.ivGenderIcon);
            backgroundImageView = itemView.findViewById(R.id.backgroundImageView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public interface DogItemClickListener {
        void onDogItemClick(Dog dog);
    }

    private interface TranslationCallback {
        void onTranslationCompleted(String translatedText);
    }
}
