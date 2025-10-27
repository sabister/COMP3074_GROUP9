package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // kept because you use buttons inside cards
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class InsideCollectionActivity extends AppCompatActivity {

    private LinearLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inside_collection);

        // Toolbar setup
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String collectionTitle = getIntent().getStringExtra("collection_title");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    (collectionTitle != null && !collectionTitle.isEmpty())
                            ? collectionTitle
                            : "Collection Details"
            );
        }

        // Optional header text (matches the new id in XML)
        TextView headerTextView = findViewById(R.id.textView);
        if (headerTextView != null && collectionTitle != null && !collectionTitle.isEmpty()) {
            headerTextView.setText(collectionTitle);
        }

        // Edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cards container
        cardContainer = findViewById(R.id.cardContainer);

        // Example data (replace with your real list / DB results)
        List<RestaurantItem> restaurants = Arrays.asList(
                new RestaurantItem("Pizza Planet", "Thin crust ~ $$ · 1.2 km"),
                new RestaurantItem("Ramen Lab", "Tonkotsu specialty ~ $$ · 800 m"),
                new RestaurantItem("Green Bowl", "Vegan bowls ~ $ · 2.0 km")
        );

        // Inflate the cards
        populateRestaurantCards(restaurants);

        // Bottom navigation
        setupBottomNavigation();
    }

    private void populateRestaurantCards(List<RestaurantItem> items) {
        cardContainer.removeAllViews();

        for (RestaurantItem item : items) {
            View card = getLayoutInflater().inflate(
                    R.layout.restaurant_card_component,
                    cardContainer,
                    false
            );

            TextView tvTitle    = card.findViewById(R.id.tvRestaurantTitle);
            TextView tvSubtitle = card.findViewById(R.id.tvRestaurantSubtitle);
            Button btnDetails   = card.findViewById(R.id.btnDetails);
            Button btnEdit      = card.findViewById(R.id.btnEdit);
            Button btnDelete    = card.findViewById(R.id.btnDelete);

            tvTitle.setText(item.title);
            tvSubtitle.setText(item.subtitle);

            btnDetails.setOnClickListener(v -> {
                // If your RestActivity expects an ID, pass it here instead of name.
                // This is just a placeholder to navigate.
                Intent intent = new Intent(InsideCollectionActivity.this, RestActivity.class);
                intent.putExtra("restaurant_name", item.title);
                startActivity(intent);
            });

            btnEdit.setOnClickListener(v -> toast("Edit tapped: " + item.title));

            btnDelete.setOnClickListener(v -> {
                cardContainer.removeView(card);
                toast("Deleted: " + item.title);
            });

            cardContainer.addView(card);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        // Highlight Collections for this screen
        bottomNav.setSelectedItemId(R.id.nav_collections);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_search) {
                startActivity(new Intent(InsideCollectionActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_collections) {
                // Already in collections section
                return true;
            } else if (id == R.id.nav_new) {
                startActivity(new Intent(InsideCollectionActivity.this, NewActivity.class));
                return true;
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(InsideCollectionActivity.this, AboutActivity.class));
                return true;
            } else if (id == R.id.nav_more) {
                // TODO: Open settings/more when implemented
                return true;
            }
            return false;
        });
    }

    private void toast(String msg) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    // Simple model; expand as needed
    static class RestaurantItem {
        final String title;
        final String subtitle;

        RestaurantItem(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }
    }
}
