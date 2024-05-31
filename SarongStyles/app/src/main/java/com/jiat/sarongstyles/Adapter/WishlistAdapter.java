package com.jiat.sarongstyles.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstyles.Activity.ItemDetailView;
import com.jiat.sarongstyles.R;
import com.squareup.picasso.Picasso;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private List<Map<String, Object>> wishList;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private Map<String, Object> itemData;

    public WishlistAdapter(List<Map<String, Object>> wishList,Context context) {
        this.wishList = wishList;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist_list, parent, false);
        return new WishlistAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.ViewHolder holder, int position) {
        Map<String, Object> wishData = wishList.get(position);

        String itemId = String.valueOf(wishData.get("itemID"));
        firestore.collection("item")
                .document(itemId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String itemName = documentSnapshot.getString("itemName");
                            String imagePath = documentSnapshot.getString("image");
                            loadImage(imagePath,holder.itemImage);
                            holder.itemNameTextView.setText(itemName);
                            holder.itemPriceTextView.setText("LKR "+String.valueOf(documentSnapshot.get("price"))+"0");
                        } else {

                        }
                    }
                })
                .addOnFailureListener(e -> {

                });

        String documentId = String.valueOf(wishData.get("wishDocumentId"));
        holder.wishDeleteRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    deleteWishItem(documentId,userEmail);
                }else{

                }

            }
        });


    }



    @Override
    public int getItemCount() {
        return wishList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNameTextView,itemPriceTextView;
        private ImageView itemImage;
        private RelativeLayout wishDeleteRelativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemName);
            itemPriceTextView = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemImage);
            wishDeleteRelativeLayout = itemView.findViewById(R.id.wish_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(WishlistAdapter.ViewHolder.this.getAdapterPosition());
                }
            });

        }
    }


    private void onItemClick(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Map<String, Object> wishData = wishList.get(position);
            Intent intent = new Intent(context, ItemDetailView.class);
            intent.putExtra("itemData", (Serializable) wishData);
            context.startActivity(intent);
        }
    }

    private void deleteWishItem(String documentId,String userEmail) {
        CollectionReference userWishCollection = firestore.collection("users").document(userEmail).collection("wishlist");
        userWishCollection.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {
                    // Handle the failure to delete the item
                });
    }


    private void loadImage(String imagePath,ImageView imageView){
        if (imagePath != null) {
            storage.getReference("item-images/"+imagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Picasso.get()
                                .load(uri)
                                .resize(200, 200)
                                .centerCrop()
                                .into(imageView);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TAG", "Error loading image from Storage", e);
                    });
        } else {
            Log.e("TAG", "imagePath is null");
        }
    }

}


