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

        // Optional header text
        TextView headerTextView = findViewById(R.id.textView);
        if (headerTextView != null && collectionTitle != null && !collectionTitle.isEmpty()) {
            headerTextView.setText(collectionTitle);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1) Find the container
        cardContainer = findViewById(R.id.cardContainer);

        // 2) Example data (replace with your real list / DB results)
        List<RestaurantItem> restaurants = Arrays.asList(
                new RestaurantItem("Pizza Planet", "Thin crust ~ $$ · 1.2 km"),
                new RestaurantItem("Ramen Lab", "Tonkotsu specialty ~ $$ · 800 m"),
                new RestaurantItem("Green Bowl", "Vegan bowls ~ $ · 2.0 km")
        );

        // 3) Inflate the cards
        populateRestaurantCards(restaurants);

        setupBottomNavigation();
//        setupCardActions(); // If you still use a single btn_details elsewhere (optional)
    }

    private void populateRestaurantCards(List<RestaurantItem> items) {
        cardContainer.removeAllViews();

        for (RestaurantItem item : items) {
            // Inflate reusable component WITHOUT attaching immediately
            View card = getLayoutInflater().inflate(
                    R.layout.restaurant_card_component,
                    cardContainer,
                    false
            );

            // Bind views inside the card
            TextView tvTitle    = card.findViewById(R.id.tvRestaurantTitle);
            TextView tvSubtitle = card.findViewById(R.id.tvRestaurantSubtitle);
            Button btnDetails   = card.findViewById(R.id.btnDetails);
            Button btnEdit      = card.findViewById(R.id.btnEdit);
            Button btnDelete    = card.findViewById(R.id.btnDelete);

            // Populate
            tvTitle.setText(item.title);
            tvSubtitle.setText(item.subtitle);

            // Actions
            btnDetails.setOnClickListener(v -> {
                Intent intent = new Intent(InsideCollectionActivity.this, RestActivity.class);
                intent.putExtra("restaurant_name", item.title);
                startActivity(intent);
            });

            btnEdit.setOnClickListener(v ->
                    toast("Edit tapped: " + item.title)
            );

            btnDelete.setOnClickListener(v -> {
                cardContainer.removeView(card);
                toast("Deleted: " + item.title);
            });

            // Add the card
            cardContainer.addView(card);
        }
    }

//    private void setupCardActions() {
//        // If you keep a single "details" button in the static layout, wire it here.
//        // Otherwise you can remove this method entirely.
//        Button detailsButton = findViewById(R.id.btn_details);
//        if (detailsButton != null) {
//            detailsButton.setOnClickListener(v -> {
//                Intent intent = new Intent(InsideCollectionActivity.this, RestActivity.class);
//                startActivity(intent);
//            });
//        }
//    }

    private void setupBottomNavigation() {
        Button searchButton = findViewById(R.id.btn_search);
        Button collectionButton = findViewById(R.id.btn_col);
        Button newEntryButton = findViewById(R.id.btn_new);
        Button aboutButton = findViewById(R.id.btn_about);

        if (searchButton != null) searchButton.setOnClickListener(v -> finish());

        if (collectionButton != null) {
            collectionButton.setOnClickListener(v -> {
                Intent intent = new Intent(InsideCollectionActivity.this, CollectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (newEntryButton != null) {
            newEntryButton.setOnClickListener(v -> {
                Intent intent = new Intent(InsideCollectionActivity.this, NewActivity.class);
                startActivity(intent);
            });
        }

        if (aboutButton != null) {
            aboutButton.setOnClickListener(v -> {
                Intent intent = new Intent(InsideCollectionActivity.this, AboutActivity.class);
                startActivity(intent);
            });
        }
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
