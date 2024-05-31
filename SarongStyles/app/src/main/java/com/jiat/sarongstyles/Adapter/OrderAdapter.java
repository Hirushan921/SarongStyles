package com.jiat.sarongstyles.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.sarongstyles.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Map<String, Object>> orderList;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Context context;

    String itemName;
    public OrderAdapter(List<Map<String, Object>> orderList, Context context) {
        this.context=context;
        this.orderList = orderList;
        firestore=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_view_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> orderData = orderList.get(position);

        String orderID = String.valueOf(orderData.get("documentId"));
        holder.orderNotextView.setText(orderID);
        holder.orderdateTextView.setText(String.valueOf(orderData.get("orderDate")));
        holder.fullnameTextView.setText("Name: "+String.valueOf(orderData.get("fullName")));
        holder.emailTextView.setText("Email: "+String.valueOf(orderData.get("userEmail")));
        holder.mobileTextView.setText("Mobile: "+String.valueOf(orderData.get("mobileNumber")));
        holder.addressTextView.setText("Address: "+String.valueOf(orderData.get("address")));
        holder.locationTextView.setText("Location: "+String.valueOf(orderData.get("location")));
        holder.totalPriceTextView.setText("Total Price: "+String.valueOf(orderData.get("grandTotal")));
        holder.o_status.setText(orderData.get("status").equals(true) ? "Delivered" : "Pending");

        holder.itemListLayout.removeAllViews();
        CollectionReference orderItemCollection = firestore.collection("orders").document(orderID).collection("items");
        orderItemCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot documentS : querySnapshot) {
                        String itemId = documentS.getString("itemID");


                        firestore.collection("item")
                                .document(itemId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        itemName = documentSnapshot.getString("itemName");
                                        String itemsQty = documentS.getString("itemsQty");
                                        String itemText = itemName + " x " + itemsQty;


                                        TextView textView = new TextView(holder.itemView.getContext());
                                        textView.setText(itemText);

                                        // Set text color using ContextCompat
                                        int textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.black);
                                        textView.setTextColor(textColor);

                                        holder.itemListLayout.addView(textView);

                                    } else {
                                        // Item with the given itemId does not exist
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to retrieve data
                                });



//                        String itemsQty = documentS.getString("itemsQty");
//                        String itemText = itemName + " x " + itemsQty;
//
//
//                        TextView textView = new TextView(holder.itemView.getContext());
//                        textView.setText(itemText);
//
//                        // Set text color using ContextCompat
//                        int textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.black);
//                        textView.setTextColor(textColor);
//
//                        holder.itemListLayout.addView(textView);


                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });




    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView o_status,fullnameTextView,emailTextView,mobileTextView,addressTextView,locationTextView,totalPriceTextView,orderNotextView,orderdateTextView;
        private LinearLayout itemListLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNotextView = itemView.findViewById(R.id.orderNo);
            orderdateTextView = itemView.findViewById(R.id.orderDate);
            fullnameTextView = itemView.findViewById(R.id.fullname);
            emailTextView = itemView.findViewById(R.id.email);
            mobileTextView = itemView.findViewById(R.id.mobile);
            addressTextView = itemView.findViewById(R.id.address);
            locationTextView = itemView.findViewById(R.id.location);
            totalPriceTextView = itemView.findViewById(R.id.totalPrice);
            itemListLayout = itemView.findViewById(R.id.itemListLayout);
            o_status = itemView.findViewById(R.id.o_status);

        }
    }

    private void updateOrderStatus(String orderId, boolean newStatus) {
        firestore.collection("orders")
                .document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

}