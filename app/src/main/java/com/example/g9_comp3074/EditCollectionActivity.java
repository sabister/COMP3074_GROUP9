package com.example.g9_comp3074;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.g9_comp3074.collection_data.Collection;
import com.example.g9_comp3074.collection_data.CollectionDao;
import com.example.g9_comp3074.collection_data.CollectionDatabase;

public class EditCollectionActivity extends AppCompatActivity {

    private EditText edtName, edtDescription;
    private Button btnSave;

    private int collectionId;
    private CollectionDao collectionDao;
    private Collection collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_collection);

        edtName = findViewById(R.id.edtCollectionName);
        edtDescription = findViewById(R.id.edtCollectionDescription);
        btnSave = findViewById(R.id.btnSaveCollection);

        collectionDao = CollectionDatabase.getInstance(this).collectionDao();

        collectionId = getIntent().getIntExtra("collectionId", -1);
        if (collectionId == -1) {
            Toast.makeText(this, "Invalid collection", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCollection();

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadCollection() {
        new Thread(() -> {
            collection = collectionDao.getCollectionById(collectionId);

            runOnUiThread(() -> {
                if (collection != null) {
                    edtName.setText(collection.name);
                    edtDescription.setText(collection.description);
                }
            });
        }).start();
    }

    private void saveChanges() {
        String newName = edtName.getText().toString().trim();
        String newDescr = edtDescription.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            collection.name = newName;
            collection.description = newDescr;
            collectionDao.updateCollection(collection);

            runOnUiThread(() -> {
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
