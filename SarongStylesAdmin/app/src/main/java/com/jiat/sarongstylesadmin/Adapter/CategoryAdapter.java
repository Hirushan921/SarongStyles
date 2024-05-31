package com.jiat.sarongstylesadmin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstylesadmin.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Map<String, Object>> categoryList;
    private FirebaseFirestore firestore;

    public CategoryAdapter(List<Map<String, Object>> categoryList) {
        this.categoryList = categoryList;
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_manage_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> categoryData = categoryList.get(position);

        holder.categoryNameTextView.setText(String.valueOf(categoryData.get("categoryName")));
        holder.categoryIdTextView.setText(String.valueOf(categoryData.get("documentId")));
        holder.regDateTextView.setText(String.valueOf(categoryData.get("registeredDate")));
        holder.statusTextView.setText(String.valueOf(categoryData.get("status")));
        holder.statusTextView.setText(categoryData.get("status").equals(true) ? "Enabled" : "Disabled");

        boolean status = (boolean) categoryData.get("status");
        String documentId = String.valueOf(categoryData.get("documentId"));

        holder.statusSwitch.setChecked(status);

        holder.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CategoryAdapter.this.updateCategoryStatus(documentId, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryNameTextView,regDateTextView,categoryIdTextView,statusTextView;
        private Switch statusSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryName);
            statusTextView = itemView.findViewById(R.id.status);
            categoryIdTextView = itemView.findViewById(R.id.categoryNo);
            regDateTextView = itemView.findViewById(R.id.regDate);
            statusSwitch = itemView.findViewById(R.id.switch1);
        }
    }

    private void updateCategoryStatus(String documentId, boolean newStatus) {
        firestore.collection("category")
                .document(documentId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

}
