package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamproject2.adapters.CommentAdapter;
import com.teamproject2.models.Comment;
import com.teamproject2.models.Post;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CsPostDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView contentTextView;
    private TextView commentTextView;
    private ImageView backbutton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cs_post_detail);
        titleTextView = findViewById(R.id.title_tv);
        dateTextView = findViewById(R.id.date_tv);
        contentTextView = findViewById(R.id.content_tv);
        commentTextView=findViewById(R.id.comment_tv);
        db = FirebaseFirestore.getInstance();
        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CsPostDetailActivity.this, CSBoardActivity.class));
            }
        });

        String authorId = getIntent().getStringExtra("AUTHOR_ID");
        String date = getIntent().getStringExtra("DATE");
        String postId = getIntent().getStringExtra("POST_ID");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String collectionPath = "user/" + userId + "/cs/";
        db.collection(collectionPath)
                .whereEqualTo("postId", postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // 일치하는 문서가 하나 이상 있다면, 첫 번째 문서를 사용합니다.
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                // DocumentSnapshot으로부터 Post 객체 가져오기
                                Post post = document.toObject(Post.class);
                                if (post != null) {
                                    titleTextView.setText(post.getTitle().toString());
                                    dateTextView.setText(post.getTimestamp().toString());
                                    contentTextView.setText(post.getContents().toString());
                                    commentTextView.setText(post.getComment().toString());
                                }
                            } else {
                                Toast.makeText(CsPostDetailActivity.this, "일치하는 게시물 없음", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CsPostDetailActivity.this, "데이터 로딩 실패.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}