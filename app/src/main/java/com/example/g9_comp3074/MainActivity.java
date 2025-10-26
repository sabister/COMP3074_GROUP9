package com.example.g9_comp3074;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RestaurantDatabase db;
    private RecyclerView rvRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = RestaurantDatabase.getInstance(this);
        setupNavigation();
        setupRestaurantCards();

        SearchView searchView = findViewById(R.id.searchView);
        TextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

        // --- Apply color styling ---
        if (searchText != null) {
            searchText.setTextColor(Color.WHITE);
            searchText.setHintTextColor(Color.GRAY);
        }
        if (searchIcon != null) searchIcon.setColorFilter(Color.WHITE);
        if (closeIcon != null) closeIcon.setColorFilter(Color.WHITE);

        searchView.setBackgroundColor(Color.parseColor("#222222"));
    }

    private void setupRestaurantCards() {
        rvRestaurants = findViewById(R.id.rvRestaurants); // Make sure this exists in activity_main.xml
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all restaurants from DB
        List<Restaurant> restaurants = db.restaurantDao().getAllRestaurants();

        android.util.Log.d("MainActivity", "Restaurants found: " + restaurants.size());

        // Attach adapter
        RestaurantCardAdapter adapter = new RestaurantCardAdapter(this, restaurants, db.restaurantDao());
        rvRestaurants.setAdapter(adapter);
    }
    private void setupNavigation() {
        Button newEntryButton = findViewById(R.id.btn_new);
        Button collectionButton = findViewById(R.id.btn_col);
        Button aboutButton = findViewById(R.id.btn_about);

        newEntryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewActivity.class);
            startActivity(intent);
        });

        collectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        });

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshRestaurantList();
    }

    private void refreshRestaurantList() {
        if (rvRestaurants != null && db != null) {
            List<Restaurant> restaurants = db.restaurantDao().getAllRestaurants();
            RestaurantCardAdapter adapter = new RestaurantCardAdapter(this, restaurants, db.restaurantDao());
            rvRestaurants.setAdapter(adapter);
        }
    }

}