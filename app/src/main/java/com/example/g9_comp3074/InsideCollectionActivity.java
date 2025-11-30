package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.g9_comp3074.join.CollectionRestaurantJoinDao;
import com.example.g9_comp3074.join.CollectionRestaurantJoinDatabase;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class InsideCollectionActivity extends AppCompatActivity {

    private LinearLayout cardContainer;
    private int collectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inside_collection);

        // 🎯 CORRECT: getIntent() MUST be inside onCreate
        collectionId = getIntent().getIntExtra("collectionId", -1);

        if (collectionId == -1) {
            Toast.makeText(this, "Invalid collection", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Title
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
        if (headerTextView != null && collectionTitle != null) {
            headerTextView.setText(collectionTitle);
        }

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Container
        cardContainer = findViewById(R.id.cardContainer);

        // Load real data
        loadRestaurantsForCollection();

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void loadRestaurantsForCollection() {
        cardContainer.removeAllViews();

        new Thread(() -> {
            // 1. Load all restaurant IDs linked to this collection
            CollectionRestaurantJoinDao joinDao = CollectionRestaurantJoinDatabase
                    .getInstance(this)
                    .collectionRestaurantJoinDao();

            List<Integer> restaurantIds =
                    joinDao.getRestaurantIdsForCollection(collectionId);

            // 2. Load actual restaurant objects
            RestaurantDatabase restaurantDb = RestaurantDatabase.getInstance(this);
            List<Restaurant> restaurants = new ArrayList<>();

            for (int id : restaurantIds) {
                Restaurant r = restaurantDb.restaurantDao().getRestaurantById(id);
                if (r != null) restaurants.add(r);
            }

            // 3. Inflate cards on UI thread
            runOnUiThread(() -> displayRestaurantCards(restaurants));
        }).start();
    }

    private void displayRestaurantCards(List<Restaurant> restaurants) {
        cardContainer.removeAllViews();

        if (restaurants.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No restaurants in this collection.");
            empty.setTextSize(18f);
            empty.setPadding(32, 32, 32, 32);
            cardContainer.addView(empty);
            return;
        }

        for (Restaurant restaurant : restaurants) {
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

            tvTitle.setText(restaurant.name);
            tvSubtitle.setText(restaurant.description);

            // DETAILS → open RestActivity
            btnDetails.setOnClickListener(v -> {
                Intent intent = new Intent(InsideCollectionActivity.this, RestActivity.class);
                intent.putExtra("restaurantId", restaurant.id);
                startActivity(intent);
            });

            // EDIT → open EditCardActivity (your existing editor)
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(InsideCollectionActivity.this, EditCardActivity.class);
                intent.putExtra("restaurantId", restaurant.id);
                startActivity(intent);
            });

            // DELETE → Remove link from join table
            btnDelete.setOnClickListener(v -> deleteRestaurantFromCollection(restaurant.id, card));

            cardContainer.addView(card);
        }
    }

    private void deleteRestaurantFromCollection(int restaurantId, View cardView) {
        new Thread(() -> {
            CollectionRestaurantJoinDao joinDao = CollectionRestaurantJoinDatabase
                    .getInstance(this)
                    .collectionRestaurantJoinDao();

            joinDao.deletePair(collectionId, restaurantId);

            runOnUiThread(() -> {
                cardContainer.removeView(cardView);
                Toast.makeText(this, "Removed from collection", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.nav_collections);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_search) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_collections) {
                return true;
            } else if (id == R.id.nav_new) {
                startActivity(new Intent(this, NewActivity.class));
                return true;
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            return false;
        });
    }
}
