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
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // “New Collection +” button
        Button addBtn = findViewById(R.id.button5);
        addBtn.setOnClickListener(v -> {
            // TODO: launch your create-new-collection flow
            startActivity(new Intent(CollectionActivity.this, NewActivity.class));
        });

        // Bottom nav
        setupBottomNavigation();
    }

    private void populateCards(List<String> titles) {
        cardContainer.removeAllViews();
        for (String title : titles) {
            View card = getLayoutInflater().inflate(
                    R.layout.restaurant_card_component, // or R.layout.collection_component if you switch the design
                    cardContainer,
                    false
            );

            TextView tvTitle    = card.findViewById(R.id.tvRestaurantTitle);
            TextView tvSubtitle = card.findViewById(R.id.tvRestaurantSubtitle);
            Button btnDetails   = card.findViewById(R.id.btnDetails);
            Button btnEdit      = card.findViewById(R.id.btnEdit);
            Button btnDelete    = card.findViewById(R.id.btnDelete);

            tvTitle.setText(title);
            tvSubtitle.setText("…");

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
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        // Highlight this tab
        bottomNav.setSelectedItemId(R.id.nav_collections);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_search) {
                startActivity(new Intent(CollectionActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_collections) {
                // already here
                return true;
            } else if (id == R.id.nav_new) {
                startActivity(new Intent(CollectionActivity.this, NewActivity.class));
                return true;
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(CollectionActivity.this, AboutActivity.class));
                return true;
            } else if (id == R.id.nav_more) {
                // TODO: open Settings/More when ready
                return true;
            }
            return false;
        });
    }
}
