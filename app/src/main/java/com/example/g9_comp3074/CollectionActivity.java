package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Arrays;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    private LinearLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Collection");
        }

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Container for dynamic cards
        cardContainer = findViewById(R.id.cardContainer);

        // Example data (replace with your real list)
        List<String> titles = Arrays.asList(
                "Restaurant I want to try",
                "Spicy ramen spots",
                "Top pizza places",
                "Brunch shortlist"
        );

        // Inflate one card per item
        populateCards(titles);

        setupBottomNavigation();
    }

    private void populateCards(List<String> titles) {
        cardContainer.removeAllViews();
        for (String title : titles) {
            // Inflate the reusable component without attaching yet
            View card = getLayoutInflater().inflate(
                    R.layout.restaurant_card_component,  // <<— updated layout name
                    cardContainer,
                    false
            );

            TextView tvTitle    = card.findViewById(R.id.tvRestaurantTitle);
            TextView tvSubtitle = card.findViewById(R.id.tvRestaurantSubtitle);
            Button btnDetails   = card.findViewById(R.id.btnDetails);
            Button btnEdit      = card.findViewById(R.id.btnEdit);
            Button btnDelete    = card.findViewById(R.id.btnDelete);

            tvTitle.setText(title);
            tvSubtitle.setText("…"); // set something meaningful if you have it

            btnDetails.setOnClickListener(v -> {
                Intent intent = new Intent(CollectionActivity.this, InsideCollectionActivity.class);
                intent.putExtra("collection_title", title);
                startActivity(intent);
            });

            btnEdit.setOnClickListener(v ->
                    android.widget.Toast.makeText(this, "Edit: " + title, android.widget.Toast.LENGTH_SHORT).show()
            );

            btnDelete.setOnClickListener(v -> {
                cardContainer.removeView(card);
                android.widget.Toast.makeText(this, "Deleted: " + title, android.widget.Toast.LENGTH_SHORT).show();
            });

            cardContainer.addView(card);
        }
    }


    private void setupBottomNavigation() {
        Button searchButton = findViewById(R.id.btn_search);
        Button newEntryButton = findViewById(R.id.btn_new);
        Button aboutButton = findViewById(R.id.btn_about);

        searchButton.setOnClickListener(v -> finish());

        newEntryButton.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, NewActivity.class);
            startActivity(intent);
        });

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }
}