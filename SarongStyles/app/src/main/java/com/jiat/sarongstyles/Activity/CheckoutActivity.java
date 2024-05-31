package com.jiat.sarongstyles.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstyles.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private TextView item_name,item_price,item_qty,item_deliveryCharge,sub_total,d_cost,price_total;
    private  TextView name,mobile,address,loctionSet,card_name,card_number;
    private ImageView backarrow;
    private ImageView item_ImageView;
    private RelativeLayout setDetails,setPayment;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    String itemId;
    String itemQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_checkout);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        item_name =findViewById(R.id.item_name);
        item_qty =findViewById(R.id.item_qty);
        item_price =findViewById(R.id.item_price);
        item_deliveryCharge =findViewById(R.id.item_deliveryCharge);
        item_ImageView = findViewById(R.id.item_image);
        sub_total = findViewById(R.id.subTotal);
        d_cost = findViewById(R.id.dCost);
        price_total = findViewById(R.id.priceTotal);

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        mobile = findViewById(R.id.mobile);
//        loctionSet =findViewById(R.id.location);

        card_name = findViewById(R.id.payment_name);
        card_number = findViewById(R.id.payment_card);

        loadSavedDeliveryDetails();

        setDetails= (RelativeLayout) findViewById(R.id.setDetails);
        setDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "set details", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CheckoutActivity.this, DeliveryDetailsActivity.class);
                startActivity(intent);

            }
        });

        loadSavedPaymentDetails();

        setPayment= (RelativeLayout) findViewById(R.id.setPayment);
        setPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "set details", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CheckoutActivity.this, PaymentDetailsActivity.class);
                startActivity(intent);

            }
        });

        AppCompatButton buttonCheckout =findViewById(R.id.button_checkout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                builder.setTitle("Confirm Order")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to confirm order")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                if (currentUser != null) {
                                    String userEmail = currentUser.getEmail();

                                    String dName = String.valueOf(name.getText());
                                    String cardName = String.valueOf(card_name.getText());

                                    if (dName=="Name"){
                                        Toast.makeText(CheckoutActivity.this, "Please Add Delivery Details", Toast.LENGTH_SHORT).show();
                                    } else if (cardName=="No Card") {
                                        Toast.makeText(CheckoutActivity.this, "Please Add Payment Details", Toast.LENGTH_SHORT).show();
                                    }else{

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        String formattedDate = dateFormat.format(new Date());

                                        Map<String, Object> orderData = new HashMap<>();
                                        orderData.put("userEmail",userEmail);
                                        orderData.put("subTotal",sub_total.getText());
                                        orderData.put("deliveryCost",d_cost.getText());
                                        orderData.put("grandTotal",price_total.getText());
                                        orderData.put("fullName",dName);
                                        orderData.put("address",address.getText());
                                        orderData.put("mobileNumber",mobile.getText());
                                        orderData.put("location","no");
                                        orderData.put("orderDate",formattedDate);
                                        orderData.put("status",false);

                                        Map<String, Object> orderItemData = new HashMap<>();
                                        orderItemData.put("itemID",itemId);
                                        orderItemData.put("itemsQty",itemQty);

                                        String currentTime = String.valueOf(System.currentTimeMillis());
                                        String orderId = "O"+currentTime;

                                        firestore.collection("orders")
                                                .document(orderId)
                                                .set(orderData)
                                                .addOnSuccessListener(aVoid -> {
                                                    //Toast.makeText(CheckoutActivity.this,"Saved Successfully",Toast.LENGTH_LONG).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    System.err.println("Error adding order: " + e.getMessage());
                                                });

                                        CollectionReference userOrderItemCollection = firestore.collection("orders").document(orderId).collection("items");
                                        userOrderItemCollection.add(orderItemData)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        //Toast.makeText(CheckoutActivity.this, "Order added", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {

                                                });


                                        Toast.makeText(CheckoutActivity.this, "Order added Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CheckoutActivity.this, OrderProceedActivity.class);
                                        intent.putExtra("YourOrderId", (Serializable) orderId);
                                        startActivity(intent);

                                        reduceQuantity(itemId,itemQty);

                                    }


                                }


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();




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



        Intent intent = getIntent();
        if (intent.hasExtra("buyData")) {
            Map<String, Object> buyData = (Map<String, Object>) intent.getSerializableExtra("buyData");

            itemId = String.valueOf(buyData.get("itemId"));
            String itemName = String.valueOf(buyData.get("itemName"));
            itemQty = String.valueOf(buyData.get("itemQty"));
            String itemPrice = String.valueOf(buyData.get("itemPrice"));
            String itemDeliveryCharge = String.valueOf(buyData.get("itemDeliveryCharge"));
            String itemImagePath = String.valueOf(buyData.get("itemImagePath"));

            item_name.setText(itemName);
            item_qty.setText(itemQty+" items");
            item_price.setText("Price: "+itemPrice);
            item_deliveryCharge.setText("Delivery Charge: LKR "+itemDeliveryCharge+"0");

            sub_total.setText(itemPrice);
            d_cost.setText("LKR "+itemDeliveryCharge+"0");

            String[] parts = itemPrice.split(" ");
            String numericPrice = parts[1];
            String[] parts2 = numericPrice.split("\\.");
            String lastPrice = parts2[0];

            double numPrice = Double.parseDouble(lastPrice);
            double numDPrice = Double.parseDouble(itemDeliveryCharge);

            double totalValue =  numPrice+numDPrice;

            String totalPrice = String.valueOf(totalValue);

            price_total.setText("LKR "+totalPrice+"0");

            storage.getReference("item-images/"+itemImagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .resize(150,150)
                                    .centerCrop()
                                    .into(item_ImageView);
                        }
                    });

        }


    }

    private void checkOutButtonProcess(){

    }



    private void loadSavedDeliveryDetails() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            firestore.collection("users")
                    .document(userEmail)
                    .collection("deliveryDetails")
                    .limit(1)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                // Handle the error
                                return;
                            }

                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                                String documentId = documentSnapshot.getId();
                                String full_name = documentSnapshot.getString("fullName");
                                String mobile_number = documentSnapshot.getString("mobileNumber");
                                String addressD = documentSnapshot.getString("address");

                                name.setText(full_name);
                                mobile.setText(mobile_number);
                                address.setText(addressD);

                            } else {

                            }
                        }
                    });



        }

    }





    private void loadSavedPaymentDetails() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            firestore.collection("users")
                    .document(userEmail)
                    .collection("paymentDetails")
                    .limit(1)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                // Handle the error
                                return;
                            }

                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                                String documentId = documentSnapshot.getId();
                                String card_Name = documentSnapshot.getString("cardName");
                                String card_Number = documentSnapshot.getString("cardNumber");

                                card_name.setText(card_Name);
                                card_number.setText(card_Number);

                            } else {
                                // Handle the case where no payment details are available
                            }


                        }
                    });




        }

    }



    private void reduceQuantity(String itemId, String itemQty) {
        DocumentReference itemDocument = firestore.collection("item").document(itemId);

        itemDocument.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String currentQuantity = documentSnapshot.getString("qty");
                        int cq = Integer.parseInt(currentQuantity);
                        int iq = Integer.parseInt(itemQty);

                        int nq = cq-iq;
                        String newQuantity = String.valueOf(nq);

                        itemDocument
                                .update("qty", newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                    // Update successful
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }




}