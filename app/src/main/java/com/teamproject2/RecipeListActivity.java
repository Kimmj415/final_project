package com.teamproject2;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamproject2.adapters.RecipeAdapter;
import com.teamproject2.models.RecipeItem;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeItemClickListener {
    public static List<RecipeItem> recipeItemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);
        RecyclerView recyclerView = findViewById(R.id.recipeRecyclerView);
        RecipeAdapter adapter = new RecipeAdapter(this, recipeItemList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onRecipeItemClick(RecipeItem recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipeName", recipe.getName());
        intent.putExtra("recipeIngredient", recipe.getIngredient());
        intent.putExtra("recipeSteps", recipe.getSteps());
        startActivity(intent);
    }
}

