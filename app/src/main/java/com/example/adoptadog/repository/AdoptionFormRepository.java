package com.example.adoptadog.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.adoptadog.database.AdoptionDAO;
import com.example.adoptadog.database.DatabaseClient;
import com.example.adoptadog.models.AdoptionForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdoptionFormRepository {

    private final AdoptionDAO adoptionDao;

    public AdoptionFormRepository(Context context) {
        adoptionDao = DatabaseClient.getInstance(context).adoptionDAO();
    }

    public void saveAdoptionForm(AdoptionForm form, MutableLiveData<Boolean> saveResult) {
        new Thread(() -> {
            try {
                adoptionDao.insert(form);
                Log.d("AdoptionFormRepository", "Form saved locally: " + form);

                syncFormWithFirebase(form);

                saveResult.postValue(true);
            } catch (Exception e) {
                Log.e("AdoptionFormRepository", "Error saving form: " + e.getMessage());
                saveResult.postValue(false);
            }
        }).start();
    }

    private void syncFormWithFirebase(AdoptionForm form) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.w("AdoptionFormRepository", "User not logged in. Skipping Firebase sync.");
            return;
        }

        String userId = user.getUid();
        String currentDate = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());

        FirebaseDatabase.getInstance()
                .getReference("adoption_forms")
                .child(userId)
                .child(currentDate)
                .setValue(form)
                .addOnSuccessListener(aVoid -> Log.d("AdoptionFormRepository", "Form synced with Firebase"))
                .addOnFailureListener(e -> Log.e("AdoptionFormRepository", "Failed to sync with Firebase: " + e.getMessage()));
    }
}
