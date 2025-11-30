package com.example.g9_comp3074;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        holder.btnDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestActivity.class);
            intent.putExtra("restaurantId", restaurant.id);
            context.startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCardComponent.class);
            intent.putExtra("restaurantId", restaurant.id);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                restaurantDao.delete(restaurant);
                ((Activity) context).runOnUiThread(() -> {
                    restaurants.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, restaurants.size());
                    Toast.makeText(context, "Deleted: " + restaurant.name, Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void filterList(List<Restaurant> filteredList) {
        this.restaurants = filteredList;
        notifyDataSetChanged();
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
