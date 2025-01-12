package com.example.adoptadog.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.adoptadog.R;
import com.example.adoptadog.firebase.AuthManager;
import com.example.adoptadog.ui.auth.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAccountActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPassword;
    private MaterialButton btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User userData = dataSnapshot.getValue(User.class);
                    if (userData != null) {
                        String name = userData.getName() != null ? userData.getName() : "Unknown User";
                        String email = userData.getEmail() != null ? userData.getEmail() : "No email provided";

                        String password = "********";

                        tvName.setText("Name: " + name);
                        tvEmail.setText("Email: " + email);
                        tvPassword.setText("Password: " + password);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }

        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Accept", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            AuthManager.getInstance().deleteUserData(userId, new AuthManager.OnDatabaseListener() {
                @Override
                public void onSuccess() {
                    currentUser.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", false);
                            editor.apply();

                            Toast.makeText(MyAccountActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MyAccountActivity.this, "Error deleting account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(MyAccountActivity.this, "Error deleting user data: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
