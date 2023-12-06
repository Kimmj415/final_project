package com.teamproject2;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.teamproject2.adapters.RecipeAdapter;
import com.teamproject2.models.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RecipeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private static final String API_KEY = "???";
    private static final String API_URL = "Grid_20150827000000000227_1";
    private static final String TYPE = "xml";
    private static final String START_INDEX = "1";
    private static final String END_INDEX = "5";
    private String IRDNT_NM;
    private EditText inputing;
    private Button searchbutton;
    private ImageView backbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        inputing=findViewById(R.id.inputing);
        searchbutton=findViewById(R.id.searchbutton);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, RecipeActivity.this);
        recyclerView.setAdapter(recipeAdapter);
        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecipeActivity.this, UserMainActivity.class));
            }
        });
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeList.clear();
                String[] ingredientsArray = inputing.getText().toString().trim().split(",\\s*");
                for (String ingredient : ingredientsArray) {
                    IRDNT_NM = ingredient;
                    new FetchRecipeTask().execute(IRDNT_NM);
                }
            }
        });
    }

    private class FetchRecipeTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            // 네트워크 작업 수행
            String recipeData = fetchData(params[0]);

            // XML 파싱
            return parseXml(recipeData);
        }

        @Override
        protected void onPostExecute(List<String> result) {
            // 작업 완료 후 UI 업데이트
            // recipeIds를 사용하여 필요한 작업 수행
            for (String recipeId : result) {
                Log.d("RecipeActivity", "Recipe ID: " + recipeId);
                Recipe recipe = getRecipeFromJson(recipeId);
                if(recipeList.contains(recipe)){
                    continue;
                }
                if (recipe != null ) {
                    recipeList.add(recipe);
                    Collections.shuffle(recipeList);
                    recipeAdapter.notifyDataSetChanged();
                }

            }
        }

        private Recipe getRecipeFromJson(String recipeId) {
            try {
                // assets 폴더의 foodinfo.json 파일 열기
                InputStream is = getAssets().open("foodinfo.json");
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
                        Recipe recipe = new Recipe();
                        recipe.setRecipeCode(recipeCode);
                        recipe.setRecipeName(jsonObject.getString("recipe_name"));
                        recipe.setSummary(jsonObject.getString("summary"));
                        recipe.setTime(jsonObject.getString("time"));
                        recipe.setKcal(jsonObject.getString("kcal"));
                        recipe.setDifficulty(jsonObject.getString("difficulty"));
                        recipe.setImageUrl(jsonObject.getString("imageurl"));

                        return recipe;
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private List<String> parseXml(String xmlData) {
            List<String> recipeIds = new ArrayList<>();

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(xmlData));

                int eventType = parser.getEventType();
                String currentRecipeId = null;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();

                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if ("RECIPE_ID".equals(tagName)) {
                                currentRecipeId = parser.nextText();
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if ("row".equals(tagName) && currentRecipeId != null) {
                                recipeIds.add(currentRecipeId);
                                currentRecipeId = null;
                            }
                            break;
                    }

                    eventType = parser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            return recipeIds;
        }

        private String fetchData(String ingredient) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String recipeXmlStr = null;

            try {
                // API 요청 URL 생성
                URL url = new URL(buildApiUrl(ingredient));

                // 연결 설정
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // 연결 및 응답 확인
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 데이터 읽기
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    recipeXmlStr = buffer.toString();
                } else {
                    Log.e("RecipeActivity", "HTTP error code: " + responseCode);
                }
            } catch (IOException e) {
                Log.e("RecipeActivity", "Error ", e);
            } finally {
                // 연결 해제
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("RecipeActivity", "Error closing stream", e);
                    }
                }
            }

            return recipeXmlStr;
        }

        private String buildApiUrl(String ingredient) {
            // API 요청 URL 생성
            return "http://211.237.50.150:7080/openapi/sample/" + TYPE + "/" +
                    API_URL + "/" + START_INDEX + "/" + END_INDEX +
                    "?API_KEY=" + API_KEY + "&IRDNT_NM=" + ingredient;
        }

    }
}