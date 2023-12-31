package com.teamproject2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamproject2.adapters.RecyclerViewAdapter;
import com.teamproject2.models.Post;
import org.apache.commons.text.similarity.JaccardSimilarity;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {

    private FloatingActionButton board_add_button;
    private RecyclerView recyclerView;
    private ImageView backbutton;
    private RecyclerViewAdapter adapter;
    private List<Post> posts = new ArrayList<>();
    private FirebaseFirestore db;
    private SearchView searchView;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        board_add_button = findViewById(R.id.board_add_button);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.postRecyclerView);
        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BoardActivity.this, UserMainActivity.class));
            }
        });

        fetchPostsFromFirestore();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Post> filteredPosts = new ArrayList<>();
                for (Post post : posts) {

                    if (post.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        filteredPosts.add(post);
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(BoardActivity.this));
                adapter = new RecyclerViewAdapter(filteredPosts,BoardActivity.this);
                recyclerView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Post> filteredPosts = new ArrayList<>();
                JaccardSimilarity similarity = new JaccardSimilarity();
                for (Post post : posts) {
                    if(similarity.apply(post.getTitle().toLowerCase(),newText.toLowerCase())>0.4||similarity.apply(post.getContents().toLowerCase(),newText.toLowerCase())>0.4){
                        filteredPosts.add(post);
                    }
                    else {
                        if (post.getTitle().toLowerCase().contains(newText.toLowerCase()) || post.getContents().toLowerCase().contains(newText.toLowerCase())) {
                            filteredPosts.add(post);
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(BoardActivity.this));
                adapter = new RecyclerViewAdapter(filteredPosts,BoardActivity.this);
                recyclerView.setAdapter(adapter);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setLayoutManager(new LinearLayoutManager(BoardActivity.this));
                adapter = new RecyclerViewAdapter(posts,BoardActivity.this);
                recyclerView.setAdapter(adapter);
                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(BoardActivity.this));
        adapter = new RecyclerViewAdapter(posts, BoardActivity.this);
        recyclerView.setAdapter(adapter);

        board_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(BoardActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });
    }

    private void fetchPostsFromFirestore() {
        db = FirebaseFirestore.getInstance();
        String collectionPath = "board/";
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