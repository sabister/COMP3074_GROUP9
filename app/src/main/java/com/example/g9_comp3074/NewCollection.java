package com.example.g9_comp3074;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.g9_comp3074.collection_data.CollectionDatabase;
import com.example.g9_comp3074.collection_data.Collection;

public class NewCollection extends AppCompatActivity {

    private EditText etCollectionName;
    private EditText etCollectionDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_collection);


        etCollectionName = findViewById(R.id.colName);
        etCollectionDescription = findViewById(R.id.colDescription);
        Button btnCreateCollection = findViewById(R.id.btn_create);

        btnCreateCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCollection();
            }
        });
    }

    private void saveCollection() {
        String collectionName = etCollectionName.getText().toString().trim();
        String collectionDescription = etCollectionDescription.getText().toString().trim();

        if (collectionName.isEmpty()) {
            Toast.makeText(this, "Please enter a collection name", Toast.LENGTH_SHORT).show();
            return;
        }


        Collection newCollection = new Collection(collectionName, collectionDescription);

        new Thread(() -> {
            CollectionDatabase.getInstance(getApplicationContext())
                    .collectionDao()
                    .insert(newCollection);

            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Collection Saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
