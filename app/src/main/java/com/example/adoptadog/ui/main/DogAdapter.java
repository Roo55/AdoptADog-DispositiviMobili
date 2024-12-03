package com.example.adoptadog.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.adoptadog.R;
import com.example.adoptadog.models.Dog;
import com.example.adoptadog.ui.details.DogDetailsActivity;

import java.util.List;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.DogViewHolder> {

    private List<Dog> dogList;
    private Context context;

    public DogAdapter(List<Dog> dogList, Context context) {
        this.dogList = dogList;
        this.context = context;
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

        // Set the dog's name
        holder.tvDogName.setText(dog.getName());

        // Load the dog's image
        Glide.with(context)
                .load(dog.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.backgroundImageView);

        // Handle the "More Information" button
        holder.btnMoreInfo.setOnClickListener(v -> {
            // Example: Open a new activity with dog details
            Intent intent = new Intent(context, DogDetailsActivity.class);
            intent.putExtra("dogId", dog.getId()); // Pass dog's ID to the next screen
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dogList.size();
    }

    public static class DogViewHolder extends RecyclerView.ViewHolder {
        TextView tvDogName;
        ImageView backgroundImageView;
        Button btnMoreInfo;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDogName = itemView.findViewById(R.id.tvDogName);
            backgroundImageView = itemView.findViewById(R.id.backgroundImageView);
            btnMoreInfo = itemView.findViewById(R.id.btnMoreInfo);
        }
    }
}
