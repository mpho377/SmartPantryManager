package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Context context;
    private List<PantryItem> pantryList;
    private DatabaseHelper dbHelper;

    public PantryAdapter(Context context, List<PantryItem> pantryList) {
        this.context = context;
        this.pantryList = pantryList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = pantryList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQty.setText(item.getQuantity() + " " + item.getUnit());

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.tvExpiry.setText("Expires: " + item.getExpiryDate());
            holder.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, AddEditPantryActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            intent.putExtra("ITEM_NAME", item.getName());
            intent.putExtra("ITEM_QTY", item.getQuantity());
            intent.putExtra("ITEM_UNIT", item.getUnit());
            intent.putExtra("ITEM_EXPIRY", item.getExpiryDate());
            context.startActivity(intent);
        });

        // Handle Remove Button Click
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    PantryItem itemToRemove = pantryList.get(currentPos);
                    boolean isDeleted = dbHelper.deletePantryItem(itemToRemove.getId());

                    if (isDeleted) {
                        pantryList.remove(currentPos);
                        notifyItemRemoved(currentPos);
                        notifyItemRangeChanged(currentPos, pantryList.size());
                        Toast.makeText(context, itemToRemove.getName() + " removed from pantry", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pantryList.size();
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvExpiry;
        Button btnRemove;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQty = itemView.findViewById(R.id.tvIngredientQty);
            tvExpiry = itemView.findViewById(R.id.tvIngredientExpiry);
            btnRemove = itemView.findViewById(R.id.btnRemovePantryItem);
        }
    }
}