package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class UserMainActivity extends AppCompatActivity {
    private AdView mAdView;
    private LinearLayout infoPanel;
    private ImageView imagebutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        infoPanel = findViewById(R.id.infoPanel);
        imagebutton=findViewById(R.id.imageView);

        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenInfoPanelClicked(infoPanel);
            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void onOpenInfoPanelClicked(View view) {
        // 개인 정보 패널을 슬라이딩하여 보이도록 함
        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        infoPanel.startAnimation(slideInAnimation);
        infoPanel.setVisibility(View.VISIBLE);
    }

    public void onCloseButtonClicked(View view) {
        // 개인 정보 패널을 숨김
        Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        infoPanel.startAnimation(slideOutAnimation);
        infoPanel.setVisibility(View.GONE);
    }
}