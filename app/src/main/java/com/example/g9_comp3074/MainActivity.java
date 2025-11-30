package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private RestaurantDatabase db;
    private RecyclerView rvRestaurants;
    private RestaurantCardAdapter adapter;
    private List<Restaurant> allRestaurants = new ArrayList<>(); // Cache for all restaurants

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Consider disabling if it causes issues with padding
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = RestaurantDatabase.getInstance(this);
        rvRestaurants = findViewById(R.id.rvRestaurants); // Initialize rvRestaurants here
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));

        initSearch();
        setupNavigation();
        // The initial data load is now handled in onResume, which is called after onCreate
    }

    /**
     * Initializes the search functionality.
     * Search filtering now operates on a cached list instead of querying the DB on every keystroke.
     */
    private void initSearch() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // FIX: Filter the cached list instead of hitting the database repeatedly.
                String searchText = newText.toLowerCase();
                List<Restaurant> filteredList = new ArrayList<>();

                for (Restaurant restaurant : allRestaurants) {
                    boolean matchesName = restaurant.name != null &&
                            restaurant.name.toLowerCase().contains(searchText);
                    boolean matchesTags = restaurant.tags != null &&
                            restaurant.tags.toLowerCase().contains(searchText);

                    if (matchesName || matchesTags) {
                        filteredList.add(restaurant);
                    }
                }
                // Update the adapter with the filtered list
                if (adapter != null) {
                    adapter.filterList(filteredList);
                }
                return true;
            }
        });
    }

    /**
     * Sets up the RecyclerView by fetching data from the DB on a background thread.
     */
    private void setupRestaurantCards() {
        // Run database query on a background thread to prevent ANR
        new Thread(() -> {
            // This runs in the background
            allRestaurants = db.restaurantDao().getAllRestaurants(); // Update the cached list
            Log.d("MainActivity", "Restaurants found: " + allRestaurants.size());

            // Switch back to the main thread to update the UI
            runOnUiThread(() -> {
                adapter = new RestaurantCardAdapter(MainActivity.this, allRestaurants, db.restaurantDao());
                rvRestaurants.setAdapter(adapter);
            });
        }).start();
    }

    private void setupNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                findViewById(R.id.bottomNavigation);

        if (bottomNav != null) {
            // Set the current item as selected before adding the listener
            bottomNav.setSelectedItemId(R.id.nav_search);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_search) {
                    return true; // Already here
                } else if (id == R.id.nav_collections) {
                    startActivity(new Intent(MainActivity.this, CollectionActivity.class));
                    return true;
                } else if (id == R.id.nav_new) {
                    startActivity(new Intent(MainActivity.this, NewActivity.class));
                    return true;
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * onResume is called every time the user returns to this activity.
     * It's the best place to refresh the data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the restaurant list from the database
        setupRestaurantCards();
    }

    // This method is no longer needed as its logic is now inside setupRestaurantCards
    // private void refreshRestaurantList() { ... }
}
