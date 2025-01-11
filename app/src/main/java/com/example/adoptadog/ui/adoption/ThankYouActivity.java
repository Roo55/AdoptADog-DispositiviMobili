package com.example.adoptadog.ui.adoption;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.adoptadog.R;
import com.example.adoptadog.ui.main.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.bumptech.glide.Glide;
import android.widget.ImageView;

public class ThankYouActivity extends AppCompatActivity {

    private MaterialButton btnReturnToMain;
    private ImageView ivBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        ivBackground = findViewById(R.id.ivBackground);
        btnReturnToMain = findViewById(R.id.btnReturnToMain);

        Glide.with(this)
                .load(R.drawable.backgroundphoto1)
                .centerCrop()
                .into(ivBackground);

        btnReturnToMain.setOnClickListener(v -> {
            Intent intent = new Intent(ThankYouActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
