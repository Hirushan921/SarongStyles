package com.jiat.sarongstylesadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstylesadmin.Activity.ItemHistoryActivity;
import com.jiat.sarongstylesadmin.Activity.ItemUpdateActivity;
import com.jiat.sarongstylesadmin.Activity.MainActivity;
import com.jiat.sarongstylesadmin.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Map<String, Object>> itemList;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Context context;

    public ItemAdapter(List<Map<String, Object>> itemList, Context context) {
        this.context=context;
        this.itemList = itemList;
        firestore=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> itemData = itemList.get(position);

        holder.itemIdTextView.setText(String.valueOf(itemData.get("documentId")));
        holder.itemRegDataTextView.setText(String.valueOf(itemData.get("registeredDate")));
        holder.itemNameTextView.setText(String.valueOf(itemData.get("itemName")));
        holder.itemDescriptionTextView.setText(String.valueOf(itemData.get("description")));
        holder.itemQtyTextView.setText("Quantity: "+String.valueOf(itemData.get("qty")));
        holder.itemPriceTextView.setText("Price: "+String.valueOf(itemData.get("price"))+"0");
        holder.itemDeChargeTextView.setText("Delivery Charge: "+String.valueOf(itemData.get("deliveryCharge"))+"0");
        holder.itemStatusTextView.setText(itemData.get("status").equals(true) ? "Enabled" : "Disabled");

        String categoryId = String.valueOf(itemData.get("categoryId"));
        firestore.collection("category")
                .document(categoryId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String categoryName = documentSnapshot.getString("categoryName");
                            holder.itemCategoryTextView.setText(categoryName);
                        } else {

                        }
                    }
                })
                .addOnFailureListener(e -> {

                });

        String imagePath = String.valueOf(itemData.get("image"));
        storage.getReference("item-images/"+imagePath)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .resize(150,150)
                                .centerCrop()
                                .into(holder.itemImage);
                    }
                });

        boolean status = (boolean) itemData.get("status");
        String documentId = String.valueOf(itemData.get("documentId"));

        holder.statusSwitch.setChecked(status);
//
//        holder.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                ItemAdapter.this.updateItemStatus(documentId, isChecked);
//            }
//        });

        holder.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(status==true){
                    updateItemStatusFalse(documentId);
                } else if (status==false) {
                    updateItemStatusTrue(documentId);
                }
            }
        });

        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemUpdateActivity.class);
                intent.putExtra("itemData", (Serializable) itemData);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNameTextView,itemCategoryTextView,itemDescriptionTextView,itemQtyTextView,itemPriceTextView,itemDeChargeTextView,itemStatusTextView,itemRegDataTextView,itemIdTextView;
        private Switch statusSwitch;
        private ImageView itemImage;
        private Button updateButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIdTextView = itemView.findViewById(R.id.itemNo);
            itemRegDataTextView = itemView.findViewById(R.id.itemRegDate);
            itemNameTextView = itemView.findViewById(R.id.itemName);
            itemCategoryTextView = itemView.findViewById(R.id.itemCategory);
            itemDescriptionTextView = itemView.findViewById(R.id.itemDescription);
            itemQtyTextView = itemView.findViewById(R.id.itemQuantity);
            itemPriceTextView = itemView.findViewById(R.id.itemPrice);
            itemDeChargeTextView = itemView.findViewById(R.id.itemDeliveryCharge);
            itemStatusTextView = itemView.findViewById(R.id.itemStatus);
            statusSwitch = itemView.findViewById(R.id.switch1);
            itemImage = itemView.findViewById(R.id.itemImage);

            updateButton = itemView.findViewById(R.id.button_update);

//            updateButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });

        }
    }

    private void updateItemStatusFalse(String documentId) {
        firestore.collection("item")
                .document(documentId)
                .update("status", false)
                .addOnSuccessListener(aVoid -> {
                        // Update successful
                })
                .addOnFailureListener(e -> {
                        // Handle error
                });
    }

    private void updateItemStatusTrue(String documentId) {
        firestore.collection("item")
                .document(documentId)
                .update("status", true)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }



//    private static void onItemClick(int position) {
//        if (position != RecyclerView.NO_POSITION) {
//            Map<String, Object> itemData = itemList.get(position);
//            Intent intent = new Intent(context, ItemDetailView.class);
//            intent.putExtra("itemData", (Serializable) itemData);
//            context.startActivity(intent);
//        }
//    }


}