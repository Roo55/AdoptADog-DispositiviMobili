package com.example.adoptadog.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

        holder.tvDogName.setText(dog.getName());

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

        holder.btnMoreInfo.setOnClickListener(v -> {

            Intent intent = new Intent(context, DogDetailsActivity.class);
            intent.putExtra("dogId", dog.getId());
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
        ProgressBar progressBar;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDogName = itemView.findViewById(R.id.tvDogName);
            backgroundImageView = itemView.findViewById(R.id.backgroundImageView);
            btnMoreInfo = itemView.findViewById(R.id.btnMoreInfo);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
