package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.teamproject2.models.Post;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageView backbutton;

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        editTextTitle = findViewById(R.id.title_et);
        editTextContent = findViewById(R.id.content_et);
        submitButton = findViewById(R.id.reg_button);
        db = FirebaseFirestore.getInstance();
        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewPostActivity.this, BoardActivity.class));
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString().trim();
                String content = editTextContent.getText().toString().trim();
                String author = mAuth.getCurrentUser().getUid();

                Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                String formattedDate = sdf.format(currentDate);
                String timestamp = formattedDate.toString();

                savePostToFirestore(title, content, author, timestamp);
            }
        });
    }

    private void savePostToFirestore(String title, String content, String author, String timestamp) {
        String collectionPath = "board/";
        Post post = new Post(author, title, content, timestamp);

        db.collection(collectionPath)
                .add(post)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            String documentId = task.getResult().getId();
                            addmyposts(documentId);
                            Map<String, Object> data = new HashMap<>();
                            data.put("postId", documentId);
                            db.collection(collectionPath).document(documentId)
                                    .set(data, SetOptions.merge()) // 문서를 업데이트 또는 생성 (merge 옵션을 사용하여 중복 방지)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // 데이터 추가 성공
                                                Intent next = new Intent(new Intent(NewPostActivity.this, BoardActivity.class));
                                                startActivity(next);
                                                finish();
                                            } else {
                                                Toast.makeText(NewPostActivity.this, "오류 발생.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(NewPostActivity.this, "오류 발생.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addmyposts(String postId){

        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> myposts = (List<String>) documentSnapshot.get("myposts");

                            if (myposts == null) {
                                myposts = new ArrayList<>();
                            }
                            myposts.add(postId);

                            db.collection("user").document(userId)
                                    .update("myposts", myposts)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(NewPostActivity.this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }


}