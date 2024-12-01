package com.example.adoptadog.ui.main;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptadog.R;
import com.example.adoptadog.api.ApiClient;
import com.example.adoptadog.models.Dog;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView dogListRecyclerView;
    private DogAdapter dogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dogListRecyclerView = findViewById(R.id.dogList);
        dogListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Llama a la API para obtener perros
        ApiClient.fetchDogsFromApi("", new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(List<Dog> dogs) {
                // Configura el adapter y muestra los datos
                dogAdapter = new DogAdapter(dogs);
                dogListRecyclerView.setAdapter(dogAdapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
