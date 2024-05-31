package com.jiat.sarongstyles.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstyles.Activity.CheckoutActivity;
import com.jiat.sarongstyles.Activity.DeliveryDetailsActivity;
import com.jiat.sarongstyles.Activity.MainActivity;
import com.jiat.sarongstyles.Activity.MapActivity;
import com.jiat.sarongstyles.Activity.OrderProceedActivity;
import com.jiat.sarongstyles.Activity.PaymentDetailsActivity;
import com.jiat.sarongstyles.Adapter.CartAdapter;
import com.jiat.sarongstyles.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CartFragment extends Fragment {


    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Map<String, Object>> cartList;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ImageView backarrow;
    private Context context;
    private RelativeLayout setDetails,setPayment;
    private TextView subTotal,deliveryCost,priceTotal,name,mobile,address,loctionSet,card_name,card_number;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = root.findViewById(R.id.recyclerView);
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(cartAdapter);

        subTotal = root.findViewById(R.id.subTotal);
        deliveryCost = root.findViewById(R.id.dCost);
        priceTotal = root.findViewById(R.id.priceTotal);

        name = root.findViewById(R.id.name);
        address = root.findViewById(R.id.address);
        mobile = root.findViewById(R.id.mobile);
//        loctionSet = root.findViewById(R.id.location);

        card_name = root.findViewById(R.id.payment_name);
        card_number = root.findViewById(R.id.payment_card);


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                setUpFirestoreListener(userEmail);
            }else{

            }


        loadSavedDeliveryDetails();

        setDetails= (RelativeLayout) root.findViewById(R.id.setDetails);
        setDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "set details", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DeliveryDetailsActivity.class);
                startActivity(intent);

            }
        });

        loadSavedPaymentDetails();

        setPayment= (RelativeLayout) root.findViewById(R.id.setPayment);
        setPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "set details", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), PaymentDetailsActivity.class);
                startActivity(intent);

            }
        });

        AppCompatButton buttonCheckout =root.findViewById(R.id.button_checkout2);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                                        Toast.makeText(context, "Please Add Delivery Details", Toast.LENGTH_SHORT).show();
                                    } else if (cardName=="No Card") {
                                        Toast.makeText(context, "Please Add Payment Details", Toast.LENGTH_SHORT).show();
                                    }else{

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        String formattedDate = dateFormat.format(new Date());

                                        Map<String, Object> orderData = new HashMap<>();
                                        orderData.put("userEmail",userEmail);
                                        orderData.put("subTotal",subTotal.getText());
                                        orderData.put("deliveryCost",deliveryCost.getText());
                                        orderData.put("grandTotal",priceTotal.getText());
                                        orderData.put("fullName",dName);
                                        orderData.put("address",address.getText());
                                        orderData.put("mobileNumber",mobile.getText());
                                        orderData.put("location","no");
                                        orderData.put("orderDate",formattedDate);
                                        orderData.put("status",false);

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


                                        CollectionReference cartCollection = firestore.collection("users").document(userEmail).collection("cart");
                                        CollectionReference orderItemsCollection = firestore.collection("orders").document(orderId).collection("items");

                                        cartCollection.get().addOnSuccessListener(querySnapshot -> {
                                            for (QueryDocumentSnapshot document : querySnapshot) {
                                                // Get the data from the cart item
                                                Map<String, Object> cartItemData = document.getData();

                                                // Add the cart item data to the "items" sub-collection under the order document
                                                orderItemsCollection.add(cartItemData);
                                            }
                                        }).addOnFailureListener(e -> {

                                        });


                                        Toast.makeText(context, "Order added Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, OrderProceedActivity.class);
                                        intent.putExtra("YourOrderId", (Serializable) orderId);
                                        startActivity(intent);

                                        reduceQuantity(orderId);
                                        removeCartData(userEmail);

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



        return  root;
    }




    private void setUpFirestoreListener(String userEmail) {
        CollectionReference userCartCollection = firestore.collection("users").document(userEmail).collection("cart");

        userCartCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                cartList.clear();

                double subTotalPrice = 0.0;
                double highestDCharge = 0.0;

                for (QueryDocumentSnapshot document : value) {
                    Map<String, Object> cartData = document.getData();
                    cartData.put("documentId", document.getId());
                    cartList.add(cartData);

                    if (cartData.containsKey("itemsPrice")) {
                        try {
                            double itemsPrice = Double.parseDouble(String.valueOf(cartData.get("itemsPrice")));
                            double itemsDCharge = Double.parseDouble(String.valueOf(cartData.get("itemsDCharge")));
                            highestDCharge = Math.max(highestDCharge, itemsDCharge);
                            subTotalPrice += itemsPrice;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                }

                updateTotalPrice(subTotalPrice,highestDCharge);
                cartAdapter.notifyDataSetChanged();

            }
        });

    }

    private void updateTotalPrice(double subTotalPrice,double highestDCharge) {
        double totalPrice = subTotalPrice+highestDCharge;
        subTotal.setText("LKR "+String.valueOf(subTotalPrice)+"0");
        deliveryCost.setText("LKR "+String.valueOf(highestDCharge)+"0");
        priceTotal.setText("LKR "+String.valueOf(totalPrice)+"0");
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


    private void reduceQuantity(String orderId) {
        CollectionReference orderItemCollection = firestore.collection("orders").document(orderId).collection("items");

        orderItemCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String itemId = document.getString("itemID");
                        String itemsQty = document.getString("itemsQty");


                        DocumentReference itemDocument = firestore.collection("item").document(itemId);

                        itemDocument.get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String currentQuantity = documentSnapshot.getString("qty");
                                        int cq = Integer.parseInt(currentQuantity);
                                        int iq = Integer.parseInt(itemsQty);

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
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }



    private void removeCartData(String userEmail) {

        CollectionReference userCartCollection = firestore.collection("users").document(userEmail).collection("cart");

        userCartCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = firestore.batch();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        batch.delete(document.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                // Deletion successful
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });

    }





}