package com.jiat.sarongstylesadmin.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstylesadmin.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ItemUpdateActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ImageButton imageButton;
    private Spinner categorySpinner;
    private TextInputEditText itemName_edit,description_edit,qty_edit,price_edit,deliveryCharge_edit,category_edit;
    String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        setContentView(R.layout.activity_item_update);

        firestore= FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        itemName_edit = findViewById(R.id.edit_item_name);
        description_edit = findViewById(R.id.edit_description);
        qty_edit = findViewById(R.id.edit_quantity);
        price_edit = findViewById(R.id.edit_price);
        deliveryCharge_edit = findViewById(R.id.edit_delivery_charge);
        category_edit = findViewById(R.id.edit_categoryy);
        imageButton = findViewById(R.id.imageButton);

        Picasso.get()
                .load(R.drawable.image_add)
                .fit()
                .centerCrop()
                .into(imageButton);

        Intent intent = getIntent();
        if (intent.hasExtra("itemData")) {
            Map<String, Object> itemData = (Map<String, Object>) intent.getSerializableExtra("itemData");
            itemId = String.valueOf(itemData.get("documentId"));
            String itemName = String.valueOf(itemData.get("itemName"));
            String itemPrice = String.valueOf(itemData.get("price"));
            String itemDescription = String.valueOf(itemData.get("description"));
            String itemQty = String.valueOf(itemData.get("qty"));
            String itemCategoryId = String.valueOf(itemData.get("categoryId"));
            String itemDeliveryCharge = String.valueOf(itemData.get("deliveryCharge"));
            String imagePath = String.valueOf(itemData.get("image"));

            itemName_edit.setText(itemName);
            description_edit.setText(itemDescription);
            qty_edit.setText(itemQty);
            price_edit.setText(itemPrice+"0");
            deliveryCharge_edit.setText(itemDeliveryCharge+"0");


            firestore.collection("category")
                    .document(itemCategoryId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String categoryName = documentSnapshot.getString("categoryName");
                                category_edit.setText(categoryName);
                            } else {

                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                    });


            storage.getReference("item-images/"+imagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .fit()
                                    .centerCrop()
                                    .into(imageButton);
                        }
                    });

        }

        Button updateButton = findViewById(R.id.button_item_update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = description_edit.getText().toString();
                String qty = qty_edit.getText().toString();

                if (description.isEmpty()) {
                    Toast.makeText(ItemUpdateActivity.this, "Please enter description", Toast.LENGTH_SHORT).show();
                }else if (qty.isEmpty()) {
                    Toast.makeText(ItemUpdateActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                }else{

                Map<String,Object> updates = new HashMap<>();
                updates.put("description",description);
                updates.put("qty",qty);

                    firestore.collection("item")
                            .document(itemId)
                            .update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ItemUpdateActivity.this, "Item Updated Successfully.", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
            }
        });
    }
}