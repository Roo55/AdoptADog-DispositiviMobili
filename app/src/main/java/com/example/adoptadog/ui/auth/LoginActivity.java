package com.example.adoptadog.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adoptadog.R;
import com.example.adoptadog.firebase.AuthManager;
import com.example.adoptadog.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private View tvRedirectToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        if (intent.hasExtra("redirectReason")) {
            String redirectReason = intent.getStringExtra("redirectReason");
            if (redirectReason != null && !redirectReason.isEmpty()) {
                Toast.makeText(this, redirectReason, Toast.LENGTH_LONG).show();
            }
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRedirectToRegister = findViewById(R.id.tvRedirectToRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRedirectToRegister.setOnClickListener(v -> {
            Intent intentToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intentToRegister);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthManager.getInstance().login(email, password, new AuthManager.OnAuthListener() {
            @Override
            public void onSuccess(FirebaseUser user) {

                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(LoginActivity.this, "Login failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
