package com.example.g9_comp3074.collection_data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Import Toast for user feedback

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.g9_comp3074.EditCardComponent;
import com.example.g9_comp3074.R;
import com.example.g9_comp3074.RestActivity;

import java.util.List;

public class CollectionCardAdapter extends RecyclerView.Adapter<CollectionCardAdapter.CollectionViewHolder> {

    private List<Collection> collections;
    private Context context;
    private CollectionDao collectionDao;

    public CollectionCardAdapter(Context context, List<Collection> collections, CollectionDao collectionDao) {
        this.context = context;
        this.collections = collections;
        this.collectionDao = collectionDao;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_component, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Collection collection = collections.get(position);

        holder.colTitle.setText(collection.name);
        holder.colSubtitle.setText(collection.description);

        holder.btnDetails.setOnClickListener(v -> {
            // TODO: Create a new Activity to show the contents of a collection.
            // For example, CollectionDetailActivity.class
            // Intent intent = new Intent(context, CollectionDetailActivity.class);
            // intent.putExtra("collectionId", collection.id);
            // context.startActivity(intent);
            Toast.makeText(context, "Details for " + collection.name, Toast.LENGTH_SHORT).show();
        });

        // Edit button should open an activity to edit the collection's details.
        holder.btnEdit.setOnClickListener(v -> {
            // TODO: Create a new Activity to edit a collection.
            // Intent intent = new Intent(context, EditCollectionActivity.class);
            // intent.putExtra("collectionId", collection.id);
            // context.startActivity(intent);
            Toast.makeText(context, "Editing " + collection.name, Toast.LENGTH_SHORT).show();
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            // Perform database operation in a background thread
            new Thread(() -> {
                collectionDao.delete(collection);
                // Switch back to the main thread to update the UI
                ((android.app.Activity) context).runOnUiThread(() -> {
                    collections.remove(position);
                    notifyItemRemoved(position);
                    // It's important to notify the adapter of the range change
                    notifyItemRangeChanged(position, collections.size());
                    Toast.makeText(context, "Deleted: " + collection.name, Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    // This ViewHolder should be specific to the Collection item.
    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        TextView colTitle, colSubtitle;
        Button btnDetails, btnEdit, btnDelete;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your collection_component.xml
            colTitle = itemView.findViewById(R.id.btnColTitle);
            colSubtitle = itemView.findViewById(R.id.btnColSubtitle);
            btnDetails = itemView.findViewById(R.id.btnColDetail);
            btnEdit = itemView.findViewById(R.id.btnColEdit);
            btnDelete = itemView.findViewById(R.id.btnColDelete);
        }
    }
}
