package com.teamproject2.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamproject2.R;
import com.teamproject2.ViewHolder.RecipeListViewHolder;
import com.teamproject2.models.RecipeItem;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeListViewHolder> {

    private Context context;
    private List<RecipeItem> recipeList;
    public interface OnRecipeItemClickListener {
        void onRecipeItemClick(RecipeItem recipe);
    }

    private OnRecipeItemClickListener listener;

    public RecipeAdapter(Context context, List<RecipeItem> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        //this.listener = listener;
    }
    public RecipeAdapter(Context context, List<RecipeItem> recipeList, OnRecipeItemClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public RecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListViewHolder holder, int position) {
        final RecipeItem recipe = recipeList.get(position);

        holder.ingredientView.setText(recipe.getIngredient());
        holder.nameView.setText(recipe.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecipeItemClick(recipe);
            }
        });
    }
    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}