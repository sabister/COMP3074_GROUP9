package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.g9_comp3074.collection_data.Collection;
import com.example.g9_comp3074.collection_data.CollectionCardAdapter;
import com.example.g9_comp3074.collection_data.CollectionDao;
import com.example.g9_comp3074.collection_data.CollectionDatabase; // Assuming this is your Room DB class

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    private RecyclerView rvCollections;
    private CollectionCardAdapter adapter;
    private CollectionDao collectionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        // Initialize the database and DAO
        // Make sure your database class is named CollectionDatabase and has a getInstance method
        collectionDao = CollectionDatabase.getInstance(getApplicationContext()).collectionDao();

        // Setup the "New Collection" button
        Button btnNewCollection = findViewById(R.id.btnNewCollection);
        btnNewCollection.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, NewCollection.class);
            startActivity(intent);
        });

        // Setup the list of collection cards, mimicking MainActivity
        setupCollectionList();

        // Setup bottom navigation
        setupNavigation();
    }

    /**
     * This method is called whenever the user returns to this activity.
     * It's the perfect place to refresh the list to show any new data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list to show any newly added collections
        setupCollectionList();
    }

    /**
     * This method mimics the setupRestaurantCards() from your MainActivity.
     * It initializes the RecyclerView, fetches data from the database on a background thread,
     * and then sets the adapter on the main UI thread.
     */
    private void setupCollectionList() {
        rvCollections = findViewById(R.id.rvCollections); // This ID must be in your activity_collection.xml
        rvCollections.setLayoutManager(new LinearLayoutManager(this));

        // Run the database query on a background thread to avoid blocking the UI
        new Thread(() -> {
            // Fetch all collections from the database
            List<Collection> collections = collectionDao.getAllCollections();
            Log.d("CollectionActivity", "Collections found: " + collections.size());

            // Switch back to the main thread to update the RecyclerView
            runOnUiThread(() -> {
                // Create and set the adapter
                adapter = new CollectionCardAdapter(this, collections, collectionDao);
                rvCollections.setAdapter(adapter);
            });
        }).start();
    }

    private void setupNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                findViewById(R.id.bottomNavigation);

        if (bottomNav != null) {
            // Set the current item as selected before adding the listener
            bottomNav.setSelectedItemId(R.id.nav_collections);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_collections) {
                    return true; // Already here
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(CollectionActivity.this, MainActivity.class));
                    return true;
                } else if (id == R.id.nav_new) {
                    startActivity(new Intent(CollectionActivity.this, NewActivity.class));
                    return true;
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(CollectionActivity.this, AboutActivity.class));
                    return true;
                }
                return false;
            });
        }
    }
}
