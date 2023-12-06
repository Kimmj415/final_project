package com.teamproject2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.teamproject2.adapters.DetailRecipeAdapter;
import com.teamproject2.models.DetailRecipe;
import com.teamproject2.models.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView foodname;
    private RecyclerView recyclerView;
    private List<DetailRecipe> recipeList = new ArrayList<>();;
    private ImageView backbutton;
    private String recipeCode;
    private DetailRecipeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        foodname=findViewById(R.id.foodname);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecipeDetailActivity.this, RecipeActivity.class));
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("recipe_code")) {
            recipeCode = intent.getStringExtra("recipe_code");
            String recipeName = intent.getStringExtra("recipe_name");
            foodname.setText(recipeName.trim());

            // 여기서부터는 레시피 코드를 사용하여 필요한 작업을 수행
            // 예: 해당 레시피에 대한 데이터를 불러와 화면에 표시
            // ...

        } else {
            // 레시피 코드가 전달되지 않은 경우 처리
            Toast.makeText(this, "레시피 코드를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        }

        recipeList = getRecipesFromJson(recipeCode);
        Collections.sort(recipeList, Comparator.comparingInt(detailRecipe -> Integer.parseInt(detailRecipe.getIndex())));
        adapter = new DetailRecipeAdapter(RecipeDetailActivity.this, recipeList);
        recyclerView.setAdapter(adapter);
    }

    private List<DetailRecipe> getRecipesFromJson(String recipeId) {
        List<DetailRecipe> recipes = new ArrayList<>();

        try {
            // assets 폴더의 recipeinfo.json 파일 열기
            InputStream is = getAssets().open("recipeinfo.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // JSON 데이터 문자열로 변환
            String json = new String(buffer, "UTF-8");

            // JSON 배열 생성
            JSONArray jsonArray = new JSONArray(json);

            // 레시피 코드에 해당하는 레시피 정보 찾기
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String recipeCode = jsonObject.getString("recipe_code");

                if (recipeCode.equals(recipeId)) {
                    // 레시피 정보 생성
                    DetailRecipe recipe = new DetailRecipe();
                    recipe.setRecipeCode(recipeCode);
                    recipe.setIndex(jsonObject.getString("index")); // Add this line
                    recipe.setExplanation(jsonObject.getString("Explanation"));
                    recipe.setImageUrl(jsonObject.getString("imageurl"));
                    recipe.setTip(jsonObject.getString("tip"));

                    recipes.add(recipe); // 추가된 부분
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return recipes;
    }

}

