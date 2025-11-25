package com.example.g9_comp3074;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException; // Import IOException
import java.util.List;
import java.util.Locale;

public class RestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String restaurantAddress = ""; // Store the address to use when the map is ready

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        // --- Toolbar Setup ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // --- Window Insets for Edge-to-Edge ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Initialize Views ---
        TextView tvName = findViewById(R.id.tvName);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvHours = findViewById(R.id.tvHours);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvTags = findViewById(R.id.tvTags);

        // --- Load Restaurant Data ---
        int restaurantId = getIntent().getIntExtra("restaurantId", -1);
        if (restaurantId == -1) {
            Toast.makeText(this, "Restaurant not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RestaurantDatabase db = RestaurantDatabase.getInstance(this);
        Restaurant restaurant = db.restaurantDao().getRestaurantById(restaurantId);

        if (restaurant != null) {
            tvName.setText(restaurant.name);
            tvDescription.setText(restaurant.description);
            tvPhone.setText(restaurant.phone);
            tvHours.setText(restaurant.hours);
            tvTags.setText(restaurant.tags);

            // Store the address and set the text
            restaurantAddress = restaurant.address;
            tvAddress.setText(restaurantAddress);
        }

        // --- Map Initialization ---
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            // The map will be configured in the onMapReady callback
            mapFragment.getMapAsync(this);
        }

        // --- Bottom Navigation ---
        setupBottomNavigation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Once the map is ready, check if we have an address to show
        if (restaurantAddress != null && !restaurantAddress.isEmpty()) {
            showAddressOnMap(restaurantAddress);
        } else {
            // If no address, fall back to a default location like Toronto
            LatLng toronto = new LatLng(43.65107, -79.347015);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toronto, 12f));
        }
    }

    private void showAddressOnMap(String address) {
        // Ensure map is ready and address is valid before starting
        if (mMap == null || address == null || address.isEmpty()) {
            return;
        }

        // --- Perform Geocoding on a Background Thread ---
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                // This is the blocking call that must be off the main thread
                List<Address> results = geocoder.getFromLocationName(address, 1);

                if (results != null && !results.isEmpty()) {
                    Address location = results.get(0);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // --- Update the UI on the Main Thread ---
                    runOnUiThread(() -> {
                        if (mMap != null) { // Double-check mMap is still valid
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        }
                    });

                } else {
                    // Show feedback on the main thread
                    runOnUiThread(() ->
                            Toast.makeText(this, "Address not found on map", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (IOException e) {
                // Handle exceptions (e.g., network unavailable) on the main thread
                runOnUiThread(() ->
                        Toast.makeText(this, "Location service unavailable", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        // Set the current item as selected to give user feedback
        bottomNav.setSelectedItemId(R.id.nav_search);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_search) {
                // Already on a details page linked from search, maybe go to MainActivity
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_collections) {
                startActivity(new Intent(this, CollectionActivity.class));
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
