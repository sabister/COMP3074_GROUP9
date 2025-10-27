package com.example.g9_comp3074;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;

import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
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
        initSearch();
        setupNavigation();
        setupRestaurantCards();

    }
    //search bar
    private void initSearch(){
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Restaurant> allRestaurants = db.restaurantDao().getAllRestaurants();
                List<Restaurant> filteredList = new ArrayList<>();

                for (Restaurant restaurant : allRestaurants) {
                    boolean matchesName = restaurant.name != null &&
                            restaurant.name.toLowerCase().contains(s.toLowerCase());
                    boolean matchesTags = restaurant.tags != null &&
                            restaurant.tags.toLowerCase().contains(s.toLowerCase());
                    if (matchesName || matchesTags) {
                        filteredList.add(restaurant);
                    }

                }

                RestaurantCardAdapter adapter = new RestaurantCardAdapter(MainActivity.this, filteredList, db.restaurantDao());
                rvRestaurants.setAdapter(adapter);
                return false;
            }
        });

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
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                findViewById(R.id.bottomNavigation);

        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_search) {
                    // already on MainActivity
                    return true;
                } else if (id == R.id.nav_collections) {
                    startActivity(new Intent(MainActivity.this, CollectionActivity.class));
                    return true;
                } else if (id == R.id.nav_new) {
                    startActivity(new Intent(MainActivity.this, NewActivity.class));
                    return true;
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    return true;
                } else if (id == R.id.nav_more) {
                    // TODO: open a More/Settings screen when you add it
                    return true;
                }
                return false;
            });

            // Optionally highlight current tab
            bottomNav.setSelectedItemId(R.id.nav_search);
        } else {
            // Fallback for old layout with buttons (defensive; safe to remove later)
            Button newEntryButton = findViewById(R.id.btn_new);
            Button collectionButton = findViewById(R.id.btn_col);
            Button aboutButton = findViewById(R.id.btn_about);

            if (newEntryButton != null) {
                newEntryButton.setOnClickListener(v ->
                        startActivity(new Intent(MainActivity.this, NewActivity.class)));
            }
            if (collectionButton != null) {
                collectionButton.setOnClickListener(v ->
                        startActivity(new Intent(MainActivity.this, CollectionActivity.class)));
            }
            if (aboutButton != null) {
                aboutButton.setOnClickListener(v ->
                        startActivity(new Intent(MainActivity.this, AboutActivity.class)));
            }
        }
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