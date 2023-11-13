package com.teamproject2.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Context;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamproject2.CsPostDetailActivity;
import com.teamproject2.PostDetailActivity;
import com.teamproject2.models.Post;
import com.teamproject2.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsPostAdapter extends RecyclerView.Adapter<CsPostAdapter.ViewHolder> {

    private List<Post> posts;
    private Activity activity;

    public CsPostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    public CsPostAdapter(List<Post> posts, Activity activity) {
        this.posts = posts;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cspost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        if(post.getComment().toString().equals("")){
            holder.titleTextView.setText(post.getTitle().toString()+"[답변대기]");
        }
        else{
            holder.titleTextView.setText(post.getTitle().toString()+"[답변완료]");
        }

        if (post.getContents().length()>15){
            holder.contentTextView.setText(post.getContents().substring(0,15)+"...");
        }
        else{
            holder.contentTextView.setText(post.getContents());
        }
        holder.timeTextView.setText(post.getTimestamp().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CsPostDetailActivity.class);
                intent.putExtra("AUTHOR_ID", post.getuserId());
                intent.putExtra("DATE", post.getTimestamp());
                intent.putExtra("POST_ID", post.getPostId());
                activity.startActivity(intent);

            }

        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView contentTextView;
        public TextView timeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_post_title);
            contentTextView = itemView.findViewById(R.id.item_post_contents);
            timeTextView=itemView.findViewById(R.id.item_post_time);
        }
    }
}