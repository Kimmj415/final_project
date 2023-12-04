package com.teamproject2;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    String recipeName;
    String recipeIngredient;
    String[] recipeSteps;
    TextView recipeStep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipeName = extras.getString("recipeName");
            recipeIngredient = extras.getString("recipeIngredient");
            recipeSteps = extras.getStringArray("recipeSteps");
            TextView nameDetail = findViewById(R.id.nameDetail);
            TextView ingredientDetail = findViewById(R.id.ingredientDetail);
            TextView stepsDetail = findViewById(R.id.stepsDetail);
            recipeStep = findViewById(R.id.stepsDetail);
            if (recipeName != null) {
                nameDetail.setText(recipeName);
            } else {
                nameDetail.setText("Recipe Name Not Available");
            }

            if (recipeIngredient != null) {
                ingredientDetail.setText("Ingredients: " + recipeIngredient);
            } else {
                ingredientDetail.setText("Ingredients: Not Available");
            }

            // Construct steps text
            StringBuilder stepsText = new StringBuilder("Steps:");
            if (recipeSteps != null && recipeSteps.length > 0) {
                for (int i = 0; i < recipeSteps.length; i++) {
                    stepsText.append(i + 1).append(". ").append(recipeSteps[i]);
                }
            } else {
                stepsText.append("No steps available.");
            }
            recipeStep.setText(stepsText.toString());
            callOpenAIAPI(recipeName, recipeIngredient);
            stepsDetail.setText(stepsText.toString());
        }
    }

    private void callOpenAIAPI(String recipeName, String recipeIngredient) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String prompt =" give me simple less than 4 steps for " + recipeName;

        JSONObject object = new JSONObject();
        try {
            object.put("model", "text-davinci-003");
            object.put("prompt", prompt);
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String result = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text");
                        StringBuilder processedResult = new StringBuilder();
                        String[] steps = result.split("\n");
                        for (int i = 0; i < steps.length; i++) {
                            processedResult.append(i + 1).append(". ").append(steps[i]).append("\n");
                        }
                        runOnUiThread(() -> recipeStep.setText(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> recipeStep.setText("Failed to get recipe details"));
                }
            }
        });
    }
}

