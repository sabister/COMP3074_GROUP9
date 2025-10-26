package com.example.g9_comp3074;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.room.Room;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private RestaurantDatabase db;

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

        setupNavigation();
        setupCardActions();
        db = Room.databaseBuilder(getApplicationContext(),
                        RestaurantDatabase.class, "restaurant_db")
                .allowMainThreadQueries() // only for small/simple data; move to background for production
                .build();
    }
    private void setupCardActions() {
        Button detailsButton = findViewById(R.id.rest_det);

        detailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RestActivity.class);
            startActivity(intent);
        });

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
}