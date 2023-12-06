package com.teamproject2.adapters;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.teamproject2.RecipeDetailActivity;
import com.teamproject2.SignupActivity;
import com.teamproject2.models.DetailRecipe;
import com.teamproject2.R;

import java.util.List;

public class DetailRecipeAdapter extends RecyclerView.Adapter<DetailRecipeAdapter.DetailRecipeViewHolder> {

    private final List<DetailRecipe> recipes;
    private Activity activity;

    public DetailRecipeAdapter(Activity activity, List<DetailRecipe> recipes) {
        this.activity = activity;
        this.recipes = recipes;
    }

    @Override
    public DetailRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_recipe, parent, false);
        return new DetailRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailRecipeViewHolder holder, int position) {
        DetailRecipe recipe = recipes.get(position);
        holder.bind(recipe);
        String url = recipe.getImageUrl();
        Glide.with(activity).load(url).override(100, 100).into(holder.recipeImageView);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class DetailRecipeViewHolder extends RecyclerView.ViewHolder {
        private final TextView explanationTextView;
        private final ImageView recipeImageView;
        private final TextView indexTextView;

        public DetailRecipeViewHolder(View itemView) {
            super(itemView);
            indexTextView = itemView.findViewById(R.id.indexTextView);
            explanationTextView = itemView.findViewById(R.id.explanationTextView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
        }

        public void bind(DetailRecipe recipe) {
            indexTextView.setText(recipe.getIndex()+"단계");
            explanationTextView.setText(recipe.getExplanation());
        }
    }
}
