package com.jiat.sarongstyles.Activity;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstyles.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailView extends AppCompatActivity {

    private ImageView addProduct,reduceProduct,fav_normal,fav_click;
    private RelativeLayout addToCartClick,buyClick;
    private TextView productCount,itemPriceTextView;
    int count = 1;
    double price;
    int solution1 = 0;
    String itemPrice;
    String itemQty;
    String itemId;
    String itemDeliveryCharge;
    String imagePath;




    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ImageView backarrow;
    private Fragment fragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_item_detail_view);

        firestore=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        productCount=findViewById(R.id.item_count);
        productCount.setText("" + count);


        Intent intent = getIntent();
        if (intent.hasExtra("itemData")) {
            Map<String, Object> itemData = (Map<String, Object>) intent.getSerializableExtra("itemData");

            itemId = String.valueOf(itemData.get("documentId"));
            String itemName = String.valueOf(itemData.get("itemName"));
            itemPrice = String.valueOf(itemData.get("price"));
            String itemDescription = String.valueOf(itemData.get("description"));
            itemQty = String.valueOf(itemData.get("qty"));
            String itemCategoryId = String.valueOf(itemData.get("categoryId"));
            itemDeliveryCharge = String.valueOf(itemData.get("deliveryCharge"));
            imagePath = String.valueOf(itemData.get("image"));

            TextView itemNameTextView = findViewById(R.id.item_name);
            itemPriceTextView = findViewById(R.id.item_price);
            TextView itemDescTextView = findViewById(R.id.item_description);
            TextView itemCatNameTextView = findViewById(R.id.item_categoryName);
            TextView itemQtyTextView = findViewById(R.id.item_quantity);
            TextView itemDChargeTextView = findViewById(R.id.item_deliveryCharge);
            ImageView itemImageView = findViewById(R.id.itemImage);

            itemNameTextView.setText(itemName);
            itemPriceTextView.setText("LKR "+itemPrice+"0");
            itemDescTextView.setText(itemDescription);
            itemQtyTextView.setText(itemQty+" items available now.");
            itemDChargeTextView.setText("LKR "+itemDeliveryCharge+"0");

            firestore.collection("category")
                    .document(itemCategoryId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String categoryName = documentSnapshot.getString("categoryName");
                                itemCatNameTextView.setText(categoryName);
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
                                    .resize(300,300)
                                    .centerCrop()
                                    .into(itemImageView);
                        }
                    });

        }

        price = Double.parseDouble(itemPrice);
        addProduct=findViewById(R.id.add_product);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(productCount.getText().toString().equals(itemQty)){
                    Toast.makeText(ItemDetailView.this, "Item Quantity Exceeded", Toast.LENGTH_SHORT).show();
                }else{
                    productCount.setText("" + ++count);
                    String value2 = productCount.getText().toString();
                    int num2 = Integer.parseInt(value2);
                    solution1 = (int) (price * num2);

                    itemPriceTextView.setText("LKR "+String.valueOf(solution1)+".00");
                }

            }
        });



        reduceProduct=findViewById(R.id.reduce_product);
        reduceProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(productCount.getText().toString().equals("1")){

                }else{

                    productCount.setText("" + --count);
                    String value2 = productCount.getText().toString();
                    int num2 = Integer.parseInt(value2);
                    solution1 = (int) (price * num2);

                    itemPriceTextView.setText("LKR "+String.valueOf(solution1)+".00");

                }

            }
        });



        //Back Arrow
        backarrow=findViewById(R.id.back_arrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        //Cart
        addToCartClick=findViewById(R.id.addToCart_click);
        addToCartClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    String cart_item_price = itemPriceTextView.getText().toString();
                    String cart_item_qty = productCount.getText().toString();

                    String[] parts = cart_item_price.split(" ");
                    String numericPrice = parts[1];
                    String[] parts2 = numericPrice.split("\\.");
                    String lastPrice = parts2[0];

                    Double items_price = Double.parseDouble(lastPrice);
                    Double items_DCharge = Double.parseDouble(itemDeliveryCharge);

                    CollectionReference userCartCollection = firestore.collection("users").document(userEmail).collection("cart");

                    Map<String, Object> cartData = new HashMap<>();
                    cartData.put("itemID", itemId);
                    cartData.put("itemsQty", cart_item_qty);
                    cartData.put("itemsPrice", items_price);
                    cartData.put("itemsDCharge", items_DCharge);

                    userCartCollection.add(cartData)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //Toast.makeText(getApplicationContext(), userEmail+" "+itemId+" "+lastPrice+" "+cart_item_qty, Toast.LENGTH_SHORT).show();
//                                    fragment = new CartFragment();
//                                    loadFragment(fragment);
                                    Toast.makeText(ItemDetailView.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {

                            });


                } else {
                    Toast.makeText(getApplicationContext(), "Please login first", Toast.LENGTH_SHORT).show();
                }


            }
        });



        buyClick=findViewById(R.id.buy_click);
        buyClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    TextView item_Name_TextView = findViewById(R.id.item_name);
                    String item_Name = item_Name_TextView.getText().toString();
                    String item_Price = itemPriceTextView.getText().toString();
                    String item_Qty = productCount.getText().toString();

                    Map<String, Object> buyData = new HashMap<>();
                    buyData.put("itemId",itemId);
                    buyData.put("itemName",item_Name);
                    buyData.put("itemPrice",item_Price);
                    buyData.put("itemQty",item_Qty);
                    buyData.put("itemDeliveryCharge",itemDeliveryCharge);
                    buyData.put("itemImagePath",imagePath);

                    Intent intent = new Intent(ItemDetailView.this, CheckoutActivity.class);
                    intent.putExtra("buyData", (Serializable) buyData);
                    startActivity(intent);


                }else{
                    Toast.makeText(ItemDetailView.this, "Please login first", Toast.LENGTH_SHORT).show();
                }

            }
        });


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            checkWishItem(itemId);
        }

        fav_normal = (ImageView) findViewById(R.id.favourite);
        fav_click = (ImageView) findViewById(R.id.favourite_click);

        fav_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    fav_normal.setVisibility(View.GONE);
                    fav_click.setVisibility(View.VISIBLE);
                    String userEmail = currentUser.getEmail();

                    CollectionReference userWishCollection = firestore.collection("users").document(userEmail).collection("wishlist");

                    Map<String, Object> wishData = new HashMap<>();
                    wishData.put("itemID", itemId);

                    userWishCollection.add(wishData)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Toast.makeText(ItemDetailView.this, "Item added to wishlist", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {

                            });
                }else{
                    fav_normal.setVisibility(View.VISIBLE);
                    fav_click.setVisibility(View.GONE);
                    Toast.makeText(ItemDetailView.this, "Please login first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        fav_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    fav_normal.setVisibility(View.VISIBLE);
                    fav_click.setVisibility(View.GONE);

                    String userEmail = currentUser.getEmail();

                    CollectionReference userWishCollection = firestore.collection("users").document(userEmail).collection("wishlist");

                    userWishCollection.whereEqualTo("itemID", itemId)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                        userWishCollection.document(document.getId())
                                                .delete()
                                                .addOnSuccessListener((Void aVoid) -> {
                                                    Toast.makeText(ItemDetailView.this, "Item removed from wishlist", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {

                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to query the wishlist
                            });
                }else{

                }

            }
        });



    }

//    private void loadFragment(Fragment fragment){
//        if (fragment != null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, fragment)
//                    .commit();
//        }
//    }


        private void checkWishItem(String itemId){
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();

                CollectionReference userWishCollection2 = firestore.collection("users").document(userEmail).collection("wishlist");

                userWishCollection2.whereEqualTo("itemID",itemId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    fav_normal.setVisibility(View.GONE);
                                    fav_click.setVisibility(View.VISIBLE);
                                } else {
                                    fav_normal.setVisibility(View.VISIBLE);
                                    fav_click.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }else{
                fav_normal.setVisibility(View.VISIBLE);
                fav_click.setVisibility(View.GONE);
            }
        }



}