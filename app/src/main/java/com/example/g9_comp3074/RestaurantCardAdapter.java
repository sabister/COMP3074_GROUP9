package com.example.g9_comp3074;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RestaurantCardAdapter extends RecyclerView.Adapter<RestaurantCardAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private Context context;
    private RestaurantDao restaurantDao;

    public RestaurantCardAdapter(Context context, List<Restaurant> restaurants, RestaurantDao restaurantDao) {
        this.context = context;
        this.restaurants = restaurants;
        this.restaurantDao = restaurantDao;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_card_component, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);

        holder.tvTitle.setText(restaurant.name);
        holder.tvSubtitle.setText(restaurant.description);
        holder.tvTags.setText(restaurant.tags);

        // Details button
        holder.btnDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestActivity.class);
            intent.putExtra("restaurantId", restaurant.id);
            context.startActivity(intent);
        });

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewActivity.class);
            intent.putExtra("restaurantId", restaurant.id);
            context.startActivity(intent);
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            restaurantDao.delete(restaurant);
            restaurants.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, restaurants.size());
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle,tvTags;
        Button btnDetails, btnEdit, btnDelete;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvRestaurantTitle);
            tvSubtitle = itemView.findViewById(R.id.tvRestaurantSubtitle);
            tvTags = itemView.findViewById(R.id.tvRestaurantTags);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}