package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamproject2.adapters.CsPostAdapter;
import com.teamproject2.adapters.RecyclerViewAdapter;
import com.teamproject2.models.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserMainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private AdView mAdView;
    private ImageView menu1,menu2,menu3,menu4;
    private ConstraintLayout infoPanel;
    private RecyclerView mypost, bookmark;
    private RecyclerViewAdapter mypostadapter, bookmarkadapter;
    private List<Post> bookmarklist = new ArrayList<>();
    private List<Post> mypostlist= new ArrayList<>();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private ImageView imagebutton;

    private Button logoutbutton;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private TextView bannerText;
    private String[] foodNames={"햄버거","피자","핫도그","토스트","떡볶이","우동","초밥","타코야끼","호떡","치킨","주먹밥","김밥","회","비빔밥","떡꼬치","라멘","삼계탕","짜장면","생선구이","만두","샐러드","파스타","스테이크"};
    private Handler handler;
    private int currentBanner = 0;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String[] banners = {"슬기로운 자취생활에 오신 것을 환영합니다.","무분별한 욕설 및 비하 발언은 삼가해주세요.", "악성 유저 발각시 사용 정지 처리됩니다.", "오늘도 좋은 하루 되세요!"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        imageView = findViewById(R.id.imageView4);
        textView = findViewById(R.id.textView);
        bookmark=findViewById(R.id.bookmark);
        mypost=findViewById(R.id.mypost);
        logoutbutton=findViewById(R.id.logoutButton);
        infoPanel = findViewById(R.id.infoPanel);
        imagebutton=findViewById(R.id.imageView);
        bannerText = findViewById(R.id.bannerText);
        menu1=findViewById(R.id.menu_1);
        menu2=findViewById(R.id.menu_2);
        menu3=findViewById(R.id.menu_3);
        menu4=findViewById(R.id.menu_4);

        handler = new Handler();

        fetchBookmarksFromFirestore();
        fetchMypostsFromFirestore();

        ConstraintLayout constraintLayout = findViewById(R.id.foodlayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSlotMachineAnimation(6);
            }
        });

        bookmark.setLayoutManager(new LinearLayoutManager(UserMainActivity.this));
        bookmarkadapter = new RecyclerViewAdapter(bookmarklist, UserMainActivity.this);
        bookmark.setAdapter(bookmarkadapter);

        mypost.setLayoutManager(new LinearLayoutManager(UserMainActivity.this));
        mypostadapter = new RecyclerViewAdapter(mypostlist, UserMainActivity.this);
        mypost.setAdapter(mypostadapter);

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
                handler.postDelayed(this, 4500);
            }
        }, 4500);

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

    private void startSlotMachineAnimation(final int repeatCount) {
        final ObjectAnimator imageTranslationY = ObjectAnimator.ofFloat(imageView, "translationY", 0f, 500f);
        imageTranslationY.setDuration(100);
        imageTranslationY.setInterpolator(new DecelerateInterpolator());

        final ObjectAnimator textTranslationY = ObjectAnimator.ofFloat(textView, "translationY", 0f, 500f);
        textTranslationY.setDuration(100);
        textTranslationY.setInterpolator(new DecelerateInterpolator());

        // 여기서 변경된 부분
        imageTranslationY.addListener(new AnimatorListenerAdapter() {
            int count = 0;

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                // 반복 횟수만큼 애니메이션을 반복
                if (count < repeatCount) {
                    count++;

                    // 초기 위치로 되돌리기 위해 translationY를 0으로 설정
                    ObjectAnimator resetImageTranslationY = ObjectAnimator.ofFloat(imageView, "translationY", 500f, 0f);
                    resetImageTranslationY.setDuration(0);

                    // 텍스트뷰도 초기 위치로 되돌리기 위해 translationY를 0으로 설정
                    ObjectAnimator resetTextTranslationY = ObjectAnimator.ofFloat(textView, "translationY", 500f, 0f);
                    resetTextTranslationY.setDuration(0);

                    // 랜덤한 음식 선택
                    Random random = new Random();
                    int randomIndex = random.nextInt(foodNames.length);
                    String selectedFood = foodNames[randomIndex];

                    // 음식 이미지 설정
                    int imageResourceId = getResources().getIdentifier("food" + (randomIndex + 1), "drawable", getPackageName());
                    imageView.setImageResource(imageResourceId);

                    // 음식 이름 설정
                    textView.setText(selectedFood);

                    // 초기 위치로 되돌리기
                    resetImageTranslationY.start();
                    resetTextTranslationY.start();

                    // 딜레이를 줘서 다음 애니메이션 시작
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageTranslationY.start();
                            textTranslationY.start();
                        }
                    }, 0);
                }
                ObjectAnimator resetImageTranslationY = ObjectAnimator.ofFloat(imageView, "translationY", 500f, 0f);
                resetImageTranslationY.setDuration(0);

                // 텍스트뷰도 초기 위치로 되돌리기 위해 translationY를 0으로 설정
                ObjectAnimator resetTextTranslationY = ObjectAnimator.ofFloat(textView, "translationY", 500f, 0f);
                resetTextTranslationY.setDuration(0);
                Random random = new Random();
                int randomIndex = random.nextInt(foodNames.length);
                String selectedFood = foodNames[randomIndex];

                // 음식 이미지 설정
                int imageResourceId = getResources().getIdentifier("food" + (randomIndex + 1), "drawable", getPackageName());
                imageView.setImageResource(imageResourceId);

                // 음식 이름 설정
                textView.setText(selectedFood+" 어때?");
                resetImageTranslationY.start();
                resetTextTranslationY.start();
            }
        });

        // 첫 번째 애니메이션 시작
        imageTranslationY.start();
        textTranslationY.start();
    }



    private void fetchBookmarksFromFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // 사용자의 문서가 존재할 경우
                    // bookmarks 필드의 값을 가져와서 처리
                    List<String> bookmarks = (List<String>) document.get("bookmarks");
                    if (bookmarks != null) {
                        // bookmarks 배열이 존재할 경우 처리
                        for (String bookmarkId : bookmarks) {
                            // 각 북마크에 대한 작업 수행
                            getBookmark(bookmarkId);
                        }
                    } else {
                        // bookmarks 배열이 존재하지 않을 경우 처리
                        // 예: Log.d("Info", "Bookmarks not found");
                    }
                } else {
                    // 사용자의 문서가 존재하지 않을 경우 처리
                    // 예: Log.d("Info", "User document not found");
                }
            } else {
                // 읽기 작업이 실패한 경우 처리
                // 예: Log.e("Error", "Error getting user document", task.getException());
            }
        });
    }

    private void fetchMypostsFromFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("user").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // 사용자의 문서가 존재할 경우
                    // bookmarks 필드의 값을 가져와서 처리
                    List<String> myposts = (List<String>) document.get("myposts");
                    if (myposts != null) {
                        // bookmarks 배열이 존재할 경우 처리
                        for (String mypostId : myposts) {
                            // 각 북마크에 대한 작업 수행
                            getMypost(mypostId);
                        }
                    } else {
                        // bookmarks 배열이 존재하지 않을 경우 처리
                        // 예: Log.d("Info", "Bookmarks not found");
                    }
                } else {
                    // 사용자의 문서가 존재하지 않을 경우 처리
                    // 예: Log.d("Info", "User document not found");
                }
            } else {
                // 읽기 작업이 실패한 경우 처리
                // 예: Log.e("Error", "Error getting user document", task.getException());
            }
        });
    }

    private void getMypost(String postId) {
        DocumentReference postRef = db.collection("board").document(postId);

        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot postDocument = task.getResult();
                if (postDocument.exists()) {
                    Post post = postDocument.toObject(Post.class);
                    mypostlist.add(post);
                    mypostadapter.notifyDataSetChanged();
                } else {
                    // 게시글 문서가 존재하지 않을 경우 처리
                    // 예: Log.d("Info", "Post document not found");
                }
            } else {
                // 읽기 작업이 실패한 경우 처리
                // 예: Log.e("Error", "Error getting post document", task.getException());
            }
        });
    }

    private void getBookmark(String postId) {
        DocumentReference postRef = db.collection("board").document(postId);

        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot postDocument = task.getResult();
                if (postDocument.exists()) {
                    Post post = postDocument.toObject(Post.class);
                    bookmarklist.add(post);
                    bookmarkadapter.notifyDataSetChanged();
                    // 게시글 문서가 존재할 경우
                    // 가져온 게시글을 리사이클러뷰에 추가하거나 처리
                    // 예: String postTitle = postDocument.getString("title");
                    //     Log.d("Post Title", postTitle);
                } else {
                    // 게시글 문서가 존재하지 않을 경우 처리
                    // 예: Log.d("Info", "Post document not found");

                    db.collection("user").document(userId)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        List<String> bookmarks = (List<String>) documentSnapshot.get("bookmarks");
                                        if (bookmarks != null) {
                                            bookmarks.remove(postId);
                                            // Firestore에 업데이트된 favoriteBoards 리스트 저장
                                            db.collection("user").document(userId)
                                                    .update("bookmarks", bookmarks)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                }
            } else {
                // 읽기 작업이 실패한 경우 처리
                // 예: Log.e("Error", "Error getting post document", task.getException());
            }
        });
    }

    private void updateBanner() {
        if (currentBanner == banners.length) {
            currentBanner = 0;
        }

        String banner = banners[currentBanner];
        bannerText.setText(banner);

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