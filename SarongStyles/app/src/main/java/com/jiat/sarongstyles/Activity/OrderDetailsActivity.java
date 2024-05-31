package com.jiat.sarongstyles.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstyles.Adapter.OrderAdapter;
import com.jiat.sarongstyles.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ImageView backarrow;
    private OrderAdapter orderAdapter;
    private List<Map<String, Object>> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        setContentView(R.layout.activity_order_details);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewOO);
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList,OrderDetailsActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(orderAdapter);

        // get realtime data
        setUpFirestoreListener();

        //Back Arrow
        backarrow=findViewById(R.id.back_arrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }


    private void setUpFirestoreListener() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            firestore.collection("orders")
                    .whereEqualTo("userEmail",userEmail)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                // Handle error
                                return;
                            }

                            // Clear previous data
                            orderList.clear();

                            // Process the new data
                            for (QueryDocumentSnapshot document : value) {
                                Map<String, Object> orderData = document.getData();
                                orderData.put("documentId", document.getId());

                                orderList.add(orderData);
                            }

                            // Update the UI
                            orderAdapter.notifyDataSetChanged();
                        }
                    });

        }

    }

}