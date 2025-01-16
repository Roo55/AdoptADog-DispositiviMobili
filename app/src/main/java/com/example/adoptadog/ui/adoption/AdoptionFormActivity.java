package com.example.adoptadog.ui.adoption;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.adoptadog.R;
import com.example.adoptadog.database.AdoptionDAO;
import com.example.adoptadog.database.DatabaseClient;
import com.example.adoptadog.models.AdoptionForm;
import com.example.adoptadog.viewmodels.AdoptionFormViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdoptionFormActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etAddress, etComments;
    private Spinner spCountryCode;
    private MaterialButton btnSubmitForm;
    private AdoptionDAO adoptionDao;
    private AdoptionFormViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_form);

        int dogId = getIntent().getIntExtra("dogId", -1);
        String dogName = getIntent().getStringExtra("dogName");

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        spCountryCode = findViewById(R.id.spCountryCode);
        etAddress = findViewById(R.id.etAddress);
        etComments = findViewById(R.id.etComments);
        btnSubmitForm = findViewById(R.id.btnSubmitForm);

        adoptionDao = DatabaseClient.getInstance(getApplicationContext()).adoptionDAO();
        viewModel = new ViewModelProvider(this).get(AdoptionFormViewModel.class);

        populateUserEmail();
        setupCountryCodeSpinner();
        setupPhoneValidation();

        btnSubmitForm.setOnClickListener(v -> {
            if (validateForm()) {
                saveFormToDatabase(dogId, dogName);
            } else {
                Snackbar.make(v, "Please fill all the required fields.", Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getPhoneError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                etPhone.setError(errorMessage);
            } else {
                etPhone.setError(null);
            }
        });
    }

    private void setupCountryCodeSpinner() {
        List<String> countryCodes = Arrays.asList(
                "+1 (US)", "+44 (GB)", "+34 (ES)", "+49 (DE)", "+33 (FR)", "+39 (IT)", "+34 (ES)", "+45 (DK)",
                "+31 (NL)", "+41 (CH)", "+46 (SE)", "+32 (BE)", "+43 (AT)", "+48 (PL)", "+359 (BG)", "+351 (PT)"
        );
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountryCode.setAdapter(adapter);
    }

    private void setupPhoneValidation() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString().trim();
                String countryCode = spCountryCode.getSelectedItem().toString();
                viewModel.validatePhoneNumber(phoneNumber, countryCode);
            }
        });
    }

    private void populateUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !TextUtils.isEmpty(user.getEmail())) {
            etEmail.setText(user.getEmail());
        }
    }

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
        if (etPhone.getError() != null) {
            return false;
        }
        if (TextUtils.isEmpty(etAddress.getText())) {
            etAddress.setError("Address is required");
            return false;
        }
        return true;
    }

    private void saveFormToDatabase(int dogId, String dogName) {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String countryCode = spCountryCode.getSelectedItem().toString();
        String address = etAddress.getText().toString().trim();
        String comments = etComments.getText() != null ? etComments.getText().toString().trim() : "";

        AdoptionForm adoptionForm = new AdoptionForm(fullName, email, countryCode + phone, address, comments, dogId, dogName);

        new Thread(() -> {
            try {
                adoptionDao.insert(adoptionForm);
                Log.d("AdoptionFormActivity", "Form saved locally: " + adoptionForm);

                syncFormWithFirebase(adoptionForm);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Form submitted successfully!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(AdoptionFormActivity.this, ThankYouActivity.class);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                Log.e("AdoptionFormActivity", "Error saving form: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Failed to submit the form. Please try again.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void syncFormWithFirebase(AdoptionForm form) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.w("AdoptionFormActivity", "User not logged in. Skipping Firebase sync.");
            return;
        }

        String userId = user.getUid();
        String currentDate = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());

        FirebaseDatabase.getInstance()
                .getReference("adoption_forms")
                .child(userId)
                .child(currentDate)
                .setValue(form)
                .addOnSuccessListener(aVoid -> Log.d("AdoptionFormActivity", "Form synced with Firebase"))
                .addOnFailureListener(e -> Log.e("AdoptionFormActivity", "Failed to sync with Firebase: " + e.getMessage()));
    }
}
