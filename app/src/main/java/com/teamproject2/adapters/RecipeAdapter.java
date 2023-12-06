package com.teamproject2.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.teamproject2.RecipeActivity;
import com.teamproject2.RecipeDetailActivity;
import com.teamproject2.SignupActivity;
import com.teamproject2.UserMainActivity;
import com.teamproject2.models.Recipe;

import java.util.List;
import com.teamproject2.R;
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Activity activity;
    private Context context;

    public RecipeAdapter(List<Recipe> recipeList, Activity activity) {
        this.recipeList = recipeList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe);

        String url = recipe.getImageUrl();
        Glide.with(activity).load(url).override(100, 100).into(holder.recipeImageView);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private TextView recipeNameTextView;
        private TextView summaryTextView;
        private TextView kcalTextView;
        private TextView timeTextView;
        private TextView difficultyTextView;
        private ImageView recipeImageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            kcalTextView = itemView.findViewById(R.id.kcalTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            difficultyTextView = itemView.findViewById(R.id.difficultyTextView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);

            // 아이템 뷰에 클릭 리스너 추가
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭된 레시피 아이템의 위치(position) 가져오기
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 해당 위치의 레시피 가져오기
                        Recipe clickedRecipe = recipeList.get(position);

                        // 레시피 코드를 사용하여 상세 화면으로 이동하는 인텐트 생성
                        Intent intent = new Intent(activity, RecipeDetailActivity.class);
                        intent.putExtra("recipe_code", clickedRecipe.getRecipeCode());
                        intent.putExtra("recipe_name", clickedRecipe.getRecipeName());
                        activity.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Recipe recipe) {
            String recipeName = (recipe.getRecipeName() != null) ? recipe.getRecipeName() : "";
            recipeName = recipeName.replace("\n", "");

            recipeNameTextView.setText(recipeName);
            summaryTextView.setText(recipe.getSummary());
            kcalTextView.setText("Kcal: " + recipe.getKcal());
            timeTextView.setText("Time: " + recipe.getTime());
            difficultyTextView.setText("난이도: " + recipe.getDifficulty());
        }
    }
}
