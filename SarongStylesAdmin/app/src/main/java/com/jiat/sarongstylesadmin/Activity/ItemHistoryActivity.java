package com.jiat.sarongstylesadmin.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jiat.sarongstylesadmin.Adapter.ItemAdapter;
import com.jiat.sarongstylesadmin.Adapter.OrderHistoryAdapter;
import com.jiat.sarongstylesadmin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button updateButton;
    private ImageView backarrow;
    private FirebaseFirestore firestore;
    private ItemAdapter itemAdapter;
    private List<Map<String, Object>> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //REMOVE TOOLBAR
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        setContentView(R.layout.activity_item_history);

        firestore = FirebaseFirestore.getInstance();


        recyclerView = findViewById(R.id.recyclerViewI);
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList,ItemHistoryActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(itemAdapter);

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
        firestore.collection("item")
                .whereEqualTo("status",false)
//                .orderBy("registeredDate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        // Clear previous data
                        itemList.clear();

                        // Process the new data
                        for (QueryDocumentSnapshot document : value) {
                            Map<String, Object> itemData = document.getData();
                            itemData.put("documentId", document.getId());
                            itemList.add(itemData);
                        }

                        // Update the UI
                        itemAdapter.notifyDataSetChanged();
                    }
                });
    }






}