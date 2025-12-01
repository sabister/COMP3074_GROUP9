package com.example.g9_comp3074;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.g9_comp3074.collection_data.Collection;
import com.example.g9_comp3074.collection_data.CollectionDatabase;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NewCollection extends AppCompatActivity {

    private EditText etCollectionName, etCollectionDescription;
    private ChipGroup chipGroupTags;
    private Button btnCreate, btnAddTag;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_collection);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        etCollectionName = findViewById(R.id.colName);
        etCollectionDescription = findViewById(R.id.colDescription);
        chipGroupTags = findViewById(R.id.tag_chip_group);
        btnCreate = findViewById(R.id.btn_create);
        btnAddTag = findViewById(R.id.btn_add_tag);
    }

    private void setupClickListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());
        btnCreate.setOnClickListener(v -> saveCollection());
        btnAddTag.setOnClickListener(v -> showAddTagDialog());
    }

    private void saveCollection() {
        String collectionName = etCollectionName.getText().toString().trim();
        String collectionDescription = etCollectionDescription.getText().toString().trim();

        if (collectionName.isEmpty()) {
            Toast.makeText(this, "Please enter a collection name", Toast.LENGTH_SHORT).show();
            return;
        }

        String tagsString = getSelectedTagsAsString(); // Assuming you have this helper method

        Collection newCollection = new Collection(collectionName, collectionDescription);
        newCollection.tags = tagsString;
        new Thread(() -> {
            CollectionDatabase.getInstance(getApplicationContext()).collectionDao().insert(newCollection);
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Collection Saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
    private void showAddTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new tag");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("e.g., Favorites, Wishlist, Visited");
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
        newChip.setCloseIconVisible(true);
        newChip.setCheckable(false); // Tags are just for display/removal here
        newChip.setClickable(false);
        newChip.setOnCloseIconClickListener(v -> chipGroupTags.removeView(newChip));
        chipGroupTags.addView(newChip);
    }

    private String getSelectedTagsAsString() {
        List<String> tagsList = new ArrayList<>();
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            tagsList.add(chip.getText().toString());
        }
        return String.join(",", tagsList);
    }
}
