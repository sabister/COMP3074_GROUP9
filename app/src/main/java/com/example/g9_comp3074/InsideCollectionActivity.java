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

public class InsideCollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inside_collection);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Collection Details");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation();
        setupCardActions();
    }

    private void setupCardActions() {
        Button detailsButton = findViewById(R.id.btn_details);
        detailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(InsideCollectionActivity.this, RestActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        Button searchButton = findViewById(R.id.btn_search);
        Button collectionButton = findViewById(R.id.btn_col);
        Button newEntryButton = findViewById(R.id.btn_new);
        Button aboutButton = findViewById(R.id.btn_about);

        searchButton.setOnClickListener(v -> finish());

        collectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(InsideCollectionActivity.this, CollectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        newEntryButton.setOnClickListener(v -> {
            Intent intent = new Intent(InsideCollectionActivity.this, NewActivity.class);
            startActivity(intent);
        });

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(InsideCollectionActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }
}
