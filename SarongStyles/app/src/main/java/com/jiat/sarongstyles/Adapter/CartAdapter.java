package com.jiat.sarongstyles.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.StorageException;
import com.jiat.sarongstyles.R;
import com.squareup.picasso.Picasso;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Map<String, Object>> cartList;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;



    Context context;


    public CartAdapter(List<Map<String, Object>> cartList) {
        this.cartList = cartList;
        firestore= FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> cartData = cartList.get(position);

        holder.itemQtyTextView.setText(String.valueOf(cartData.get("itemsQty")+" items"));
        holder.itemPriceTextView.setText("Price: LKR "+String.valueOf(cartData.get("itemsPrice"))+"0");

        String itemId = String.valueOf(cartData.get("itemID"));
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
                            holder.itemDeChargeTextView.setText("Delivery Charge: LKR "+String.valueOf(documentSnapshot.get("deliveryCharge"))+"0");
                        } else {

                        }
                    }
                })
                .addOnFailureListener(e -> {

                });


        String documentId = String.valueOf(cartData.get("documentId"));
        holder.deleteRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    deleteCartItem(documentId,userEmail);
                }else{

                }

            }
        });

    }



    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNameTextView,itemQtyTextView,itemPriceTextView,itemDeChargeTextView;
        private ImageView itemImage;
        private RelativeLayout deleteRelativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name);
            itemQtyTextView = itemView.findViewById(R.id.item_qty);
            itemPriceTextView = itemView.findViewById(R.id.item_price);
            itemDeChargeTextView = itemView.findViewById(R.id.item_deliveryCharge);
            itemImage = itemView.findViewById(R.id.item_image);
            deleteRelativeLayout = itemView.findViewById(R.id.cart_delete);
        }
    }

    private void deleteCartItem(String documentId,String userEmail) {
        CollectionReference userCartCollection = firestore.collection("users").document(userEmail).collection("cart");
        userCartCollection.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {
                    // Handle the failure to delete the item
                });
    }

    private void loadImage(String imagePath,ImageView imageView){
        if (imagePath != null) {
            storage.getReference("item-images/" + imagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Picasso.get()
                                .load(uri)
                                .resize(150, 150)
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