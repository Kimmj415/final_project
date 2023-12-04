package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class UserMainActivity extends AppCompatActivity {
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
    private Handler handler;
    private int currentBanner = 0;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String[] banners = {"슬기로운 자취생활에 오신 것을 환영합니다.","무분별한 욕설 및 비하 발언은 삼가해주세요.", "악성 유저 발각시 사용 정지 처리됩니다.", "오늘도 좋은 하루 되세요!"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

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