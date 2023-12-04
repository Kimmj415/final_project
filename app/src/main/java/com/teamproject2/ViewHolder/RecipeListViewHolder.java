package com.teamproject2.ViewHolder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamproject2.R;

public class RecipeListViewHolder extends RecyclerView.ViewHolder {
    public TextView nameView, ingredientView;
    public RecipeListViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.name);
        ingredientView = itemView.findViewById(R.id.ingredient);
    }
}
