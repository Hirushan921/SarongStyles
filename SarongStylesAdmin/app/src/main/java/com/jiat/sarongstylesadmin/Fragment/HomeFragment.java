package com.jiat.sarongstylesadmin.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.firestore.FirebaseFirestore;
import com.jiat.sarongstylesadmin.R;


public class HomeFragment extends Fragment {
    private FirebaseFirestore firestore;


    private Context context;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        firestore = FirebaseFirestore.getInstance();


        firestore.collection("item")
                .whereEqualTo("status",true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalItems = queryDocumentSnapshots.size();
                    TextView tI = root.findViewById(R.id.total_items);
                    tI.setText(String.valueOf(totalItems));
                })
                .addOnFailureListener(e -> {

                });

        firestore.collection("category")
                .whereEqualTo("status",true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalCategory = queryDocumentSnapshots.size();
                    TextView tC = root.findViewById(R.id.total_cat);
                    tC.setText(String.valueOf(totalCategory));
                })
                .addOnFailureListener(e -> {

                });

        firestore.collection("orders").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalOrders = queryDocumentSnapshots.size();
                    TextView tO = root.findViewById(R.id.total_orders);
                    tO.setText(String.valueOf(totalOrders));
                })
                .addOnFailureListener(e -> {

                });

        firestore.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalUsers = queryDocumentSnapshots.size();
                    TextView tU = root.findViewById(R.id.total_cus);
                    tU.setText(String.valueOf(totalUsers));
                })
                .addOnFailureListener(e -> {

                });















        return  root;
    }


}