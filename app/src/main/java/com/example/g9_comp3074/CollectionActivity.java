package com.example.g9_comp3074;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Arrays;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private RestaurantDao restaurantDao;
    private LinearLayout cardContainer;

    @SuppressLint("MissingInflatedId")
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
        // populateCards(titles);

        setupBottomNavigation();
        RestaurantDatabase db = Room.databaseBuilder(getApplicationContext(),
                        RestaurantDatabase.class, "restaurant_database")
                .allowMainThreadQueries()
                .build();

        restaurantDao = db.restaurantDao();
        // Load data
        List<Restaurant> restaurants = restaurantDao.getAllRestaurants();

// Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRestaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(restaurants);
        recyclerView.setAdapter(adapter);
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
