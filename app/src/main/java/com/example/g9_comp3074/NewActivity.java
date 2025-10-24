package com.example.g9_comp3074;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewActivity extends AppCompatActivity implements OnMapReadyCallback {

    // UI elements
    private EditText etRestaurantName, etDescription, etPhone, etHours, etAddress;
    private ChipGroup chipGroupTags;
    private Button btnCreateEntry, btnAddTag;
    private GoogleMap mMap;

    private Handler handler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new);

        setupToolbar();
        initializeViews();
        setupMap();
        setupClickListeners();
        setupBottomNavigation();
        setupAddressAutoSearch();

        // Handle insets (for EdgeToEdge layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 🗺️ Initialize Google Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Default location: Toronto
        LatLng toronto = new LatLng(43.65107, -79.347015);
        mMap.addMarker(new MarkerOptions().position(toronto).title("Toronto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toronto, 12f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Entry");
        }
    }

    private void initializeViews() {
        etRestaurantName = findViewById(R.id.textView);
        etDescription = findViewById(R.id.textView3);
        etPhone = findViewById(R.id.textView4);
        etHours = findViewById(R.id.textView5);
        etAddress = findViewById(R.id.textView6);
        chipGroupTags = findViewById(R.id.tag_chip_group);
        btnCreateEntry = findViewById(R.id.btn_create);
        btnAddTag = findViewById(R.id.btn_add_tag);
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupAddressAutoSearch() {
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    String address = s.toString().trim();
                    if (!address.isEmpty()) {
                        showAddressOnMap(address);
                    }
                };
                handler.postDelayed(searchRunnable, 1500);
            }
        });
    }

    private void setupClickListeners() {
        btnCreateEntry.setOnClickListener(v -> {
            String name = etRestaurantName.getText().toString();
            String description = etDescription.getText().toString();
            String phone = etPhone.getText().toString();
            String hours = etHours.getText().toString();
            String address = etAddress.getText().toString();
            List<String> selectedTags = getSelectedTags();

            if (name.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Restaurant Name and Address are required.", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d("NewEntryData", "Name: " + name);
            Log.d("NewEntryData", "Description: " + description);
            Log.d("NewEntryData", "Phone: " + phone);
            Log.d("NewEntryData", "Hours: " + hours);
            Log.d("NewEntryData", "Address: " + address);
            Log.d("NewEntryData", "Tags: " + selectedTags.toString());

            Toast.makeText(this, "Entry for '" + name + "' created!", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnAddTag.setOnClickListener(v -> showAddTagDialog());
    }

    // 📍 Show address on Google Map
    private void showAddressOnMap(String address) {
        if (mMap == null) return;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> results = geocoder.getFromLocationName(address, 1);
            if (results != null && !results.isEmpty()) {
                Address location = results.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Unable to connect to location service", Toast.LENGTH_SHORT).show();
        }
    }

    // 🏷️ Show Add Tag Dialog
    private void showAddTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new tag");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("e.g., Vegan, Outdoor Seating");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newTagText = input.getText().toString().trim();
            if (!newTagText.isEmpty()) {
                addChipToGroup(newTagText);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addChipToGroup(String tagText) {
        Chip newChip = new Chip(this);
        newChip.setText(tagText);
        newChip.setCheckable(true);
        newChip.setChecked(true);
        newChip.setCloseIconVisible(true);
        newChip.setOnCloseIconClickListener(v -> chipGroupTags.removeView(newChip));
        chipGroupTags.addView(newChip);
    }

    private List<String> getSelectedTags() {
        List<String> selectedTags = new ArrayList<>();
        List<Integer> checkedChipIds = chipGroupTags.getCheckedChipIds();
        for (Integer id : checkedChipIds) {
            Chip chip = chipGroupTags.findViewById(id);
            if (chip != null) {
                selectedTags.add(chip.getText().toString());
            }
        }
        return selectedTags;
    }

    private void setupBottomNavigation() {
        Button searchButton = findViewById(R.id.btn_search);
        Button collectionButton = findViewById(R.id.btn_col);
        Button newButton = findViewById(R.id.btn_new);
        Button aboutButton = findViewById(R.id.btn_about);

        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewActivity.this, MainActivity.class);
            startActivity(intent);
        });

        collectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewActivity.this, CollectionActivity.class);
            startActivity(intent);
        });

        newButton.setOnClickListener(v ->
                Toast.makeText(this, "You are already on the New Entry screen", Toast.LENGTH_SHORT).show()
        );

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }
}