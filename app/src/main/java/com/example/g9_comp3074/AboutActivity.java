package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        // Highlight "About"
        bottomNav.setSelectedItemId(R.id.nav_about);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_search) {
                startActivity(new Intent(AboutActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_collections) {
                startActivity(new Intent(AboutActivity.this, CollectionActivity.class));
                return true;
            } else if (id == R.id.nav_new) {
                startActivity(new Intent(AboutActivity.this, NewActivity.class));
                return true;
            } else if (id == R.id.nav_about) {
                // Already here
                return true;
            } else if (id == R.id.nav_more) {
                // TODO: open Settings/More when you add it
                return true;
            }
            return false;
        });
    }
}
