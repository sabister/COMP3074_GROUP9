package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RestaurantDatabase db;
    private RecyclerView rvRestaurants;
    private RestaurantCardAdapter adapter;
    private List<Restaurant> allRestaurants = new ArrayList<>(); // Cache for all restaurants

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = RestaurantDatabase.getInstance(this);
        rvRestaurants = findViewById(R.id.rvRestaurants);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));

        initSearch();
        setupNavigation();

        // ---- KEEP search styling from main branch ----
        SearchView searchView = findViewById(R.id.searchView);
        TextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

        if (searchText != null) {
            searchText.setTextColor(Color.WHITE);
            searchText.setHintTextColor(Color.GRAY);
        }
        if (searchIcon != null) searchIcon.setColorFilter(Color.WHITE);
        if (closeIcon != null) closeIcon.setColorFilter(Color.WHITE);

        // The initial data loading now happens in onResume
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
        new Thread(() -> {
            allRestaurants = db.restaurantDao().getAllRestaurants(); // Update cached list
            Log.d("MainActivity", "Restaurants found: " + allRestaurants.size());

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
            bottomNav.setSelectedItemId(R.id.nav_search);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_search) {
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
                }
                return false;
            });
        }
    }

    /**
     * Called every time the user returns to this activity.
     * Best place to refresh restaurant data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setupRestaurantCards(); // Refresh list
    }
}
