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
        collectionDao = CollectionDatabase.getInstance(getApplicationContext()).collectionDao();

        Button btnNewCollection = findViewById(R.id.btnNewCollection);
        btnNewCollection.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, NewCollection.class);
            startActivity(intent);
        });

        setupCollectionList();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCollectionList();
    }

    private void setupCollectionList() {
        rvCollections = findViewById(R.id.rvCollections); // This ID must be in your activity_collection.xml
        rvCollections.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<Collection> collections = collectionDao.getAllCollections();
            Log.d("CollectionActivity", "Collections found: " + collections.size());
            runOnUiThread(() -> {
                adapter = new CollectionCardAdapter(this, collections, collectionDao);
                rvCollections.setAdapter(adapter);
            });
        }).start();
    }

    private void setupNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                findViewById(R.id.bottomNavigation);

        if (bottomNav != null) {
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
