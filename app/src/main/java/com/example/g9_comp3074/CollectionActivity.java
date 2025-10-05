package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        // Apply padding for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCardActions();
        setupBottomNavigation();
    }

    private void setupCardActions() {
        Button detailsButton = findViewById(R.id.btn_det);

        detailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, InsideCollectionActivity.class);
            startActivity(intent);
        });

    }

    private void setupBottomNavigation() {
        // Find the buttons from the layout
        Button searchButton = findViewById(R.id.btn_search);
        Button collectionButton = findViewById(R.id.btn_col);
        Button newEntryButton = findViewById(R.id.btn_new); // Corrected variable name

        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, MainActivity.class);
            startActivity(intent);
        });

        collectionButton.setOnClickListener(v -> {
        });

        newEntryButton.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, NewActivity.class);
            startActivity(intent);
        });
    }

}