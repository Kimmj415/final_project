package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.teamproject2.models.Post;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewCsPostActivity extends AppCompatActivity {
    private Spinner categorySpinner;
    private EditText editTextContent;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String selectedCategory;
    private ImageView backbutton;

    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cs_post);

        categorySpinner = findViewById(R.id.category_spinner);
        editTextContent = findViewById(R.id.content_et);
        submitButton = findViewById(R.id.reg_button);
        db = FirebaseFirestore.getInstance();
        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewCsPostActivity.this, CSBoardActivity.class));
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 선택된 항목의 값을 추출
                selectedCategory = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 사용자가 아무것도 선택하지 않았을 때의 동작
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = selectedCategory.trim();
                String content = editTextContent.getText().toString().trim();
                String author = mAuth.getCurrentUser().getUid();

                Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                String formattedDate = sdf.format(currentDate);
                String timestamp = formattedDate.toString();
                savePostToFirestore2(title, content, author, timestamp);
                savePostToFirestore(title, content, author, timestamp);
            }
        });
    }

    private void savePostToFirestore(String title, String content, String author, String timestamp) {

        String collectionPath = "csboard/";

        Post post = new Post(author, title, content, timestamp);

        db.collection(collectionPath)
                .add(post)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            String documentId = task.getResult().getId();
                            Map<String, Object> data = new HashMap<>();
                            data.put("postId", documentId);
                            data.put("target",target);
                            db.collection(collectionPath).document(documentId)
                                    .set(data, SetOptions.merge()) // 문서를 업데이트 또는 생성 (merge 옵션을 사용하여 중복 방지)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // 데이터 추가 성공
                                                Intent next = new Intent(new Intent(NewCsPostActivity.this, CSBoardActivity.class));
                                                startActivity(next);
                                                finish();
                                            } else {
                                                Toast.makeText(NewCsPostActivity.this, "오류 발생.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(NewCsPostActivity.this, "오류 발생.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void savePostToFirestore2(String title, String content, String author, String timestamp) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String collectionPath = "user/" + userId + "/cs/";
        Post post = new Post(author, title, content, timestamp,"");

        db.collection(collectionPath)
                .add(post)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            String documentId = task.getResult().getId();
                            target=documentId;
                            Map<String, Object> data = new HashMap<>();
                            data.put("postId", documentId);
                            db.collection(collectionPath).document(documentId)
                                    .set(data, SetOptions.merge()) // 문서를 업데이트 또는 생성 (merge 옵션을 사용하여 중복 방지)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // 데이터 추가 성공
                                                Intent next = new Intent(new Intent(NewCsPostActivity.this, CSBoardActivity.class));
                                                startActivity(next);
                                                finish();
                                            } else {
                                                Toast.makeText(NewCsPostActivity.this, "오류 발생.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(NewCsPostActivity.this, "오류 발생.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}