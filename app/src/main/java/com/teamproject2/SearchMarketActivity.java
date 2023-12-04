package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.protobuf.GeneratedMessageLite;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.widget.Toast;

import com.teamproject2.adapters.CVSAdapter;
import com.teamproject2.models.CVS_item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchMarketActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView backbutton;
    private List<CVS_item> itemList;
    private Button searchbutton;
    private Spinner cvs, promotion, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_market);

        searchbutton=findViewById(R.id.searchbutton);
        cvs=findViewById(R.id.spinnerConvenienceStore);
        promotion=findViewById(R.id.spinnerPromotions);
        category=findViewById(R.id.spinnerCategory);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();

        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchMarketActivity.this, UserMainActivity.class));
            }
        });

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cvsindex=cvs.getSelectedItemPosition();
                int promotionindex=promotion.getSelectedItemPosition();
                int categoryindex=category.getSelectedItemPosition();
                String scvs=(String)cvs.getItemAtPosition(cvsindex);
                String spromotion=(String)promotion.getItemAtPosition(promotionindex);
                String scategory=(String)category.getItemAtPosition(categoryindex);

                if(scvs.equals("CU")){
                    scvs="cu";
                }
                else if(scvs.equals("GS25")){
                    scvs="gs25";
                }
                else if(scvs.equals("미니스톱")){
                    scvs="ministop";
                }
                else if(scvs.equals("세븐일레븐")){
                    scvs="seven";
                }
                else if(scvs.equals("이마트24")){
                    scvs="emart24";
                }

                try {
                    JSONArray jsonArray = new JSONArray(loadJSONFromAsset(scvs));
                    itemList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String brandName = jsonObject.getString("BrandName");
                        String productName = jsonObject.getString("ProductName");
                        String categoryName = jsonObject.getString("CategoryName");
                        String price = jsonObject.getString("Price");
                        String eventName = jsonObject.getString("EventName");
                        String imageURL = jsonObject.getString("ImageURL");
                        if(spromotion.equals(eventName)&&scategory.equals(categoryName)) {
                            CVS_item item = new CVS_item(brandName, productName, categoryName, price, eventName, imageURL);
                            itemList.add(item);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CVSAdapter adapter = new CVSAdapter(itemList,SearchMarketActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private String loadJSONFromAsset(String name) {
        String json;
        try {
            InputStream is = getAssets().open(name+".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}