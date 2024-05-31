package com.jiat.sarongstyles.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstyles.Activity.ItemDetailView;
import com.jiat.sarongstyles.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Map<String, Object>> itemList;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Context context;

    public ItemAdapter(List<Map<String, Object>> itemList,Context context) {
        this.itemList = itemList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_list, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> itemData = itemList.get(position);

        holder.itemNameTextView.setText(String.valueOf(itemData.get("itemName")));
        holder.itemPriceTextView.setText("LKR "+String.valueOf(itemData.get("price"))+"0");

        String imagePath = String.valueOf(itemData.get("image"));
        storage.getReference("item-images/"+imagePath)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .resize(200,200)
                                .centerCrop()
                                .into(holder.itemImage);
                    }
                });



    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNameTextView,itemPriceTextView;
        private ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemName);
            itemPriceTextView = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(ViewHolder.this.getAdapterPosition());
                }
            });

        }
    }


    private void onItemClick(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Map<String, Object> itemData = itemList.get(position);
            Intent intent = new Intent(context, ItemDetailView.class);
            intent.putExtra("itemData", (Serializable) itemData);
            context.startActivity(intent);
        }
    }


}
