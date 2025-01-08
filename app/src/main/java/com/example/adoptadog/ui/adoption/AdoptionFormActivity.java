package com.example.adoptadog.ui.adoption;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adoptadog.R;
import com.example.adoptadog.database.AdoptionDAO;
import com.example.adoptadog.database.DatabaseClient;
import com.example.adoptadog.models.AdoptionForm;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AdoptionFormActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etAddress, etComments;
    private MaterialButton btnSubmitForm;
    private AdoptionDAO adoptionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_form);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etComments = findViewById(R.id.etComments);
        btnSubmitForm = findViewById(R.id.btnSubmitForm);

        // Initialize Room DAO
        adoptionDao = DatabaseClient.getInstance(getApplicationContext()).adoptionDAO();

        // Autocomplete email if user is logged in
        populateUserEmail();

        // Set up submit button click listener
        btnSubmitForm.setOnClickListener(v -> {
            if (validateForm()) {
                saveFormToDatabase();
            } else {
                Snackbar.make(v, "Please fill all the required fields.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Autocompletes the email field with the user's Firebase email.
     */
    private void populateUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !TextUtils.isEmpty(user.getEmail())) {
            etEmail.setText(user.getEmail());
        }
    }

    /**
     * Validates the form to ensure all required fields are filled.
     */
    private boolean validateForm() {
        if (TextUtils.isEmpty(etFullName.getText())) {
            etFullName.setError("Full name is required");
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError("Phone number is required");
            return false;
        }
        if (TextUtils.isEmpty(etAddress.getText())) {
            etAddress.setError("Address is required");
            return false;
        }
        return true;
    }

    /**
     * Synchronize with Firebase.
     */
    private void saveFormToDatabase() {
        // Gather form data
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String comments = etComments.getText() != null ? etComments.getText().toString().trim() : "";

        // Create AdoptionForm object
        AdoptionForm adoptionForm = new AdoptionForm(fullName, email, phone, address, comments);

        // Save to Room database in a separate thread
        new Thread(() -> {
            try {
                adoptionDao.insert(adoptionForm);
                Log.d("AdoptionFormActivity", "Form saved locally: " + adoptionForm.toString());

                // Sync with Firebase
                syncFormWithFirebase(adoptionForm);

                runOnUiThread(() -> Toast.makeText(this, "Form submitted successfully!", Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                Log.e("AdoptionFormActivity", "Error saving form: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Failed to submit the form. Please try again.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    /**
     * Syncs the adoption form with Firebase Realtime Database.
     */
    private void syncFormWithFirebase(AdoptionForm form) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.w("AdoptionFormActivity", "User not logged in. Skipping Firebase sync.");
            return;
        }

        String userId = user.getUid();
        FirebaseDatabase.getInstance()
                .getReference("adoption_forms")
                .child(userId)
                .push()
                .setValue(form)
                .addOnSuccessListener(aVoid -> Log.d("AdoptionFormActivity", "Form synced with Firebase"))
                .addOnFailureListener(e -> Log.e("AdoptionFormActivity", "Failed to sync with Firebase: " + e.getMessage()));
    }
}
