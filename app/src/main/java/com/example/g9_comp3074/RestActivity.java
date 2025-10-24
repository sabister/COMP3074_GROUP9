package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class RestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rest);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Entry Details");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        Button searchButton = findViewById(R.id.btn_search);
        Button collectionButton = findViewById(R.id.btn_col);
        Button aboutButton = findViewById(R.id.btn_about);
        Button newEntryButton = findViewById(R.id.btn_new);

        searchButton.setOnClickListener(v -> finish());

        collectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(RestActivity.this, CollectionActivity.class);
            startActivity(intent);
        });

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(RestActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        newEntryButton.setOnClickListener(v -> {
            Intent intent = new Intent(RestActivity.this, NewActivity.class);
            startActivity(intent);
        });
    }
}
