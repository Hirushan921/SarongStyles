package com.jiat.sarongstyles.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.jiat.sarongstyles.Activity.CategoryViseItemView;
import com.jiat.sarongstyles.Activity.ItemDetailView;
import  com.jiat.sarongstyles.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Map<String, Object>> categoryList;
    private Context context;

    public CategoryAdapter(List<Map<String, Object>> categoryList,Context context) {
        this.categoryList = categoryList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> categoryData = categoryList.get(position);

        holder.categoryNameText.setText(String.valueOf(categoryData.get("categoryName")));

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryNameText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameText = itemView.findViewById(R.id.categoryListButton);

            categoryNameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(CategoryAdapter.ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }

    private void onItemClick(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Map<String, Object> categoryData = categoryList.get(position);
            Intent intent = new Intent(context, CategoryViseItemView.class);
            intent.putExtra("categoryData", (Serializable) categoryData);
            context.startActivity(intent);
        }
    }



}
