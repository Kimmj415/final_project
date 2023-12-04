package com.teamproject2.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.teamproject2.R;
import androidx.recyclerview.widget.RecyclerView;

import com.teamproject2.SearchMarketActivity;
import com.teamproject2.models.CVS_item;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class CVSAdapter extends RecyclerView.Adapter<CVSAdapter.ViewHolder>{

    private List<CVS_item> itemList;
    private Activity context;

    public CVSAdapter(List<CVS_item> itemList,Activity context) {
        this.itemList = itemList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cvs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CVS_item item = itemList.get(position);

        // 데이터를 뷰에 바인딩
        holder.brandNameTextView.setText(item.getBrandName());
        holder.productNameTextView.setText(item.getProductName());
        holder.categoryNameTextView.setText(item.getCategoryName());
        holder.priceTextView.setText(item.getPrice()+"원");
        holder.eventNameTextView.setText(item.getEventName());
        String url=item.getImageURL();
        if(item.getBrandName().equals("cu")){
            url="https:"+url;
        }
        else if(item.getBrandName().equals("seven")){
            url=url.replace("http","https");
        }
        Glide.with(context).load(url).override(100,100).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView brandNameTextView;
        private TextView productNameTextView;
        private TextView categoryNameTextView;
        private TextView priceTextView;
        private TextView eventNameTextView;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            brandNameTextView = itemView.findViewById(R.id.brandNameTextView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
