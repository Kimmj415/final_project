package com.teamproject2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private ImageView optionImageView;
    private TextView dateTextView;
    private TextView contentTextView;
    private boolean isBookmark = false;
    ImageView bookmark;
    private EditText commentEditText;
    private Button registerButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        RecyclerView commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        titleTextView = findViewById(R.id.title_tv);
        dateTextView = findViewById(R.id.date_tv);
        contentTextView = findViewById(R.id.content_tv);
        optionImageView=findViewById(R.id.option);
        commentEditText = findViewById(R.id.comment_et);
        registerButton = findViewById(R.id.reg_button);
        bookmark=findViewById(R.id.bookmark);
        db = FirebaseFirestore.getInstance();

        String authorId = getIntent().getStringExtra("AUTHOR_ID");
        String date = getIntent().getStringExtra("DATE");
        String postId = getIntent().getStringExtra("POST_ID");

        checkIfbookmark(postId);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBookmark) {
                    removeshowConfirmationDialog(postId);
                } else {
                    addshowConfirmationDialog(postId);
                }
            }
        });

        optionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 팝업 메뉴 표시
                showPopupMenu(optionImageView, authorId,postId);
            }
        });

        db.collection("board")
                .document(postId)
                .collection("comment")
                .orderBy("timestamp", Query.Direction.DESCENDING) // 타임스탬프 내림차순 정렬
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // 오류 처리
                            return;
                        }

                        // 댓글 데이터를 리스트로 변환
                        List<Comment> comments = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Comment comment = document.toObject(Comment.class);
                            comments.add(comment);
                        }

                        // CommentAdapter에 댓글 데이터 설정
                        CommentAdapter commentAdapter = new CommentAdapter(comments,PostDetailActivity.this);
                        commentRecyclerView.setAdapter(commentAdapter);
                    }
                });


        registerButton.setOnClickListener(view -> {
            String comment = filterProfanity(commentEditText.getText().toString().trim());
            if (!comment.isEmpty()) {
                Date currentDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                String formattedDate = sdf.format(currentDate);
                Comment newcomment = new Comment(comment, mAuth.getCurrentUser().getUid(), formattedDate.toString());
                db.collection("board")
                        .document(postId)
                        .collection("comment")
                        .add(newcomment)
                        .addOnSuccessListener(documentReference -> {
                            // 댓글 추가 성공 시 처리
                            Toast.makeText(PostDetailActivity.this, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            commentEditText.setText("");
                            Intent intent = new Intent(PostDetailActivity.this, PostDetailActivity.class);
                            intent.putExtra("USER_ID", userId);
                            intent.putExtra("DATE", date);
                            intent.putExtra("POST_ID", postId);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // 댓글 추가 실패 시 처리
                            Toast.makeText(PostDetailActivity.this, "댓글 추가 실패.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(PostDetailActivity.this, "댓글 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("board")
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
                                    // Post 객체에서 데이터를 가져와 TextView에 설정
                                    titleTextView.setText(post.getTitle().toString());
                                    dateTextView.setText(post.getTimestamp().toString());
                                    contentTextView.setText(post.getContents().toString());
                                }
                            } else {
                                Toast.makeText(PostDetailActivity.this, "일치하는 게시물 없음", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostDetailActivity.this, "데이터 로딩 실패.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public String filterProfanity(String input) {
        String replacement = "*";
        String[] profanityList = {"시발","씨발","ㅅㅂ","시바","ㅆㅂ","씨바","개같다","개같네", "병신", "좆까", "존나", "미친", "개새끼","찐따","새끼","지랄","ㅈ같네","ㅈ같","좆같","ㅂㅅ","ㅄ"};

        for (String profanity : profanityList) {
            if (input.contains(profanity)) {
                String replacementString = new String(new char[profanity.length()]).replace("\0", replacement);
                input = input.replaceAll(profanity, replacementString);
            }
        }

        return input;
    }

    private void checkIfbookmark(String postId) {
        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> bookmarks = (List<String>) documentSnapshot.get("bookmarks");
                            if (bookmarks != null && bookmarks.contains(postId)) {
                                // 해당 게시판이 즐겨찾기 목록에 있는 경우
                                bookmark.setImageResource(R.drawable.round_star_24);
                                isBookmark = true;
                            } else {
                                // 해당 게시판이 즐겨찾기 목록에 없는 경우
                                bookmark.setImageResource(R.drawable.round_star_border_24);
                                isBookmark = false;
                            }
                        }
                    }
                });
    }

    private void addTobookmark(String postId) {
        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> bookmarks = (List<String>) documentSnapshot.get("bookmarks");
                            if (bookmarks == null) {
                                bookmarks = new ArrayList<>();
                            }
                            bookmarks.add(postId);
                            db.collection("user").document(userId)
                                    .update("bookmarks", bookmarks)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 즐겨찾기 목록에 추가한 경우 아이콘 변경
                                            bookmark.setImageResource(R.drawable.round_star_24);
                                            isBookmark = true;
                                        }
                                    });
                        }
                    }
                });
    }

    private void removeFrombookmark(String postId) {
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
                                                // 즐겨찾기 목록에서 제거한 경우 아이콘 변경
                                                bookmark.setImageResource(R.drawable.round_star_border_24);
                                                isBookmark = false;
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void showPopupMenu(View view, String authorId, String postId) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_options);

        // 팝업 메뉴 아이템 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.menu_delete) {
                    if(authorId.equals(userId)) {
                        deletePost(postId);
                    }
                    else{
                        Toast.makeText(PostDetailActivity.this, "글의 작성자만 게시글을 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        // 팝업 메뉴 표시
        popupMenu.show();
    }

    private void addshowConfirmationDialog(final String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("스크랩 확인");
        builder.setMessage("게시물을 스크랩하시겠습니까?");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 확인을 눌렀을 때 실행되는 코드
                addTobookmark(postId);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 취소를 눌렀을 때 실행되는 코드
                // 여기서는 아무 동작 없음
            }
        });

        // 알림 창 표시
        builder.create().show();
    }

    private void removeshowConfirmationDialog(final String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("스크랩 취소");
        builder.setMessage("스크랩을 취소하시겠습니까?");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 확인을 눌렀을 때 실행되는 코드
                removeFrombookmark(postId);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 취소를 눌렀을 때 실행되는 코드
                // 여기서는 아무 동작 없음
            }
        });

        // 알림 창 표시
        builder.create().show();
    }

    private void deletePost(String postId) {
        // Firestore 인스턴스 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // "boards" 컬렉션에서 해당 postId의 문서 삭제
        db.collection("board").document(postId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 게시글이 성공적으로 삭제됨
                        // 추가로 수행해야 할 동작이 있다면 여기에 구현
                        Toast.makeText(PostDetailActivity.this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PostDetailActivity.this, BoardActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 게시글 삭제 실패
                        Toast.makeText(PostDetailActivity.this, "게시글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}






