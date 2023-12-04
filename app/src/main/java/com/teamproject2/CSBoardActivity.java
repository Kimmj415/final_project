package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamproject2.adapters.CsPostAdapter;
import com.teamproject2.adapters.RecyclerViewAdapter;
import com.teamproject2.models.Post;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CSBoardActivity extends AppCompatActivity {

    private FloatingActionButton board_add_button;
    private RecyclerView recyclerView;
    private CsPostAdapter adapter;
    private List<Post> posts = new ArrayList<>();
    private FirebaseFirestore db;
    private ImageView backbutton;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csboard);

        board_add_button = findViewById(R.id.board_add_button);
        recyclerView = findViewById(R.id.postRecyclerView);
        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CSBoardActivity.this, UserMainActivity.class));
            }
        });

        fetchPostsFromFirestore();

        recyclerView.setLayoutManager(new LinearLayoutManager(CSBoardActivity.this));
        adapter = new CsPostAdapter(posts, CSBoardActivity.this);
        recyclerView.setAdapter(adapter);

        board_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(CSBoardActivity.this, NewCsPostActivity.class);
                startActivity(newPostIntent);
            }
        });
    }

    private void fetchPostsFromFirestore() {
        db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String collectionPath = "user/" + userId + "/cs/";
        Query query = db.collection(collectionPath)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Post post = documentSnapshot.toObject(Post.class);
                            posts.add(post);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}