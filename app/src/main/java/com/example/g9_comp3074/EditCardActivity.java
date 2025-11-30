package com.example.g9_comp3074;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditCardActivity extends AppCompatActivity implements OnMapReadyCallback {

    // View components from the XML layout
    private EditText etName, etDescription, etAddress, etPhone, etHours;
    private Button btnSave; // Renamed from btn_edit for clarity
    private GoogleMap mMap;

    private RestaurantDatabase db;
    private Restaurant restaurantToEdit;
    private int restaurantId = -1;

    // Executor for background database and geocoding tasks
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card_component);

        // --- 1. Initialize Views and Database ---
        initializeViews();
        db = RestaurantDatabase.getInstance(this);

        // --- 2. Setup Toolbar ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Entry");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish()); // Handle back button

        // --- 3. Get Restaurant ID from Intent ---
        restaurantId = getIntent().getIntExtra("restaurantId", -1);
        if (restaurantId == -1) {
            Toast.makeText(this, "Error: No restaurant found to edit.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- 4. Load Existing Data ---
        loadRestaurantData();

        // --- 5. Setup Save Button ---
        btnSave.setOnClickListener(v -> saveChanges());

        // --- 6. Setup Map ---
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // --- 7. Setup Auto-Search for Address ---
        setupAddressListener();

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        // Correctly map the EditText views using IDs from the XML
        etName = findViewById(R.id.tvName);
        etDescription = findViewById(R.id.tvDescription);
        etAddress = findViewById(R.id.tvAddress);
        etPhone = findViewById(R.id.tvPhone);
        etHours = findViewById(R.id.tvHours);
        btnSave = findViewById(R.id.btn_edit); // The ID from your XML is btn_edit
    }

    /**
     * Loads the restaurant data from the database on a background thread.
     */
    private void loadRestaurantData() {
        executor.execute(() -> {
            restaurantToEdit = db.restaurantDao().getRestaurantById(restaurantId);
            // Update the UI on the main thread after data is loaded
            if (restaurantToEdit != null) {
                runOnUiThread(this::populateFields);
            }
        });
    }

    /**
     * Fills the EditText fields with data from the loaded restaurant.
     */
    private void populateFields() {
        if (restaurantToEdit == null) return;
        etName.setText(restaurantToEdit.name);
        etDescription.setText(restaurantToEdit.description);
        etAddress.setText(restaurantToEdit.address);
        etPhone.setText(restaurantToEdit.phone);
        etHours.setText(restaurantToEdit.hours);

        // After populating fields, update the map to the initial address
        if (mMap != null && restaurantToEdit.address != null) {
            showAddressOnMap(restaurantToEdit.address);
        }
    }

    /**
     * Validates input, updates the restaurant object, and saves changes to the database.
     */
    private void saveChanges() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String hours = etHours.getText().toString().trim();
        String tags = ""; // You have a ChipGroup and a TextView for tags, so this needs to be implemented

        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Name and Address cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (restaurantToEdit != null) {
            restaurantToEdit.name = name;
            restaurantToEdit.description = description;
            restaurantToEdit.address = address;
            restaurantToEdit.phone = phone;
            restaurantToEdit.hours = hours;
            restaurantToEdit.tags = tags; // Update this based on how you manage tags

            // Save to database on a background thread
            executor.execute(() -> {
                db.restaurantDao().updateRestaurant(restaurantToEdit);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Entry Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity and return
                });
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // If the restaurant data has already been loaded, show the address on the map
        if (restaurantToEdit != null && restaurantToEdit.address != null) {
            showAddressOnMap(restaurantToEdit.address);
        } else {
            // Otherwise, show a default location
            LatLng defaultLocation = new LatLng(43.65107, -79.347015); // Toronto
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f));
        }
    }

    private void showAddressOnMap(String address) {
        if (mMap == null || address.trim().isEmpty()) return;

        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> results = geocoder.getFromLocationName(address, 1);
                if (results != null && !results.isEmpty()) {
                    Address location = results.get(0);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    runOnUiThread(() -> {
                        if (mMap != null) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Geocoding service unavailable", Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Listens for changes in the address field to update the map in real-time.
     */
    private void setupAddressListener() {
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // A small delay can be added here if needed, but for simplicity, we call directly
                if (s != null) {
                    showAddressOnMap(s.toString());
                }
            }
        });
    }
}
