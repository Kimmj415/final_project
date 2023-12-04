package com.teamproject2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.teamproject2.models.RecipeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.*;

public class RecipeActivity extends AppCompatActivity {

    private EditText editTextQuestion;
    private TextView textViewResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        editTextQuestion = findViewById(R.id.editTextQuestion);
        textViewResponse = findViewById(R.id.textViewResponse);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userQuestion = editTextQuestion.getText().toString().trim();
                callOpenAIAPI(userQuestion);
            }
        });
    }

    private void callOpenAIAPI(String userQuestion) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //RecipeListActivity.recipeItemList = null;
        String question = "Using"+userQuestion+", give me 3, 4 food name and ingredients. without numbering output format:food name - ingredients";
        JSONObject object = new JSONObject();
        try {
            object.put("model", "text-davinci-003");
            object.put("prompt", question);
            object.put("max_tokens", 100);
            object.put("temperature", 0.7);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        RecipeListActivity.recipeItemList.clear();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        String[] recipes = result.split("\n");
                        for (String recipe : recipes) {
                            String[] parts = recipe.split("- ");
                            if (parts.length == 2) {
                                String recipeName = parts[0].trim();
                                String recipeIngredients = parts[1].trim();
                                RecipeItem newRecipeItem = new RecipeItem(recipeName, recipeIngredients);
                                RecipeListActivity.recipeItemList.add(newRecipeItem);
                            }
                        }
                        runOnUiThread(() -> textViewResponse.setText(result.trim()));
                        Intent intent = new Intent(RecipeActivity.this, RecipeListActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> textViewResponse.setText("Failed"));
                }
            }
        });
    }
}