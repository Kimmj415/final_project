package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

public class UserMainActivity extends AppCompatActivity {
    private AdView mAdView;
    private ImageView menu1,menu2,menu3,menu4;
    private LinearLayout infoPanel;
    private ImageView imagebutton;

    private Button logoutbutton;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private TextView bannerText;
    private Handler handler;
    private int currentBanner = 0;
    private String[] banners = {"슬기로운 자취생활에 오신 것을 환영합니다.","무분별한 욕설 및 비하 발언은 삼가해주세요.", "악성 유저 발각시 사용 정지 처리됩니다.", "오늘도 좋은 하루 되세요!"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        logoutbutton=findViewById(R.id.logoutButton);
        infoPanel = findViewById(R.id.infoPanel);
        imagebutton=findViewById(R.id.imageView);
        bannerText = findViewById(R.id.bannerText);
        menu1=findViewById(R.id.menu_1);
        menu2=findViewById(R.id.menu_2);
        menu3=findViewById(R.id.menu_3);
        menu4=findViewById(R.id.menu_4);

        handler = new Handler();

        updateBanner();

        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, RecipeActivity.class));
            }
        });

        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, SearchMarketActivity.class));
            }
        });

        menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, BoardActivity.class));
            }
        });

        menu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, CSBoardActivity.class));
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateBanner();
                handler.postDelayed(this, 6000);
            }
        }, 6000);

        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(UserMainActivity.this, MainActivity.class));
                Toast.makeText(UserMainActivity.this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

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

    private void updateBanner() {
        if (currentBanner == banners.length) {
            currentBanner = 0;
        }

        String banner = banners[currentBanner];
        bannerText.setText("#"+banner+"#");

        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        bannerText.startAnimation(slideDown);

        currentBanner++;
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